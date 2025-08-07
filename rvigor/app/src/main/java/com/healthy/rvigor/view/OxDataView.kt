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
 * @CreateDate:     2024/5/23 16:03
 * @UpdateRemark:
 */
class OxDataView : View {

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
    private var mMaxData = 100f

    //最小数据
    private var mMinData = 80f

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

    interface OnItemTouchListener {
        fun onItemTouchListener(item: MainViewItem?, pos : Float)
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
        typedArray.recycle()

        mXYTextPaint.isAntiAlias = true
        mXYTextPaint.color = Color.parseColor("#3E3E58")
        mXYTextPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()
        mXYTextPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        mXLinePaint.color = Color.parseColor("#3E3E58")
        mXLinePaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

        rightPadding = DeviceUtil.dip2px(context, 12f)
        leftPadding = DeviceUtil.dip2px(context, 6f)
        bottomPadding = DeviceUtil.dip2px(context, 5f)

        textTopPadding = DeviceUtil.dip2px(context, 5f)
        yPadding = DeviceUtil.dip2px(context, 10f)

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

        //文字的宽度
        textMaxWidth = mXYTextPaint.measureText("$mMaxData")
        textMinWidth = mXYTextPaint.measureText("$mMinData")

//        LogUtils.i(" mMaxData $mMaxData ")
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

        //画最大的数值
        val len = (mMaxData - mMinData) / 4
        for (i in 0..len.toInt()) {
            val yItemHeight = mYpos - i * mDataHeight / 4
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

        if (mTimeMode == SpecDateSelectedView.TimeMode.Day) {
            canvas.drawText(
                "00",
                mXpos,
                (mOrgPoint.y - bottomPadding).toFloat(),
                mXYTextPaint
            )
            canvas.drawText(
                "06",
                mXpos + itemWidth - textWidth / 2f,
                (mOrgPoint.y - bottomPadding).toFloat(),
                mXYTextPaint
            )
            canvas.drawText(
                "12",
                mXpos + 2 * itemWidth - textWidth / 2f,
                (mOrgPoint.y - bottomPadding).toFloat(),
                mXYTextPaint
            )
            canvas.drawText(
                "18",
                mXpos + 3 * itemWidth - textWidth / 2f,
                (mOrgPoint.y - bottomPadding).toFloat(),
                mXYTextPaint
            )
            canvas.drawText(
                "00",
                mXpos + 4 * itemWidth - textWidth,
                (mOrgPoint.y - bottomPadding).toFloat(),
                mXYTextPaint
            )
        } else if (mTimeMode == SpecDateSelectedView.TimeMode.Week) {
            mBottomList = ViewDataUtil.getWeekBottomString(mStartDate)
            drawBottomText(canvas, mBottomList)
        } else {
            mBottomList = ViewDataUtil.getMonthBottomString(mStartDate, mEndDate)
            drawBottomText(canvas, mBottomList)
        }
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
                var textLeftX = (centerX - textWidth / 2f)
                if (textLeftX <= 0) {
                    textLeftX = 0f
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
                if (mTimeMode == SpecDateSelectedView.TimeMode.Day) {
                    drawBar(canvas)
                } else {
                    drawRange(canvas)
                }
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
        drawShiLine(canvas)
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

            var top90 = 0
            var top70 = 0

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


                curMinLen =
                    (90 - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                        context,
                        4f
                    )
                top90 = (mYpos - curMinLen).toInt()

                curMinLen =
                    (70 - mMinData) * (mDataHeight / len) + DeviceUtil.dip2px(
                        context,
                        4f
                    )
                top70 = (mYpos - curMinLen).toInt()

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

                if (dataItem.maxData == dataItem.minData) {
                    mPaint.color = ViewDataUtil.getOxDataColor(dataItem.maxData.toInt())
                    canvas.drawCircle(
                        (centerX.toFloat()),
                        topMax.toFloat(),
                        mBarWidth / 2f,
                        mPaint
                    )
                } else {
                    var rectF: RectF? = null
                    if (dataItem.maxData >= 90) {
                        if (dataItem.minData < 70) {
                            rectF = RectF(
                                (centerX - mBarWidth / 2f).toFloat(), top70.toFloat(),
                                (centerX + mBarWidth / 2f).toFloat(), bottomMin.toFloat()
                            )
                            mPaint.color = ViewDataUtil.getOxDataColor(65)
                            canvas.drawRoundRect(
                                rectF,
                                (mBarWidth / 2f).toFloat(),
                                (mBarWidth / 2f).toFloat(),
                                mPaint
                            )
                        }
                        if (dataItem.minData < 90) {
                            rectF = RectF(
                                (centerX - mBarWidth / 2f).toFloat(),
                                top90.toFloat(),
                                (centerX + mBarWidth / 2f).toFloat(),
                                if (bottomMin > top70) top70.toFloat() else bottomMin.toFloat()
                            )
                            mPaint.color = ViewDataUtil.getOxDataColor(85)
                            canvas.drawRoundRect(
                                rectF,
                                (mBarWidth / 2f).toFloat(),
                                (mBarWidth / 2f).toFloat(),
                                mPaint
                            )
                            if (bottomMin > top70) {
                                val rect = Rect(
                                    (centerX - mBarWidth / 2f).toInt(),
                                    (top70 - mBarWidth / 2f).toInt(),
                                    (centerX + mBarWidth / 2f).toInt(),
                                    (top70 + mBarWidth / 2f).toInt()
                                )
                                canvas.drawRect(rect, mPaint)
                            }
                        }
                        if (dataItem.minData <= 100) {
                            rectF = RectF(
                                (centerX - mBarWidth / 2f).toFloat(),
                                topMax.toFloat(),
                                (centerX + mBarWidth / 2f).toFloat(),
                                if (bottomMin > top90) top90.toFloat() else bottomMin.toFloat()
                            )
                            mPaint.color = ViewDataUtil.getOxDataColor(95)
                            canvas.drawRoundRect(
                                rectF,
                                (mBarWidth / 2f).toFloat(),
                                (mBarWidth / 2f).toFloat(),
                                mPaint
                            )
                            if (bottomMin > top90) {
                                val rect = Rect(
                                    (centerX - mBarWidth / 2f).toInt(),
                                    (top90 - mBarWidth / 2f).toInt(),
                                    (centerX + mBarWidth / 2f).toInt(),
                                    (top90 + mBarWidth / 2f).toInt()
                                )
                                canvas.drawRect(rect, mPaint)
                            }
                        }
                    } else if (dataItem.maxData >= 70) {
                        if (dataItem.minData < 70) {
                            rectF = RectF(
                                (centerX - mBarWidth / 2f).toFloat(), top70.toFloat(),
                                (centerX + mBarWidth / 2f).toFloat(), bottomMin.toFloat()
                            )
                            mPaint.color = ViewDataUtil.getOxDataColor(65)
                            canvas.drawRoundRect(
                                rectF,
                                (mBarWidth / 2f).toFloat(),
                                (mBarWidth / 2f).toFloat(),
                                mPaint
                            )
                        }
                        if (dataItem.minData < 90) {
                            rectF = RectF(
                                (centerX - mBarWidth / 2f).toFloat(),
                                topMax.toFloat(),
                                (centerX + mBarWidth / 2f).toFloat(),
                                if (bottomMin > top70) top70.toFloat() else bottomMin.toFloat()
                            )
                            mPaint.color = ViewDataUtil.getOxDataColor(85)
                            canvas.drawRoundRect(
                                rectF,
                                (mBarWidth / 2f).toFloat(),
                                (mBarWidth / 2f).toFloat(),
                                mPaint
                            )

                            if (bottomMin > top70) {
                                val rect = Rect(
                                    (centerX - mBarWidth / 2f).toInt(),
                                    (top90 - mBarWidth / 2f).toInt(),
                                    (centerX + mBarWidth / 2f).toInt(),
                                    (top90 + mBarWidth / 2f).toInt()
                                )
                                canvas.drawRect(rect, mPaint)
                            }
                        }
                    } else {
                        rectF = RectF(
                            (centerX - mBarWidth / 2f).toFloat(), topMax.toFloat(),
                            (centerX + mBarWidth / 2f).toFloat(), bottomMin.toFloat()
                        )
                        mPaint.color = ViewDataUtil.getOxDataColor(65)
                        canvas.drawRoundRect(
                            rectF,
                            (mBarWidth / 2f).toFloat(),
                            (mBarWidth / 2f).toFloat(),
                            mPaint
                        )
                    }
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

        val barWidth = mDataWidth / 24
        if (mBarWidth > barWidth) {
            mBarWidth = barWidth
        }

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
                    mPaint.color = dataItem.color
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
                (centerX - mBarWidth / 2f).toFloat(), top.toFloat(),
                (centerX + mBarWidth / 2f).toFloat(), mYpos.toFloat()
            )

//            LogUtils.i(" barWidth $mBarWidth mXpos $mXpos centerX $centerX")

            canvas.drawRoundRect(
                rectF,
                (mBarWidth / 2f).toFloat(),
                (mBarWidth / 2f).toFloat(),
                mPaint
            )

            val rect = Rect(
                (centerX - mBarWidth / 2f).toInt(), (top + mBarWidth / 2f).toInt(),
                (centerX + mBarWidth / 2f).toInt(), mYpos.toInt()
            )
            canvas.drawRect(rect, mPaint)

            if (dataItem != null) {
                val rectTouch = Rect(
                    (centerX - barWidth / 2f).toInt(), 0,
                    (centerX + barWidth / 2f).toInt(), mYpos.toInt()
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
                    top = ((barWidth / 2f).toInt())
                }
                var size = MainViewSize(centerX.toFloat(), top.toFloat(), dataItem.data)
                list.add(size)

                val rectTouch = Rect(
                    (centerX - barWidth / 2f).toInt(), 0,
                    (centerX + barWidth / 2f).toInt(), mYpos.toInt()
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

//            LogUtils.i(" drawScrollLine " + i + " data " + point[i].data)

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

            val wt = (startp.x + endp.x) / 2f
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
                    top = ((barWidth / 2f).toInt())
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
//                    top = ((barWidth / 2f).toInt())
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
//                    (centerX - barWidth / 2f).toInt(), 0,
//                    (centerX + barWidth / 2f).toInt(), mYpos.toInt()
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
                if (i + 1 < pointList.size && isDrawPoint(
                        mDataList.get(i),
                        mDataList.get(i + 1)
                    )
                ) {
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

    private fun drawShiLine(canvas: Canvas) {
        if (mTouchDataItem == null || mDataList.isNullOrEmpty()) return

        val mSplitWidth = mDataWidth / mDataList.size

        val x = mXpos + mSplitWidth * (mDrawPos + 1) - mSplitWidth / 2f

//        LogUtils.i(" getDataItemByMouseX drawShiLine x $x mDrawPos $mDrawPos ")

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
        if (mTimeMode == SpecDateSelectedView.TimeMode.Day) {
            val linearGradient = LinearGradient(
                x,
                0f,
                x,
                mYpos,
                Color.parseColor("#226DFFE9"),
                Color.parseColor("#6DFFE9"),
                Shader.TileMode.CLAMP
            )
            mXLinePaint.shader = linearGradient
            canvas.drawLine(
                x,
                0f,
                x,
                mYpos, mXLinePaint
            )
        } else {
            val linearGradient = LinearGradient(
                x,
               0f,
                x,
                mYpos,
                intArrayOf(0x7f6DFFE9, 0x6DFFE9),
                floatArrayOf(0.5f, 0.9f),
                Shader.TileMode.MIRROR
            )
            mXLinePaint.shader = linearGradient
            canvas.drawLine(
                x,
                (topMax + padding).toFloat(),
                x,
                mYpos - padding, mXLinePaint
            )
        }
        itemTouchListener?.onItemTouchListener(mTouchDataItem, x)
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

    private fun getBaseline(p: Paint): Float {
        val fontMetrics: Paint.FontMetrics = p.fontMetrics
        return (fontMetrics.descent - fontMetrics.ascent) / 2f - fontMetrics.descent
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (!mTouchEnable) return super.dispatchTouchEvent(event)
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
//                LogUtils.i(
//                    " getDataItemByMouseX x $x left ${dataItem.currentDrawRect.left} " +
//                            "right ${dataItem.currentDrawRect.right}"
//                )
                if (x >= dataItem.currentDrawRect.left && x < dataItem.currentDrawRect.right) {
                    mDrawPos = i
                    mTouchDataItem = dataItem
//                    LogUtils.i(
//                        " getDataItemByMouseX mDrawPos $mDrawPos mTouchDataItem ${
//                            Gson().toJson(
//                                mTouchDataItem
//                            )
//                        }"
//                    )
                    break
                }
            }
        }
    }

    fun setOxData(min: Float, max: Float, list: List<MainViewItem>?) {
        if (list.isNullOrEmpty()) {
            mMaxData = 100f
            mMinData = 60f
            mDataList.clear()
            postInvalidate()
            return
        }
        if (min >= 60) {
            this.mMinData = 60f
        } else if (min >= 50) {
            this.mMinData = 50f
        } else {
            this.mMinData = 0f
        }
        this.mMaxData = 100f
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

    fun setOnItemTouchListener(onItemTouchListener: OnItemTouchListener){
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
}