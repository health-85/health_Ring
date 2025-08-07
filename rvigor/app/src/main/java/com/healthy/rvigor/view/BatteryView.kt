package com.healthy.rvigor.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.R


/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/30 8:24
 * @UpdateRemark:
 */
class BatteryView : View {

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

    //图片宽高
    private val mOrgPoint = Point(0, 0)

    private var mViewWidth = 0
    private var mViewHeight = 0

    private var mPath: Path? = null

    private var mPaint: Paint? = null

    private var mBgColor = 0
    private var mBatteryColor = 0

    private var mBatteryRadius = 0f
    private var mRectRadius = 0f

    private var mRectF: RectF? = null

    private var mRectWidth = 0f
    private var mRectHeight = 0f

    private var mMatrix: Matrix? = null

    private var mBitmap : Bitmap? = null

    private var mPadding = 0f

    private var mPer = 0f

    fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BatteryView, defStyleAttr, 0)
        mBatteryColor =
            typedArray.getColor(R.styleable.BatteryView_batteryBgColor, Color.parseColor("#5AADB5"))
        mBgColor =
            typedArray.getColor(R.styleable.BatteryView_batteryColor, Color.parseColor("#2B5862"))
        mBatteryRadius =
            typedArray.getDimension(
                R.styleable.BatteryView_batteryRadius,
                DeviceUtil.dip2px(context, 2f).toFloat()
            )
        mRectRadius =
            typedArray.getDimension(
                R.styleable.BatteryView_batteryRectRadius,
                DeviceUtil.dip2px(context, 1f).toFloat()
            )

        typedArray.recycle()

        mPath = Path()

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.style = Style.FILL
        mPaint?.color = mBatteryColor
        mPaint?.strokeWidth = DeviceUtil.dip2px(context, 0.5f).toFloat()

        mRectF = RectF(100f, 100f, 300f, 300f)

        mMatrix = Matrix()

        mPadding = DeviceUtil.dip2px(context, 0.5f).toFloat()

        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_battery)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mViewWidth = measuredWidth
        mViewHeight = measuredHeight

        mRectWidth = DeviceUtil.dip2px(context, 4f).toFloat()
        mRectHeight = (measuredHeight - DeviceUtil.dip2px(context, 2f)).toFloat()

        mOrgPoint.x = 0
        mOrgPoint.y = DeviceUtil.dip2px(context, 1f)

        var count = mViewWidth / (mRectWidth + mPadding).toInt()

        mPaint?.let {

            //边框
            it.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()
            mPaint?.style = Style.STROKE
            canvas?.drawRoundRect(0f, 0f, mViewWidth.toFloat(), mViewHeight.toFloat(), mBatteryRadius, mBatteryRadius, it)

            //画矩形
            mPaint?.style = Style.FILL
            // 设置Matrix为倾斜
            mMatrix?.reset()
            mMatrix?.setSkew(0.35f, 0f) // 设置倾斜因子，根据需要调整
            canvas?.concat(mMatrix)

            //电池背景
            it.color = mBgColor
            for (i in 0 .. count){
                canvas?.drawRoundRect((mRectWidth + mPadding) * (i - 1), mOrgPoint.y.toFloat(),
                    mRectWidth + (mRectWidth + mPadding) * (i - 1), mRectHeight + mOrgPoint.y,
                    mRectRadius, mRectRadius, it)
            }
            it.color = mBatteryColor
            var batteryPer = (mPer / 100f * count).toInt()
            if (batteryPer > 0){
                for (i in 0 .. batteryPer){
                    canvas?.drawRoundRect((mRectWidth + mPadding) * (i - 1), mOrgPoint.y.toFloat(),
                        mRectWidth + (mRectWidth + mPadding) * (i - 1), mRectHeight + mOrgPoint.y,
                        mRectRadius, mRectRadius, it)
                }
            }
        }
    }

    public fun setBatteryPer(per : Float){
        mPer = per
        postInvalidate()
    }

    override fun onDetachedFromWindow() {
        if (mBitmap != null) {
            mBitmap!!.recycle()
            mBitmap = null
        }
        super.onDetachedFromWindow()
    }


}