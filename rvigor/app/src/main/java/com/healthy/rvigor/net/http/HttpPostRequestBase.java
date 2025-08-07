package com.healthy.rvigor.net.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * post输出请求
 * @param <T>
 */
public abstract class HttpPostRequestBase<T> extends  HttpBodyRequestBase<T> {


    public HttpPostRequestBase(String url) {
        super(url);
    }


    @Override
    public final void writeBody(OutputStream outputStream) throws IOException {
          writePostBody(outputStream);
    }

    /**
     * 写post输出内容
     * @param outputStream
     */
      public    abstract void    writePostBody(OutputStream  outputStream) throws IOException;
}
