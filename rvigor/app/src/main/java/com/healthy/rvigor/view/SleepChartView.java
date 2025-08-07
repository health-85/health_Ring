package com.healthy.rvigor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.healthbit.framework.util.DeviceUtil;
import com.healthy.rvigor.R;
import com.healthy.rvigor.bean.SleepDayBean;
import com.healthy.rvigor.bean.SleepItem;
import com.healthy.rvigor.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SleepChartView extends View {

    public SleepChartView(Context context) {
        super(context);
        init();
    }

    public SleepChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SleepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SleepChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    //触摸点
    private int mDrawPos;
    //文字长度
    private int mTextWidth = 0;

    //间隔宽度
    private int mSplitWidth = 0;
    //图形的边距
    private int mChartPadding = 0;
    //测量文字高度
    private int mXYTextHeight = 0;
    private int mBarWidth = 0;
    //文字和线之间的距离
    private int mBottomTextPadding = 0;
    //文字测量大小
    private int mFontMeasuredHeight = 0;
    //触摸点
    private int mTouchX;

    private int mViewWidth;

    //线条
    private Paint mPaint = new Paint();
    //画X轴文字和横线
    private Paint mXLinePaint = new Paint();
    //区域
    private Paint mAreaPaint = new Paint();
    //画X轴和Y轴文字
    private Paint mXYTextPaint = new TextPaint();
    //触摸Paint
    private Paint mTouchPaint = new Paint();

    //坐标原点位置
    private Point mOrgPoint = new Point(0, 0);
    //坐标实际高宽
    private Size mCoordinateSize = new Size(0, 0);

    private List<String> mSleepList = new ArrayList<>();

    //底部文本占用的区域
    private ArrayList<Rect> mBottomTextRects = new ArrayList<>();

    //数据信息
    public final List<SleepItem> mDataList = new ArrayList<>();
    //
    public List<SleepDayBean> mDayBeanList = new ArrayList<>();

    //触摸Item
    private SleepItem mTouchDataItem = null;

    //起始时间
    private long mStartTime = 0;
    //结束时间
    private long mEndTime = 0;
    //底部文字行数
    private SleepItem mOldItem;

    private int lastX;
    private int offsetX;

    private int lastOffsetX;

    private int lastY;
    private Scroller mScroller;
    //底部文字
    private List<String> mBottomTextList = new ArrayList<>();

    private void init() {
        mSplitWidth = DeviceUtil.dip2px(getContext(), 2);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(DeviceUtil.dip2px(getContext(), 1));

        mXLinePaint.setColor(Color.parseColor("#E8E8E8"));
        mXLinePaint.setStrokeWidth(DeviceUtil.dip2px(getContext(), 1));

        mXYTextPaint.setAntiAlias(true);
        mXYTextPaint.setColor(Color.parseColor("#858585"));
        mXYTextPaint.setTextSize(DeviceUtil.sp2px(getContext(), 12));
        mXYTextPaint.setStrokeWidth(DeviceUtil.dip2px(getContext(), 1));

        mAreaPaint.setStyle(Paint.Style.FILL);
        mAreaPaint.setStrokeWidth(DeviceUtil.dip2px(getContext(), 1));
        mAreaPaint.setTextSize(DeviceUtil.sp2px(getContext(), 12));

        mTouchPaint.setAntiAlias(true);
        mTouchPaint.setColor(Color.parseColor("#E8E8E8"));
        mTouchPaint.setStrokeWidth(DeviceUtil.dip2px(getContext(), 2));

        mXYTextHeight = getFontHeight(mXYTextPaint);
        mTextWidth = (int) mXYTextPaint.measureText(getResources().getString(R.string.light_sleep) + "");
        mFontMeasuredHeight = mXYTextHeight;

        mBottomTextPadding = DeviceUtil.dip2px(getContext(), 3);
        mBarWidth = DeviceUtil.dip2px(getContext(), 1);
        mChartPadding = 0;

        mScroller = new Scroller(getContext());

        mSleepList.clear();
        mSleepList.add(getResources().getString(R.string.deep_sleep));
        mSleepList.add(getResources().getString(R.string.light_sleep));
        mSleepList.add(getResources().getString(R.string.sober));

    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initValue();

        //画X轴文字和横线
//        drawHorizontalLine(canvas);

        drawArea(canvas);

//        drawBottomText(canvas);

//        drawShiLine(canvas);
    }


    /**
     * 初始化特殊值
     */
    private void initValue() {
        mBottomTextRects.clear();
        mOrgPoint.x = mChartPadding;
        mOrgPoint.y = (getMeasuredHeight() - (2 * mChartPadding) - mFontMeasuredHeight - mBottomTextPadding);
        if (mOrgPoint.y < 0) {//如果小于零
            mOrgPoint.y = 0;
        }
        mTextWidth = (int) mXYTextPaint.measureText(getResources().getString(R.string.light_sleep) + "");
        mCoordinateSize.width = (getMeasuredWidth() /*- mTextWidth - (DeviceUtil.dip2px(getContext(), 20))*/);
        mViewWidth = (getMeasuredWidth()/* - mTextWidth - (DeviceUtil.dip2px(getContext(), 50))*/);
        if (mCoordinateSize.width < 0) {
            mCoordinateSize.width = 0;
        }
        mCoordinateSize.height = (getMeasuredHeight() - (2 * mChartPadding) - mFontMeasuredHeight - mBottomTextPadding);
        if (mCoordinateSize.height < 0) {
            mCoordinateSize.height = 0;
        }
        if (mDataList.size() > 0) {
            mSplitWidth = (mCoordinateSize.width - mTextWidth) / (mDataList.size() + 1);
        }
        long len = getSleepLen(mDayBeanList);
        if (len > mCoordinateSize.width) {
            mCoordinateSize.width = (int) len;
        }
//        LogUtils.i(" getMeasuredWidth() " + getMeasuredWidth() + " mViewWidth " + mViewWidth + " len " + len);
    }

    private int getSleepLen(List<SleepDayBean> dayBeanList) {
        if (dayBeanList == null || dayBeanList.isEmpty()) return 0 ;
        int len = 0;
        for (int i = 0; i < dayBeanList.size(); i++){
            SleepDayBean dayBean = dayBeanList.get(i);
            len += dayBean.getSleepLen() / (1000 * 60) * mBarWidth;
            if (i != dayBeanList.size() - 1) len += DeviceUtil.dip2px(getContext(), 80);
        }
        return len;
    }

    public void setSleepDayBean(List<SleepDayBean> dayBeanList){
        mDayBeanList = dayBeanList;
//        if(dayBeanList != null && !dayBeanList.isEmpty()){
//            for (SleepDayBean dayBean : dayBeanList){
//                LogUtils.i(" SleepDayBean len " + dayBean.getSleepLen() / (1000 * 60) + " start " + DateTimeUtils.s_long_2_str(dayBean.getStartTime(), DateTimeUtils.f_format) +
//                        " " + " end " + DateTimeUtils.s_long_2_str(dayBean.getEndTime(), DateTimeUtils.f_format) );
//            }
//        }
//        LogUtils.i(" SleepDayBean mDataList " + new Gson().toJson(mDataList));
//        LogUtils.i(" SleepDayBean mDayBeanList " + new Gson().toJson(mDayBeanList));
    }

    /**
     * 画X轴文字和横线
     *
     * @param canvas
     */
    private void drawHorizontalLine(Canvas canvas) {
        int totalLen = mCoordinateSize.height - mXYTextHeight;
        for (int i = 0; i < 5; i++) {
            int yHeight;
            String text;
            if (i == 0) {
                text = getResources().getString(R.string.deep_sleep);
                yHeight = (int) (mOrgPoint.y - ((float) 1 / 5 * (float) totalLen) /*+ 1 / (float)(10 * totalLen)*/);
            } else if (i == 1) {
                text = getResources().getString(R.string.light_sleep);
                yHeight = (int) (mOrgPoint.y - ((float) 2 / 5 * (float) totalLen) /*+ 1 / (float)(10 * totalLen)*/);
            } else if (i == 2) {
                text = getResources().getString(R.string.fall_asleep);
                yHeight = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen) /*+ 1 / (float)(10 * totalLen)*/);
            } else if (i == 3) {
                text = "REM";
                yHeight = (int) (mOrgPoint.y - ((float) 4 / 5 * (float) totalLen) /*+ 1 / (float)(10 * totalLen)*/);
            } else {
                text = getResources().getString(R.string.sober);
                yHeight = (int) (mOrgPoint.y - ((float) 5 / 5 * (float) totalLen) /*+ 1 / (float)(10 * totalLen)*/);
            }
            canvas.drawText(text, mOrgPoint.x, yHeight + mXYTextHeight, mXYTextPaint);
        }
    }

    private void drawArea(Canvas canvas) {
        if (mDataList == null || mDataList.size() <= 0) return;
        int oldColor = 0;
        int lineStart = 0;
        int lineEnd = 0;
        int startEnd = 0;
        int end = 0;
        int oldx = mOrgPoint.x /*+ mTextWidth + DeviceUtil.dip2px(getContext(), 10)*/;
//        LogUtils.i(" mDataList " + new Gson().toJson(mDataList));
        for (int i = 0; i < mDataList.size(); i++) {
            SleepItem dataItem = mDataList.get(i);
            if (i == 0) mOldItem = dataItem;
            if (dataItem == null) return;
            int rectTop = 0;
            int rectBottom = 0;
            int totalLen = mCoordinateSize.height - mXYTextHeight;
            float len = ((float) 1 / (float) 10 * (float) totalLen);
            if (dataItem.sleepType == SleepItem.DEEP_SLEEP_TYPE) {
                rectTop = (int) (mOrgPoint.y - ((float) 1 / 5 * (float) totalLen));
                rectBottom = (int) (mOrgPoint.y - len);
            } else if (dataItem.sleepType == SleepItem.LIGHT_SLEEP_TYPE) {
                rectTop = (int) (mOrgPoint.y - ((float) 2 / 5 * (float) totalLen));
                rectBottom = (int) (mOrgPoint.y - ((float) 1 / 5 * (float) totalLen) - len);
            } else if (dataItem.sleepType == SleepItem.FALL_SLEEP_TYPE) {
                rectTop = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen));
                rectBottom = (int) (mOrgPoint.y - ((float) 2 / 5 * (float) totalLen) - len);
            } else if (dataItem.sleepType == SleepItem.WAKE_SLEEP_TYPE) {
                rectTop = (int) (mOrgPoint.y - totalLen);
                rectBottom = (int) (mOrgPoint.y - ((float) 4 / 5 * (float) totalLen) - len);
            } else if (dataItem.sleepType == SleepItem.REM_SLEEP_TYPE) {
                rectTop = (int) (mOrgPoint.y - ((float) 4 / 5 * (float) totalLen));
                rectBottom = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen) - len);
            }
//            if (rectTop >= 0 && rectBottom >= 0) {
            int width = mCoordinateSize.width - mTextWidth - mChartPadding;
            if ((timeInRange(dataItem.startTime) && (timeInRange(dataItem.endTime)))) {
                if (dataItem.startTime <= dataItem.endTime) {
                    long step = mEndTime - mStartTime;
                    if (dataItem.getSleepType() == SleepItem.FALL_SLEEP_TYPE) {
                        if (end != 0) oldx += DeviceUtil.dip2px(getContext(), 80);
                        end++;
                    }
                    if (step >= 0) {
//                            LogUtils.i(" SleepType " + dataItem.getSleepType());
                        if (step == 0) step = 1;
//                        int currStart = (int) ((((double) (dataItem.startTime - mStartTime)) * width) / (double) (step)) + mTextWidth
//                                + DeviceUtil.dip2px(getContext(), 10);
//                        int currEnd = (int) ((((double) (dataItem.endTime - mStartTime)) * width) / (double) (step)) + mTextWidth
//                                + DeviceUtil.dip2px(getContext(), 10);
                        if (dataItem.getSleepType() != SleepItem.END_SLEEP_TYPE) {
//                            LogUtils.i(" start 222 " + DateTimeUtils.s_long_2_str(dataItem.startTime, DateTimeUtils.day_hm_format) + " " +
//                                    " end " + DateTimeUtils.s_long_2_str(dataItem.endTime, DateTimeUtils.day_hm_format) + " " + dataItem.getSleepType());
                            int currStart = oldx;
                            int currEnd = (int) (dataItem.endTime - dataItem.startTime) / (1000 * 60) * mBarWidth + oldx;
                            oldx = currEnd;
                            Rect rect = new Rect(/*mOrgPoint.x +*/ currStart, rectTop, currEnd /*+ mOrgPoint.x*/, rectBottom);
                            if (i > 0 && dataItem.getSleepType() != SleepItem.END_SLEEP_TYPE) {
                                int addLen = DeviceUtil.dip2px(getContext(), 2);
                                if (mOldItem.sleepType == SleepItem.DEEP_SLEEP_TYPE) {
                                    lineStart = (int) (mOrgPoint.y - ((float) 1 / 5 * (float) totalLen)) + addLen;
                                    lineEnd = rectBottom - addLen;
                                } else if (mOldItem.sleepType == SleepItem.LIGHT_SLEEP_TYPE) {
                                    if (dataItem.sleepType == SleepItem.DEEP_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 1 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectTop + addLen;
                                    } else if (dataItem.sleepType == SleepItem.WAKE_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 2 / 5 * (float) totalLen)) + addLen;
                                        lineEnd = rectBottom - addLen;
                                    } else if (dataItem.sleepType == SleepItem.REM_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 2 / 5 * (float) totalLen)) + addLen;
                                        lineEnd = rectBottom - addLen;
                                    } else if (dataItem.sleepType == SleepItem.LIGHT_SLEEP_TYPE) {
                                        lineStart = 0;
                                        lineEnd = 0;
                                    }
                                } else if (mOldItem.sleepType == SleepItem.FALL_SLEEP_TYPE) {
                                    if (dataItem.sleepType == SleepItem.LIGHT_SLEEP_TYPE || dataItem.sleepType == SleepItem.DEEP_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 2 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectTop + addLen;
                                    } else if (dataItem.sleepType == SleepItem.WAKE_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 4 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectBottom + addLen;
                                    } else if (dataItem.sleepType == SleepItem.REM_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectBottom + addLen;
                                    }
                                } else if (mOldItem.sleepType == SleepItem.REM_SLEEP_TYPE) {
                                    if (dataItem.sleepType == SleepItem.LIGHT_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectTop + addLen;
                                    } else if (dataItem.sleepType == SleepItem.WAKE_SLEEP_TYPE) {
                                        lineStart = rectBottom - addLen;
                                        lineEnd = (int) (mOrgPoint.y - ((float) 4 / 5 * (float) totalLen)) + addLen;
                                    } else if (dataItem.sleepType == SleepItem.FALL_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectBottom + addLen;
                                    } else if (dataItem.sleepType == SleepItem.DEEP_SLEEP_TYPE) {
                                        lineStart = (int) (mOrgPoint.y - ((float) 3 / 5 * (float) totalLen) - len) - addLen;
                                        lineEnd = rectTop + addLen;
                                    }
                                } else if (mOldItem.sleepType == SleepItem.WAKE_SLEEP_TYPE) {
                                    lineStart = (int) (mOrgPoint.y - ((float) 4 / 5 * (float) totalLen) - len) - addLen;
                                    lineEnd = rectTop + addLen;
                                }
                                if (lineStart > 0 && dataItem.getSleepType() != SleepItem.FALL_SLEEP_TYPE) {
                                    Rect rect1 = new Rect(mOrgPoint.x + currStart, lineStart, mOrgPoint.x + currStart, lineEnd);
                                    LinearGradient linearGradient = new LinearGradient(mOrgPoint.x + currStart, lineStart, mOrgPoint.x + currStart,
                                            lineEnd, new int[]{mOldItem.color, dataItem.color}, new float[]{0.5f, 0.9f}, Shader.TileMode.CLAMP);
                                    mAreaPaint.setShader(linearGradient);
                                    canvas.drawRect(rect1, mAreaPaint);
//                                mAreaPaint.setColor(dataItem.color);
                                    canvas.drawLine(currStart + mOrgPoint.x, lineStart, currStart + mOrgPoint.x, lineEnd, mAreaPaint);
                                }
                                mOldItem = dataItem;
                            }

                            mAreaPaint.setShader(null);
                            mAreaPaint.setStyle(Paint.Style.FILL);
                            mAreaPaint.setColor(dataItem.color);

                            if (dataItem.getSleepType() != SleepItem.END_SLEEP_TYPE) {
                                RectF rectF = new RectF(mOrgPoint.x + currStart - DeviceUtil.dip2px(getContext(), 1), rectTop,
                                        currEnd + mOrgPoint.x + DeviceUtil.dip2px(getContext(), 1), rectBottom);
                                canvas.drawRoundRect(rectF, DeviceUtil.dip2px(getContext(), 2), DeviceUtil.dip2px(getContext(), 2), mAreaPaint);
                            }
                            dataItem.currentDrawRect = rect;//当前的绘制矩形区域

                            if (dataItem.getSleepType() == SleepItem.FALL_SLEEP_TYPE) {
                                String leftText = DateTimeUtils.toDateString(new Date(dataItem.getStartTime()), "HH:mm") + "-";
                                int txtw = (int) mXYTextPaint.measureText(leftText);
                                int leftAdd = currStart;
                                if (2 * txtw + currStart > mCoordinateSize.width) {
                                    leftAdd = mCoordinateSize.width - 2 * txtw;
                                }
                                startEnd = mOrgPoint.x + leftAdd + txtw;
                                canvas.drawText(leftText, mOrgPoint.x + leftAdd, mOrgPoint.y + mFontMeasuredHeight /*+ (fh - mXYTextPaint.getFontMetrics().descent)*/,
                                        mXYTextPaint);
                            }
                        }

                        if (dataItem.getSleepType() == SleepItem.END_SLEEP_TYPE) {
//                            LogUtils.i(" END_SLEEP_TYPE " + DateTimeUtils.s_long_2_str(dataItem.getStartTime(), DateTimeUtils.day_hm_format));
                            String rightText = DateTimeUtils.toDateString(new Date(dataItem.getStartTime()), "HH:mm") + "";
                            int txtw = (int) mXYTextPaint.measureText(rightText);
                            canvas.drawText(rightText, /*mOrgPoint.x + currStart - txtw - mChartPadding - mTextWidth*/startEnd,
                                    mOrgPoint.y + mFontMeasuredHeight  /*+ (fh - mXYTextPaint.getFontMetrics().descent)*/, mXYTextPaint);
                        }
                    }
                }
            }
//            }
        }
    }

    /**
     * 绘制底部文字
     *
     * @param canvas
     */
    private void drawBottomText(Canvas canvas) {
        int fh = getFontHeight(mXYTextPaint);
        String leftText = DateTimeUtils.toDateString(new Date(mStartTime), "HH:mm") + getResources().getString(R.string.fall_asleep);
        String rightText = DateTimeUtils.toDateString(new Date(mEndTime), "HH:mm") + "醒来";
        if (mStartTime != 0 && mEndTime != 0) {
            canvas.drawText(leftText, mOrgPoint.x, mOrgPoint.y + mFontMeasuredHeight /*+ (fh - mXYTextPaint.getFontMetrics().descent)*/,
                    mXYTextPaint);
            int txtw = (int) mXYTextPaint.measureText(rightText);
            canvas.drawText(rightText, mOrgPoint.x + mCoordinateSize.width - txtw - mChartPadding - mTextWidth,
                    mOrgPoint.y + mFontMeasuredHeight /*+ (fh - mXYTextPaint.getFontMetrics().descent)*/, mXYTextPaint);
        }
    }

    /**
     * 设置时间范围
     *
     * @param start
     * @param end
     */
    public void setTimeRange(long start, long end, int bottomTextCount) {
        if (end <= start) {
            end = DateTimeUtils.AddMinute(new Date(start), 30).getTime();
        }
        if (end > start) {
            mStartTime = start;
            mEndTime = end;
            postInvalidate();
        }
    }

    public void setTimeRange(long start, long end) {
        if (end <= start) {
            end = DateTimeUtils.AddMinute(new Date(start), 30).getTime();
        }
        if (end > start) {
            mStartTime = start;
            mEndTime = end;
            postInvalidate();
        }
    }

    /**
     * 时间是否在指定范围
     *
     * @param time
     * @return
     */
    private boolean timeInRange(long time) {
        return ((time >= mStartTime) && (time <= mEndTime));
    }

    /**
     * 获取文字的高度
     *
     * @param paint
     * @return
     */
    private int getFontHeight(Paint paint) {
        return (int) paint.getFontMetrics(paint.getFontMetrics());
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        return super.dispatchTouchEvent(event);
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            getDataItemByMouseX((int) event.getX());
//        } else {
//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                getDataItemByMouseX((int) event.getX());
//            }
//        }
//        if ((event.getAction() == MotionEvent.ACTION_UP)
//                || (event.getAction() == MotionEvent.ACTION_CANCEL)) {
////            mouseTouchDataItem = null;
//        }
//        postInvalidate();
//        return true;
//    }

    /**
     * 获取触摸点
     */
    private void getDataItemByMouseX(int x) {
        for (int i = 0; i < mDataList.size(); i++) {
            SleepItem dataItem = mDataList.get(i);
            if ((dataItem.currentDrawRect != null) && (!dataItem.currentDrawRect.isEmpty())) {
                if ((x > dataItem.currentDrawRect.left) && (x <= dataItem.currentDrawRect.right)) {
                    mTouchX = x;
                    mDrawPos = i;
                    mTouchDataItem = dataItem;
                }
            }
        }
    }

    /**
     * 画线
     *
     * @param canvas
     */
    private void drawShiLine(Canvas canvas) {
        if (mCoordinateSize == null || mTouchDataItem == null) return;
        int totalLen = mCoordinateSize.height - mXYTextHeight;
        if (totalLen < 0) {
            totalLen = 0;
        }
        float len = ((float) 1 / (float) 10 * (float) totalLen);
        if (mTouchX > 0) {
            canvas.drawLine(mTouchX, mOrgPoint.y - totalLen, mTouchX, (mOrgPoint.y - len), mTouchPaint);
        } else {
            canvas.drawLine(mOrgPoint.x + mSplitWidth * (mDrawPos + 1), mOrgPoint.y - totalLen,
                    mOrgPoint.x + mSplitWidth * (mDrawPos + 1), (mOrgPoint.y - len), mTouchPaint);
        }
        if (listener != null) {
            listener.OnTouchBar(mTouchDataItem);
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
//        LogUtils.i(" computeScroll lastOffsetX == " + lastOffsetX + " offsetX == " + offsetX);
        //判断Scroller是否执行完毕
//        if (mScroller.computeScrollOffset()) {
//            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//            //通过重绘不断来调用computeScroll
//            invalidate();
//        }
    }

    int mLastX;
    int mLastY;

    //处理触摸事件的分发 是从dispatchTouchEvent开始的
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //触摸点相对于其所在组件原点的X坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手按下屏幕,父布局没有作用,进行拦截
                //让父布局ViewPager禁用拦截功能,从而让父布局忽略事件后的一切行为
                //requestDisallowInterceptTouchEvent(true)表示：
                //getParent() 获取到父视图 父视图不拦截触摸事件
                //孩子不希望父视图拦截触摸事件
                if (mDataList == null || mDataList.isEmpty()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //水平移动的增量
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;

//                if (getScrollX() + mViewWidth >= mCoordinateSize.width){
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }

                //Math.abs绝对值
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
//                    //当水平增量大于竖直增量时，表示水平滑动，此时需要父View去处理事件，所以不拦截
//                    //让父布局ViewPager使用拦截功能,从而让父布局完成事件后的一切行为
//                    //requestDisallowInterceptTouchEvent(false)表示：
//                    //孩子希望父视图拦截触摸事件,也就是让CustomViewPager拦截触摸事件，进行左右滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastOffsetX = getScrollX();
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = x - lastX;
                int distance = -offsetX + lastOffsetX;
                if (distance <= 0) {
                    distance = 0;
                }
                if ((distance + mViewWidth) > mCoordinateSize.width) {
                    distance = mCoordinateSize.width - mViewWidth;
                }
                scrollTo(distance, 0);
//                LogUtils.i(" onTouchEvent offsetX == " + -offsetX + " lastX == " + lastX + " mViewWidth == " + mViewWidth +
//                        " lastOffsetX == " + lastOffsetX + " distance == " + distance + " curX == " + mScroller.getCurrX() + " ScrollX == " + getScrollX());
                break;
            case MotionEvent.ACTION_UP:
                if (mScroller != null) {
                    mScroller.startScroll(getScrollX(),
                            getScrollY(),
                            -getScrollX(),
                            -getScrollY());
//                    LogUtils.i(" ACTION_UP ScrollX " + getScrollX() + " ScrollY " + getScrollY() + " -getScrollX " + -getScrollX() + " -getScrollY " + -getScrollY());
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * 清除触摸Item
     */
    public void cleanTouchItem() {
        mTouchDataItem = null;
        mDataList.clear();
        postInvalidate();
    }

    public void setTouchDataItem(SleepItem item) {
        if (item == null) return;
        mDrawPos = -1;
        mTouchDataItem = item;
        for (int i = 0; i < mDataList.size(); i++) {
            SleepItem dataItem = mDataList.get(i);
            if (item.startTime == dataItem.startTime && mDrawPos < 0) {
                mDrawPos = i;
                mTouchDataItem = dataItem;
            }
        }
        postInvalidate();
    }

    public void addDateItem(SleepItem item) {
        mDataList.add(item);
    }

    public void addDateItem(List<SleepItem> item) {
        mDataList.addAll(item);
    }

    public void setBottomTextList(List<String> mBottomTextList) {
        this.mBottomTextList.clear();
        this.mBottomTextList.addAll(mBottomTextList);
        postInvalidate();
    }


    public List<SleepItem> getDataList() {
        return mDataList;
    }

    public OnTouchBarListener listener;

    public void setListener(OnTouchBarListener listener) {
        this.listener = listener;
    }

    /**
     * 触摸监听
     */
    public interface OnTouchBarListener {
        void OnTouchBar(SleepItem item);
    }

    public static class Size {

        public int width = 0;
        public int height = 0;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

}