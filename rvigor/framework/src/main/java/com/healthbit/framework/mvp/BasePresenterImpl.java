package com.healthbit.framework.mvp;

import android.content.Intent;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
* @Description:    MVP Presenter基类，提供基础的MVP框架
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public abstract class BasePresenterImpl<V extends IBaseView/*, M extends IBaseModel*/> implements IBasePresenter {

//    protected M mModel;

    private V viewProxy;

    /**
     * 使用弱引用管理与View的绑定关系
     */
    protected WeakReference<V> mViewRef;

    /**
     * 创建子类Presenter需要的M层
     *
     * @return 相应M层
     */
//    protected abstract M createModel();

    /**
     * 获取view的方法
     *
     * @return 当前关联的view
     */
    @Override
    public V getView() {
        return viewProxy;
    }

    @Override
    public void onViewAttached(IBaseView view) {
        mViewRef = new WeakReference<V>((V) view);
        Class<?>[] interfaces = view.getClass().getInterfaces();
        boolean found = false;
        for (Class<?> anInterface : interfaces) {
            if (anInterface == IBaseView.class) {
                found = true;
            }
        }
        if (!found) {
            Class<?>[] infs = Arrays.copyOf(interfaces, interfaces.length + 1);
            infs[interfaces.length] = IBaseView.class;
            interfaces = infs;
        }
        Object proxyInstance = Proxy.newProxyInstance(view.getClass().getClassLoader(), interfaces, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (mViewRef == null || mViewRef.get() == null) {
                    return null;
                } else {
                    return method.invoke(mViewRef.get(), args);
                }
            }
        });
        viewProxy = (V) proxyInstance;


//        mModel = createModel();
    }

    @Override
    public void onViewDetached() {
        if (isAttach()) {
            mViewRef.clear();
            mViewRef = null;
        }
//        if (mModel != null) {
//            mModel = null;
//        }
        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
            mSubscriptions.clear();
        }
        System.gc();
    }

    protected boolean isAttach() {
        return mViewRef != null &&
                mViewRef.get() != null;
    }

    private CompositeSubscription mSubscriptions;

    protected void addSubscription(Subscription subscribe) {
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeSubscription();
        }
        mSubscriptions.add(subscribe);
    }

    protected void removeSubscription(Subscription subscribe) {
        if (mSubscriptions != null) {
            mSubscriptions.remove(subscribe);
        }
    }

    @Override
    public void loadData(Bundle bundle) {

    }

    @Override
    public void loadData(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
