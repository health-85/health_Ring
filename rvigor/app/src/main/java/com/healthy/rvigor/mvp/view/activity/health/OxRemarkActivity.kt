package com.healthy.rvigor.mvp.view.activity.health

import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityOxRemarkBinding
import com.healthy.rvigor.mvp.presenter.OxRemarkPresenter

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/22 20:45
 * @UpdateRemark:   血氧说明
 */
class OxRemarkActivity : BaseMVPActivity<ActivityOxRemarkBinding, OxRemarkPresenter>(){

    override fun getLayoutResID(): Int {
        return R.layout.activity_ox_remark
    }

    override fun createPresenter(): OxRemarkPresenter {
        return OxRemarkPresenter()
    }

}