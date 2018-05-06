package com.netty.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jack on 2018/5/5.
 * 管理请求和响应的关系
 * 主要是通过唯一的请求标识id
 */
public class DefaultFuture {

    //请求id
    private long id;

    //请求id对应的响应结果
    private volatile Response response;

    //存储响应结果
    public final static Map<Long,DefaultFuture> FUTURES= new ConcurrentHashMap<Long,DefaultFuture>();

    //超时时间
    private long timeout;

    private final long start=System.currentTimeMillis();

    //获取锁
    private volatile Lock lock = new ReentrantLock();

    //线程通知条件
    private volatile Condition condition = lock.newCondition();


    public DefaultFuture(ClientRequest request){
        id=request.getId();
        FUTURES.put(id, this);
    }


    /**
     * 根据请求id获取响应结果
     * @param timeout
     * @return
     */
    public Response get(long timeout){
        long start = System.currentTimeMillis();
        lock.lock();
        while(!hasDone()){
            try {
                condition.await(timeout, TimeUnit.SECONDS);
                if(System.currentTimeMillis()-start>=timeout){
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
                lock.unlock();
            }
        }
        return response;
    }

    /**
     * 存储服务器端的响应
     * @param res
     */
    public static void recive(Response res){
        //找到res相对应的DefaultFuture
        DefaultFuture future = FUTURES.remove(res.getId());
        if(future==null){
            return ;
        }
        Lock lock= future.getLock();
        lock.lock();
        try{
            //设置响应
            future.setResponse(res);
            Condition condition =  future.getCondition();
            if(condition!=null){
                //通知
                condition.signal();
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }


    private boolean hasDone() {
        return response !=null? true:false;
    }

    public long getId() {
        return id;
    }




    public void setResponse(Response response) {
        this.response = response;
    }

    public Lock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }





    public long getTimeout() {
        return timeout;
    }



    public long getStart() {
        return start;
    }


    /**
     * 处理请求超时的线程
     */
    static class FutureTimeOutThread extends Thread{

        @Override
        public void run() {

            while(true){
                for(long futureId : FUTURES.keySet()){
                    DefaultFuture f = FUTURES.get(futureId);
                    if(f==null){
                        FUTURES.remove(futureId);
                        continue;
                    }
                    if(f.getTimeout()>0){
                        if((System.currentTimeMillis()-f.getStart())>f.getTimeout()){
                            Response res = new Response();
                            res.setContent(null);
                            res.setMsg("请求超时！");
                            res.setStatus(1);//响应异常处理
                            res.setId(f.getId());
                            DefaultFuture.recive(res);
                        }
                    }


                }


            }
        }

    }

    /**
     * 设置为后台线程
     */
    static{
        FutureTimeOutThread timeOutThread = new FutureTimeOutThread();
        timeOutThread.setDaemon(true);
        timeOutThread.start();

    }


}
