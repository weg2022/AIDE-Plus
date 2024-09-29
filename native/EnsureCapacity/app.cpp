#include <dlfcn.h>
#include <jni.h>
#include <sys/mman.h>
#include <unistd.h>

#include <cstring>

#include "elf_img.h"

static constexpr uintptr_t align_down(uintptr_t p, size_t align) {
  return p & ~(align - 1);
}

uint32_t VMRuntime_getFinalizerTimeoutMs(JNIEnv* env) {
  jclass vm_runtime_cls = env->FindClass("dalvik/system/VMRuntime");
  jobject vm_runtime = env->CallStaticObjectMethod(
      vm_runtime_cls,
      env->GetStaticMethodID(vm_runtime_cls, "getRuntime", "()Ldalvik/system/VMRuntime;"));
  return env->CallLongMethod(vm_runtime,
                             env->GetMethodID(vm_runtime_cls, "getFinalizerTimeoutMs", "()J"));
}

jlong Runtime_maxMemory(JNIEnv* env) {
  jclass runtime_cls = env->FindClass("java/lang/Runtime");
  jobject runtime = env->CallStaticObjectMethod(
      runtime_cls, env->GetStaticMethodID(runtime_cls, "getRuntime", "()Ljava/lang/Runtime;"));
  return env->CallLongMethod(runtime, env->GetMethodID(runtime_cls, "maxMemory", "()J"));
}

struct DalvikMemoryInfo {
  size_t main_space_start;
  size_t main_space_end;
  size_t max_main_space_end;
  size_t fault_page_start;
};

DalvikMemoryInfo GetDalvikMemoryInfoInMaps() {
  auto maps = fopen("/proc/self/maps", "r");
  char buf[128];
  size_t start, end;
  DalvikMemoryInfo info{};

  while (fgets(buf, sizeof(buf), maps)) {
    sscanf(buf, "%lx-%lx", &start, &end);
    if (start > 0xFFFFFFFFUL) break;
    if (info.main_space_start) {
      if (strstr(buf, "Sentinel fault page")) {
        info.fault_page_start = start;
      } else {
        info.max_main_space_end = start;
        break;
      }
    } else if (strstr(buf, "dalvik-main space")) {
      info.main_space_start = start;
      info.main_space_end = end;
    }
  }

  fclose(maps);

  if (!info.max_main_space_end) {
    info.max_main_space_end = 0x100000000UL - getpagesize();
  }
  return info;
}

bool ExpandMaxMemory(JNIEnv* env, size_t* mem) {
  auto info = GetDalvikMemoryInfoInMaps();
  if (info.main_space_start == 0) return false;
  auto safe_max_mem_size = std::min(info.main_space_end - info.main_space_start,
                                    (info.max_main_space_end - info.main_space_start) / 2);

  size_t backup[4];
  memcpy(backup, mem, sizeof(backup));

  mem[0] = safe_max_mem_size;
  mem[1] = safe_max_mem_size;
  mem[2] = safe_max_mem_size;
  mem[3] = safe_max_mem_size;

  if (Runtime_maxMemory(env) != safe_max_mem_size) {
    memcpy(mem, backup, sizeof(backup));
    return false;
  }

  if (info.fault_page_start) {
    munmap(reinterpret_cast<void*>(info.fault_page_start), getpagesize());
  }
  mremap(reinterpret_cast<void*>(info.main_space_start),
         info.main_space_end - info.main_space_start,
         info.max_main_space_end - info.main_space_start,
         0,
         nullptr);
  return true;
}

void FuckFunction(void* addr) {
  if (!addr) return;
  auto page_size = getpagesize();
  mprotect(reinterpret_cast<void*>(align_down(reinterpret_cast<uintptr_t>(addr), page_size)),
           page_size,
           PROT_READ | PROT_WRITE | PROT_EXEC);
  *reinterpret_cast<uint32_t*>(addr) = 0xd65f03c0u;
  __builtin___clear_cache(reinterpret_cast<char*>(addr), reinterpret_cast<char*>(addr) + 4);
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

  if (!runtime_instance) return JNI_VERSION_1_6;
  auto finalizer_timeout_ms = VMRuntime_getFinalizerTimeoutMs(env);
  auto runtime = reinterpret_cast<uint32_t*>(*runtime_instance);

  for (int i = 0; 256 > i; i++) {
    if (runtime[i] != finalizer_timeout_ms) continue;
    auto heap = *reinterpret_cast<size_t**>(&runtime[i + (i % 2 ? 1 : 2)]);
    if (!heap) continue;
    auto max_mem = Runtime_maxMemory(env);
    for (int j = 0; 128 > j; ++j) {
      if (heap[j] != max_mem) continue;
      if (ExpandMaxMemory(env, heap + j)) {
        FuckFunction(task_run_func);
      }
      break;
    }
    break;
  }

  return JNI_VERSION_1_6;
}
