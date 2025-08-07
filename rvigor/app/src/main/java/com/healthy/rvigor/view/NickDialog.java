package com.healthy.rvigor.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.healthy.rvigor.R;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.zyyoona7.picker.DatePickerView;
import com.zyyoona7.picker.listener.OnDateSelectedListener;
import com.zyyoona7.wheel.WheelView;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/16 17:44
 * @UpdateRemark:
 */
public class NickDialog extends Dialog {

    private Context context;

    private EditText editName;
    private TextView tvSure;
    private TextView tvCancel;
    private TextView tvTitle;

    public interface OnBtnClickListener {
        void onClick(Dialog dialog, String value);
    }

    public NickDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_nick);
        tvTitle = findViewById(R.id.tv_title);
        editName = findViewById(R.id.et_nick_name);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSure = findViewById(R.id.tv_sure);
        //设置点击布局外则Dialog消失
        setCanceledOnTouchOutside(true);
    }

    public void showDialog(String nickName, OnBtnClickListener listener) {
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

        if (!TextUtils.isEmpty(nickName)){
            editName.setText(nickName);
        }

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(NickDialog.this, editName.getText().toString());
                }
                dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        show();
    }
}
