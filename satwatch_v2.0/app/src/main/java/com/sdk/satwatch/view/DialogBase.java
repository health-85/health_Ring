package com.sdk.satwatch.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdk.satwatch.util.CommonFuc;


public abstract class DialogBase extends Dialog {
    public DialogBase(@NonNull Context context) {
        super(context);
    }

    public DialogBase(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogBase(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setBackgroundColor(Color.WHITE);
        int padding = getPadding();
        relativeLayout.setPadding(padding, padding, padding, padding);
        View subview = getSubContentView();
        if (subview != null) {
            relativeLayout.addView(subview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        setContentView(relativeLayout);
    }


    protected int getBackgroudRadio() {
        return CommonFuc.diptopx(getContext(), 2);
    }

    protected abstract View getSubContentView();

    @Override
    protected void onStart() {
        super.onStart();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        if (layoutParams != null) {
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = CommonFuc.getDisplayWidth(getContext()) - (2 * getWidthMargin());
            getWindow().setAttributes(layoutParams);
        }
    }

    protected int getWidthMargin() {
        return CommonFuc.diptopx(getContext(), 20);
    }

    /**
     * @return
     */
    protected int getPadding() {
        return CommonFuc.diptopx(getContext(), 15);
    }

}
