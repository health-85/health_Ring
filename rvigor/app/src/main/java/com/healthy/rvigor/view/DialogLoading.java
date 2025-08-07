package com.healthy.rvigor.view;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.healthbit.framework.base.BaseDialogFragment;
import com.healthbit.framework.util.DeviceUtil;
import com.healthy.rvigor.R;


/**
 * 全局加载的样式
 *
 * @author dr
 * create at 2019/11/5 10:37 AM
 */
public class DialogLoading extends BaseDialogFragment {

    private long mTime;
    private boolean isCancelable;
    private int textResID;

    @Override
    protected int initDialogLayout() {
        return R.layout.dialog_fragment_loading;
    }

    @Override
    protected void initBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            textResID = bundle.getInt("message", textResID);
            mTime = bundle.getLong("timeout", mTime);
            isCancelable = bundle.getBoolean("isCancelable", true);
        }
    }

    public static DialogLoading newInstance(int textResID, long time, boolean isCancelable) {
        DialogLoading loadViewDialog = new DialogLoading();
        Bundle bundle = new Bundle();
        if (textResID > 0) {
            bundle.putInt("message", textResID);
        }
        bundle.putLong("timeout", time);
        bundle.putBoolean("isCancelable", isCancelable);
        loadViewDialog.setArguments(bundle);
        return loadViewDialog;
    }

    public void update(int textResID, long time, boolean isCancelable) {
        this.mTime = time;
        this.isCancelable = isCancelable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = DeviceUtil.dip2px(getContext(), 100);
        layoutParams.height = DeviceUtil.dip2px(getContext(), 100);
        layoutParams.dimAmount = 0;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().setAttributes(layoutParams);
        return dialog;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mTime < 0) {
            mTime = Integer.MAX_VALUE;
            isCancelable = false;
        }
        setCancelable(false);
        setDialogCancelable(isCancelable);
        setDialogTimeout(mTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void upDateTime(int mTime) {
        this.mTime = mTime;
    }
}