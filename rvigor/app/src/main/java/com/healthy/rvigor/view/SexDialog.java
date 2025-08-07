package com.healthy.rvigor.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.healthy.rvigor.Constants;
import com.healthy.rvigor.R;
import com.healthy.rvigor.util.DateTimeUtils;
import com.healthy.rvigor.util.LogUtils;
import com.zyyoona7.picker.DatePickerView;
import com.zyyoona7.picker.listener.OnDateSelectedListener;
import com.zyyoona7.wheel.WheelView;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/16 18:18
 * @UpdateRemark:
 */
public class SexDialog extends Dialog {

    private Context context;

    private RadioGroup rgSex;
    private RadioButton rbtMale;
    private RadioButton rbtFemale;
    private RadioButton rbtSecrecy;
    private TextView tvSure;
    private TextView tvCancel;
    private TextView tvTitle;

    public interface OnBtnClickListener {
        void onClick(Dialog dialog, int value, String msg);
    }

    public SexDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_sex);
        tvTitle = findViewById(R.id.tv_title);
        rgSex = findViewById(R.id.rg_sex);
        rbtMale = findViewById(R.id.rbt_male);
        rbtFemale = findViewById(R.id.rbt_female);
        rbtSecrecy = findViewById(R.id.rbt_secrecy);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSure = findViewById(R.id.tv_sure);
        //设置点击布局外则Dialog消失
        setCanceledOnTouchOutside(true);
    }

    public void showDialog(int sex, OnBtnClickListener listener) {
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

        final String[] msg = {rbtMale.getText().toString()};
        final int[] selSex = {sex};
        if (selSex[0] == Constants.MALE){
            rbtMale.setChecked(true);
        }else if (selSex[0] == Constants.FEMALE){
            rbtFemale.setChecked(true);
        }else if (selSex[0] == Constants.SECRECY){
            rbtSecrecy.setChecked(true);
        }
        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbt_male) {
                    selSex[0] = Constants.MALE;
                    msg[0] = rbtMale.getText().toString();
                } else if (checkedId == R.id.rbt_female) {
                    selSex[0] = Constants.FEMALE;
                    msg[0] = rbtFemale.getText().toString();
                } else if (checkedId == R.id.rbt_secrecy) {
                    selSex[0] = Constants.SECRECY;
                    msg[0] = rbtSecrecy.getText().toString();
                }
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(SexDialog.this, selSex[0], msg[0]);
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
