package com.healthy.rvigor.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.bean.MainViewSize
import com.healthy.rvigor.util.LogUtils

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 16:10
 * @UpdateRemark:
 */
class RectDataView : View {

    private var mSelPos = 200f
    private var mXpos = 0f
    private var mDataWidth = 0f
    private var rightPadding = 0
    private var leftPadding = 0
    private var textMaxWidth = 0f
    //图片宽高
    private val mOrgPoint = Point(0, 0)

    //线条
    private val mPaint = Paint()

    private var mRadius = 0f
    private var mCirclePadding = 0f
    private var mCircleRadius = 0f

    private val mPosList = mutableListOf<MainViewSize>()

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

        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.parseColor("#3E3E58")
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()
        mPaint.textSize = DeviceUtil.sp2px(context, 12f).toFloat()

        mRadius = DeviceUtil.dip2px(context, 16f).toFloat()
        mCirclePadding = DeviceUtil.dip2px(context, 8f).toFloat()
        mCircleRadius = DeviceUtil.dip2px(context, 8f).toFloat()

        rightPadding = DeviceUtil.dip2px(context, 12f)
        leftPadding = DeviceUtil.dip2px(context, 4f)
        textMaxWidth = mPaint.measureText("200")
    }

    fun setRectData(leftPadding : Int, rightPadding : Int, textMaxWidth : Float){
        this.rightPadding = leftPadding
        this.leftPadding = rightPadding
        this.textMaxWidth = textMaxWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mOrgPoint.x = 0
        mOrgPoint.y = (measuredHeight - mCircleRadius).toInt()
        if (mOrgPoint.y < 0) mOrgPoint.y = 0

        mXpos = leftPadding + textMaxWidth + DeviceUtil.dip2px(context, 3f)
        mDataWidth = measuredWidth - mXpos - rightPadding

        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.parseColor("#3E3E58")
        val rectF = RectF(0f, 0f, measuredWidth.toFloat(), mOrgPoint.y.toFloat())
        canvas?.drawRoundRect(rectF, mRadius, mRadius, mPaint)

        if (mPosList.isNullOrEmpty() || mSelPos <= 0) return

        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#171525")
        canvas?.drawCircle(mSelPos, mOrgPoint.y.toFloat(), 2.5f * mCircleRadius, mPaint)

        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#292B3C")
        canvas?.drawCircle(mSelPos, mOrgPoint.y.toFloat(), mCircleRadius, mPaint)

        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.parseColor("#3E3E58")
        canvas?.drawCircle(mSelPos, mOrgPoint.y.toFloat(), mCircleRadius, mPaint)

        drawScrollLine(canvas, mPosList)
    }

    private fun drawScrollLine(canvas: Canvas?, point: List<MainViewSize>) {

        var startp : MainViewSize? = null
        var endp  : MainViewSize? = null

        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.parseColor("#3E3E58")
        mPaint.strokeWidth = DeviceUtil.dip2px(context, 1f).toFloat()

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
            canvas?.drawPath(path, mPaint)

            startp.x = oldStartX
            endp.x = oldEndX
        }
    }

    fun setPos(pos : Float){

        if (pos <= 0f){
            mSelPos = 0f
            mPosList.clear()
            postInvalidate()
            return
        }

        mSelPos = pos

        if (mSelPos + 1 * mCircleRadius > mDataWidth){
//            mSelPos = mDataWidth - 3 * mCircleRadius - mCirclePadding
            mSelPos = mDataWidth - mCircleRadius
        }

        if (mSelPos - 3 * mCircleRadius <= 0){
            mSelPos = 3 * mCircleRadius + mCirclePadding
        }

        val x1 = mSelPos - 3 * mCircleRadius
        val y1 = mOrgPoint.y.toFloat()

        val x2 = mSelPos
        val y2 = mOrgPoint.y - mCircleRadius - mCirclePadding

        val x3 = mSelPos + 3 * mCircleRadius
        val y3 = mOrgPoint.y.toFloat()

        mPosList.clear()
        mPosList.add(MainViewSize(x1, y1))
        mPosList.add(MainViewSize(x2, y2))
        mPosList.add(MainViewSize(x3, y3))

        postInvalidate()

    }

}