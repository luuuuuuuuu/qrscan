package com.gentlelu.common.utils.qrcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 可使用redis代替
 * @ClassName: PoolCache 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author lu
 * @date 2017年10月30日 上午10:16:21
 */
public class PoolCache {
    private static final Logger logger = LoggerFactory.getLogger(PoolCache.class);

    // 缓存超时时间 10分钟
    private static Long timeOutSecond = 10 * 60 * 1000L;

    // 每半小时清理一次缓存
    private static Long cleanIntervalSecond = 30 * 60 * 1000L;

    //此map在多线程中会出现 ConcurrentModificationException
    //public static Map<String, ScanPool> cacheMap = new HashMap<String, ScanPool>();
    
    //List
    //public static CopyOnWriteArrayList<Map<String, ScanPool>> copyOnWriteArrayList = new CopyOnWriteArrayList<Map<String,ScanPool>>();
    
    //专用于高并发的map类-----Map的并发处理（ConcurrentHashMap）
    public static ConcurrentHashMap<String, ScanPool> cacheMap = new ConcurrentHashMap<String, ScanPool>();
    

    static {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(cleanIntervalSecond);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clean();
                }
            }

            public void clean() {
                    
                    try {
                        
                        /*if (copyOnWriteArrayList.size() > 0) {
                            Iterator<Map<String, ScanPool>> iterator = copyOnWriteArrayList.iterator();
                            while (iterator.hasNext()) {
                                Map<String, ScanPool> map = iterator.next();
                                Iterator<String> it2 = map.keySet().iterator();
                                while (it2.hasNext()){
                                    String uuid = it2.next();
                                    ScanPool pool = map.get(uuid);
                                    if (System.currentTimeMillis() - pool.getCreateTime() > timeOutSecond ) {
                                        copyOnWriteArrayList.remove(map);
                                        System.err.println("失效了:   ..  "+ uuid);
                                        System.err.println("失效了:   ..  "+ map);
                                        break;
                                    }
                                }
                            }
                        }*/
                        
                        if (cacheMap.keySet().size() > 0) {
                            Iterator<String> iterator = cacheMap.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                ScanPool pool = cacheMap.get(key);
                                if (System.currentTimeMillis() - pool.getCreateTime() > timeOutSecond ) {
                                    cacheMap.remove(key);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("定时清理uuid异常", e);
                    }
            }
        }).start();
    }

}
