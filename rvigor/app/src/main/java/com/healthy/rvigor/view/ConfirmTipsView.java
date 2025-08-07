package com.healthy.rvigor.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.healthy.rvigor.R;


/**
 */
public class ConfirmTipsView extends Dialog {
    Context context;

    TextView tv_msg;
    TextView tv_cancel;
    TextView tv_sure;
    TextView tv_title;

    public interface OnBtnClickListener{
        void onClick(Dialog dialog, boolean isConfirm);
    }

    public ConfirmTipsView(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_confirm_tips);
        tv_title = findViewById(R.id.tv_title);
        tv_msg = findViewById(R.id.tv_msg);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_sure = findViewById(R.id.tv_sure);
        //设置点击布局外则Dialog消失
        setCanceledOnTouchOutside(true);
    }

    public void showDialog(String msg, String left_btn_txt, String right_btn_txt, OnBtnClickListener listener) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);

        tv_title.setVisibility(View.GONE);
        tv_msg.setText(msg);
        tv_cancel.setText(left_btn_txt);
        tv_sure.setText(right_btn_txt);

        tv_cancel.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,false);
            }
            dismiss();
        });

        tv_sure.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,true);
            }
            dismiss();
        });
        show();
    }

    public void showDialog(String title, String msg, String left_btn_txt, String right_btn_txt, OnBtnClickListener listener) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);

        tv_title.setText(title);
        tv_msg.setText(msg);
        tv_cancel.setText(left_btn_txt);
        tv_sure.setText(right_btn_txt);

        tv_cancel.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,false);
            }
            dismiss();
        });

        tv_sure.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,true);
            }
            dismiss();
        });
        show();
    }

    public void showDialog(OnBtnClickListener listener) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);


        tv_cancel.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,false);
            }
        });

        tv_sure.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,true);
            }
        });
        show();
    }

    public void showUpdateDialog(String msg, String left_btn_txt, String right_btn_txt, OnBtnClickListener listener) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        //设置弹窗位置
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);

        tv_msg.setText(msg);
        tv_cancel.setText(left_btn_txt);
        tv_sure.setText(right_btn_txt);

        tv_cancel.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,false);
            }
            dismiss();
        });

        tv_sure.setOnClickListener(v -> {
            if (listener != null){
                listener.onClick(this,true);
            }
        });
        show();
    }
}
