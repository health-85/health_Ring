package com.healthy.rvigor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.MainViewSize
import com.healthy.rvigor.bean.SleepDayBean
import com.healthy.rvigor.bean.SleepItem
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/27 21:26
 * @UpdateRemark:
 */
class SleepDataView : View {

    //触摸点
    var mDrawPos = 0

    //文字长度
    var mTextWidth = 0

    //间隔宽度
    var mSplitWidth = 0

    //图形的边距
    var mChartPadding = 0

    //测量文字高度
    var mXYTextHeight = 0
    var mBarWidth = 0

    //文字和线之间的距离
    var mBottomTextPadding = 0

    //文字测量大小
    var mFontMeasuredHeight = 0

    //触摸点
    var mTouchX = 0
    var mTouchRectX = 0f

    var mViewWidth = 0

    //线条
    val mPaint = Paint()

    //画X轴文字和横线
    val mXLinePaint = Paint()

    //区域
    val mAreaPaint = Paint()
    val mPath = Path()

    //画X轴和Y轴文字
    val mXYTextPaint: Paint = TextPaint()

    //触摸Paint
    val mTouchPaint = Paint()

    //坐标原点位置
    val mOrgPoint = Point(0, 0)

    //坐标实际高宽
    val mCoordinateSize = MainViewSize(0f, 0f)

    val mSleepList: MutableList<String> = ArrayList()

    //底部文本占用的区域
    val mBottomTextRects = ArrayList<Rect>()

    //数据信息
    val mDataList = mutableListOf<SleepItem>()

    //
    var mDayBeanList = mutableListOf<SleepDayBean>()

    //触摸Item
    var mTouchDataItem: SleepItem? = null

    //起始时间
    var mStartTime: Long = 0

    //结束时间
    var mEndTime: Long = 0

    //底部文字行数
    var mOldItem: SleepItem? = null

    var lastX = 0
    var offsetX = 0

    var lastOffsetX = 0

    var lastY = 0
    var mScroller: Scroller? = null

    //底部文字
    val mBottomTextList = mutableListOf<String>()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr)
    }

    fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        mSplitWidth = DeviceUtil.dip2px(getContext(), 2f)
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = DeviceUtil.dip2px(getContext(), 1f).toFloat()
        mXLinePaint.color = Color.parseColor("#E8E8E8")
        mXLinePaint.strokeWidth = DeviceUtil.dip2px(getContext(), 1f).toFloat()
        mXYTextPaint.isAntiAlias = true
        mXYTextPaint.color = Color.parseColor("#858585")
        mXYTextPaint.textSize = DeviceUtil.sp2px(getContext(), 12f).toFloat()
        mXYTextPaint.strokeWidth = DeviceUtil.dip2px(getContext(), 1f).toFloat()
        mAreaPaint.style = Paint.Style.FILL
        mAreaPaint.strokeWidth = DeviceUtil.dip2px(getContext(), 1f).toFloat()
        mAreaPaint.textSize = DeviceUtil.sp2px(getContext(), 12f).toFloat()
        mTouchPaint.isAntiAlias = true
        mTouchPaint.color = Color.parseColor("#E8E8E8")
        mTouchPaint.strokeWidth = DeviceUtil.dip2px(getContext(), 1f).toFloat()
        mXYTextHeight = getFontHeight(mXYTextPaint)
        mTextWidth =
            mXYTextPaint.measureText(resources.getString(R.string.light_sleep) + "").toInt()
        mFontMeasuredHeight = mXYTextHeight
        mBottomTextPadding = DeviceUtil.dip2px(getContext(), 3f)
        mBarWidth = DeviceUtil.dip2px(getContext(), 0.7f)
        mChartPadding = 0
        mScroller = Scroller(getContext())
        mSleepList.clear()
        mSleepList.add(resources.getString(R.string.deep_sleep))
        mSleepList.add(resources.getString(R.string.light_sleep))
        mSleepList.add(resources.getString(R.string.sober))
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        initValue()

        drawArea(canvas)

        drawShiLine(canvas)
    }

    /**
     * 初始化特殊值
     */
    private fun initValue() {
        mBottomTextRects.clear()
        mOrgPoint.x = mChartPadding
        mOrgPoint.y =
            getMeasuredHeight() - 2 * mChartPadding - mFontMeasuredHeight - mBottomTextPadding
        if (mOrgPoint.y < 0) { //如果小于零
            mOrgPoint.y = 0
        }
        mTextWidth =
            mXYTextPaint.measureText(getResources().getString(R.string.light_sleep) + "").toInt()
        mCoordinateSize.x =
            measuredWidth.toFloat()
        mViewWidth = measuredWidth
        if (mCoordinateSize.x < 0) {
            mCoordinateSize.x = 0f
        }
        mCoordinateSize.y =
            (measuredHeight - 2 * mChartPadding - mFontMeasuredHeight - mBottomTextPadding).toFloat()
        if (mCoordinateSize.y < 0) {
            mCoordinateSize.y = 0f
        }
        if (mDataList.size > 0) {
            mSplitWidth = ((mCoordinateSize.x) / (mDataList.size + 1)).toInt()
        }
        val len: Long = getSleepLen(mDayBeanList).toLong()
        if (len > mCoordinateSize.x) {
            mCoordinateSize.x = len.toFloat() + mOrgPoint.x
        }
//        LogUtils.i(" getMeasuredWidth() " + getMeasuredWidth() + " mViewWidth " + mViewWidth + " len " + len);
    }

    private fun getSleepLen(dayBeanList: List<SleepDayBean>?): Int {
        if (dayBeanList.isNullOrEmpty()) return 0
        var len = 0f
        for (i in dayBeanList.indices) {
            val dayBean: SleepDayBean = dayBeanList[i]
            len += dayBean.getSleepLen() / (1000 * 60) * mBarWidth
            if (i != dayBeanList.size - 1) len += DeviceUtil.dip2px(getContext(), 30f)
        }
//        len += DeviceUtil.dip2px(getContext(), 80f)
        return len.toInt()
    }

    fun setSleepDayBean(dayBeanList: List<SleepDayBean>?) {
        if (dayBeanList.isNullOrEmpty()) return
        mDayBeanList.clear()
        mDayBeanList.addAll(dayBeanList)
//        if(dayBeanList != null && !dayBeanList.isEmpty()){
//            for (SleepDayBean dayBean : dayBeanList){
//                LogUtils.i(" SleepDayBean len " + dayBean.getSleepLen() / (1000 * 60) + " start " + DateTimeUtils.s_long_2_str(dayBean.getStartTime(), DateTimeUtils.f_format) +
//                        " " + " end " + DateTimeUtils.s_long_2_str(dayBean.getEndTime(), DateTimeUtils.f_format) );
//            }
//        }
//        LogUtils.i(" SleepDayBean mDataList " + new Gson().toJson(mDataList));
//        LogUtils.i(" SleepDayBean mDayBeanList " + new Gson().toJson(mDayBeanList));
    }

    private fun drawArea(canvas: Canvas?) {
        if (mDataList == null || mDataList.size <= 0) return
        val oldColor = 0
        var lineStart = 0
        var lineEnd = 0
        var startEnd = 0
        var txtEnd = 0f
        var txtStart = 0f
        var txtEndHeight = 0f
        var end = 0
        var oldx = mOrgPoint.x /*+ mTextWidth + DeviceUtil.dip2px(getContext(), 10)*/
        //        LogUtils.i(" mDataList " + new Gson().toJson(mDataList));

        val sizeList = mutableListOf<MainViewSize>()

        for (i in mDataList.indices) {
            val dataItem: SleepItem = mDataList[i]
            if (i == 0) mOldItem = dataItem
            if (dataItem == null) return
            var rectTop = 0
            var rectBottom = 0
            val totalLen = mCoordinateSize.y - mXYTextHeight
            val len = 1f / 10f * totalLen.toFloat()
            if (dataItem.sleepType === SleepItem.DEEP_SLEEP_TYPE) {
                rectTop = (mOrgPoint.y - 1f / 4 * totalLen.toFloat()).toInt()
                rectBottom = (mOrgPoint.y - len).toInt()
            } else if (dataItem.sleepType === SleepItem.LIGHT_SLEEP_TYPE) {
                rectTop = (mOrgPoint.y - 2f / 4 * totalLen.toFloat()).toInt()
                rectBottom = (mOrgPoint.y - 1f / 4 * totalLen.toFloat() - len).toInt()
            } else if (dataItem.sleepType === SleepItem.FALL_SLEEP_TYPE) {
                rectTop = (mOrgPoint.y - 3f / 4 * totalLen.toFloat()).toInt()
                rectBottom = (mOrgPoint.y - 2f / 4 * totalLen.toFloat() - len).toInt()
            } else if (dataItem.sleepType === SleepItem.WAKE_SLEEP_TYPE) {
//                rectTop = ((mOrgPoint.y - totalLen).toInt())
//                rectBottom = (mOrgPoint.y - 4f / 4 * totalLen.toFloat() - len).toInt()
                rectTop = (mOrgPoint.y - 4f / 4 * totalLen.toFloat()).toInt()
                rectBottom = (mOrgPoint.y - 3f / 4 * totalLen.toFloat() - len).toInt()
            } else if (dataItem.sleepType === SleepItem.REM_SLEEP_TYPE) {
//                rectTop = (mOrgPoint.y - 4f / 4 * totalLen.toFloat()).toInt()
//                rectBottom = (mOrgPoint.y - 3f / 4 * totalLen.toFloat() - len).toInt()
                rectTop = (mOrgPoint.y - 3f / 4 * totalLen.toFloat()).toInt()
                rectBottom = (mOrgPoint.y - 2f / 4 * totalLen.toFloat() - len).toInt()
            }
            //            if (rectTop >= 0 && rectBottom >= 0) {
            val width = mCoordinateSize.x - mTextWidth - mChartPadding
            if (timeInRange(dataItem.startTime) && timeInRange(dataItem.endTime)) {
                if (dataItem.startTime <= dataItem.endTime) {
                    var step = mEndTime - mStartTime
                    if (dataItem.getSleepType() === SleepItem.FALL_SLEEP_TYPE) {
                        if (end != 0) oldx += DeviceUtil.dip2px(getContext(), 30f)
                        end++
                    }
                    if (step >= 0) {
//                            LogUtils.i(" SleepType " + dataItem.getSleepType());
                        if (step == 0L) step = 1
                        //                        int currStart = (int) ((((double) (dataItem.startTime - mStartTime)) * width) / (double) (step)) + mTextWidth
//                                + DeviceUtil.dip2px(getContext(), 10);
//                        int currEnd = (int) ((((double) (dataItem.endTime - mStartTime)) * width) / (double) (step)) + mTextWidth
//                                + DeviceUtil.dip2px(getContext(), 10);
                        if (dataItem.getSleepType() !== SleepItem.END_SLEEP_TYPE) {
//                            LogUtils.i(" start 222 " + DateTimeUtils.s_long_2_str(dataItem.startTime, DateTimeUtils.day_hm_format) + " " +
//                                    " end " + DateTimeUtils.s_long_2_str(dataItem.endTime, DateTimeUtils.day_hm_format) + " " + dataItem.getSleepType());
                            val currStart = oldx
                            val currEnd =
                                ((dataItem.endTime - dataItem.startTime) / (1000 * 60) * mBarWidth + oldx).toInt()
                            oldx = currEnd.toInt()
                            txtEnd = currEnd.toFloat()
                            val rect = Rect( /*mOrgPoint.x +*/currStart,
                                rectTop,
                                currEnd /*+ mOrgPoint.x*/,
                                rectBottom
                            )
                            if (i > 0 && dataItem.getSleepType() !== SleepItem.END_SLEEP_TYPE) {
                                val addLen: Int = DeviceUtil.dip2px(getContext(), 2f)
                                if (mOldItem?.sleepType === SleepItem.DEEP_SLEEP_TYPE) {
                                    lineStart =
                                        (mOrgPoint.y - 1f / 4 * totalLen.toFloat()).toInt() + addLen
                                    lineEnd = rectBottom - addLen
                                } else if (mOldItem?.sleepType === SleepItem.LIGHT_SLEEP_TYPE) {
                                    if (dataItem.sleepType === SleepItem.DEEP_SLEEP_TYPE) {
                                        lineStart =
                                            (mOrgPoint.y - 1f / 4 * totalLen.toFloat() - len).toInt() - addLen
                                        lineEnd = rectTop + addLen
                                    } else if (dataItem.sleepType === SleepItem.WAKE_SLEEP_TYPE) {
                                        lineStart =
                                            (mOrgPoint.y - 2f / 4 * totalLen.toFloat()).toInt() + addLen
                                        lineEnd = rectBottom - addLen
                                    } else if (dataItem.sleepType === SleepItem.REM_SLEEP_TYPE) {
                                        lineStart =
                                            (mOrgPoint.y - 2f / 4 * totalLen.toFloat()).toInt() + addLen
                                        lineEnd = rectBottom - addLen
                                    } else if (dataItem.sleepType === SleepItem.LIGHT_SLEEP_TYPE) {
                                        lineStart = 0
                                        lineEnd = 0
                                    }
                                } else if (mOldItem?.sleepType === SleepItem.FALL_SLEEP_TYPE) {
//                                    if (dataItem.sleepType === SleepItem.LIGHT_SLEEP_TYPE || dataItem.sleepType === SleepItem.DEEP_SLEEP_TYPE) {
//                                        lineStart =
//                                            (mOrgPoint.y - 2f / 5 * totalLen.toFloat() - len).toInt() - addLen
//                                        lineEnd = rectTop + addLen
//                                    } else if (dataItem.sleepType === SleepItem.WAKE_SLEEP_TYPE) {
//                                        lineStart =
//                                            (mOrgPoint.y - 4f / 5 * totalLen.toFloat() - len).toInt() - addLen
//                                        lineEnd = rectBottom + addLen
//                                    } else if (dataItem.sleepType === SleepItem.REM_SLEEP_TYPE) {
//                                        lineStart =
//                                            (mOrgPoint.y - 3f / 5 * totalLen.toFloat() - len).toInt() - addLen
//                                        lineEnd = rectBottom + addLen
//                                    }
                                } else if (mOldItem?.sleepType === SleepItem.REM_SLEEP_TYPE) {
                                    if (dataItem.sleepType === SleepItem.LIGHT_SLEEP_TYPE) {
                                        lineStart =
                                            (mOrgPoint.y - 2f / 4 * totalLen.toFloat() - len).toInt() - addLen
                                        lineEnd = rectTop + addLen
                                    } else if (dataItem.sleepType === SleepItem.WAKE_SLEEP_TYPE) {
                                        lineStart = rectBottom - addLen
                                        lineEnd =
                                            (mOrgPoint.y - 3f / 4 * totalLen.toFloat()).toInt() + addLen
                                    } else if (dataItem.sleepType === SleepItem.FALL_SLEEP_TYPE) {
                                        lineStart =
                                            (mOrgPoint.y - 2f / 4 * totalLen.toFloat() - len).toInt() - addLen
                                        lineEnd = rectBottom + addLen
                                    } else if (dataItem.sleepType === SleepItem.DEEP_SLEEP_TYPE) {
                                        lineStart =
                                            (mOrgPoint.y - 2f / 4 * totalLen.toFloat() - len).toInt() - addLen
                                        lineEnd = rectTop + addLen
                                    }
                                } else if (mOldItem?.sleepType === SleepItem.WAKE_SLEEP_TYPE) {
                                    lineStart =
                                        (mOrgPoint.y - 3f / 4 * totalLen.toFloat() - len).toInt() - addLen
                                    lineEnd = rectTop + addLen
                                }
                                if (lineStart > 0 && dataItem.getSleepType() !== SleepItem.FALL_SLEEP_TYPE) {
                                    val rect1 = Rect(
                                        mOrgPoint.x + currStart,
                                        lineStart,
                                        mOrgPoint.x + currStart,
                                        lineEnd
                                    )
                                    val linearGradient = LinearGradient(
                                        (mOrgPoint.x + currStart).toFloat(),
                                        lineStart.toFloat(),
                                        (mOrgPoint.x + currStart).toFloat(),
                                        lineEnd.toFloat(),
                                        intArrayOf(
                                            mOldItem?.color ?: dataItem.color,
                                            dataItem.color
                                        ),
                                        floatArrayOf(0.5f, 0.9f),
                                        Shader.TileMode.CLAMP
                                    )
                                    mAreaPaint.shader = linearGradient
                                    canvas?.drawRect(rect1, mAreaPaint)
                                    //                                mAreaPaint.setColor(dataItem.color);
                                    canvas?.drawLine(
                                        (currStart + mOrgPoint.x).toFloat(),
                                        lineStart.toFloat(),
                                        (currStart + mOrgPoint.x).toFloat(),
                                        lineEnd.toFloat(),
                                        mAreaPaint
                                    )
                                }
                                mOldItem = dataItem
                            }
                            mAreaPaint.shader = null
                            mAreaPaint.style = Paint.Style.FILL
                            mAreaPaint.color = dataItem.color

                            if (dataItem.getSleepType() !== SleepItem.END_SLEEP_TYPE && dataItem.getSleepType() != SleepItem.FALL_SLEEP_TYPE) {

                                var left = (mOrgPoint.x + currStart - DeviceUtil.dip2px(
                                    context,
                                    0.8f
                                )).toFloat()
                                var right = (currEnd + mOrgPoint.x + DeviceUtil.dip2px(
                                    context,
                                    0.8f
                                )).toFloat()

//                                var left = (mOrgPoint.x + currStart + DeviceUtil.dip2px(
//                                    context,
//                                    2f
//                                )).toFloat()
//                                var right = (currEnd + mOrgPoint.x - DeviceUtil.dip2px(
//                                    context,
//                                    2f
//                                )).toFloat()
//
//                                if (right <= left) {
//                                    val len = DeviceUtil.dip2px(context, 0.5f)
//                                    right = left + len
//                                    left -= len
//                                }

                                //外边框
                                var rectF = RectF(
                                    left,
                                    rectTop.toFloat(),
                                    right,
                                    rectBottom.toFloat()
                                )
                                txtEnd = (currEnd + mOrgPoint.x).toFloat()
                                mAreaPaint.color = dataItem.lightColor
                                canvas?.drawRoundRect(
                                    rectF,
                                    DeviceUtil.dip2px(getContext(), 5f).toFloat(),
                                    DeviceUtil.dip2px(getContext(), 5f).toFloat(),
                                    mAreaPaint
                                )

                                //内边框
                                rectF = RectF(
                                    left + DeviceUtil.dip2px(context, 0.8f),
                                    rectTop.toFloat() + DeviceUtil.dip2px(
                                        getContext(),
                                        2f
                                    ),
                                    right - DeviceUtil.dip2px(context, 0.8f),
                                    rectBottom.toFloat() - DeviceUtil.dip2px(
                                        getContext(),
                                        2f
                                    )
                                )
                                mAreaPaint.color = dataItem.color
                                canvas?.drawRoundRect(
                                    rectF,
                                    DeviceUtil.dip2px(getContext(), 5f).toFloat(),
                                    DeviceUtil.dip2px(getContext(), 5f).toFloat(),
                                    mAreaPaint
                                )
                            }
                            dataItem.currentDrawRect = rect //当前的绘制矩形区域
                            if (dataItem.getSleepType() === SleepItem.FALL_SLEEP_TYPE) {
                                val leftText: String = DateTimeUtils.toDateString(
                                    Date(dataItem.getStartTime()),
                                    "HH:mm"
                                ) + resources.getString(R.string.view_fall_sleep)
                                val txtw = mXYTextPaint.measureText(leftText).toInt()
                                var leftAdd = currStart
                                if (/*2 * */txtw + currStart > mCoordinateSize.x) {
                                    leftAdd = (mCoordinateSize.x - /*2 **/ txtw).roundToInt()
                                }

                                txtStart = (mOrgPoint.x + leftAdd).toFloat()

                                canvas?.drawText(
                                    leftText,
                                    (mOrgPoint.x + leftAdd).toFloat(),
                                    (mOrgPoint.y + mFontMeasuredHeight /*+ (fh - mXYTextPaint.getFontMetrics().descent)*/).toFloat(),
                                    mXYTextPaint
                                )
                            }
                        }
                        if (dataItem.getSleepType() === SleepItem.END_SLEEP_TYPE) {
//                            LogUtils.i(" END_SLEEP_TYPE " + DateTimeUtils.s_long_2_str(dataItem.getStartTime(), DateTimeUtils.day_hm_format));
                            val rightText: String = DateTimeUtils.toDateString(
                                Date(dataItem.getStartTime()),
                                "HH:mm"
                            ) + resources.getString(R.string.view_wake)
                            val txtw = mXYTextPaint.measureText(rightText).toInt()

                            if (((txtEnd - txtw) <= (txtStart + txtw)) || (txtEnd - txtw) <= 0) {
                                txtEnd = txtStart.toFloat() + txtw
                                txtEndHeight = mOrgPoint.y.toFloat()
                            } else {
                                txtEndHeight = mOrgPoint.y + mFontMeasuredHeight.toFloat()
                            }

                            if (txtEndHeight <= 0) {
                                txtEndHeight = mOrgPoint.y + mFontMeasuredHeight.toFloat()
                            }

                            canvas?.drawText(
                                rightText,
                                (txtEnd - txtw).toFloat(),
                                txtEndHeight,
                                mXYTextPaint
                            )
                        }
                    }
                }
            }
        }
    }

    private fun drawScrollLine(canvas: Canvas?, point: List<MainViewSize>) {

        var startp: MainViewSize? = null
        var endp: MainViewSize? = null

        mAreaPaint.style = Paint.Style.FILL

        for (i in 0 until (point.size - 1)) {

            LogUtils.i(" drawScrollLine " + i + " data " + point[i].x)

            if ((i + 1) >= point.size) continue

            startp = point[i]
            endp = point[i + 1]

            var oldStartX = startp.x
            var oldEndX = endp.x

            val wt = (startp.x + endp.x) / 2
            val p3 = Point()
            val p4 = Point()
            p3.y = startp.y.toInt()
            p3.x = wt.toInt()
            p4.y = endp.y.toInt()
            p4.x = wt.toInt()
            val path = Path()
            path.moveTo(startp.x, startp.y)
            path.cubicTo(
                p3.x.toFloat(),
                p3.y.toFloat(),
                p4.x.toFloat(),
                p4.y.toFloat(),
                endp.x,
                endp.y
            )
            canvas?.drawPath(path, mAreaPaint)

            startp.x = oldStartX
            endp.x = oldEndX
        }

        mAreaPaint.style = Paint.Style.STROKE
    }

    private fun drawScrollLine(canvas: Canvas?, startp: MainViewSize, endp: MainViewSize) {

//        var startp : MainViewSize? = null
//        var endp  : MainViewSize? = null

        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.parseColor("#3E3E58")
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

//        for (i in 0 until (point.size - 1)) {

//            LogUtils.i(" drawScrollLine " + i + " data " + point[i].x)

//            if ((i + 1) >= point.size) continue

//            startp = point[i]
//            endp = point[i + 1]

        var oldStartX = startp.x
        var oldEndX = endp.x

        val wt = (startp.x + endp.x) / 2
        val p3 = Point()
        val p4 = Point()
        p3.y = startp.y.toInt()
        p3.x = wt.toInt()
        p4.y = endp.y.toInt()
        p4.x = wt.toInt()
        val path = Path()
        path.moveTo(startp.x, startp.y)
        path.cubicTo(
            p3.x.toFloat(),
            p3.y.toFloat(),
            p4.x.toFloat(),
            p4.y.toFloat(),
            endp.x,
            endp.y
        )
        canvas?.drawPath(path, mPaint)

        startp.x = oldStartX
        endp.x = oldEndX
//        }
    }

    /**
     * 设置时间范围
     *
     * @param start
     * @param end
     */
    fun setTimeRange(start: Long, end: Long, bottomTextCount: Int) {
        var end = end
        if (end <= start) {
            end = DateTimeUtils.AddMinute(Date(start), 30).getTime()
        }
        if (end > start) {
            mStartTime = start
            mEndTime = end
            postInvalidate()
        }
    }

    fun setTimeRange(start: Long, end: Long) {
        var end = end
        if (end <= start) {
            end = DateTimeUtils.AddMinute(Date(start), 30).getTime()
        }
        if (end > start) {
            mStartTime = start
            mEndTime = end
            postInvalidate()
        }
    }

    /**
     * 时间是否在指定范围
     *
     * @param time
     * @return
     */
    open fun timeInRange(time: Long): Boolean {
        return time >= mStartTime && time <= mEndTime
    }

    /**
     * 获取文字的高度
     *
     * @param paint
     * @return
     */
    open fun getFontHeight(paint: Paint): Int {
        return paint.getFontMetrics(paint.fontMetrics).toInt()
    }

    /**
     * 获取触摸点
     */
    open fun getDataItemByMouseX(x: Int) {
        for (i in mDataList.indices) {
            val dataItem: SleepItem = mDataList[i]
            val with = x + lastOffsetX
            if (dataItem.currentDrawRect != null && !dataItem.currentDrawRect.isEmpty()) {
                if (with > dataItem.currentDrawRect.left && with <= dataItem.currentDrawRect.right) {
                    mTouchRectX = x.toFloat()
                    mTouchX = with
                    mDrawPos = i
                    mTouchDataItem = dataItem
                    break
                }
            }
        }
    }

    /**
     * 画线
     *
     * @param canvas
     */
    fun drawShiLine(canvas: Canvas?) {
        if (mCoordinateSize == null || mTouchDataItem == null) return
        var totalLen = mCoordinateSize.y - mXYTextHeight
        if (totalLen < 0) {
            totalLen = 0f
        }
        val len = 1f / 10f * totalLen.toFloat()
        if (mTouchX > 0) {
            canvas?.drawLine(
                mTouchX.toFloat(), (mOrgPoint.y - totalLen).toFloat(), mTouchX.toFloat(),
                mOrgPoint.y - len, mTouchPaint
            )
        } /*else {
            canvas?.drawLine(
                (mOrgPoint.x + mSplitWidth * (mDrawPos + 1)).toFloat(),
                (mOrgPoint.y - totalLen).toFloat(),
                (
                        mOrgPoint.x + mSplitWidth * (mDrawPos + 1)).toFloat(),
                mOrgPoint.y - len,
                mTouchPaint
            )
        }*/
        if (listener != null) {
            listener?.onSleepTouchBar(mTouchDataItem, mTouchRectX)
        }
    }


    override fun computeScroll() {
        super.computeScroll()
//        LogUtils.i(" computeScroll lastOffsetX == " + lastOffsetX + " offsetX == " + offsetX);
        //判断Scroller是否执行完毕
//        if (mScroller.computeScrollOffset()) {
//            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//            //通过重绘不断来调用computeScroll
//            invalidate();
//        }
    }


    var mLastX = 0
    var mLastY = 0


    //处理触摸事件的分发 是从dispatchTouchEvent开始的
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        //触摸点相对于其所在组件原点的X坐标
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN ->                 //手按下屏幕,父布局没有作用,进行拦截
                //让父布局ViewPager禁用拦截功能,从而让父布局忽略事件后的一切行为
                //requestDisallowInterceptTouchEvent(true)表示：
                //getParent() 获取到父视图 父视图不拦截触摸事件
                //孩子不希望父视图拦截触摸事件
            {
                getDataItemByMouseX(x)

                if (mDataList == null || mDataList.isEmpty()) {
                    getParent().requestDisallowInterceptTouchEvent(false)
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true)
                }
            }


            MotionEvent.ACTION_MOVE -> {
                //水平移动的增量
                val deltaX = x - mLastX
                val deltaY = y - mLastY

//                if (getScrollX() + mViewWidth >= mCoordinateSize.x){
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }

                //Math.abs绝对值
                if (Math.abs(deltaX) < Math.abs(deltaY)) {
//                    //当水平增量大于竖直增量时，表示水平滑动，此时需要父View去处理事件，所以不拦截
//                    //让父布局ViewPager使用拦截功能,从而让父布局完成事件后的一切行为
//                    //requestDisallowInterceptTouchEvent(false)表示：
//                    //孩子希望父视图拦截触摸事件,也就是让CustomViewPager拦截触摸事件，进行左右滑动
                    getParent().requestDisallowInterceptTouchEvent(false)
                }
            }

            else -> {}
        }
        mLastX = x
        mLastY = y
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastOffsetX = getScrollX()
                lastX = event.x.toInt()
                lastY = event.y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                offsetX = x - lastX
                var distance = -offsetX + lastOffsetX
                if (distance <= 0) {
                    distance = 0
                }
                if (distance + mViewWidth > mCoordinateSize.x) {
                    distance = (mCoordinateSize.x - mViewWidth + mTextWidth).roundToInt()
                }
                scrollTo(distance, 0)
            }

            MotionEvent.ACTION_UP -> if (mScroller != null) {
                mScroller!!.startScroll(
                    getScrollX(),
                    getScrollY(),
                    -getScrollX(),
                    -getScrollY()
                )
                //                    LogUtils.i(" ACTION_UP ScrollX " + getScrollX() + " ScrollY " + getScrollY() + " -getScrollX " + -getScrollX() + " -getScrollY " + -getScrollY());
                invalidate()
            }
        }
        return true
    }

    /**
     * 清除触摸Item
     */
    fun cleanTouchItem() {
        mTouchDataItem = null
        mDataList.clear()
        postInvalidate()
    }

    fun setTouchDataItem(item: SleepItem?) {
        if (item == null) return
        mDrawPos = -1
        mTouchDataItem = item
        for (i in mDataList.indices) {
            val dataItem: SleepItem = mDataList[i]
//            if (item.startTime === dataItem.startTime && mDrawPos < 0) {
            mDrawPos = 0
            mTouchDataItem = dataItem
            mTouchRectX =
                ((dataItem.currentDrawRect.left + dataItem.currentDrawRect.right) / 2f).toFloat()
            break
//            }
        }
        postInvalidate()
    }

    fun addDateItem(item: SleepItem) {
        mDataList.add(item)
    }

    fun addDateItem(item: List<SleepItem>?) {
        mDataList.addAll(item!!)
    }

    fun setBottomTextList(mBottomTextList: List<String>?) {
        this.mBottomTextList.clear()
        if (mBottomTextList.isNullOrEmpty()) return
        this.mBottomTextList.addAll(mBottomTextList)
        postInvalidate()
    }


    fun getDataList(): List<SleepItem>? {
        return mDataList
    }

    var listener: OnSleepTouchBarListener? = null

    fun setSleepTouchListener(listener: OnSleepTouchBarListener) {
        this.listener = listener
    }

    /**
     * 触摸监听
     */
    interface OnSleepTouchBarListener {
        fun onSleepTouchBar(item: SleepItem?, pos: Float)
    }
}