package com.healthy.rvigor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
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
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/7/30 16:19
 * @UpdateRemark:
 */
class MindView : View {

    //文字长度
    private var mTextWidth = 0f

    //测量文字高度
    private var mXYTextHeight = 0

    //线条
    private val mPaint = Paint()

    //画X轴文字和横线
    private val mXLinePaint = Paint()

    private val touchPaint = Paint()

    //画X轴和Y轴文字
    private val mXYTextPaint: Paint = TextPaint()

    //图片宽高
    private val mOrgPoint = Point(0, 0)

    //最大数据
    private var mMaxData = 200f

    //最小数据
    private var mMinData = 40f

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

    private var mBarWidth = 0f

    private var mStyle = DataStyle.BAR

    private var mStartDate: Date? = null
    private var mEndDate: Date? = null
    private var mTimeMode = SpecDateSelectedView.TimeMode.Day

    private var mBottomList: List<String>? = null

    //触摸点
    private var mDrawPos = 0

    //触摸Item
    private var mTouchDataItem: MainViewItem? = null

    //触摸是否可用
    private var mTouchEnable = true

    private var itemTouchListener: OnItemTouchListener? = null

    private val datapoints = mutableListOf<MainViewSize>()

    private var touchX = 0f

    private var touchRectWidth = 0f
    private var touchRectHeight = 0f

    private var mCalendar: Calendar? = null

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
            context.obtainStyledAttributes(attrs, R.styleable.OxDataView, defStyleAttr, 0)
        mStartColor =
            typedArray.getColor(R.styleable.OxDataView_dataStartColor, Color.parseColor("#F9AC33"))
        mEndColor =
            typedArray.getColor(R.styleable.OxDataView_dataEndColor, Color.parseColor("#F66B2A"))
        mBarColor =
            typedArray.getColor(R.styleable.OxDataView_dataBarColor, Color.parseColor("#6DFFE9"))
        mLineColor =
            typedArray.getColor(R.styleable.OxDataView_dataLineColor, Color.parseColor("#FE475A"))
        mDotColor =
            typedArray.getColor(R.styleable.OxDataView_dataDotColor, Color.parseColor("#FE475A"))
        isGradient = typedArray.getBoolean(R.styleable.OxDataView_dataIsShowGradient, false)
        mStyle = typedArray.getInt(R.styleable.OxDataView_dataStyle, DataStyle.BAR)
        mDotRadius = typedArray.getDimension(
            R.styleable.OxDataView_dataDotRadius,
            DeviceUtil.dip2px(context, 5f).toFloat()
        )
        mBarWidth = typedArray.getDimension(
            R.styleable.OxDataView_dataBarWidth,
            DeviceUtil.dip2px(context, 4f).toFloat()
        )
        touchRectWidth = DeviceUtil.dip2px(context, 85f).toFloat()
        touchRectHeight = DeviceUtil.dip2px(context, 40f).toFloat()
        typedArray.recycle()

        mXYTextPaint.isAntiAlias = true
        mXYTextPaint.color = Color.parseColor("#3E3E58")
        mXYTextPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()
        mXYTextPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        mXLinePaint.color = Color.parseColor("#3E3E58")
        mXLinePaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        touchPaint.style = Paint.Style.FILL
        touchPaint.color = Color.parseColor("#802CC9")
        touchPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        rightPadding = DeviceUtil.dip2px(context, 12f)
        leftPadding = DeviceUtil.dip2px(context, 4f)
        bottomPadding = DeviceUtil.dip2px(context, 5f)

        textTopPadding = DeviceUtil.dip2px(context, 5f)
        yPadding = DeviceUtil.dip2px(context, 10f)

        mTextWidth = mXYTextPaint.measureText(mMaxData.toString() + "")

        mXYTextHeight = getFontHeight(mXYTextPaint)
        textMaxWidth = mXYTextPaint.measureText("$mMaxData")
        textMinWidth = mXYTextPaint.measureText("$mMinData")

        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 2f).toFloat()

        mCalendar = Calendar.getInstance()
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

        //文字的宽度
        textMaxWidth = mXYTextPaint.measureText("$mMaxData")
        textMinWidth = mXYTextPaint.measureText("$mMinData")

        LogUtils.i(" mMaxData $mMaxData ")
        //X、Y的起点
        mXpos = leftPadding + textMaxWidth + DeviceUtil.dip2px(context, 3f)
        mYpos = (mOrgPoint.y - bottomPadding - mXYTextHeight - textTopPadding).toFloat()
        //数据宽、高
        mDataWidth = measuredWidth - mXpos - rightPadding
        mDataHeight =
            (mOrgPoint.y - bottomPadding - mXYTextHeight - textTopPadding - DeviceUtil.dip2px(
                context,
                13f
            )).toFloat()
    }

    //画X轴文字和横线
    private fun drawHorizontalLine(canvas: Canvas) {

        mXLinePaint.shader = null
        mXLinePaint.color = Color.parseColor("#3E3E58")
        val pathEffect: PathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f) // 创建虚线样式

        //画最大的数值
        mXYTextPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()
        val len = (mMaxData - mMinData) / 5
        for (i in 0..len.toInt()) {
            val yItemHeight = mYpos - i * mDataHeight / 5
            val txt: Int = (mMinData + len * i).toInt()
            var textWidth = mXYTextPaint.measureText(txt.toString())
            var yHeight = yItemHeight + getBaseline(mXYTextPaint)
            canvas.drawText(
                "$txt",
                leftPadding + textMaxWidth - textWidth,
                yHeight,
                mXYTextPaint
            )

//            if (i == 0) {
//                mXLinePaint.pathEffect = null
//            } else {
//                mXLinePaint.pathEffect = pathEffect // 设置画笔的虚线样式
//            }
//            canvas.drawLine(mXpos, yItemHeight, mXpos + mDataWidth, yItemHeight, mXLinePaint)
        }

        //画X轴文字
        var itemWidth = mDataWidth / 4
        var textWidth = mXYTextPaint.measureText("00")

        canvas.drawText(
            "00",
            mXpos,
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
            mXpos + 4 * itemWidth - textWidth,
            (mOrgPoint.y - bottomPadding).toFloat(),
            mXYTextPaint
        )
    }

    private fun drawBottomText(canvas: Canvas, list: List<String>?) {
        if (list.isNullOrEmpty()) return
        val splitWidth = mDataWidth / list.size
        for (i in list.indices) {
            val bottomTxt = list[i]
            if (!TextUtils.isEmpty(bottomTxt)) {
                val centerX =
                    (mXpos + splitWidth * (i + 1) - splitWidth / 2f).toInt()
                val textWidth = mXYTextPaint.measureText(bottomTxt).toInt()
                val textHeight = getFontHeight(mXYTextPaint)
                var textLeftX = (centerX - textWidth / 2)
                if (textLeftX <= 0) {
                    textLeftX = 0
                }
                val baseLine = getBaseline(mXYTextPaint)
                canvas.drawText(
                    bottomTxt,
                    textLeftX.toFloat(),
                    (mOrgPoint.y - bottomPadding).toFloat(),
                    mXYTextPaint
                )
            }
        }
    }

    //画数据
    private fun drawData(canvas: Canvas) {
        when (mStyle) {
            DataStyle.BAR -> {
                drawBar(canvas)
                drawTouchBar(canvas)
            }

            DataStyle.LINE -> {
                drawLine(canvas)
                drawDayShiLine(canvas)
            }

            DataStyle.DOT -> {
                drawDot(canvas)
            }

            DataStyle.Cub_LINE -> {
                drawCubicLine(canvas)
                drawTouchLine(canvas)
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

    private fun drawRange(canvas: Canvas) {
        if (mBottomList.isNullOrEmpty()) return
        val barWidth = mDataWidth / (mBottomList?.size?.toFloat() ?: 1f)
        if (mBarWidth > barWidth) {
            mBarWidth = barWidth
        }
        for (i in 0 until mBottomList!!.size) {
            var dataItem: MainViewItem? = null
            if (mDataList.size > i) {
                dataItem = mDataList[i]
            }
            var topMax = 0
            var bottomMin = 0

            var high = DeviceUtil.dip2px(context, 4f)
            var default = (mYpos - high).toInt()
            if (dataItem != null && dataItem.data > 0) {
                var len = mMaxData - mMinData
                if (len <= 0) len = 1f
                var curMaxLen =
                    (dataItem.maxData - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                        context,
                        4f
                    )
                topMax = (mYpos - curMaxLen).toInt()

                var curMinLen =
                    (dataItem.minData - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                        context,
                        4f
                    )
                bottomMin = (mYpos - curMinLen).toInt()


                mPaint.shader = null

//                LogUtils.i(" top $top  curLen $curMaxLen dataItem ${dataItem?.data}")

                mPaint.style = Paint.Style.FILL

                if (topMax > default) {
                    topMax = default
                }
                if (topMax < high) {
                    topMax = high
                }

                val centerX = (mXpos + barWidth * (i + 1) - barWidth / 2f).toInt()

                mPaint.color = dataItem.color
                if (dataItem.maxData == dataItem.minData) {
                    canvas.drawCircle(
                        (centerX.toFloat()),
                        topMax.toFloat(),
                        mBarWidth / 2f,
                        mPaint
                    )
                } else {
                    var rectF = RectF(
                        (centerX - mBarWidth / 2f).toFloat(), topMax.toFloat(),
                        (centerX + mBarWidth / 2f).toFloat(), bottomMin.toFloat()
                    )
                    canvas.drawRoundRect(
                        rectF,
                        (mBarWidth / 2f).toFloat(),
                        (mBarWidth / 2f).toFloat(),
                        mPaint
                    )
                }
                if (dataItem != null) {
                    val rectTouch = Rect(
                        (centerX - barWidth / 2f).toInt(), 0,
                        (centerX + barWidth / 2f).toInt(), mYpos.toInt()
                    )
                    dataItem.currentDrawRect = rectTouch //当前的绘制矩形区域
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

    private fun drawTouchLine(canvas: Canvas) {
        var curLen = 0f
        var drawX = 0
        var drawY = 0f
        var lastPoint = 9999999
        var touchItem: MainViewItem? = null
        var barWidth = mDataWidth / mDataList.size
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


                if (touchX > 0) {
                    val touchLen: Float = Math.abs(touchX - centerX)
                    LogUtils.i(" touchItem touchX $touchX centerX $centerX touchLen $touchLen drawX $drawX lastPoint $lastPoint")
                    if (touchLen <= lastPoint) {
                        lastPoint = touchLen.toInt()
                        touchItem = dataItem
                        drawX = centerX
                        drawY = top.toFloat()
                    }
                } else {
                    drawX = centerX
                    drawY = top.toFloat()
                    touchItem = dataItem
                }
            }
        }

        LogUtils.i(" touchItem data ${touchItem?.data} drawX $drawX ")

        if (touchItem != null && drawX > 0) {
            val linearGradient = LinearGradient(
                x,
                0f,
                x,
                mOrgPoint.y.toFloat(),
                intArrayOf(0x7f802CC9, 0x802CC9),
                floatArrayOf(0.5f, 0.9f),
                Shader.TileMode.MIRROR
            )
            touchPaint.shader = linearGradient
            touchPaint.strokeWidth = DeviceUtil.dip2px(context, 0.5f).toFloat()

            canvas.drawLine(
                drawX.toFloat(), 0f,
                drawX.toFloat(), mOrgPoint.y.toFloat(), touchPaint
            )

            touchPaint.shader = null
            touchPaint.style = Paint.Style.FILL
            touchPaint.color = Color.parseColor("#1A192A")
            canvas.drawCircle(
                drawX.toFloat(),
                drawY,
                DeviceUtil.dip2px(context, 3f).toFloat(),
                touchPaint
            )
            touchPaint.style = Paint.Style.STROKE
            touchPaint.color = Color.parseColor("#802CC9")
            canvas.drawCircle(
                drawX.toFloat(),
                drawY,
                DeviceUtil.dip2px(context, 4f).toFloat(),
                touchPaint
            )

            val item: MainViewItem? = touchItem
            itemTouchListener?.onItemTouchListener(item, touchX.toFloat())

            var leftX = drawX - touchRectWidth / 2f
            var rightX = drawX + touchRectWidth / 2f
            if (leftX <= 0) {
                leftX = 0f
                rightX = touchRectWidth
            }
            touchPaint.style = Paint.Style.FILL
            touchPaint.color = Color.parseColor("#292B3C")
            var rectF = RectF(leftX, 0f, rightX, touchRectHeight)
            canvas.drawRoundRect(rectF, 5f, 5f, touchPaint)

            mXYTextPaint.color = Color.parseColor("#FFFFFF")
            mXYTextPaint.textSize = DeviceUtil.sp2px(context, 18f).toFloat()
            var textWidth = mXYTextPaint.measureText(touchItem.data.toInt().toString()).toInt()
            var textHeight = getFontHeight(mXYTextPaint)
            var txtLeft = drawX - textWidth / 2f
            if (txtLeft <= 0) txtLeft = 0f
            canvas.drawText(
                touchItem.data.toInt().toString(),
                txtLeft,
                textHeight.toFloat(),
                mXYTextPaint
            )

            mXYTextPaint.color = Color.parseColor("#7C7D8C")
            mXYTextPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()
            mCalendar?.timeInMillis = touchItem.time
            var hour = mCalendar?.get(Calendar.HOUR_OF_DAY)
            var txt = "$hour:00-${hour?.plus(1)}:00"
            textWidth = mXYTextPaint.measureText(txt).toInt()
            textHeight = getFontHeight(mXYTextPaint)
            txtLeft = drawX - textWidth / 2f
            if (txtLeft <= 0) txtLeft = 0f
            canvas.drawText(
                txt,
                txtLeft,
                touchRectHeight - 10,
                mXYTextPaint
            )
        }
    }

    private fun calculate(dataItem: MainViewItem): MainViewSize {

        val point = MainViewSize(0f, 0f)

        point.item = dataItem

        val startTime = DateTimeUtils.getDateTimeDatePart(Date(dataItem.time)).time

        var textWidth = mXYTextPaint.measureText("00")

        var curTimer = dataItem.time - startTime
        val timeLen: Long = 24 * 60 * 60 * 1000

        val width = mDataWidth

        if (curTimer in 0..timeLen) {
            point.x =
                ((mXpos + (curTimer.toDouble() / timeLen.toDouble() * width)).toFloat())
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
            point.y = ((mYpos - (datarela / dataLen.toDouble() * mDataHeight)).toFloat())
        }

//        val point = Point(0, 0)
//        val startTime = DateTimeUtils.getDateTimeDatePart(Date(dataItem.time)).time
//        val timelen: Long = dataItem.time - startTime
//        val currtimerela: Long = dataItem.time - startTime
//        if (currtimerela >= 0 && currtimerela <= timelen) {
//            point.x =
//                mOrgPoint.x + (currtimerela.toDouble() / timelen.toDouble() * mDataWidth) as Int
//        }
//        val dataLen: Long = (mMaxData - mMinData).toLong()
//        var datarela = dataItem.data - mMinData
//        if (datarela < 0) {
//            datarela = 0.0
//        }
//        if (datarela > dataLen) {
//            datarela = dataLen.toDouble()
//        }
//        if (datarela >= 0 && datarela <= dataLen) {
//            point.y =
//                mOrgPoint.y - (datarela as Double / dataLen.toDouble() * mDataHeight) as Int
//        }
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
        datapoints.clear()
        val path = Path()
        var dataminy = 0
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 0.5f).toFloat()
        if (mDataList.size > 0) {
            for (i in mDataList.indices) {
                val currpoint = calculate(mDataList.get(i))
                if (currpoint.x > 0) {
                    datapoints.add(currpoint)
                }
//
//                LogUtils.i(" drawLine x ${currpoint.x} y ${currpoint.y}")
//
//                if (i == 0) {
//                    dataminy = currpoint.y
//                    path.moveTo(currpoint.x.toFloat(), mYpos.toFloat())
//                    path.lineTo(currpoint.x.toFloat(), currpoint.y.toFloat())
//                } else {
//                    if (dataminy > currpoint.y) {
//                        dataminy = currpoint.y
//                    }
//                }
//                if (i + 1 < mDataList.size) {
//                    val nextPoint: Point = calculate(mDataList.get(i + 1))
//                    if (isDrawPoint(mDataList.get(i), mDataList.get(i + 1))) {
//                        path.lineTo(nextPoint.x.toFloat(), nextPoint.y.toFloat())
//                    } else {
//                        path.lineTo(currpoint.x.toFloat(), mYpos.toFloat())
//                        path.moveTo(
//                            (nextPoint.x + DeviceUtil.dip2px(context, 1f)).toFloat(),
//                            mYpos.toFloat()
//                        )
//                        path.lineTo(
//                            (nextPoint.x + DeviceUtil.dip2px(context, 1f)).toFloat(),
//                            nextPoint.y.toFloat()
//                        )
//                    }
//                }
//                if (i == mDataList.size - 1) {
//                    path.lineTo(currpoint.x.toFloat(), mYpos.toFloat())
//                }
                //                LogUtils.i(" drawData currpoint.x " + currpoint.x + " data " +
//                        datas.get(i).data + " time " + DateTimeUtils.s_long_2_str(datas.get(i).dateTime, DateTimeUtils.f_format));
            }
//            path.close()
//            mPaint.setColor(Color.parseColor("#FFFE475A"))
//            val linearGradient = LinearGradient(
//                0f,
//                dataminy.toFloat(),
//                0f,
//                mYpos.toFloat(),
//                intArrayOf(-0x7f00695f, 0xFFAFBD),
//                floatArrayOf(0.5f, 0.9f),
//                Shader.TileMode.CLAMP
//            )
//            val oldshader: Shader = mPaint.getShader()
//            mPaint.setShader(linearGradient)
//            mPaint.setStyle(Paint.Style.FILL)
//            canvas.drawPath(path, mPaint)
//            mPaint.setShader(null)
            var allpointsame = true
            if (datapoints.size > 0) {
                mPaint.setColor(-0x1b8a6)
                for (i in datapoints.indices) {
                    val currp: MainViewSize = datapoints.get(i)
                    if (i + 1 < datapoints.size) {
                        val nextpoint: MainViewSize = datapoints.get(i + 1)
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
                        val rect = RectF(
                            currp.x.toFloat(), currp.y.toFloat(), (currp.x + DeviceUtil.dip2px(
                                context, 1f
                            )).toFloat(), (currp.y + DeviceUtil.dip2px(context, 1f)).toFloat()
                        )
                        canvas.drawRoundRect(
                            rect,
                            DeviceUtil.dip2px(context, 1f).toFloat(),
                            DeviceUtil.dip2px(context, 1f).toFloat(),
                            mPaint
                        )
                    }
                }
//                if (allpointsame) {
//                    val currp: MainViewSize = datapoints.get(0)
//                    canvas.drawLine(
//                        currp.x.toFloat(),
//                        mYpos.toFloat(),
//                        currp.x.toFloat(),
//                        currp.y.toFloat(),
//                        mPaint
//                    )
//                }
            }
        }
    }

    private fun drawTouchBar(canvas: Canvas) {
        if (mTouchDataItem == null || mDataList.isNullOrEmpty()) return
        if (mTouchDataItem!!.data <= 0) return

        val mSplitWidth = mDataWidth / mDataList.size.toFloat()

        val x = mXpos + mSplitWidth * (mDrawPos + 1) - mSplitWidth / 2f

        LogUtils.i(" getDataItemByMouseX drawShiLine x $x mDrawPos $mDrawPos ")

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
        mXLinePaint.pathEffect = null
        mXLinePaint.color = Color.parseColor("#89a9de")
        val linearGradient = LinearGradient(
            x,
            0f,
            x,
            mOrgPoint.y.toFloat(),
            Color.parseColor("#2289a9de"),
            Color.parseColor("#89a9de"),
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


        var leftX = x - touchRectWidth / 2f
        var rightX = x + touchRectWidth / 2f
        if (leftX <= 0) {
            leftX = 0f
            rightX = touchRectWidth
        }
        touchPaint.style = Paint.Style.FILL
        touchPaint.color = Color.parseColor("#292B3C")
        var rectF = RectF(leftX, 0f, rightX, touchRectHeight)
        canvas.drawRoundRect(rectF, 5f, 5f, touchPaint)

        mXYTextPaint.color = Color.parseColor("#FFFFFF")
        mXYTextPaint.textSize = DeviceUtil.sp2px(context, 18f).toFloat()
        var textWidth = mXYTextPaint.measureText(mTouchDataItem!!.data.toInt().toString()).toInt()
        var textHeight = getFontHeight(mXYTextPaint)
        var txtLeft = x - textWidth / 2f
        if (txtLeft <= 0) txtLeft = 0f
        canvas.drawText(
            mTouchDataItem!!.data.toInt().toString(),
            txtLeft,
            textHeight.toFloat(),
            mXYTextPaint
        )

        mXYTextPaint.color = Color.parseColor("#7C7D8C")
        mXYTextPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()
        mCalendar?.timeInMillis = mTouchDataItem!!.time
        var hour = mCalendar?.get(Calendar.HOUR_OF_DAY)
        var txt = "$hour:00-${hour?.plus(1)}:00"
        textWidth = mXYTextPaint.measureText(txt).toInt()
        textHeight = getFontHeight(mXYTextPaint)
        txtLeft = x - textWidth / 2f
        if (txtLeft <= 0) txtLeft = 0f
        canvas.drawText(
            txt,
            txtLeft,
            touchRectHeight - 10,
            mXYTextPaint
        )
    }

    private fun drawDayShiLine(canvas: Canvas) {
        if (datapoints == null || datapoints.isEmpty()) return
        val point: MainViewSize = caculateTouchPoint(datapoints) ?: return

        val linearGradient = LinearGradient(
            x,
            0f,
            x,
            mOrgPoint.y.toFloat(),
            intArrayOf(0x7fFE475A, 0xFE475A),
            floatArrayOf(0.5f, 0.9f),
            Shader.TileMode.MIRROR
        )
        touchPaint.shader = linearGradient
        touchPaint.strokeWidth = DeviceUtil.dip2px(context, 0.5f).toFloat()

        canvas.drawLine(
            point.x.toFloat(), 0f,
            point.x.toFloat(), mOrgPoint.y.toFloat(), touchPaint
        )

        touchPaint.shader = null
        touchPaint.style = Paint.Style.FILL
        touchPaint.color = Color.parseColor("#1A192A")
        canvas.drawCircle(point.x, point.y, DeviceUtil.dip2px(context, 3f).toFloat(), touchPaint)
        touchPaint.style = Paint.Style.STROKE
        touchPaint.color = Color.parseColor("#802CC9")
        canvas.drawCircle(point.x, point.y, DeviceUtil.dip2px(context, 4f).toFloat(), touchPaint)

        val item: MainViewItem? = point.item
        itemTouchListener?.onItemTouchListener(item, touchX.toFloat())
    }

    private fun caculateTouchPoint(datapoints: List<MainViewSize>?): MainViewSize? {
        if (datapoints == null || datapoints.isEmpty()) return null
        var point: MainViewSize? = null
        var lastPoint = 9999999
        for (testPoint in datapoints) {
            val len: Float = Math.abs(touchX - testPoint.x)
            if (len <= lastPoint) {
                point = testPoint
                lastPoint = len.toInt()
            }
        }
        return point
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

    private fun getBaseline(p: Paint): Float {
        val fontMetrics: Paint.FontMetrics = p.fontMetrics
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (!mTouchEnable) return super.dispatchTouchEvent(event)
        super.dispatchTouchEvent(event)
        if (mTimeMode == SpecDateSelectedView.TimeMode.Day) {
            touchX = event.x
            getDataItemByMouseX(event.x.toInt())
        } else {
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

    fun setMindData(min: Float, max: Float, list: List<MainViewItem>?) {
        if (list.isNullOrEmpty()) {
            mMinData = 0f
            mMaxData = 100f
            mDataList.clear()
            postInvalidate()
            return
        }
        mMinData = min
        mMaxData = max
        mDataList.clear()
        mDataList.addAll(list)
        postInvalidate()
    }

    fun setTimeMode(
        timeMode: SpecDateSelectedView.TimeMode?,
        startDate: Date?,
        endDate: Date?
    ) {
        mTimeMode = timeMode ?: SpecDateSelectedView.TimeMode.Day
        mStartDate = startDate
        mEndDate = endDate
        postInvalidate()
    }

    fun setOnItemTouchListener(onItemTouchListener: OnItemTouchListener) {
        itemTouchListener = onItemTouchListener
    }

    fun setTouchDataItem(item: MainViewItem?) {
        if (item == null) return
        mDrawPos = -1
        mTouchDataItem = item
        for (i in mDataList.indices) {
            val dataItem: MainViewItem = mDataList[i]
            if (item.time == dataItem.time && mDrawPos < 0) {
                mDrawPos = i
                mTouchDataItem = dataItem
            }
        }
        postInvalidate()
    }

    fun setStyle(style: Int) {
        mStyle = style
    }
}