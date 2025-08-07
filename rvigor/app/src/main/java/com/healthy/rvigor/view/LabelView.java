package com.healthy.rvigor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.healthy.rvigor.R;
import com.kyleduo.switchbutton.SwitchButton;


/**
 * Label界面  用于设置界面
 */
public class LabelView extends FrameLayout {

    private ImageView imgLabelRight;
    private TextView tvLabelTitle;
    private TextView tvLabelValue;
    private TextView tvLabelTip;
    private SwitchButton switchBtn;
    private ImageView imgRed;

    private ImageView imgTvRight; //title右侧图片

    public LabelView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public LabelView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public LabelView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public LabelView(@NonNull Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LabelView);// TypedArray是一个数组容器
        String title = a.getString(R.styleable.LabelView_label_title);
        String labelValue = a.getString(R.styleable.LabelView_label_value);
        String labelHint = a.getString(R.styleable.LabelView_label_hint);
        String labelTip = a.getString(R.styleable.LabelView_label_tip);
        Drawable leftDrawable = a.getDrawable(R.styleable.LabelView_label_left_img);
        Drawable tvRightDrawable = a.getDrawable(R.styleable.LabelView_label_title_right_img);
        boolean isSwitch = a.getBoolean(R.styleable.LabelView_label_is_switch, false);
        boolean isShowLeftImg = a.getBoolean(R.styleable.LabelView_label_show_left_img, false);
        boolean isShowRed = a.getBoolean(R.styleable.LabelView_label_is_show_red, false);
        boolean isShowRight = a.getBoolean(R.styleable.LabelView_label_is_show_right, true);

        a.recycle();

        LayoutInflater.from(context).inflate(R.layout.view_label, this, true);

        ConstraintLayout itemContent = findViewById(R.id.item_content);

        ImageView imgLabelLeft = findViewById(R.id.img_label_left);
        imgRed = findViewById(R.id.img_red);
        tvLabelTitle = findViewById(R.id.tv_label_title);
        tvLabelValue = findViewById(R.id.tv_label_value);
        tvLabelTip = findViewById(R.id.tv_label_tip);
        switchBtn = findViewById(R.id.switch_btn);
        imgLabelRight = findViewById(R.id.img_label_right);
        imgTvRight = findViewById(R.id.img_title_right);

        if (!TextUtils.isEmpty(title)) {
            tvLabelTitle.setText(title);
        }
        if (leftDrawable != null) {
            imgLabelLeft.setVisibility(VISIBLE);
            imgLabelLeft.setImageDrawable(leftDrawable);
        } else {
            imgLabelLeft.setVisibility(GONE);
        }
        if (tvRightDrawable != null) {
            imgTvRight.setVisibility(VISIBLE);
            imgTvRight.setImageDrawable(tvRightDrawable);
        } else {
            imgTvRight.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(labelHint)) {
            tvLabelValue.setHint(labelHint);
        }
        if (!TextUtils.isEmpty(labelValue)) {
            tvLabelValue.setText(labelValue);
        }
        if (!TextUtils.isEmpty(labelTip)) {
            tvLabelTip.setText(labelTip);
            tvLabelTip.setVisibility(VISIBLE);
        } else {
            tvLabelTip.setVisibility(GONE);
        }

        imgRed.setVisibility(isShowRed ? VISIBLE : GONE);
        switchBtn.setVisibility(isSwitch ? VISIBLE : GONE);
        imgLabelRight.setVisibility((isShowRight && !isSwitch) ? VISIBLE : GONE);
        imgLabelLeft.setVisibility(isShowLeftImg ? VISIBLE : GONE);

        imgTvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleRightImgListener != null) {
                    titleRightImgListener.onTitleRightImgListener(v);
                }
            }
        });

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onSwitchListener != null) {
                    onSwitchListener.onSwitchChangeListener(LabelView.this, isChecked);
                }
            }
        });
    }

    public void setLabelTitle(String title) {
        if (tvLabelTitle != null) {
            tvLabelTitle.setText(title);
        }
    }

    public void setLabelValue(String value) {
        if (tvLabelValue != null) {
            tvLabelValue.setText(value);
        }
    }

    public void setLabelHint(String hint) {
        if (tvLabelValue != null) {
            tvLabelValue.setHint(hint);
        }
    }

    public String getLabelValue() {
        if (tvLabelValue == null) return "";
        return tvLabelValue.getText().toString();
    }

    public void setCheckSwitch(boolean check) {
        if (switchBtn != null) {
            switchBtn.setChecked(check);
        }
    }

    public boolean getCheckSwitch() {
        if (switchBtn != null) {
            return switchBtn.isChecked();
        }
        return false;
    }

    public void setShowRed(boolean check) {
        if (imgRed != null) {
            imgRed.setVisibility(check ? VISIBLE : GONE);
        }
    }

    public void setLabelSingleLine() {
        if (tvLabelValue == null) return;
        tvLabelValue.setMaxLines(1);
        tvLabelValue.setEllipsize(TextUtils.TruncateAt.END);
    }

    public void setShowRight(boolean visible) {
        if (imgLabelRight != null) {
            imgLabelRight.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    public void setLabelTip(String tip) {
        if (!TextUtils.isEmpty(tip) && tvLabelTip != null) {
            tvLabelTip.setText(tip);
            tvLabelTip.setVisibility(VISIBLE);
        } else {
            tvLabelTip.setVisibility(GONE);
        }
    }

//    private OnItemClickListener itemClickListener;

    //文字标题右侧图片
    private OnTitleRightImgListener titleRightImgListener;

    private OnSwitchListener onSwitchListener;

    public interface OnTitleRightImgListener {
        void onTitleRightImgListener(View view);
    }

    public interface OnSwitchListener {
        void onSwitchChangeListener(View view, boolean isChecked);
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    public void setTitleRightImgListener(OnTitleRightImgListener titleRightImgListener) {
        this.titleRightImgListener = titleRightImgListener;
    }

    public ImageView getImgLabelRight() {
        return imgLabelRight;
    }

    public void setImgLabelRight(ImageView imgLabelRight) {
        this.imgLabelRight = imgLabelRight;
    }

    public TextView getTvLabelTitle() {
        return tvLabelTitle;
    }

    public void setTvLabelTitle(TextView tvLabelTitle) {
        this.tvLabelTitle = tvLabelTitle;
    }

    public TextView getTvLabelValue() {
        return tvLabelValue;
    }

    public void setTvLabelValue(TextView tvLabelValue) {
        this.tvLabelValue = tvLabelValue;
    }

    public TextView getTvLabelTip() {
        return tvLabelTip;
    }

    public void setTvLabelTip(TextView tvLabelTip) {
        this.tvLabelTip = tvLabelTip;
    }

    public SwitchButton getSwitchBtn() {
        return switchBtn;
    }

    public void setSwitchBtn(SwitchButton switchBtn) {
        this.switchBtn = switchBtn;
    }

    public ImageView getImgRed() {
        return imgRed;
    }

    public void setImgRed(ImageView imgRed) {
        this.imgRed = imgRed;
    }

    public ImageView getImgTvRight() {
        return imgTvRight;
    }

    public void setImgTvRight(ImageView imgTvRight) {
        this.imgTvRight = imgTvRight;
    }
}
