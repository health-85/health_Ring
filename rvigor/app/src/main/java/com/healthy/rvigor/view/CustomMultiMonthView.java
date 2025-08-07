package com.healthy.rvigor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MultiMonthView;


/**
 * 高仿魅族日历布局
 * Created by huanghaibin on 2017/11/15.
 */

public class CustomMultiMonthView extends MultiMonthView {

    private int mRadius;

    private boolean isSelCurDay;

    public CustomMultiMonthView(Context context) {
        super(context);
    }


    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme,
                                     boolean isSelectedPre, boolean isSelectedNext) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        mSelectedPaint.setFakeBoldText(false);
        if (isSelectedPre) {
//            if (isSelectedNext) {
//                canvas.drawRect(x, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
//            } else {//最后一个，the last
//                canvas.drawRect(x, cy - mRadius, cx, cy + mRadius, mSelectedPaint);
                canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
//            }
        } else {
//            if(isSelectedNext){
//                canvas.drawRect(cx, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
//            }
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            //
        }

        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        mSchemePaint.setFakeBoldText(false);
        mSelectedPaint.setFakeBoldText(false);
        if (TextUtils.equals(calendar.getScheme(), "当前选择")){
//            LogUtils.i( " 当前选择 " + calendar.getDay());
            isSelCurDay = true;
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        }else if (TextUtils.equals(calendar.getScheme(), "当前")){
//            LogUtils.i( " 当前 " + calendar.getDay());
            isSelCurDay = false;
            canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
        }else {
//            LogUtils.i( " 选择 " + calendar.getDay());
            isSelCurDay = false;
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        }

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        boolean isInRange = isInRange(calendar);
        boolean isEnable = !onCalendarIntercept(calendar);
        mSchemeTextPaint.setFakeBoldText(false);
        mCurMonthTextPaint.setFakeBoldText(false);
        mOtherMonthTextPaint.setFakeBoldText(false);
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            mCurDayTextPaint.setColor(isSelCurDay ?  Color.WHITE : Color.parseColor("#ffffff"));
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            mCurDayTextPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
