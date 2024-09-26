#include <dlfcn.h>
#include <jni.h>
#include <sys/mman.h>
#include <unistd.h>

#include "elf_img.h"

static constexpr uintptr_t align_down(uintptr_t p, size_t align) {
  return p & ~(align - 1);
}

static inline uint32_t VMRuntime_getFinalizerTimeoutMs(JNIEnv* env) {
  jclass vm_runtime_cls = env->FindClass("dalvik/system/VMRuntime");
  jobject vm_runtime = env->CallStaticObjectMethod(
      vm_runtime_cls,
      env->GetStaticMethodID(vm_runtime_cls, "getRuntime", "()Ldalvik/system/VMRuntime;"));
  return env->CallLongMethod(vm_runtime,
                             env->GetMethodID(vm_runtime_cls, "getFinalizerTimeoutMs", "()J"));
}

static inline jlong Runtime_maxMemory(JNIEnv* env) {
  jclass vm_runtime_cls = env->FindClass("java/lang/Runtime");
  jobject vm_runtime = env->CallStaticObjectMethod(
      vm_runtime_cls,
      env->GetStaticMethodID(vm_runtime_cls, "getRuntime", "()Ljava/lang/Runtime;"));
  return env->CallLongMethod(vm_runtime, env->GetMethodID(vm_runtime_cls, "maxMemory", "()J"));
}

jint JNI_OnLoad(JavaVM* vm, void*) {
  JNIEnv* env;
  vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);

  Dl_info info;
  dladdr(reinterpret_cast<void*>(env->functions->FindClass), &info);
  pine::ElfImg art(info.dli_fname, false, false);
  auto runtime_instance =
      reinterpret_cast<void**>(art.GetSymbolAddress("_ZN3art7Runtime9instance_E"));
  auto task_run_func = art.GetSymbolAddress("_ZN3art2gc4Heap17SetIdealFootprintEm");
  if (task_run_func) {
    auto page_size = getpagesize();
    mprotect(
        reinterpret_cast<void*>(align_down(reinterpret_cast<uintptr_t>(task_run_func), page_size)),
        page_size,
        PROT_READ | PROT_WRITE | PROT_EXEC);
    *reinterpret_cast<uint32_t*>(task_run_func) = 0xd65f03c0u;
    __builtin___clear_cache(reinterpret_cast<char*>(task_run_func),
                            reinterpret_cast<char*>(task_run_func) + 4);
  }

  if (runtime_instance) {
    auto finalizer_timeout_ms = VMRuntime_getFinalizerTimeoutMs(env);
    auto runtime = reinterpret_cast<uint32_t*>(*runtime_instance);
    for (int i = 0; 256 > i; i++) {
      if (runtime[i] != finalizer_timeout_ms) continue;
      auto heap = *reinterpret_cast<size_t**>(&runtime[i + (i % 2 ? 1 : 2)]);
      if (!heap) continue;
      auto max_mem = Runtime_maxMemory(env);
      for (int j = 0; 128 > j; ++j) {
        if (heap[j] != max_mem) continue;
        static constexpr size_t kAllocatedMemorySize = 1024 * 1024 * 1024;
        heap[j] = kAllocatedMemorySize;
        heap[j + 1] = kAllocatedMemorySize;
        heap[j + 2] = kAllocatedMemorySize;
        heap[j + 3] = kAllocatedMemorySize;
      }
      break;
    }
  }

  return JNI_VERSION_1_6;
}
