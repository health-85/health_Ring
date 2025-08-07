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
import android.view.View
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.MainViewSize
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/13 10:17
 * @UpdateRemark:   首页运动
 */
class MainDataView : View {

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
    private var mMaxData = 1000f

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
    private var isGradient = false

    private var mStyle = DataStyle.BAR

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
            context.obtainStyledAttributes(attrs, R.styleable.MainDataView, defStyleAttr, 0)
        mStartColor =
            typedArray.getColor(R.styleable.MainDataView_startColor, Color.parseColor("#F9AC33"))
        mEndColor =
            typedArray.getColor(R.styleable.MainDataView_endColor, Color.parseColor("#F66B2A"))
        mBarColor =
            typedArray.getColor(R.styleable.MainDataView_barColor, Color.parseColor("#6DFFE9"))
        mLineColor =
            typedArray.getColor(R.styleable.MainDataView_lineColor, Color.parseColor("#FE475A"))
        mDotColor =
            typedArray.getColor(R.styleable.MainDataView_dotColor, Color.parseColor("#FE475A"))
        isGradient = typedArray.getBoolean(R.styleable.MainDataView_isShowGradient, false)
        mStyle = typedArray.getInt(R.styleable.MainDataView_style, DataStyle.BAR)
        mDotRadius = typedArray.getFloat(
            R.styleable.MainDataView_dotRadius,
            DeviceUtil.dip2px(context, 5f).toFloat()
        )
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

        mXpos = leftPadding + textMaxWidth - textMinWidth + DeviceUtil.dip2px(context, 12f)
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
        canvas.drawText(
            "${mMaxData.toInt()}",
            leftPadding.toFloat(),
            (mXYTextHeight + yPadding).toFloat(),
            mXYTextPaint
        )
        //画最小的数值
        canvas.drawText(
            "${mMinData.toInt()}",
            leftPadding + textMaxWidth - textMinWidth,
            (mOrgPoint.y - DeviceUtil.dip2px(context, 18f)).toFloat(),
            mXYTextPaint
        )

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
        //画5条竖线
        var lineWidth = mDataWidth / 8
        for (i in 0 until 4) {
            canvas.drawLine(
                mXpos + lineWidth + i * itemWidth,
                0f,
                mXpos + lineWidth + i * itemWidth,
                mOrgPoint.y.toFloat(),
                mXLinePaint
            )
        }
    }

    //画数据
    private fun drawData(canvas: Canvas) {

        when (mStyle) {
            DataStyle.BAR -> {
                drawBar(canvas)
            }

            DataStyle.LINE -> {
                drawLine(canvas)
            }

            DataStyle.DOT -> {
                drawDot(canvas)
            }

            DataStyle.Cub_LINE -> {
                drawCubicLine(canvas)
            }
        }
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

//                LogUtils.i(" top $top  curLen $curLen dataItem ${dataItem?.data}")
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

    private fun drawLine(canvas: Canvas) {

        if (mDataList == null || mDataList.isEmpty()) return

        var curLen = 0f
        var barWidth = mDataWidth / mDataList.size

        val pointList = mutableListOf<Point>()

//        var index = 0
//        val path = Path()

        for (i in mDataList.indices) {
            val dataItem = mDataList[i]
            pointList.add(calculate(dataItem))
//            LogUtils.i(" $i ${Gson().toJson(dataItem)}")
//            val centerX = (mXpos + barWidth * (i + 1) - barWidth / 2f).toInt()
//            if (dataItem.data > 0) {

//                var len = mMaxData - mMinData
//                if (len <= 0) len = 1f
//                curLen = (dataItem.data - mMinData) * (mDataHeight / len)
//                var top = (mYpos - curLen).toInt()
//                if (top <= 0) {
//                    top = ((barWidth / 2).toInt())
//                }
//                var size = MainViewSize(
//                    centerX.toFloat(),
//                    top.toFloat(),
//                    dataItem.data,
//                    dataItem.time,
//                    dataItem.isOneMin
//                )
//                list.add(size)

//                if (index == 0){
//                    index++
//                    path.moveTo(centerX.toFloat(), mYpos)
//                    path.lineTo(centerX.toFloat(), top.toFloat())
//                }else if (i < mDataList.size - 1){
//                    if (isDrawPoint(dataItem, mDataList[i + 1])){
//                        path.lineTo(centerX.toFloat(), top.toFloat())
//                    }else{
//                        path.lineTo(centerX.toFloat(), mYpos)
//                        path.moveTo(centerX.toFloat(), top.toFloat())
//                    }
//                }else{
//                    path.lineTo(centerX.toFloat(), mYpos)
//                }

//                val rectTouch = Rect(
//                    (centerX - barWidth / 2).toInt(), 0,
//                    (centerX + barWidth / 2).toInt(), mYpos.toInt()
//                )
//                dataItem.currentDrawRect = rectTouch
//            }
//            path.close()
//            canvas.drawPath(path, mPaint)
            if (pointList.size <= 0) return
            mPaint.color = mLineColor
            mPaint.strokeWidth = DeviceUtil.dip2px(context, 0.5f).toFloat()
            var allpointsame = true
            for (i in pointList.indices) {
                val currp: Point = pointList.get(i)
//                LogUtils.i(" $i 数据 ${Gson().toJson(currp)}")
                if (i + 1 < pointList.size && isDrawPoint(mDataList.get(i), mDataList.get(i + 1))) {
                    val nextpoint: Point = pointList.get(i + 1)
                    if (nextpoint.y != currp.y || nextpoint.x != currp.x) {
                        allpointsame = false
                    }
                    canvas.drawLine(
                        currp.x.toFloat(),
                        currp.y.toFloat(),
                        nextpoint.x.toFloat(),
                        nextpoint.y.toFloat(),
                        mPaint
                    )
                } else {
//                    val rect = RectF(
//                        currp.x.toFloat(), currp.y.toFloat(), (currp.x + DeviceUtil.dip2px(
//                            context, 1f)).toFloat(), (currp.y + DeviceUtil.dip2px(context, 1f)).toFloat()
//                    )
//                    canvas.drawRoundRect(
//                        rect,
//                        DeviceUtil.dip2px(context, 1f).toFloat(),
//                        DeviceUtil.dip2px(context, 1f).toFloat(),
//                        mPaint
//                    )
                }
            }
//            if (allpointsame) {
//                val currp: MainViewSize = list.get(0)
//                canvas.drawLine(
//                    currp.x.toFloat(),
//                    mYpos.toFloat(),
//                    currp.x.toFloat(),
//                    currp.y.toFloat(),
//                    mPaint
//                )
//            }
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

    fun setMotionData(min: Float, max: Float, list: List<MainViewItem>?) {
        if (min > 0) {
            this.mMinData = 0f
        } else {
            this.mMinData = min
        }
        if (max < 1000) {
            this.mMaxData = 1000f
        } else if (max < 1500) {
            this.mMaxData = 1500f
        } else if (max < 2000) {
            this.mMaxData = 2000f
        } else {
            this.mMaxData = max
        }
        mDataList.clear()
        if (!list.isNullOrEmpty()) {
            mDataList.addAll(list)
        }
        postInvalidate()
    }

    fun setHeartData(min: Float, max: Float, list: List<MainViewItem>?) {
        if (min > 60) {
            this.mMinData = 60f
        } else if (min > 40) {
            this.mMinData = 40f
        } else {
            this.mMinData = 0f
        }
        if (max < 100) {
            this.mMaxData = 100f
        } else if (max < 120) {
            this.mMaxData = 120f
        } else if (max < 150) {
            this.mMaxData = 150f
        } else if (max < 200) {
            this.mMaxData = 200f
        } else {
            this.mMaxData = max
        }
        mDataList.clear()
        if (!list.isNullOrEmpty()) {
            mDataList.addAll(list)
        }
        postInvalidate()
    }

    fun setOxData(min: Float, max: Float, list: List<MainViewItem>?) {
        if (min > 80) {
            this.mMinData = 80f
        } else if (min > 70) {
            this.mMinData = 70f
        } else if (min > 60) {
            this.mMinData = 60f
        } else if (min > 50) {
            this.mMinData = 50f
        } else {
            this.mMinData = min
        }
        this.mMaxData = 100f
        mDataList.clear()
        if (!list.isNullOrEmpty()) {
            mDataList.addAll(list)
        }
        postInvalidate()

    }
}