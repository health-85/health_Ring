package com.healthy.rvigor.mvp.view.activity.health

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.healthy.rvigor.Constants
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.databinding.ActivityOxBinding
import com.healthy.rvigor.mvp.contract.IOxContract
import com.healthy.rvigor.mvp.presenter.OxPresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.view.CusToolbar
import com.healthy.rvigor.view.OxDataView
import com.healthy.rvigor.view.SpecDateSelectedView
import java.util.Calendar
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/22 19:14
 * @UpdateRemark:   血氧
 */
class OxActivity : BaseMVPActivity<ActivityOxBinding, OxPresenter>(), IOxContract.View,
    View.OnClickListener {

    companion object {
        fun startOxActivity(activity: Activity?, time: Long) {
            if (activity == null || time == 0L) return
            val intent = Intent(activity, OxActivity::class.java)
            intent.putExtra(Constants.EXTRA, time)
            activity.startActivity(intent)
        }
    }

    private var mSpeTime = 0L

    override fun getLayoutResID(): Int {
        return R.layout.activity_ox
    }

    override fun createPresenter(): OxPresenter {
        return OxPresenter()
    }

    override fun initView() {
        super.initView()
        binding?.toolBar?.setRightImgClickListener(object : CusToolbar.OnRightImgClickListener {
            override fun rightImgClickListener(v: View?) {
                startActivity(Intent(this@OxActivity, OxRemarkActivity::class.java))
            }
        })
        binding?.rgDay?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbt_one_day -> {
                    binding?.specDate?.timeMode = SpecDateSelectedView.TimeMode.Day
                }

                R.id.rbt_7_day -> {
                    binding?.specDate?.timeMode = SpecDateSelectedView.TimeMode.Week
                }

                R.id.rbt_30_day -> {
                    binding?.specDate?.timeMode = SpecDateSelectedView.TimeMode.Month
                }
            }
            mPresenter.getOxData(binding?.specDate)
        }
        binding?.specDate?.setEvent {
            mPresenter.getOxData(it)
        }
        binding?.oxView?.setOnItemTouchListener(object : OxDataView.OnItemTouchListener{
            override fun onItemTouchListener(item: MainViewItem?, pos: Float) {
                if (binding?.specDate?.timeMode == SpecDateSelectedView.TimeMode.Day) {
                    binding?.tvAverageUni?.text = resources.getString(R.string.average)
                } else {
                    binding?.tvAverageUni?.text = ""
                }
                if (item == null || item.data<= 0) {
                    binding?.tvOx?.text = "--"
                    binding?.tvTime?.text = ""
                }else{
                    if (binding?.specDate?.timeMode == SpecDateSelectedView.TimeMode.Day) {
                        binding?.tvOx?.text = item.data.toInt().toString()
                        binding?.tvAverageUni?.text = resources.getString(R.string.average)
                        binding?.tvTime?.text = DateTimeUtils.s_long_2_str(item.time, DateTimeUtils.hm_format)
                    } else {
                        binding?.tvOx?.text = "${item.minData.toInt()}-${item.maxData.toInt()}"
                        binding?.tvAverageUni?.text = ""
                        binding?.tvTime?.text = DateTimeUtils.s_long_2_str(item.time, DateTimeUtils.month_day_format_1)
                    }
                }
                binding?.rectDataView?.setPos(pos)
            }
        })
        binding?.specDate?.setCalendarListener { v, msg ->
            if (msg is Long) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = msg.toLong()
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0

                binding?.specDate?.updateTime(calendar.time)

                val startTime = binding?.specDate?.datestart?.time ?: 0
                val endTime = binding?.specDate?.dateend?.time ?: 0
                LogUtils.i(
                    " dateSelectedView startTime " + DateTimeUtils.s_long_2_str(
                        startTime,
                        DateTimeUtils.f_format
                    )
                            + " endTime " + DateTimeUtils.s_long_2_str(
                        endTime,
                        DateTimeUtils.f_format
                    )
                )
                mPresenter.getOxData(binding?.specDate)
            }
        }
        binding?.tvSpoMeasure?.setOnClickListener(this@OxActivity)
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
        mSpeTime = System.currentTimeMillis()
        if (intent != null) {
            mSpeTime = intent.getLongExtra(Constants.EXTRA, System.currentTimeMillis())
        }
        binding?.specDate?.updateTime(Date(mSpeTime))
        mPresenter.getOxData(binding?.specDate)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_spo_measure -> {
                startActivity(Intent(this@OxActivity, OxMeasureActivity::class.java))
            }
        }
    }

    override fun onOxData(
        itemList: List<MainViewItem>?,
        lastItem: MainViewItem?,
        min: Int,
        max: Int,
        average: Int
    ) {
        if (binding?.specDate?.timeMode == SpecDateSelectedView.TimeMode.Day) {
            binding?.tvAverageUni?.text = resources.getString(R.string.average)
        } else {
            binding?.tvAverageUni?.text = ""
        }
        if (lastItem == null) {
            binding?.tvOx?.text = "--"
            binding?.tvTime?.text = ""
            binding?.oxView?.setOxData(0f, 100f, null)
            binding?.tvRangeOx?.text = "--"
            binding?.tvAverageOx?.text = "--"
            binding?.rectDataView?.setPos(0f)
            return
        }
        if (binding?.specDate?.timeMode == SpecDateSelectedView.TimeMode.Day) {
            binding?.tvOx?.text = lastItem.data.toInt().toString()
            binding?.tvAverageUni?.text = resources.getString(R.string.average)
            binding?.tvTime?.text = DateTimeUtils.s_long_2_str(lastItem.time, DateTimeUtils.hm_format)
        } else {
            binding?.tvOx?.text = "${lastItem.minData.toInt()}-${lastItem.maxData.toInt()}"
            binding?.tvAverageUni?.text = ""
            binding?.tvTime?.text = DateTimeUtils.s_long_2_str(lastItem.time, DateTimeUtils.month_day_format_1)
        }
        binding?.oxView?.setTimeMode(binding?.specDate?.timeMode, binding?.specDate?.datestart, binding?.specDate?.dateend)
        binding?.oxView?.setOxData(0f, 100f, itemList)
        binding?.tvRangeOx?.text = "$min-$max"
        binding?.tvAverageOx?.text = "$average"
        binding?.oxView?.setTouchDataItem(lastItem)
        binding?.rectDataView?.setPos(((lastItem.currentDrawRect.left + lastItem.currentDrawRect.right) / 2).toFloat())
    }
}