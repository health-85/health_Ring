package com.healthbit.framework.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;


import com.healthy.rvigor.R;

import org.xutils.common.TaskController;
import org.xutils.x;

import java.lang.reflect.Field;


/**
* @Description:    DialogFragment基类
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public abstract class BaseDialogFragment extends DialogFragment implements DialogInterface.OnDismissListener {
    private static final String TAG = "BaseDialogFragment";
    protected Dialog mDialog;
    protected Window mWindow;
    protected static long DIALOG_TIMEOUT = 15000;
    private boolean isTimeout = false;
    private TaskController taskController;
    private boolean pressBackCancelable = true;
    private boolean touchOutsidCancelable = true;

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.mDialogCallback = dialogCallback;
    }

    protected DialogCallback mDialogCallback;

    protected void initBundle() {
    }

    protected void initToolbar() {
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    protected void initStart() {

    }

    protected abstract int initDialogLayout();

    protected int setTheme() {
        return R.style.BaseTheme_Dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(initDialogLayout(), null);
        mDialog = new Dialog(getActivity(), setTheme());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setOnDismissListener(this);
        mWindow = mDialog.getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.getDecorView().setFitsSystemWindows(true);
        mDialog.setContentView(rootView);
        taskController = x.task();
        initBundle();
        initToolbar();
        initView();
        initData();
        initListener();
        return mDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void setDialogTimeout(long time) {
        if (time > 0) {
            this.isTimeout = true;
            taskController.postDelayed(runnable, time);
        }
    }

    public void setDialogCancelable(boolean cancelable) {
        this.pressBackCancelable = cancelable;
    }

    public void setDialogTouchOutsideCancelable(boolean cancelable) {
        this.touchOutsidCancelable = cancelable;
    }

    protected void setDialogTimeout() {
        setDialogTimeout(DIALOG_TIMEOUT);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "BaseDialogFragment-setDialogTimeout" + "isAdded:" + isAdded());
            if (isAdded() && isTimeout) {
                Log.d(TAG, "BaseDialogFragment-setDialogTimeout" + "isTimeout:" + isTimeout + "执行超时内容，超时执行dismissAllowingStateLoss");
                if (mDialogCallback != null) {
                    mDialogCallback.timeout();
                }
                dismissAllowingStateLoss();
            } else {
                Log.d(TAG, "BaseDialogFragment-setDialogTimeout" + "isTimeout:" + isTimeout + "不执行超时内容");
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(touchOutsidCancelable);
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (!pressBackCancelable) {
                            return true;
                        }
                        Log.d(TAG, "BaseDialogFragment-onKey" + "taskController.removeCallbacks");
                        isTimeout = false;
                        taskController.removeCallbacks(runnable);
                        if (mDialogCallback != null) {
                            mDialogCallback.reset();
                        }
                        isShowing = false;
                        return false;
                    }
                    return false;
                }
            });
        }
        initStart();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDialogCallback != null) {
            mDialogCallback.onDismiss();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        isTimeout = false;
        taskController.removeCallbacks(runnable);
        super.dismissAllowingStateLoss();
        isShowing = false;
        Log.d(TAG, "BaseDialogFragment-dismissAllowingStateLoss" + "isTimeout:" + isTimeout);
    }

    @Override
    public void dismiss() {
        isTimeout = false;
        taskController.removeCallbacks(runnable);
        super.dismiss();
        isShowing = false;
        Log.d(TAG, "BaseDialogFragment-dismiss" + "isTimeout:" + isTimeout);
    }

    private boolean isShowing;

    @Override
    public void show(androidx.fragment.app.FragmentManager manager, String tag) {
        try {
            if (!isAdded() && null == manager.findFragmentByTag(tag)) {
                if (!isShowing) {
                    isShowing = true;
                    //防止在saveinstancestate之后弹出，造成崩溃
                    try {
                        Class<? extends DialogFragment> aClass = getClass();
                        while (true) {
                            if (aClass == DialogFragment.class) {
                                break;
                            }
                            aClass = (Class<? extends DialogFragment>) aClass.getSuperclass();
                        }
                        Field mDismissed = aClass.getDeclaredField("mDismissed");
                        Field mShownByMe = aClass.getDeclaredField("mShownByMe");
                        mDismissed.setAccessible(true);
                        mShownByMe.setAccessible(true);
                        mDismissed.setBoolean(this, false);
                        mShownByMe.setBoolean(this, true);
                        mDismissed.setAccessible(false);
                        mShownByMe.setAccessible(false);
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.add(this, tag);
                        ft.commit();
                    } catch (NoSuchFieldException e) {
                        super.show(manager, tag);
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        super.show(manager, tag);
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Override
//    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            dismissAllowingStateLoss();
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static class BaseDialogCallback implements DialogCallback {

        @Override
        public void onDismiss() {

        }

        @Override
        public void reset() {

        }

        @Override
        public void start() {

        }

        @Override
        public void timeout() {

        }

        @Override
        public void buttonLeft(View view) {

        }

        @Override
        public void buttonRight(View view) {

        }

        @Override
        public void buttonRight(View view, String text) {

        }

        @Override
        public void buttonRight(View view, boolean isSuccess) {

        }

        @Override
        public void buttonRight(View view, String text, int position) {

        }

        @Override
        public void buttonRight(View view, String first, String second) {

        }

        @Override
        public void buttonRight(View view, String first, String second, String third, String fourth) {

        }

        @Override
        public void buttonRight(View view, String[] date) {

        }

        @Override
        public void buttonRight(View view, String first, String second, int floor_id, int room_id) {

        }

        @Override
        public void buttonRight(View view, String floorName, String roomName, int unit_id, int floor_id, int room_id) {

        }
    }

    public interface DialogCallback {

        void onDismiss();

        void reset();

        void start();

        void timeout();

        void buttonLeft(View view);

        void buttonRight(View view);

        void buttonRight(View view, String text);

        void buttonRight(View view, boolean isSuccess);

        void buttonRight(View view, String text, int position);

        void buttonRight(View view, String first, String second);

        void buttonRight(View view, String first, String second, String third, String fourth);

        void buttonRight(View view, String[] date);

        void buttonRight(View view, String floorName, String roomName, int floor_id, int room_id);

        void buttonRight(View view, String floorName, String roomName, int unit_id, int floor_id, int room_id);

    }
}
