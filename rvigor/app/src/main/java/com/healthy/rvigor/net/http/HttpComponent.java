package com.healthy.rvigor.net.http;

import android.text.TextUtils;

import com.healthy.rvigor.MyApplication;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * http网络请求
 */
public abstract class HttpComponent extends AppComponentBase {

    protected LinkedList<HttpRequestBase> requestBases = new LinkedList<>();
    //请求处理中的
    protected List<HttpRequestBase> reqRunning = new ArrayList<>();

    //锁定请求
    protected Object objlock = new Object();

    private int maxrequest = 1;

    public HttpComponent(MyApplication con, String serviceName, int maxrequest) {
        super(con, serviceName);
        if (maxrequest > 1) {
            this.maxrequest = maxrequest;
        }
    }

    /**
     * 取消指定的任务
     *
     * @param tag
     */
    public abstract void cancel(String tag);

    /**
     * 是否包含
     *
     * @param tag
     * @return
     */
    public boolean containsRequestByTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            synchronized (objlock) {
                for (int i = 0; i < requestBases.size(); i++) {
                    HttpRequestBase curr = requestBases.get(i);
                    if (curr.getTag().equals(tag)) {
                        return true;
                    }
                }
                for (int i = 0; i < reqRunning.size(); i++) {
                    HttpRequestBase curr = reqRunning.get(i);
                    if (curr.getTag().equals(tag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 添加请求
     *
     * @param req
     * @return
     */
    public boolean AddRequest(HttpRequestBase req) {
        synchronized (objlock) {
            if ((!requestBases.contains(req)) && (!reqRunning.contains(req))) {
                requestBases.addLast(req);
                if (reqRunning.size() < this.maxrequest) {
                    HttpRequestBase curr = requestBases.removeFirst();
                    reqRunning.add(curr);
                    startRequest(curr);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加请求在队列前面
     *
     * @param req
     * @return
     */
    public boolean AddRequestAtFront(HttpRequestBase req) {
        synchronized (objlock) {
            if ((!requestBases.contains(req)) && (!reqRunning.contains(req))) {
                requestBases.addFirst(req);
                if (reqRunning.size() < this.maxrequest) {
                    HttpRequestBase curr = requestBases.removeFirst();
                    reqRunning.add(curr);
                    startRequest(curr);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 移除已经处理完毕的请求，并请求下一个任务
     */
    protected void removeFinishReqAndRequestNext(HttpRequestBase finishreq) {
        synchronized (objlock) {
            reqRunning.remove(finishreq);
            if ((reqRunning.size() <= maxrequest)) {
                if (requestBases.size() > 0) {
                    HttpRequestBase curr = requestBases.removeFirst();
                    reqRunning.add(curr);
                    startRequest(curr);
                }
            }
        }
    }

    /**
     * 开始请求
     *
     * @param req
     */
    protected abstract void startRequest(HttpRequestBase req);

    @Override
    protected void onComponentAdded() {

    }

    @Override
    protected void onComponentRemoved() {

    }


}
