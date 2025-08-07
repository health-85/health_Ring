package com.healthbit.framework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import com.healthy.rvigor.R;

/**
 * 自定义流式RadioGroup
 */
public class FlowRadioGroup extends RadioGroup {

    private int horizontalPadding;
    private int verticalPadding;

    public FlowRadioGroup(Context context) {
        super(context);
    }

    public FlowRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowRadioGroup);
        horizontalPadding = typedArray.getDimensionPixelSize(R.styleable.FlowRadioGroup_horizontalPadding, 0);
        verticalPadding = typedArray.getDimensionPixelSize(R.styleable.FlowRadioGroup_verticalPadding, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int row = 0;

        for (int index = 0; index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                // 此处增加onlayout中的换行判断，用于计算所需的高度
                int width = child.getMeasuredWidth() + horizontalPadding;
                int height = child.getMeasuredHeight();
                x += width;
                y = (row + 1) * height + row * verticalPadding;
                if (x > maxWidth) {
                    x = width;
                    row++;
                    y = (row + 1) * height + row * verticalPadding;
                }
            }
        }
        // 设置容器所需的宽度和高度
        setMeasuredDimension(maxWidth, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        int maxWidth = r - l;
        int x = 0;
        int y = 0;
        int row = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth() + horizontalPadding;
                int height = child.getMeasuredHeight();
                x += width;
                y = row * height + height;
                if (x > maxWidth) {
                    x = width;
                    row++;
                    y = row * height + height;
                }
                y += row * verticalPadding;
                child.layout(x - width, y - height, x, y);
            }
        }
    }
}
