package com.healthy.rvigor.view;

/**
 * 加载进度栏
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.healthy.rvigor.R;


/**
 * 加载提醒对话框
 */
public class CustomLoadingDialog extends Dialog {
    public TextView tv_load_dialog;

    public CustomLoadingDialog(Context context) {
        super(context);
    }

    public CustomLoadingDialog(Context context, int theme) {
        super(context, theme);
        init(getContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        setContentView(R.layout.load_dialog);
        tv_load_dialog = findViewById(R.id.tv_load_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    public void showMsg(String msg) {
        try {
            tv_load_dialog.setText(msg == null ? getContext().getResources().getString(R.string.loading) : msg);
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reSetMsg(String msg) {
        tv_load_dialog.setText(msg == null ? getContext().getResources().getString(R.string.loading) : msg);
    }
}

