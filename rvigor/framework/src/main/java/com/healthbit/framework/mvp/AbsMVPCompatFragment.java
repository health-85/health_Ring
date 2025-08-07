package com.healthbit.framework.mvp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthbit.framework.base.BaseCompatFragment;
import com.healthbit.framework.util.ToastUtil;
//import com.tbruyelle.rxpermissions3.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.rxjava3.core.Observable;


/**
 * @Description: MVP View基类，提供基础的MVP框架
 * @Author: zxy(1051244836 @ qq.com)
 * @CreateDate: 2019/4/23
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public abstract class AbsMVPCompatFragment<P extends IBasePresenter> extends BaseCompatFragment implements IBaseView {

    protected P mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(getLayoutResID(), container, false);
        View view = getLayoutView();
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        mPresenter = createPresenter();
        mPresenter.onViewAttached(this);
        initView();
        initData(getArguments());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (mPresenter != null) {
            mPresenter.onViewDetached();
        }
    }

    public Observable<Boolean> requestPermission(String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        return rxPermissions.request(permissions);
    }


    /**
     * 子类必须实现该方法获取Presenter
     *
     * @return 返回该子类所需的Presenter
     */
    protected abstract P createPresenter();

    /**
     * @return 返回ContentView
     */
    protected abstract View getLayoutView();

    protected boolean useEventBus() {
        return false;
    }

    /**
     * 子类初始化页面方法
     */
    protected void initView() {

    }

    /**
     * 子类初始化数据方法
     *
     * @param bundle 传入的数据，可能为空
     */
    protected void initData(Bundle bundle) {
        mPresenter.loadData(bundle);
    }

    @Override
    public void startToActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showToast(int resID) {
        ToastUtil.showToast(getActivity().getApplicationContext(), resID);
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(getActivity().getApplicationContext(), msg);
    }

    @Override
    public void showToastLong(String msg) {
        ToastUtil.showToastLong(getActivity().getApplicationContext(), msg);
    }

    @Override
    public void showToastLong(int resID) {
        ToastUtil.showToastLong(getActivity().getApplicationContext(), resID);
    }
}
