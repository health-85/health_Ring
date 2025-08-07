package com.healthy.rvigor.net.http;

import android.text.TextUtils;

import com.healthy.rvigor.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * okhttp网络访问组件
 */
public class OKHttpComponent extends HttpComponent {

    private OkHttpClient okHttpClient = null;

    public OKHttpComponent(MyApplication con, String serviceName, int maxrequest) {
        super(con, serviceName, maxrequest);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
//        builder.hostnameVerifier(new AllowAllHostnameVerifier());
        okHttpClient = builder.build();
    }

    @Override
    public void cancel(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        ArrayList<HttpRequestBase> reqCanceled = new ArrayList<>();
        synchronized (objlock) {
            removeByTag(requestBases, tag, reqCanceled);
            cancelCallByTag(tag);
        }
        for (int i = 0; i < reqCanceled.size(); i++) {
            HttpRequestBase curr = reqCanceled.get(i);
            if (curr.getRequestCallBack() != null) {
                curr.getRequestCallBack().onError(getCommonApplication(), curr, new IOException("Canceled"));
                curr.getRequestCallBack().onAfter(getCommonApplication(), curr);
            }
        }
    }

    /**
     * @param tag
     */
    private void cancelCallByTag(String tag) {
        for (int i = 0; i < runningCalls.size(); i++) {
            Call call = runningCalls.get(i);
            HttpRequestBase requestBase = (HttpRequestBase) call.request().tag();
            if (tag.equals(requestBase.getTag())) {
                call.cancel();
            }
        }
    }

    /**
     * @param requestBases
     * @param tag
     * @return
     */
    private void removeByTag(List<HttpRequestBase> requestBases, String tag, List<HttpRequestBase> requestCanceled) {
        for (int i = 0; i < requestBases.size(); i++) {
            HttpRequestBase curr = requestBases.get(i);
            if (tag.equals(curr.getTag())) {
                requestBases.remove(curr);
                requestCanceled.add(curr);
                i--;
            }
        }
    }


    /**
     * 正在运行的请求体
     */
    private ArrayList<Call> runningCalls = new ArrayList<>();

    @Override
    protected void startRequest(HttpRequestBase req) {
        Call newcall = null;
        try {
            Request.Builder reqbuilder = new Request.Builder();
            Set<Map.Entry<String, String>> entrySet = req.getHeaders().entrySet();
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> curr = iterator.next();
                reqbuilder.addHeader(curr.getKey(), curr.getValue());
            }
            reqbuilder.url(req.getUrl());
            if (req instanceof HttpGetRequestBase) {
                reqbuilder.get();
            }
            if (req instanceof HttpDeleteRequestBase) {
                reqbuilder.delete();
            }
            if (req instanceof HttpPostRequestBase) {
                TempRequestBody body = new TempRequestBody((HttpPostRequestBase) req);
                reqbuilder.post(body);
            }
            if (req instanceof HttpPutRequestBase) {
                TempRequestBody body = new TempRequestBody((HttpPutRequestBase) req);
                reqbuilder.put(body);
            }
            reqbuilder.tag(req);
            Request okreq = reqbuilder.build();
            newcall = okHttpClient.newCall(okreq);
            runningCalls.add(newcall);
            newcall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    HttpRequestBase requestBase = RemoveCallInRunningAndFetchRequestBase(call);
                    if (requestBase.getRequestCallBack() != null) {
                        requestBase.getRequestCallBack().onError(getCommonApplication(), requestBase, e);
                        requestBase.getRequestCallBack().onAfter(getCommonApplication(), requestBase);
                    }
                    removeFinishReqAndRequestNext(requestBase);
                }


                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    HttpRequestBase requestBase = RemoveCallInRunningAndFetchRequestBase(call);
                    if (requestBase.getRequestCallBack() != null) {
                        if (response.isSuccessful()) {
                            try {
                                Object obj = requestBase.getRequestCallBack().convertSuccess(getCommonApplication(), requestBase, response.body().contentLength(), response.body().byteStream());
                                requestBase.getRequestCallBack().onSuccess(getCommonApplication(), requestBase, obj);
                            } catch (Exception e) {
                                e.printStackTrace();
                                requestBase.getRequestCallBack().onError(getCommonApplication(), requestBase, e);
                            }
                        } else {
                            String msg = response.message();
                            if (TextUtils.isEmpty(msg)) {
                                String s = response.body().string();
                                msg = TextUtils.isEmpty(s) ? "" : s;
                            }
                            requestBase.getRequestCallBack().onError(getCommonApplication(), requestBase, new Exception("code:" + response.code() + ",message:" + msg));
                        }
                        requestBase.getRequestCallBack().onAfter(getCommonApplication(), requestBase);
                    }
                    removeFinishReqAndRequestNext(requestBase);
                }
            });
        } catch (Exception ex) {
            if (newcall != null) {
                runningCalls.remove(newcall);
            }
            HttpRequestBase requestBase = req;
            if (requestBase.getRequestCallBack() != null) {
                requestBase.getRequestCallBack().onError(getCommonApplication(), requestBase, ex);
                requestBase.getRequestCallBack().onAfter(getCommonApplication(), requestBase);
            }
            removeFinishReqAndRequestNext(requestBase);
        }
    }

    /**
     * 从正在处理的集合中移除当前请求
     *
     * @param call
     * @return
     */
    private HttpRequestBase RemoveCallInRunningAndFetchRequestBase(Call call) {
        synchronized (objlock) {
            runningCalls.remove(call);
        }
        return (HttpRequestBase) call.request().tag();
    }

    private static class TempRequestBody extends RequestBody {

        private HttpBodyRequestBase mreq = null;

        public TempRequestBody(HttpBodyRequestBase mreq) {
            this.mreq = mreq;
        }

        @Override
        public long contentLength() throws IOException {
            return super.contentLength();
        }

        //@javax.annotation.Nullable
        @Override
        public MediaType contentType() {
            return MediaType.parse(mreq.getRequestBodyContentType());
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            mreq.writeBody(sink.outputStream());
        }
    }


}
