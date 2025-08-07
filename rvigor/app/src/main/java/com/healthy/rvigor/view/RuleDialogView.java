package com.healthy.rvigor.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.healthy.rvigor.R;


/**
 *
 */
public class RuleDialogView extends Dialog {
    private Context context;
    private TextView weightTv;
    private TextView unitTv;
    private RuleView ruleView;
    private TextView tvSure;
    private TextView tvCancel;
    private TextView tvTitle;

    public interface OnBtnClickListener {
        void onClick(Dialog dialog, float value);
    }

    public RuleDialogView(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_edit_rule);
        tvTitle = findViewById(R.id.tv_rule_title);
        weightTv = findViewById(R.id.tv_weight);
        unitTv = findViewById(R.id.tv_unit);
        ruleView = findViewById(R.id.rule_view);
        tvCancel = findViewById(R.id.tv_cancel);
        tvSure = findViewById(R.id.tv_sure);
        //设置点击布局外则Dialog消失
        setCanceledOnTouchOutside(true);
    }

    public void showWeightDialog(float minValue, float maxValue, float curValue, OnBtnClickListener listener) {
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置弹窗位置
        wl.gravity = Gravity.BOTTOM;
        window.setAttributes(wl);

        tvTitle.setText(context.getResources().getString(R.string.weight));

        float value = curValue;
        if (value < minValue) {
            value = minValue;
        }
        weightTv.setText(value + "");
        unitTv.setText("kg");

        ruleView.setValue(minValue, maxValue, curValue, 0.1f, 10);
        final float[] weight = {50};
        weight[0] = curValue;
        ruleView.setOnValueChangedListener(new RuleView.OnValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                weight[0] = value;
                weightTv.setText(value + "");
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(RuleDialogView.this, weight[0]);
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

    public void showHeightDialog(float minValue, float maxValue, float curValue, OnBtnClickListener listener){
        Window window = getWindow();
        //设置弹窗动画
        window.setWindowAnimations(R.style.style_dialog);
        //设置Dialog背景色
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置弹窗位置
        wl.gravity = Gravity.BOTTOM;
        window.setAttributes(wl);

        tvTitle.setText(context.getResources().getString(R.string.height));

        float value = curValue;
        if (value < minValue) {
            value = minValue;
        }
        weightTv.setText((int) value + "");
        unitTv.setText("cm");

        ruleView.setValue(minValue, maxValue, curValue, 1, 10);
        final float[] weight = {50};
        weight[0] = curValue;
        ruleView.setOnValueChangedListener(new RuleView.OnValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                weight[0] = value;
                weightTv.setText((int) value + "");
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(RuleDialogView.this, weight[0]);
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
