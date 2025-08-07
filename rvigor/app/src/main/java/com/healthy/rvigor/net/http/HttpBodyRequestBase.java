package com.healthy.rvigor.net.http;

import java.io.IOException;
import java.io.OutputStream;

public abstract class HttpBodyRequestBase<T>  extends  HttpRequestBase<T>   {

    public HttpBodyRequestBase(String url) {
        super(url);
    }
    /**
     * 是否为块状发送  意思就是一小块一小块的发送
     */
    private boolean  isblocked=true;

    /**
     * 是否为块状发送  意思就是一小块一小块的发送
     */
    public boolean isIsblocked() {
        return isblocked;
    }

    /**
     * 设置是否以块状发送
     * @param isblocked
     */
    public void setIsblocked(boolean isblocked) {
        this.isblocked = isblocked;
    }

    /**
     * 获取内容大小
     * @return
     */
    public   abstract int  getRequestBodyContentLength();

    /**
     * 获取请求发送的内容类型  如application/x-www-form-urlencoded
     * @return
     */
    public   abstract  String  getRequestBodyContentType();
    /**
     * 写post输出内容
     * @param outputStream
     */
    public    abstract void    writeBody(OutputStream outputStream) throws IOException;
}
