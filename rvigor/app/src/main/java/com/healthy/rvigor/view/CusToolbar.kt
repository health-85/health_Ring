package com.healthy.rvigor.view

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.healthy.rvigor.R

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/16 9:45
 * @UpdateRemark:
 */
class CusToolbar : FrameLayout, View.OnClickListener {

    private var tvTitle: TextView? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleRes: Int) {
        if (attrs == null) return
        val a =
            getContext().obtainStyledAttributes(attrs, R.styleable.CusToolbar) // TypedArray是一个数组容器
        val title = a.getString(R.styleable.CusToolbar_center_title)
        val left_title = a.getString(R.styleable.CusToolbar_left_title)
        val right_title = a.getString(R.styleable.CusToolbar_right_title)
        val right_right_title = a.getString(R.styleable.CusToolbar_right_right_title)
        val right_drawable = a.getDrawable(R.styleable.CusToolbar_right_img)
        val right_right_drawable = a.getDrawable(R.styleable.CusToolbar_right_right_img)
        a.recycle()

        LayoutInflater.from(context).inflate(R.layout.view_toolbar, this, true)

        val tvLeftTitle = findViewById<TextView>(R.id.tv_title_left)
        tvTitle = findViewById(R.id.tv_title_center)
        val tvTitleRight = findViewById<TextView>(R.id.tv_title_right)
        val tvTitleRightLeft = findViewById<TextView>(R.id.tv_title_right_left)
        val ivTitleLeft = findViewById<ImageView>(R.id.iv_title_left)
        val ivTitleRight = findViewById<ImageView>(R.id.iv_title_right)
        val ivTitleRightRight = findViewById<ImageView>(R.id.iv_title_right_right)

        if (!TextUtils.isEmpty(title)) {
            tvTitle?.text = title
        }
        if (!TextUtils.isEmpty(left_title)) {
            tvLeftTitle.text = left_title
        }
        tvLeftTitle.visibility = if (TextUtils.isEmpty(left_title)) GONE else VISIBLE
        if (!TextUtils.isEmpty(right_title)) {
            tvTitleRight.text = right_title
        }
        tvTitleRight.visibility = if (TextUtils.isEmpty(right_title)) GONE else VISIBLE
        if (!TextUtils.isEmpty(right_right_title)) {
            tvTitleRightLeft.text = right_right_title
        }
        tvTitleRightLeft.visibility = if (TextUtils.isEmpty(right_right_title)) GONE else VISIBLE

//        if (left_drawable != null) {
//            ivTitleLeft.setImageDrawable(left_drawable);
//        }
//        ivTitleLeft.setVisibility(left_drawable == null ? GONE : VISIBLE);
        if (right_drawable != null) {
            ivTitleRight.setImageDrawable(right_drawable)
        }
        ivTitleRight.visibility = if (right_drawable == null) GONE else VISIBLE
        if (right_right_drawable != null) {
            ivTitleRightRight.setImageDrawable(right_right_drawable)
        }
        ivTitleRightRight.visibility = if (right_right_drawable == null) GONE else VISIBLE

        tvLeftTitle.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        tvTitleRightLeft.setOnClickListener(this)
        tvTitle?.setOnClickListener(this)
        ivTitleLeft.setOnClickListener(this)
        ivTitleRight.setOnClickListener(this)
        ivTitleRightRight.setOnClickListener(this)
    }


    public fun setTitle(title : String){
        tvTitle?.text = title

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_title_left -> {
                if (context is Activity) {
                    (context as Activity).onBackPressed()
                }
            }

            R.id.iv_title_right -> {
                if (rightImgClickListener != null) {
                    rightImgClickListener?.rightImgClickListener(v)
                }
            }
        }
    }

    private var rightImgClickListener: OnRightImgClickListener? = null

    fun setRightImgClickListener(rightImgClickListener: OnRightImgClickListener?) {
        this.rightImgClickListener = rightImgClickListener
    }

    interface OnRightImgClickListener {
        fun rightImgClickListener(v: View?)
    }
}