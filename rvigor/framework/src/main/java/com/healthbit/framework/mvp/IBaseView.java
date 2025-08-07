package com.healthbit.framework.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.healthbit.framework.base.BaseDialogFragment;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
* @Description:
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public interface IBaseView {

    Observable<Boolean> requestPermission(String... permissions);

    Activity getActivity();

    void startToActivity(Intent intent);

    Context getContext();

    void showToast(int resID);

    void showToast(String msg);

    void showToastLong(String msg);

    void showToastLong(int resID);

    void showLoadingTextDialog(int textResID, long time);

    void showLoadingTextDialog(int textResID, long time, boolean isCancelable, BaseDialogFragment.BaseDialogCallback dialogCallback);

    void hideLoadingTextDialog();

    void showWarningDialog(int textResID);

    void addDisposable(Disposable disposable);

}
