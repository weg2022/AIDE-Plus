package com.aide.ui.build.android;

import com.aide.ui.ServiceContainer;
import com.aide.ui.project.internal.GradleTools;
import io.github.zeroaicy.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import com.aide.ui.services.ZeroAicyProjectService;


@Deprecated
public class AndroidProjectBuildServicePro extends AndroidProjectBuildService {

	@Override
	public void XG() {
		ZeroAicyProjectService.getProjectServiceThreadPoolService().submit(new Runnable(){
				@Override
				public void run() {
					XGAsync();
				}
			});
	}

	private void XGAsync() {
		super.XG();
		//XG2();
	}
	
	/**
	 * 测试
	 */
	 public void XG2() {
		try {
			// 此时 系统服务可能改没有初始化完成
			// libraryMapping并不完整 所以有些key没有value
            Map<String, List<String>> libraryMapping = ServiceContainer.getProjectService().getLibraryMapping();
			
			//System.out.printf("libraryMapping type -> %s \n", libraryMapping.getClass());
			//System.out.printf("isUiThread -> %s\n\n", ThreadPoolService.isUiThread());
			
			Map<String, List<String>> oY = oY(libraryMapping, null);
			ServiceContainer.EQ().Sf(new ArrayList<String>(oY.keySet()));
        }catch (Error th) {
            throw  th;			
		}
		catch (Throwable th) {
			throw new Error(th);
        }
	}
	public static Map<String, List<String>> oY(Map<String, List<String>> map, String str) {
        try {
            HashMap<String, List<String>> hashMap = new HashMap<>();
            Set<Map.Entry<String, List<String>>> entrySet = map.entrySet();
			for(Map.Entry<String, List<String>> next : entrySet){
				String key = next.getKey();
				List<String> value = next.getValue();
				if( value == null ){
					Log.d("爆炸💥 第一次遍历时", String.format( "key -> %s, value -> %s", key, value));
					throw new Error("爆炸💥 第一次遍历时value就是null");
				}
                if (GradleTools.nw(key)) {
                    ArrayList<String> arrayList = new ArrayList<>();

                    hK(FH(key, value, map), str, arrayList);
                    hashMap.put(GradleTools.J8(key), arrayList);
                }
			}
            return hashMap;
        }catch (Error th) {
            throw th;			
		}
		catch (Throwable th) {
			throw new Error(th);
        }
    }
	private static void hK(List<String> list, String str, List<String> list2) {
        try {
            for (String str2 : list) {
                String Ws = GradleTools.Ws(str2);
                if (Ws != null) {
                    list2.add(Ws);
                }
                String J0 = GradleTools.J0(str2, str);
                if (J0 != null) {
                    list2.add(J0);
                }
                list2.add(GradleTools.yS(str2));
            }
        }catch (Error th) {
            throw th;			
		}
		catch (Throwable th) {
			throw new Error(th);
        }
    }
	
    public static List<String> FH(String key, List<String> value, Map<String, List<String>> map) {
        try {
            HashSet<String> hashSet = new HashSet<>();
            ArrayList<String> arrayList = new ArrayList<>();
            Hw(key, value, map, hashSet, arrayList);
            return arrayList;
        }catch (Error th) {
            throw th;			
		}
		catch (Throwable th) {
			throw new Error(th);
        }
    }
    private static void Hw(String key, List<String> oldValue, Map<String, List<String>> map, Set<String> set, List<String> list) {
        try {
            if (set.contains(key)) {
                return;
            }
            set.add(key);
            if (!list.contains(key)) {
                list.add(key);
            }
            List<String> value = map.get(key);
			
			if( value == null ){
				Log.d("找到你了", String.format( "key -> %s, oldValue -> %s", key, oldValue));
			}
			
			for( String key2 : value){
				// 此时map中不包含key2
				// 因为map中的k2并不完整
				if( !map.containsKey(key2)){
					Log.d("没有找到", String.format( "map中不包含 -> %s，推测数据不完整", key2));
					//continue;
				}
				Hw(key2, map.get(key2), map, set, list);
			}
        }catch (Error th) {
            throw th;			
		}
		catch (Throwable th) {
			throw new Error(th);
        }
    }
}
