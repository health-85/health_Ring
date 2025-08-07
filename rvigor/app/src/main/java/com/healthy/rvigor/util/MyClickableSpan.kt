package com.healthy.rvigor.util

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/29 19:07
 * @UpdateRemark:
 */
class MyClickableSpan : ClickableSpan {

    private var target: String? = null

    constructor()

    constructor(target: String?) {
        this.target = target
    }

    constructor(target: String?, listener: OnMyClickListener) {
        this.target = target
        this.clickListener = listener
    }

    override fun onClick(widget: View) {
        if (clickListener != null){
            clickListener?.onViewClick(target, widget)
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }

    var clickListener : OnMyClickListener? = null

    public interface OnMyClickListener{
        fun onViewClick(target : String?, widget: View)
    }

}