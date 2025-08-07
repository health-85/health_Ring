package com.healthy.rvigor.view

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.R
import com.healthy.rvigor.util.LogUtils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

//运动类
class PerformanceView : View {

    private var mContext: Context? = null

    private var mArcColor = 0
    private var mArcWidth = 0
    private var mInnerArcWidth = 0
    private var mCenterTextColor = 0
    private var mCenterTextSize = 0
    private var mCircleRadius = 0

    private var arcPaint: Paint? = null
    private var arcWhiteCirclePaint: Paint? = null
    private var arcInnerCirclePaint: Paint? = null
    private var arcCirclePaint: Paint? = null
    private var centerTextPaint: Paint? = null
    private var startCirclePaint: Paint? = null

    private var arcRectF: RectF = RectF()
    private var textBoundRect: Rect = Rect()

    private var mCurData = 0f
    private var arcStartColor = 0
    private var arcEndColor = 0
    private var mArcWhiteCircleRadius = 0

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

    fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        mContext = context
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PerformanceView, defStyleAttr, 0)
        mArcColor =
            typedArray.getColor(
                R.styleable.PerformanceView_circleArcColor,
                Color.parseColor("#37D250")
            )
        mArcWidth = typedArray.getDimensionPixelSize(
            R.styleable.PerformanceView_circleArcWidth,
            DeviceUtil.dip2px(context, 10f)
        )
        mInnerArcWidth = typedArray.getDimensionPixelSize(
            R.styleable.PerformanceView_circleInnerArcWidth,
            DeviceUtil.dip2px(context, 10f)
        )
        mCenterTextColor =
            typedArray.getColor(R.styleable.PerformanceView_circleCenterTextColor, 0x222222)
        mCenterTextSize = typedArray.getDimensionPixelSize(
            R.styleable.PerformanceView_circleCenterTextSize,
            DeviceUtil.dip2px(context, 20f)
        )
        mCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.PerformanceView_circleCircleRadius,
            DeviceUtil.dip2px(context, 100f)
        )
        mArcWhiteCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.PerformanceView_circleArcWhiteCircleRadius,
            DeviceUtil.dip2px(context, 4f)
        )
        arcStartColor = typedArray.getColor(
            R.styleable.PerformanceView_circleArcStartColor,
            Color.parseColor("#FBB236")
        )
        arcEndColor = typedArray.getColor(
            R.styleable.PerformanceView_circleArcEndColor,
            Color.parseColor("#F59519")
        )

        typedArray.recycle()

        initPaint()
    }

    private fun initPaint() {
        startCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        startCirclePaint?.style = Paint.Style.FILL
        startCirclePaint?.strokeWidth = mArcWidth.toFloat()
        startCirclePaint?.color = arcStartColor

        arcCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcCirclePaint?.style = Paint.Style.STROKE
        arcCirclePaint?.strokeWidth = mInnerArcWidth.toFloat()
        arcCirclePaint?.color = Color.parseColor("#3E3E58")
        arcCirclePaint?.strokeCap = Paint.Cap.ROUND

        arcWhiteCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcWhiteCirclePaint?.style = Paint.Style.FILL
        arcWhiteCirclePaint?.strokeWidth = mArcWhiteCircleRadius.toFloat()
        arcWhiteCirclePaint?.color = Color.parseColor("#ffffff")

        arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcPaint?.style = Paint.Style.STROKE
        arcPaint?.strokeWidth = mArcWidth.toFloat()
        arcPaint?.color = mArcColor
        arcPaint?.strokeCap = Paint.Cap.ROUND

        arcInnerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcInnerCirclePaint?.style = Paint.Style.FILL
        arcInnerCirclePaint?.color = mArcColor
        arcInnerCirclePaint?.strokeCap = Paint.Cap.ROUND

        centerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        centerTextPaint?.style = Paint.Style.STROKE
        centerTextPaint?.color = mCenterTextColor
        centerTextPaint?.textSize = mCenterTextSize.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            measureDimension(widthMeasureSpec),
            measureDimension(heightMeasureSpec)
        )
    }

    private fun measureDimension(measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = mCircleRadius * 2
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        arcRectF.left = mArcWidth.toFloat()
        arcRectF.top = mArcWidth.toFloat()
        arcRectF.right = width.toFloat() - mArcWidth * 2
        arcRectF.bottom = width.toFloat() - mArcWidth * 2
        arcCirclePaint?.let {
            canvas.drawArc(arcRectF, 180f, 180f, false, it)
        }
        if (mCurData <= 0) return
        val sweepAngle = 180f * mCurData / 100f
        LogUtils.i(" onDraw sweepAngle $sweepAngle mCurData $mCurData ")
        //圆弧
        arcPaint?.let {
            canvas.drawArc(arcRectF, 180f, sweepAngle, false, it)
        }
        //圆点
        arcWhiteCirclePaint?.let {
            val x = width.toFloat() / 2 - mArcWidth / 2
            val y = width.toFloat() / 2 - mArcWidth / 2
            val radius = arcRectF.height() / 2
            val angel = Math.toRadians(sweepAngle.toDouble())
            val whiteCircleX = (x - radius * cos(angel)).toFloat()
            val whiteCircleY = (y - radius * sin(angel)).toFloat()
            canvas.drawCircle(whiteCircleX, whiteCircleY, mArcWhiteCircleRadius.toFloat(), it)
        }
        //圆横线
        arcInnerCirclePaint?.let {

            val innerRadius = DeviceUtil.dip2px(context, 10f)

            val x = width.toFloat() / 2 - mArcWidth / 2
            val y = width.toFloat() / 2 - mArcWidth / 2

            val radius = arcRectF.height() / 2 - innerRadius

            val radiusEnd = arcRectF.height() / 2 - innerRadius - DeviceUtil.dip2px(context, 4f)
            val radiusLongEnd = arcRectF.height() / 2 - innerRadius - DeviceUtil.dip2px(context, 6f)

            var temp = 0f
            var angel = 0.0
            val angleLen = 4
            var startX = 0f
            var startY = 0f
            var endX = 0f
            var endY = 0f

            while (temp <= 180) {
                angel = Math.toRadians(temp.toDouble())
                startX = (x - radius * cos(angel)).toFloat()
                startY = (y - radius * sin(angel)).toFloat()
                it.style = Paint.Style.FILL
                if (temp % 36 == 0f) {
                    it.color = Color.parseColor("#9b9bbb")
                    endX = (x - radiusLongEnd * cos(angel)).toFloat()
                    endY = (y - radiusLongEnd * sin(angel)).toFloat()
                } else {
                    it.color = Color.parseColor("#3a3a53")
                    endX = (x - radiusEnd * cos(angel)).toFloat()
                    endY = (y - radiusEnd * sin(angel)).toFloat()
                }
                canvas.drawLine(startX, startY, endX, endY, it)
                temp += angleLen
            }
        }
    }

    fun setPercentData(data: Int, interpolator: TimeInterpolator?) {
        val valueAnimator = ValueAnimator.ofInt(mCurData.toInt(), data)
        valueAnimator.duration = (abs(mCurData - data) * 30).toLong()
        valueAnimator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            mCurData = ((value * 10).toFloat().roundToInt() / 10).toFloat()
            invalidate()
        }
        valueAnimator.interpolator = interpolator
        valueAnimator.start()
    }

    fun setPercentData(data: Int){
        mCurData = data.toFloat()
        invalidate()
    }

    fun setArcColor(color: Int) {
        if (arcPaint == null) return
        mArcColor = color
        arcStartColor = color
        arcEndColor = color
        arcPaint?.color = mArcColor
        startCirclePaint?.color = arcStartColor
        invalidate()
    }

}