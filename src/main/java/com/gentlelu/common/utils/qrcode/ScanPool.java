package com.gentlelu.common.utils.qrcode;

import java.io.Serializable;


public class ScanPool implements Serializable{
    
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
     */
    private static final long serialVersionUID = -9117921544228636689L;
    
    
    private Object session ;
    //创建时间  
    private Long createTime = System.currentTimeMillis();  
      
    //登录状态  
    private boolean scanFlag = false;  
    
    public boolean isScan(){  
        return scanFlag;  
    }  
      
    public void setScan(boolean scanFlag){  
        this.scanFlag = scanFlag; 
    } 

    /** 
     * 获取扫描状态，如果还没有扫描，则等待固定秒数 
     * @param wiatSecond 需要等待的秒数 
     * @return 
     */  
    public synchronized boolean getScanStatus(){  
        try  
        {  
            if(!isScan()){ //如果还未扫描，则等待
                this.wait();  
            }  
            if (isScan())  
            {   System.err.println("手机扫描完成设置getScanStatus..true...........");
                return true;  
            }  
        } catch (InterruptedException e)  
        {  
            e.printStackTrace();  
        }  
        return false;  
    }  
      
    /** 
     * 扫码之后设置扫码状态 
     * @param token 
     * @param id 
     */  
    public synchronized void scanSuccess(){  
        try  
        {  System.err.println("手机扫描完成setScan(true)....同时释放notifyAll(手机扫码时,根据uuid获得的scanpool对象)");
            setScan(true); 
            this.notifyAll();  
        } catch (Exception e)  
        {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
      
    public synchronized void notifyPool(){  
        try  
        {  
            this.notifyAll();  
        } catch (Exception e)  
        {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }  
  
    /***********************************************/
    public Long getCreateTime()  
    {  
        return createTime;  
    }  
  
    public void setCreateTime(Long createTime)  
    {  
        this.createTime = createTime;  
    }

    public Object getSession() {
        return session;
    }

    public void setSession(Object session) {
        this.session = session;
    }

    

}
