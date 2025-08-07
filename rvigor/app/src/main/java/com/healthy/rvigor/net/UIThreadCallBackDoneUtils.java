package com.healthy.rvigor.net;


import android.os.Handler;
import android.os.Looper;

/**
 * 将Runnable接口放在UI消息队列中由系统进行处理
 *
 * @author tcb
 */
public class UIThreadCallBackDoneUtils {

    private Handler hd = null;
    private Thread uitd = null;

    public UIThreadCallBackDoneUtils() {
        super();
        hd = new Handler(Looper.getMainLooper());
        uitd = Looper.getMainLooper().getThread();
    }

    /**
     * 将接口发送到UI线程处理并立即返回 （非等待处理）
     *
     * @param r
     * @return
     */
    public boolean post1(Runnable r) {
        if (r == null) {
            return false;
        }
        return hd.post(r);
    }

    /**
     * 将接口发送到UI线程处理并立即返回 （非等待处理）
     *
     * @param r
     * @param delaymillis
     * @return
     */
    public boolean postDelayed(Runnable r, long delaymillis) {
        if (r == null) {
            return false;
        }
        return hd.postDelayed(r, delaymillis);
    }


    /**
     * 将接口发送到UI线程并等待处理
     *
     * @param r
     */
    public boolean PostAndWait(Runnable r) {
        if (r == null) {
            return false;
        }
        if (uitd == Thread.currentThread()) {
            r.run();
            return true;
        }

        boolean R = false;
        UIRunnable UIRun = new UIRunnable(r);
        synchronized (UIRun) {
            if (hd.post(UIRun)) {
                R = true;
                try {
                    UIRun.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return R;
    }

    /**
     * 释放处理
     */
    public void Release() {

    }

}

/**
 * 实现Runnable接口
 *
 * @author tcb
 */
class UIRunnable implements Runnable {

    private Runnable r = null;

    public UIRunnable(Runnable r) {
        super();
        this.r = r;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            r.run();
        } finally {
            synchronized (this) {
                this.notifyAll();
            }
            r = null;
        }
    }

}
