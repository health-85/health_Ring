package com.healthy.rvigor.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.healthbit.framework.util.DeviceUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 创建者：
 * 创建时间：
 * 描述：
 */
public class CustomViewPager extends ViewPager {


    private static final String TAG = "CustomViewPager";

    private OnHeightChangeListener mOnHeightChangeListener;

    private int mHeiht;
    private int mCurrentPosition;

    private Context mContext;

    private Map<Integer, Integer> mHeightMap = new HashMap<>();

    public interface OnHeightChangeListener {
        void onHeightChangeListener(int height, int currentItem);
    }

    public CustomViewPager(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        //下面遍历所有child的高度
        try {
            //下面遍历所有child的高度
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > 0 && i == getCurrentItem()) {//采用最大的view的高度。
                    height = h;
                    break;
                }
            }
            if (height <= 30) {
                height = DeviceUtil.dip2px(mContext, 30);
            }
            if (mOnHeightChangeListener != null) {
                mOnHeightChangeListener.onHeightChangeListener(height, getCurrentItem());
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
            if (height > 0 || !mHeightMap.containsKey(getCurrentItem()) || mHeightMap.get(getCurrentItem()) <= height) {
                mHeightMap.put(getCurrentItem(), height);
            }
//           DemoLog.i(TAG, "item " + getCurrentItem() + " height " + height + "view  " + getChildAt(getCurrentItem()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setHeight(int height, int position) {
        mHeiht = height;
        mCurrentPosition = position;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A12 measureSpec packed into an int
     * @param view        the base view with already measured height
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // set the height from the base view if available
            if (view != null) {
                result = view.getMeasuredHeight();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void setOnHeightChangeListener(OnHeightChangeListener onHeightChangeListener) {
        this.mOnHeightChangeListener = onHeightChangeListener;
    }

    public Map<Integer, Integer> getHeightMap() {
        return mHeightMap;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}
