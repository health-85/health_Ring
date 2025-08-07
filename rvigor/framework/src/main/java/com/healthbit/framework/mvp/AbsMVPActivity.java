package com.healthbit.framework.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.healthbit.framework.base.BaseActivity;
import com.healthbit.framework.util.ToastUtil;
//import com.tbruyelle.rxpermissions3.RxPermissions;

import io.reactivex.rxjava3.core.Observable;


/**
 * @Description: MVP View基类，提供基础的MVP框架
 * @Author: zxy(1051244836 @ qq.com)
 * @CreateDate: 2019/4/23
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public abstract class AbsMVPActivity<P extends IBasePresenter> extends BaseActivity implements IBaseView {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.onViewAttached(this);
        }
        onSaveFragmentInstanceState(savedInstanceState);
        initView();
        initData(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onViewDetached();
        }
    }


    /**
     * 子类必须实现该方法获取Presenter
     *
     * @return 返回该子类所需的Presenter
     */
    protected abstract P createPresenter();

    /**
     * 子类初始化页面方法
     */
    protected void initView() {

    }

    /**
     * 子类初始化数据方法
     *
     * @param intent 传入的数据，可能为空
     */
    protected void initData(Intent intent) {
        if (mPresenter != null) {
            mPresenter.loadData(intent);
        }
    }

    protected void onSaveFragmentInstanceState(Bundle savedInstanceState) {

    }

    public Observable<Boolean> requestPermission(String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(this);
        return rxPermissions.request(permissions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPresenter != null) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void startToActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showToast(int resID) {
        ToastUtil.showToast(this, resID);
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(this, msg);
    }

    @Override
    public void showToastLong(String msg) {
        ToastUtil.showToastLong(this, msg);
    }

    @Override
    public void showToastLong(int resID) {
        ToastUtil.showToastLong(this, resID);
    }
}
