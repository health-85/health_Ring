package com.sdk.satwatch.util;

import android.os.Looper;

import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

    public static <T> ObservableTransformer<T, T> IoToMainObserve() {
        return upstream -> {
            if (Looper.myLooper() != null || Looper.myLooper() == Looper.getMainLooper()) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            } else {
                return upstream.observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> SingleTransformer<T, T> IoToMainSingle() {
        return upstream -> {
            if (Looper.myLooper() != null || Looper.myLooper() == Looper.getMainLooper()) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            } else {
                return upstream.observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static CompletableTransformer IoToMainComplete() {
        return upstream -> {
            if (Looper.myLooper() != null || Looper.myLooper() == Looper.getMainLooper()) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            } else {
                return upstream.observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
