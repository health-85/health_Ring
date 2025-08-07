package com.healthy.rvigor.net;

import android.content.Context;


import com.healthbit.framework.mvp.IBaseView;
import com.healthy.rvigor.base.BaseMVPActivity;
import com.healthy.rvigor.bean.TokenInvalidEventBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.adapter.rxjava.HttpException;


public abstract class BeanObserver<T> implements Observer<BaseResponse<T>> {

    private static final String TAG = "BeanObserver";

    private Context mContext;

    private IBaseView mView;

    private BaseMVPActivity mActivity;

    public BeanObserver() {

    }

    public BeanObserver(Context context) {
        mContext = context;
    }

    public BeanObserver(BaseMVPActivity activity) {
        mActivity = activity;
    }

    public BeanObserver(IBaseView view) {
        mView = view;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (mActivity != null) {
            mActivity.addDisposable(d);
        }else if (mView != null){
            mView.addDisposable(d);
        }
    }

    @Override
    public void onNext(BaseResponse<T> o) {
        if (mActivity != null) {
            mActivity.hideLoadingTextDialog();
        }else if (mView != null){
            mView.hideLoadingTextDialog();
        }
        onSuccess(o);
    }

    @Override
    public void onError(Throwable e) {
        if (mActivity != null) {
            mActivity.hideLoadingTextDialog();
        }else if (mView != null){
            mView.hideLoadingTextDialog();
        }
        if (e instanceof retrofit2.adapter.rxjava.HttpException) {
            retrofit2.adapter.rxjava.HttpException httpException = (HttpException) e;
            try {
                if (httpException.code() == -1) {
                    TokenInvalidEventBean bean = new TokenInvalidEventBean(true);
                    EventBus.getDefault().post(bean);
                    onFailure(e, "登录已过期,请重新登录");
                } else {
                    onFailure(e, httpException.response().errorBody().string());
                }
                return;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (e instanceof IOException) {
            onFailure(e, "网络错误");
        } else {
            onFailure(e, "数据错误");
        }
        e.printStackTrace();
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(BaseResponse<T> bean);

    protected abstract void onFailure(Throwable e, String rawResponse);

}
