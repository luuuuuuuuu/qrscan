package com.gentlelu.controller;

import com.alibaba.fastjson.JSONObject;
import com.gentlelu.common.utils.qrcode.PoolCache;
import com.gentlelu.common.utils.qrcode.ScanPool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by lu on 2018/7/5.
 */
@Controller
public class QRScanController {

    /**
     * 加载二维码页面
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model){
        String uuid = UUID.randomUUID().toString();
        PoolCache.cacheMap.put(uuid, new ScanPool());
        model.addAttribute("uuid",uuid);
        return "index";
    }
    /**
     * 加载二维码页面
     * @return
     */
    @RequestMapping("/")
    public String index2(Model model){
        String uuid = UUID.randomUUID().toString();
        PoolCache.cacheMap.put(uuid, new ScanPool());
        model.addAttribute("uuid",uuid);
        return "index";
    }

    @RequestMapping("/scanLogin")
    @ResponseBody
    public JSONObject scanLogin(String uuid){
        JSONObject obj = new JSONObject();
        ScanPool pool = null;
        if( !(PoolCache.cacheMap == null || PoolCache.cacheMap.isEmpty())){
            pool = PoolCache.cacheMap.get(uuid);
        }
        if (pool == null) {
            obj.put("successFlag","0");
            obj.put("msg","该二维码已经失效,请重新获取");
        } else {
            pool.setSession("123123");
            pool.scanSuccess();
            obj.put("msg","扫码成功!");
            obj.put("successFlag","1");

        }


        return obj;
    }

    @RequestMapping("/success")
    @ResponseBody
    public String success(){
        return "扫码成功!!";
    }

    /**
     * 查询扫码状态
     * @return
     */
    @RequestMapping("/pool")
    @ResponseBody
    public JSONObject pool(String uuid){
        System.out.println("检测[   " + uuid + "   ]是否登录");
        JSONObject obj = new JSONObject();
        ScanPool pool = null;
        if( !(PoolCache.cacheMap == null || PoolCache.cacheMap.isEmpty()) ) {
            pool = PoolCache.cacheMap.get(uuid);
        }

        try {
            if (pool == null) {
                // 扫码超时,进线程休眠
                Thread.sleep(10 * 1000L);
                obj.put("successFlag","0");
                obj.put("msg","该二维码已经失效,请重新获取");
            } else {
                // 使用计时器，固定时间后不再等待扫描结果--防止页面访问超时
                new Thread(new ScanCounter(uuid, pool)).start();

                //这里得到的ScanPool(时间靠前)和用户使用手机扫码后得到的不是一个,用户扫码后又重新更新了ScanPool对象,并重新放入了redis中,,所以这里要等待上面的计时器走完,才能获得最新的ScanPool
                boolean scanFlag = pool.getScanStatus();
                if (scanFlag) {
                    // 根据uuid从redis中获取pool对象,得到对应的sessionId,返给页面,通过js存cookie中
                    obj.put("successFlag","1");
                    obj.put("cname", "SESSIONKEY");
                    obj.put("cvalue", pool.getSession());
                } else {
                    obj.put("successFlag","2");
                    obj.put("msg","等待扫描");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return obj;
    }



    @RequestMapping("/scan")
    @ResponseBody
    public String scan(){
        return "haha";
    }



}

class ScanCounter implements Runnable {

    public Long timeout = 30 * 1000L;

    // 传入的对象
    private String uuid;
    private ScanPool scanPool;

    public ScanCounter(String p, ScanPool scanPool) {
        uuid = p;
        this.scanPool = scanPool;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        notifyPool(uuid, scanPool);
    }

    public synchronized void notifyPool(String uuid, ScanPool scanPool) {
        if (scanPool != null) scanPool.notifyPool();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ScanPool getScanPool() {
        return scanPool;
    }

    public void setScanPool(ScanPool scanPool) {
        this.scanPool = scanPool;
    }
}