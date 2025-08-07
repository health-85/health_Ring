package com.healthy.rvigor.view

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.plus
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.R
import com.zhangteng.utils.drawableToBitmap
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

//运动类
class MotionCircleView : View {

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
    private var mStartDrawable: Drawable? = null

    private var mStepBitmap: Bitmap? = null
//    private var mCalorieBitmap: Bitmap? = null
//    private var mDistanceBitmap: Bitmap? = null

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
            context.obtainStyledAttributes(attrs, R.styleable.MotionCircleView, defStyleAttr, 0)
        mArcColor =
            typedArray.getColor(
                R.styleable.MotionCircleView_motionArcColor,
                Color.parseColor("#37D250")
            )
        mArcWidth = typedArray.getDimensionPixelSize(
            R.styleable.MotionCircleView_motionArcWidth,
            DeviceUtil.dip2px(context, 20f)
        )
        mInnerArcWidth = typedArray.getDimensionPixelSize(
            R.styleable.MotionCircleView_motionInnerArcWidth,
            DeviceUtil.dip2px(context, 10f)
        )
        mCenterTextColor =
            typedArray.getColor(R.styleable.MotionCircleView_motionCenterTextColor, 0x222222)
        mCenterTextSize = typedArray.getDimensionPixelSize(
            R.styleable.MotionCircleView_motionCenterTextSize,
            DeviceUtil.dip2px(context, 20f)
        )
        mCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.MotionCircleView_motionCircleRadius,
            DeviceUtil.dip2px(context, 100f)
        )
        mArcWhiteCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.MotionCircleView_motionArcWhiteCircleRadius,
            DeviceUtil.dip2px(context, 4f)
        )
        arcStartColor = typedArray.getColor(
            R.styleable.MotionCircleView_motionArcStartColor,
            Color.parseColor("#FBB236")
        )
        arcEndColor = typedArray.getColor(
            R.styleable.MotionCircleView_motionArcEndColor,
            Color.parseColor("#F59519")
        )
        mStartDrawable = typedArray.getDrawable(R.styleable.MotionCircleView_motionStartDrawable)
        if (mStartDrawable != null) {
            mStepBitmap = mStartDrawable.drawableToBitmap()
        }

        typedArray.recycle()

        initPaint()
    }

    private fun initPaint() {
        startCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        startCirclePaint?.style = Paint.Style.FILL
        //startCirclePaint.setStrokeWidth(mArcWidth);
        startCirclePaint?.color = arcStartColor

        arcCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcCirclePaint?.style = Paint.Style.STROKE
        arcCirclePaint?.strokeWidth = mInnerArcWidth.toFloat()
        arcCirclePaint?.color = mArcColor
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

//        mStepBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_step)
//        mCalorieBitmap = BitmapFactory.decodeResource(resources, R.drawable.svg_calorie)
//        mDistanceBitmap = BitmapFactory.decodeResource(resources, R.drawable.svg_location)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            measureDimension(widthMeasureSpec),
            measureDimension(heightMeasureSpec)
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mStepBitmap?.recycle()
        mStepBitmap = null
//        mCalorieBitmap?.recycle()
//        mCalorieBitmap = null
//        mDistanceBitmap?.recycle()
//        mDistanceBitmap = null
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

        arcRectF[(width / 2 - mCircleRadius + mArcWidth / 2).toFloat(),
                (height / 2 - mCircleRadius + mArcWidth / 2).toFloat(),
                (width / 2 + mCircleRadius - mArcWidth / 2).toFloat()] =
            (height / 2 + mCircleRadius - mArcWidth / 2).toFloat()

        arcCirclePaint?.let {
            canvas.drawArc(arcRectF, 135f, 270f, false, it)
        }

        if (mCurData > 0) {
            //圆弧
            arcPaint?.let {
                it.shader = SweepGradient(
                    (width / 2).toFloat(),
                    (height / 2).toFloat(),
                    arcStartColor,
                    arcEndColor
                )
                canvas.drawArc(arcRectF, 135f, 270f * mCurData / 100, false, it)
            }
        }

        //圆弧上白点
        arcWhiteCirclePaint?.let {

            var x = width / 2 - mCircleRadius + mArcWidth / 2
            var y = height / 2 - mCircleRadius + mArcWidth / 2

            var whiteCircleX = x.toFloat()
            var whiteCircleY = y.toFloat()

            val radius = arcRectF.height() / 2
            var angel = Math.toRadians(45.0)
            whiteCircleX = (x + radius - radius * sin(angel)).toFloat()
            whiteCircleY = (y + radius + radius * cos(angel)).toFloat()
            if (mStepBitmap != null && !mStepBitmap!!.isRecycled) {
                canvas.drawBitmap(
                    mStepBitmap!!,
                    whiteCircleX - mStepBitmap!!.width / 2f,
                    whiteCircleY - mStepBitmap!!.height / 2f,
                    it
                )
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

    fun setPercentData(data: Float){
        mCurData = data
        postInvalidate()
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