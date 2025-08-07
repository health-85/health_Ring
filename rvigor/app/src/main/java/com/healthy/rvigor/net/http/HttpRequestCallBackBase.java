package com.healthy.rvigor.net.http;

import android.text.TextUtils;
import android.widget.Toast;

import com.healthy.rvigor.MyApplication;

import java.io.InputStream;
import java.lang.reflect.Method;


/**
 * http请求回调
 */
public abstract class HttpRequestCallBackBase<T> {


    /**
     * 执行成功要调用的方法
     */
    protected String onSuccessInUIInvokeMethodName = "";

    /**
     * 执行失败要调用的方法
     */
    protected String onErrorInUIInvokeMethodName = "";

    /**
     * 执行完成之后要调用的方法
     */
    protected String onAfterInUIInvokeMethodName = "";


    public HttpRequestCallBackBase(String onSuccessInUIInvokeMethodName, String onErrorInUIInvokeMethodName, String onAfterInUIInvokeMethodName) {
        this.onSuccessInUIInvokeMethodName = onSuccessInUIInvokeMethodName;
        this.onErrorInUIInvokeMethodName = onErrorInUIInvokeMethodName;
        this.onAfterInUIInvokeMethodName = onAfterInUIInvokeMethodName;
    }

    /**
     * 获取UI里面指定的方法
     *
     * @param con
     * @param ui
     * @param method
     * @return
     */
    protected Method getUIMethod(MyApplication con, IActivityManagerCallBase ui, String method) {
        if (!TextUtils.isEmpty(method)) {
            if (ui != null) {
                try {
                    return getClassMethod(ui.getClass(), method);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }


    private Method getClassMethod(Class cls, String method) {
        if (cls != null) {
            Method[] mds = cls.getDeclaredMethods();
            if (mds != null) {
                for (int i = 0; i < mds.length; i++) {
                    Method md = mds[i];
                    if (method.equals(md.getName())) {
                        md.setAccessible(true);
                        return md;
                    }
                }
            }
            if (cls.getSuperclass() != null) {
                Method supermd = getClassMethod(cls.getSuperclass(), method);
                if (supermd != null) {
                    return supermd;
                }
            }
        }
        return null;
    }

    /**
     * 显示错误
     *
     * @param con
     * @param text
     */
    protected void showToast(MyApplication con, String text) {
        Toast.makeText(con, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 访问成功时候回调  非UI线程
     *
     * @param req    请求信息
     * @param object 返回的结果对象
     */
    public void onSuccess(MyApplication con, HttpRequestBase req, T object) {
        TempRunInUIRunnableArgs args = new TempRunInUIRunnableArgs(con, req, object);
        con.getUiHandler().PostAndWait(new TempRunInUIRunnable(this, "onSuccessInUI", args));
    }

    /**
     *
     */
    private static class TempRunInUIRunnableArgs {
        public MyApplication application = null;
        public HttpRequestBase requestBase = null;
        public Object object = null;

        public TempRunInUIRunnableArgs(MyApplication application, HttpRequestBase requestBase, Object object) {
            this.application = application;
            this.requestBase = requestBase;
            this.object = object;
        }
    }

    /**
     * 临时参数
     */
    private static class TempRunInUIRunnable implements Runnable {

        private HttpRequestCallBackBase requestCallBackBase = null;

        private String methodName = "";

        private TempRunInUIRunnableArgs args = null;

        public TempRunInUIRunnable(HttpRequestCallBackBase requestCallBackBase, String methodName, TempRunInUIRunnableArgs args) {
            this.requestCallBackBase = requestCallBackBase;
            this.methodName = methodName;
            this.args = args;
        }


        @Override
        public void run() {
            if (methodName.equals("onSuccessInUI")) {
                requestCallBackBase.onSuccessInUI(args.application, args.requestBase, args.object);
            }

            if (methodName.equals("onErrorInUI")) {
                requestCallBackBase.onErrorInUI(args.application, args.requestBase, (Exception) args.object);
            }

            if (methodName.equals("onAfterInUI")) {
                requestCallBackBase.onAfterInUI(args.application, args.requestBase);
            }
            args = null;
        }
    }


    /**
     * 在UI线程触发回调
     *
     * @param con
     * @param req
     * @param object
     */
    public void onSuccessInUI(MyApplication con, HttpRequestBase req, T object) {
        if (!TextUtils.isEmpty(req.getUUID())) {
//            IActivityManagerCallBase curr = con.getActivityManagerCallBaseByUUID(req.getUUID());
//            if (curr != null) {
                onSuccessInIActivityManagerCallBase(con, req, object, null);
//            }
        }
    }


    /**
     * 在IActivityManagerCallBase管理的界面当中执行
     *
     * @param con
     * @param req
     * @param object
     * @param ui
     */
    protected void onSuccessInIActivityManagerCallBase(MyApplication con, HttpRequestBase req, T object, IActivityManagerCallBase ui) {
        Method uimethod = getUIMethod(con, ui, onSuccessInUIInvokeMethodName);
        if (uimethod != null) {
            Class<?>[] paramTypes = uimethod.getParameterTypes();
            if ((paramTypes != null) && (paramTypes.length == 2)) {
                if ((paramTypes[0].isInstance(req)) || (paramTypes[1].isInstance(object))) {
                    try {
                        uimethod.invoke(ui, req, object);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 产生错误的时候回调  非UI线程
     *
     * @param req 请求信息
     * @param ex  错误
     */
    public void onError(MyApplication con, HttpRequestBase req, Exception ex) {
        TempRunInUIRunnableArgs args = new TempRunInUIRunnableArgs(con, req, ex);
        con.getUiHandler().PostAndWait(new TempRunInUIRunnable(this, "onErrorInUI", args));
    }


    /**
     * 在UI界面触发回调
     *
     * @param con
     * @param req
     * @param ex
     */
    public void onErrorInUI(MyApplication con, HttpRequestBase req, Exception ex) {
//        if (!TextUtils.isEmpty(req.getUUID())) {
//            IActivityManagerCallBase curr = con.getActivityManagerCallBaseByUUID(req.getUUID());
//            if (curr != null) {
                onErrorInIActivityManagerCallBase(con, req, ex, null);
//            }
//        }
    }

    /**
     * 在IActivityManagerCallBase中触发
     *
     * @param con
     * @param req
     * @param ex
     * @param curr
     */
    protected void onErrorInIActivityManagerCallBase(MyApplication con, HttpRequestBase req, Exception ex, IActivityManagerCallBase curr) {
        Method uimethod = getUIMethod(con, curr, onErrorInUIInvokeMethodName);
        if (uimethod != null) {
            Class<?>[] paramTypes = uimethod.getParameterTypes();
            if ((paramTypes != null) && (paramTypes.length == 2)) {
                if ((paramTypes[0].isInstance(req)) || (paramTypes[1].isInstance(ex))) {
                    try {
                        uimethod.invoke(curr, req, ex);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * 将流中的流数据转换成对应的对象   非UI线程
     *
     * @param req
     * @param contentlength
     * @param input
     * @return
     */
    public abstract T convertSuccess(MyApplication con, HttpRequestBase req, long contentlength, InputStream input) throws Exception;


    /**
     * 请求之后进行调用  非UI线程
     *
     * @param req
     */
    public void onAfter(MyApplication con, HttpRequestBase req) {
        TempRunInUIRunnableArgs args = new TempRunInUIRunnableArgs(con, req, null);
        con.getUiHandler().PostAndWait(new TempRunInUIRunnable(this, "onAfterInUI", args));
    }

    /**
     * 请求之后进行调用   UI线程
     *
     * @param con
     * @param req
     */
    public void onAfterInUI(MyApplication con, HttpRequestBase req) {
//        if (!TextUtils.isEmpty(req.getUUID())) {
//            IActivityManagerCallBase curr = con.getActivityManagerCallBaseByUUID(req.getUUID());
//            if (curr != null) {
                onAfterInIActivityManagerCallBase(con, req, null);
//                if (!TextUtils.isEmpty(req.getProgressUUID())) {
//                    curr.CloseProgressByUUID(req.getProgressUUID());
//                }
//            }
//        }
    }

    /**
     * 请求之后进行调用   IActivityManagerCallBase中调用
     *
     * @param con
     * @param req
     * @param curr
     */
    protected void onAfterInIActivityManagerCallBase(MyApplication con, HttpRequestBase req, IActivityManagerCallBase curr) {
        curr.CloseProgressByUUID(req.getProgressUUID());
        Method uimethod = getUIMethod(con, curr, onAfterInUIInvokeMethodName);
        if (uimethod != null) {
            Class<?>[] paramTypes = uimethod.getParameterTypes();
            if ((paramTypes != null) && (paramTypes.length == 1)) {
                try {
                    if (paramTypes[0].isInstance(req)) {
                        uimethod.invoke(curr, req);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

}
