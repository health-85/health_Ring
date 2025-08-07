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
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.gson.Gson
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.MainViewSize
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.ViewDataUtil
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 20:54
 * @UpdateRemark:
 */
class MotionStepView : View {

    //文字长度
    private var mTextWidth = 0f

    //测量文字高度
    private var mXYTextHeight = 0

    //线条
    private val mPaint = Paint()

    //画X轴文字和横线
    private val mXLinePaint = Paint()

    //画X轴和Y轴文字
    private val mXYTextPaint: Paint = TextPaint()

    //图片宽高
    private val mOrgPoint = Point(0, 0)

    //最大数据
    private var mMaxData = 1500f

    //最小数据
    private var mMinData = 0f

    //起点
    private var mXpos = 0f
    private var mYpos = 0f

    //Item宽度
    private var mDataWidth = 0f
    private var mDataHeight = 0f

    private var bottomPadding = 0
    private var rightPadding = 0
    private var leftPadding = 0
    private var yPadding = 0
    private var textTopPadding = 0

    private var textMaxWidth = 0f
    private var textMinWidth = 0f

    private var mDotRadius = 0f

    //数据信息
    private val mDataList = mutableListOf<MainViewItem>()

    private var mStartColor = 0
    private var mEndColor = 0
    private var mBarColor = 0
    private var mLineColor = 0
    private var mDotColor = 0
    private var isGradient = true

    private var mStyle = DataStyle.BAR

    //触摸点
    private var mDrawPos = 0

    //触摸Item
    private var mTouchDataItem: MainViewItem? = null

    private var itemTouchListener: OnItemTouchListener? = null

    interface OnItemTouchListener {
        fun onItemTouchListener(item: MainViewItem?, pos: Float)
    }


    annotation class DataStyle {
        companion object {
            var BAR = 0
            var LINE = 1
            var DOT = 2
            var Cub_LINE = 3
        }
    }

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

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MainStepView, defStyleAttr, 0)
        mStartColor =
            typedArray.getColor(R.styleable.MainStepView_stepStartColor, Color.parseColor("#F9AC33"))
        mEndColor =
            typedArray.getColor(R.styleable.MainStepView_stepEndColor, Color.parseColor("#F66B2A"))
        mBarColor =
            typedArray.getColor(R.styleable.MainStepView_stepBarColor, Color.parseColor("#6DFFE9"))
        mLineColor =
            typedArray.getColor(R.styleable.MainStepView_stepLineColor, Color.parseColor("#FE475A"))
        isGradient = typedArray.getBoolean(R.styleable.MainStepView_stepIsShowGradient, true)
        typedArray.recycle()

        mXYTextPaint.isAntiAlias = true
        mXYTextPaint.color = Color.parseColor("#3E3E58")
        mXYTextPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()
        mXYTextPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        mXLinePaint.color = Color.parseColor("#3E3E58")
        mXLinePaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        bottomPadding = DeviceUtil.dip2px(context, 5f)
        rightPadding = DeviceUtil.dip2px(context, 25f)
        leftPadding = DeviceUtil.dip2px(context, 6f)
        yPadding = DeviceUtil.dip2px(context, 10f)
        textTopPadding = DeviceUtil.dip2px(context, 2f)

        mTextWidth = mXYTextPaint.measureText(mMaxData.toString() + "")

        mXYTextHeight = getFontHeight(mXYTextPaint)
        textMaxWidth = mXYTextPaint.measureText("$mMaxData")
        textMinWidth = mXYTextPaint.measureText("$mMinData")

        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 2f).toFloat()
    }

    //绘制
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initValue()
        //画X轴文字和横线
        drawHorizontalLine(canvas)
        //画数据
        drawData(canvas)
    }

    //初始化特殊值
    private fun initValue() {
        mOrgPoint.x = 0
        mOrgPoint.y = measuredHeight
        if (mOrgPoint.y < 0) mOrgPoint.y = 0

        //X轴起点
        textMaxWidth = mXYTextPaint.measureText("$mMaxData")
        textMinWidth = mXYTextPaint.measureText("$mMinData")

        LogUtils.i(" mMaxData $mMaxData ")

        mXpos = leftPadding + textMaxWidth + DeviceUtil.dip2px(context, 12f)
        mYpos = (mOrgPoint.y - bottomPadding - mXYTextHeight - textTopPadding).toFloat()
        //数据宽、高
        mDataWidth = measuredWidth - mXpos - rightPadding
        mDataHeight =
            (mOrgPoint.y - bottomPadding - mXYTextHeight - textTopPadding - DeviceUtil.dip2px(
                context,
                19f
            )).toFloat()
    }

    //画X轴文字和横线
    private fun drawHorizontalLine(canvas: Canvas) {

        //画最大的数值
        val len = (mMaxData - mMinData) / 3
        for (i in 0..len.toInt()) {
            val yItemHeight = mYpos - i * mDataHeight / 3
            val txt: Int = (mMinData + len * i).toInt()
            var textWidth = mXYTextPaint.measureText(txt.toString())
            var yHeight = yItemHeight + getBaseline(mXYTextPaint)
            canvas.drawText(
                "$txt",
                leftPadding + textMaxWidth - textWidth,
                yHeight,
                mXYTextPaint
            )
        }

        //画X轴文字
        var itemWidth = mDataWidth / 4
        var textWidth = mXYTextPaint.measureText("00")

        canvas.drawText(
            "00",
            mXpos - DeviceUtil.dip2px(context, 4f),
            (mOrgPoint.y - bottomPadding).toFloat(),
            mXYTextPaint
        )
        canvas.drawText(
            "06",
            mXpos + itemWidth - textWidth / 2,
            (mOrgPoint.y - bottomPadding).toFloat(),
            mXYTextPaint
        )
        canvas.drawText(
            "12",
            mXpos + 2 * itemWidth - textWidth / 2,
            (mOrgPoint.y - bottomPadding).toFloat(),
            mXYTextPaint
        )
        canvas.drawText(
            "18",
            mXpos + 3 * itemWidth - textWidth / 2,
            (mOrgPoint.y - bottomPadding).toFloat(),
            mXYTextPaint
        )
        canvas.drawText(
            "00",
            mXpos + 4 * itemWidth - textWidth + DeviceUtil.dip2px(context, 4f),
            (mOrgPoint.y - bottomPadding).toFloat(),
            mXYTextPaint
        )

        //底部横线
//        canvas.drawLine(
//            mXpos,
//            (mOrgPoint.y - bottomPadding - mXYTextHeight - textTopPadding).toFloat(),
//            mDataWidth,
//            (mOrgPoint.y - bottomPadding - mXYTextHeight - textTopPadding).toFloat(),
//            mXLinePaint
//        )
    }

    //画数据
    private fun drawData(canvas: Canvas) {
        drawBar(canvas)
        drawShiLine(canvas)
    }

    private fun drawBar(canvas: Canvas) {
        val linearGradient = LinearGradient(
            0f,
            0f,
            0f,
            mOrgPoint.y.toFloat(),
            intArrayOf(mStartColor, mEndColor),
            floatArrayOf(0.5f, 0.9f),
            Shader.TileMode.CLAMP
        )
        var barWidth = mDataWidth / 24
        for (i in 0 until 24) {
            var dataItem: MainViewItem? = null
            if (mDataList.size > i) {
                dataItem = mDataList[i]
            }
            var top = 0
            var curLen = 0f
            var high = DeviceUtil.dip2px(context, 4f)
            var default = (mYpos - high).toInt()
            if (dataItem != null && dataItem.data > 0) {
                var len = mMaxData - mMinData
                if (len <= 0) len = 1f
                curLen = (dataItem.data - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                    context,
                    4f
                )
                top = (mYpos - curLen).toInt()
                if (isGradient) {
                    mPaint.shader = linearGradient
                } else {
                    mPaint.shader = null
                    mPaint.color = mBarColor
                }

                LogUtils.i(" top $top  curLen $curLen dataItem ${dataItem?.data}")
            } else {
                top = default
                mPaint.color = Color.parseColor("#3E3E58")
                mPaint.shader = null
            }
            mPaint.style = Paint.Style.FILL

            if (top > default) {
                top = default
            }

            if (top < high) {
                top = high
            }

            val centerX = (mXpos + barWidth * (i + 1) - barWidth / 2f).toInt()
            val rectF = RectF(
                (centerX - barWidth / 4).toFloat(), top.toFloat(),
                (centerX + barWidth / 4).toFloat(), mYpos.toFloat()
            )

//            LogUtils.i(" barWidth $barWidth mXpos $mXpos centerX $centerX")

            canvas.drawRoundRect(
                rectF,
                (barWidth / 4).toFloat(),
                (barWidth / 4).toFloat(),
                mPaint
            )

            val rect = Rect(
                (centerX - barWidth / 4).toInt(), (top + barWidth / 4).toInt(),
                (centerX + barWidth / 4).toInt(), mYpos.toInt()
            )
            canvas.drawRect(rect, mPaint)

            if (dataItem != null) {
                val rectTouch = Rect(
                    (centerX - barWidth / 2).toInt(), 0,
                    (centerX + barWidth / 2).toInt(), mYpos.toInt()
                )
                dataItem.currentDrawRect = rectTouch //当前的绘制矩形区域
            }

        }
    }

    private fun drawShiLine(canvas: Canvas) {
        if (mTouchDataItem == null || mDataList.isNullOrEmpty()) return

        val mSplitWidth = mDataWidth / mDataList.size

        val x = mXpos + mSplitWidth * (mDrawPos + 1) - mSplitWidth / 2f

        LogUtils.i(" getDataItemByMouseX drawShiLine x $x mDrawPos $mDrawPos ")

        var startColor = ViewDataUtil.getOxDataColor(mTouchDataItem?.maxData?.toInt() ?: 0)
        var endColor = ViewDataUtil.getOxDataColor(mTouchDataItem?.minData?.toInt() ?: 0)

        val padding = DeviceUtil.dip2px(context, 5f)
        val lineWith = DeviceUtil.dip2px(context, 0.2f)

        var len = mMaxData - mMinData
        if (len <= 0) len = 1f

        var curMaxLen =
            (mTouchDataItem!!.maxData - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                context,
                4f
            )
        var topMax = (mYpos - curMaxLen).toInt() - padding
        if (topMax <= 0) topMax = 0

        var curMinLen =
            (mTouchDataItem!!.minData - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                context,
                4f
            )
        var bottomMin = (mYpos - curMinLen).toInt() + padding
        if (bottomMin >= mYpos) bottomMin = mYpos.roundToInt()

        mXLinePaint.color = startColor
        val linearGradient = LinearGradient(
            x,
            0f,
            x,
            mYpos,
            Color.parseColor("#11FFBC76"),
            Color.parseColor("#FFBC76"),
            Shader.TileMode.CLAMP
        )
        mXLinePaint.shader = linearGradient
        canvas.drawLine(
            x,
            0f,
            x,
            mYpos, mXLinePaint
        )
        itemTouchListener?.onItemTouchListener(mTouchDataItem, x)
    }

    private fun getBaseline(p: Paint): Float {
        val fontMetrics: Paint.FontMetrics = p.fontMetrics
        return (fontMetrics.descent - fontMetrics.ascent) / 2f - fontMetrics.descent
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN) {
            getDataItemByMouseX(event.x.toInt())
        } else {
            if (event.action == MotionEvent.ACTION_MOVE) {
                getDataItemByMouseX(event.x.toInt())
            }
        }
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
//            mouseTouchDataItem = null;
        }
        postInvalidate()
        return true
    }

    private fun getDataItemByMouseX(x: Int) {
        for (i in mDataList.indices) {
            val dataItem = mDataList[i]
            if (!dataItem.currentDrawRect.isEmpty) {
                LogUtils.i(
                    " getDataItemByMouseX x $x left ${dataItem.currentDrawRect.left} " +
                            "right ${dataItem.currentDrawRect.right}"
                )
                if (x >= dataItem.currentDrawRect.left && x < dataItem.currentDrawRect.right) {
                    mDrawPos = i
                    mTouchDataItem = dataItem
                    LogUtils.i(
                        " getDataItemByMouseX mDrawPos $mDrawPos mTouchDataItem ${
                            Gson().toJson(
                                mTouchDataItem
                            )
                        }"
                    )
                    break
                }
            }
        }
    }


    private fun drawCubicLine(canvas: Canvas) {
        if (mDataList == null || mDataList.isEmpty()) return
        var curLen = 0f
        var barWidth = mDataWidth / mDataList.size
        val list: MutableList<MainViewSize> = ArrayList()
        for (i in mDataList.indices) {
            val dataItem = mDataList[i]
            val centerX = (mXpos + barWidth * (i + 1) - barWidth / 2f).toInt()
            if (dataItem.data > 0) {
                var len = mMaxData - mMinData
                if (len <= 0) len = 1f
                curLen = (dataItem.data - mMinData) * (mDataHeight / len)
                var top = (mYpos - curLen).toInt()
                if (top <= 0) {
                    top = ((barWidth / 2).toInt())
                }
                var size = MainViewSize(centerX.toFloat(), top.toFloat(), dataItem.data)
                list.add(size)

                val rectTouch = Rect(
                    (centerX - barWidth / 2).toInt(), 0,
                    (centerX + barWidth / 2).toInt(), mYpos.toInt()
                )
                dataItem.currentDrawRect = rectTouch
            }
        }
        if (list.size > 1) {
            drawScrollLine(canvas, list)
        } else {
            drawDot(canvas)
        }
    }

    private fun calculate(dataItem: MainViewItem): Point {

        val point = Point(0, 0)

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(dataItem.time)).time

        var textWidth = mXYTextPaint.measureText("00")

        var curTimer = dataItem.time - startTime
        val timeLen: Long = 24 * 60 * 60 * 1000

        val width = mDataWidth

        if (curTimer in 0..timeLen) {
            point.x =
                (mXpos + (curTimer.toDouble() / timeLen.toDouble() * width)).toInt()
        }
        val dataLen: Long = (mMaxData - mMinData).toLong()
        var datarela: Double = (dataItem.data - mMinData).toDouble()
        if (datarela < 0) {
            datarela = 0.0
        }
        if (datarela > dataLen) {
            datarela = dataLen.toDouble()
        }
        if (datarela >= 0 && datarela <= dataLen) {
            point.y = (mYpos - (datarela / dataLen.toDouble() * mDataHeight)).toInt()
        }
        return point
    }

    private fun drawScrollLine(canvas: Canvas, point: List<MainViewSize>) {

        var startp = MainViewSize()
        var endp = MainViewSize()

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 0.5f).toFloat()

        for (i in 0 until (point.size - 1)) {

            LogUtils.i(" drawScrollLine " + i + " data " + point[i].data)

            if ((i + 1) >= point.size) continue

            startp = point[i]
            endp = point[i + 1]

            var startColor = mLineColor
            var endColor = mLineColor

            var oldStartX = startp.x
            var oldEndX = endp.x

            val linearGradient = LinearGradient(
                startp.x,
                startp.y,
                endp.x,
                endp.y,
                intArrayOf(startColor, endColor),
                floatArrayOf(0.5f, 0.9f),
                Shader.TileMode.CLAMP
            )
            mPaint.shader = linearGradient

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
            canvas.drawPath(path, mPaint)

            startp.x = oldStartX
            endp.x = oldEndX
        }
    }

    private fun drawDot(canvas: Canvas) {
        if (mDataList == null || mDataList.size <= 0) return
        var barWidth = mDataWidth / 24
        for (i in mDataList.indices) {
            val dataItem = mDataList[i]
            val centerX = (mXpos + barWidth * (i + 1) - barWidth / 2f).toInt()
            mPaint.color = mDotColor
            if (dataItem.data > 0) {
                var len = mMaxData - mMinData
                if (len <= 0) len = 1f
                var curLen = (dataItem.data - mMinData) * (mDataHeight / len)
                var top = (mYpos - curLen).toInt()
                if (top <= 0) {
                    top = ((barWidth / 2).toInt())
                }
                canvas.drawCircle(
                    centerX.toFloat(),
                    top.toFloat(),
                    mDotRadius.toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun isDrawPoint(curItem: MainViewItem, nextItem: MainViewItem): Boolean {
        if (curItem.isOneMin) {
            if (nextItem.time - curItem.time <= 5 * 60 * 1000) {
                return true
            }
        } else {
            if (nextItem.time - curItem.time <= 15 * 60 * 1000) {
                return true
            }
        }
        return false
    }

    /**
     * 获取文字的高度
     *
     * @param paint
     * @return
     */
    private fun getFontHeight(paint: Paint): Int {
        return paint.getFontMetrics(paint.fontMetrics).toInt()
    }

    fun setTouchDataItem(item: MainViewItem?) {
        if (item == null) {
            mDrawPos = -1
            mTouchDataItem = null
            return
        }
        mDrawPos = -1
        mTouchDataItem = item
        for (i in mDataList.indices) {
            val dataItem: MainViewItem = mDataList[i]
            if (TextUtils.equals(item.showTimeString, dataItem.showTimeString) && mDrawPos < 0){
                mDrawPos = i
                mTouchDataItem = dataItem
                break
            }
        }
        postInvalidate()
    }

    fun setOnItemTouchListener(onItemTouchListener: OnItemTouchListener){
        itemTouchListener = onItemTouchListener
    }

    fun setMotionData(min: Float, max: Float, list: List<MainViewItem>?) {
        if (min > 0) {
            this.mMinData = 0f
        } else {
            this.mMinData = min
        }
        if (max <= 1500) {
            this.mMaxData = 1500f
        } else {
            var temp = 1500f
            while (temp < max) {
                temp += 300f
            }
            this.mMaxData = temp
        }
        mDataList.clear()
        if (!list.isNullOrEmpty()) {
            mDataList.addAll(list)
        }
        postInvalidate()
    }

}