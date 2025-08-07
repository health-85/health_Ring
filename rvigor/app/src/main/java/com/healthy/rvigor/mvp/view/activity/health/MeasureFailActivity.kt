package com.healthy.rvigor.mvp.view.activity.health

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityMeasureFailBinding
import com.healthy.rvigor.mvp.presenter.MeasureFailPresenter
import okhttp3.internal.EMPTY_HEADERS

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/23 10:07
 * @UpdateRemark:   测量失败
 */
class MeasureFailActivity : BaseMVPActivity<ActivityMeasureFailBinding, MeasureFailPresenter>(){

    companion object {
        //心率测量
        var RETRY_HEART_MEASURE = 1
        //血氧测量
        var RETRY_OX_MEASURE = 2
        //压力测量
        var RETRY_PRESSURE_MEASURE = 3
        //疲劳测量
        var RETRY_TIRE_MEASURE = 4
        //体温测量
        var RETRY_HEAT_MEASURE = 5
        //环境温度测量
        var RETRY_TEMP_MEASURE = 6
        //血压测量
        var RETRY_BLOOD_MEASURE = 7
        fun startMeasureFailActivity(activity: AppCompatActivity, type: Int) {
            var intent = Intent(activity, MeasureFailActivity::class.java)
            intent.putExtra("type", type)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResID(): Int {
        return R.layout.activity_measure_fail
    }

    override fun createPresenter(): MeasureFailPresenter {
        return MeasureFailPresenter()
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
        var type = RETRY_OX_MEASURE
        if (intent != null){
            type = intent.getIntExtra("type", RETRY_OX_MEASURE)
        }
        if (type == RETRY_HEART_MEASURE){
            binding?.toolBar?.setTitle(resources.getString(R.string.heart_measure))
        }else{
            binding?.toolBar?.setTitle(resources.getString(R.string.ox_measure))
        }

    }
}