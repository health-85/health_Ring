package com.healthy.rvigor.net.http;

import android.text.TextUtils;

import java.util.HashMap;


/**
 * http请求信息
 */
public abstract class HttpRequestBase<T> {

    public HttpRequestBase(String url) {
        this.url = TextUtils.isEmpty(url) ? "" : url;
    }

    /**
     * 请求url
     */
    private String url = "";

    /**
     * 获取  请求url
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置  请求url
     *
     * @param value
     */
    public HttpRequestBase setUrl(String value) {
        this.url = value;
        return this;
    }

    /**
     * 请求标识
     */
    private String tag = "";

    /**
     * 获取  请求标识
     *
     * @return
     */
    public String getTag() {
        return tag;
    }

    /**
     * 设置  请求标识
     *
     * @param value
     */
    public HttpRequestBase setTag(String value) {
        if (!TextUtils.isEmpty(value)) {
            this.tag = value;
        }
        return this;
    }


    /**
     * 请求回调接口
     */
    private HttpRequestCallBackBase<T> requestCallBack = null;

    /**
     * 获取  请求回调接口
     *
     * @return
     */
    public HttpRequestCallBackBase<T> getRequestCallBack() {
        return requestCallBack;
    }

    /**
     * 设置  请求回调接口
     *
     * @param value
     */
    public HttpRequestBase requestCallBack(HttpRequestCallBackBase<T> value) {
        this.requestCallBack = value;
        return this;
    }

    /**
     * ui界面唯一id
     */
    private String UUID = "";

    /**
     * 获取  ui界面唯一id
     *
     * @return
     */
    public String getUUID() {
        return UUID;
    }

    /**
     * 设置  ui界面唯一id
     *
     * @param value
     */
    public HttpRequestBase setUUID(String value) {
        this.UUID = value;
        return this;
    }

    private Object attuchmentObject = null;

    /**
     * 获取附加对象
     *
     * @return
     */
    public Object getAttuchmentObject() {
        return attuchmentObject;
    }

    /**
     * 设置附加对象
     *
     * @param attuchmentObject
     */
    public HttpRequestBase setAttuchmentObject(Object attuchmentObject) {
        this.attuchmentObject = attuchmentObject;
        return this;
    }

    /**
     * http头部
     */
    private HashMap<String, String> headers = new HashMap<>();

    /**
     * http头部
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * http传递的参数
     */
    private HashMap<String, String> params = new HashMap<String, String>();

    /**
     * http传递的参数
     *
     * @return
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * 获取指定的header
     *
     * @param key
     * @return
     */
    public String getParam(String key) {
        return TextUtils.isEmpty(params.get(key)) ? "" : params.get(key);
    }


    private String progressUUID = "";

    /**
     * 进度对话框唯一id
     *
     * @return
     */
    public String getProgressUUID() {
        return progressUUID;
    }

    /**
     * 进度对话框唯一id
     *
     * @param progressUUID
     */
    public HttpRequestBase setProgressUUID(String progressUUID) {
        this.progressUUID = progressUUID;
        return this;
    }


    /**
     * 添加头部
     *
     * @param key
     * @param value
     * @return
     */
    public HttpRequestBase putHeader(String key, String value) {
        getHeaders().put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public HttpRequestBase putParam(String key, String value) {
        getParams().put(key, value);
        return this;
    }

    /**
     * 获取指定的head
     *
     * @param key
     * @return
     */
    public String getHeader(String key) {
        return TextUtils.isEmpty(getHeaders().get(key)) ? "" : getHeaders().get(key);
    }

    /**
     * 执行请求
     *
     * @param httpComponent
     */
    public void execute(HttpComponent httpComponent) {
        httpComponent.AddRequest(this);
    }

    /**
     * 优先执行请求
     *
     * @param httpComponent
     */
    public void executeAtFront(HttpComponent httpComponent) {
        httpComponent.AddRequestAtFront(this);
    }

    /**
     * 释放资源
     */
    public void Release() {

    }
}
