package com.healthy.rvigor.net.http;

public abstract class HttpPutRequestBase<T>  extends   HttpBodyRequestBase<T> {

    public HttpPutRequestBase(String url) {
        super(url);
    }
}
