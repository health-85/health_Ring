package com.healthy.rvigor.mvp.view.activity.health

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthy.rvigor.Constants
import com.healthy.rvigor.R
import com.healthy.rvigor.adapter.AbnormalHeartAdapter
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.bean.HeartDataInfo
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.databinding.ActivityHeartBinding
import com.healthy.rvigor.mvp.contract.IHeartContract
import com.healthy.rvigor.mvp.presenter.HeartPresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.view.HeartDataView
import com.healthy.rvigor.view.SpecDateSelectedView
import java.util.Calendar
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 19:55
 * @UpdateRemark:
 */
class HeartActivity : BaseMVPActivity<ActivityHeartBinding, HeartPresenter>(), IHeartContract.View,
    View.OnClickListener {

    companion object {
        fun startHeartActivity(activity: Activity?, time: Long) {
            if (activity == null || time == 0L) return
            val intent = Intent(activity, HeartActivity::class.java)
            intent.putExtra(Constants.EXTRA, time)
            activity.startActivity(intent)
        }
    }

    private val mAbnormalAdapter by lazy { AbnormalHeartAdapter() }

    private var mSpeTime = 0L

    override fun getLayoutResID(): Int {
        return R.layout.activity_heart
    }

    override fun createPresenter(): HeartPresenter {
        return HeartPresenter()
    }

    override fun initView() {
        super.initView()

        binding?.rgDay?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbt_one_day -> {
                    binding?.heartView?.setStyle(HeartDataView.DataStyle.LINE)
                    binding?.specDate?.timeMode = SpecDateSelectedView.TimeMode.Day
                }

                R.id.rbt_7_day -> {
                    binding?.heartView?.setStyle(HeartDataView.DataStyle.BAR)
                    binding?.specDate?.timeMode = SpecDateSelectedView.TimeMode.Week
                }

                R.id.rbt_30_day -> {
                    binding?.heartView?.setStyle(HeartDataView.DataStyle.BAR)
                    binding?.specDate?.timeMode = SpecDateSelectedView.TimeMode.Month
                }
            }
            mAbnormalAdapter.mTimeMode = binding?.specDate?.timeMode
            getData()
        }
        binding?.specDate?.setEvent {
            getData()
        }
        binding?.heartView?.setOnItemTouchListener(object : HeartDataView.OnItemTouchListener {
            override fun onItemTouchListener(item: MainViewItem?, pos: Float) {
                if (item == null || item.data <= 0) {
                    binding?.tvHeart?.text = "--"
                    binding?.tvTime?.text = ""
                } else {
                    if (binding?.specDate?.timeMode == SpecDateSelectedView.TimeMode.Day) {
                        binding?.tvHeart?.text = item.data.toInt().toString()
                        binding?.tvTime?.text =
                            DateTimeUtils.s_long_2_str(item.time, DateTimeUtils.hm_format)
                    } else {
                        binding?.tvHeart?.text = "${item.minData.toInt()}-${item.maxData.toInt()}"
                        binding?.tvTime?.text =
                            DateTimeUtils.s_long_2_str(item.time, DateTimeUtils.month_day_format_1)
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
                getData()
            }
        }
        //异常数据列表
        mAbnormalAdapter.isStateViewEnable = true
        mAbnormalAdapter.mTimeMode = binding?.specDate?.timeMode
        val empty = layoutInflater.inflate(R.layout.empty_data, null, false)
        mAbnormalAdapter.stateView = empty
        binding?.rvAbnormalRate?.layoutManager = LinearLayoutManager(this@HeartActivity)
        binding?.rvAbnormalRate?.adapter = mAbnormalAdapter

        binding?.tvHeartMeasure?.setOnClickListener(this@HeartActivity)
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
        mSpeTime = System.currentTimeMillis()
        if (intent != null) {
            mSpeTime = intent.getLongExtra(Constants.EXTRA, System.currentTimeMillis())
        }
        binding?.specDate?.updateTime(Date(mSpeTime))
        getData()
    }

    private fun getData() {
        mPresenter.getHeartData(binding?.specDate)
        mPresenter.getAbnormalHeartData(binding?.specDate)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_heart_measure -> {
                startActivity(Intent(this@HeartActivity, HeartMeasureActivity::class.java))
            }
        }
    }

    override fun onHeartDataListener(
        itemList: MutableList<MainViewItem>?,
        lastItem: MainViewItem?,
        max: Int,
        min: Int,
        average: Int
    ) {
        if (lastItem == null) {
            binding?.tvHeart?.text = "--"
            binding?.tvTime?.text = ""
            binding?.heartView?.setHeartData(0f, 200f, null)
            binding?.tvAverageHeart?.text = "--"
            binding?.tvRestingRate?.text = "--"
            binding?.tvMaxHeart?.text = "--"
            binding?.tvMinRate?.text = "--"
            binding?.rectDataView?.setPos(0f)
            return
        }
        if (binding?.specDate?.timeMode == SpecDateSelectedView.TimeMode.Day) {
            binding?.tvHeart?.text = lastItem.data.toInt().toString()
            binding?.tvTime?.text =
                DateTimeUtils.s_long_2_str(lastItem.time, DateTimeUtils.hm_format)
        } else {
            binding?.tvHeart?.text = "${lastItem.minData.toInt()}-${lastItem.maxData.toInt()}"
            binding?.tvTime?.text =
                DateTimeUtils.s_long_2_str(lastItem.time, DateTimeUtils.month_day_format_1)
        }
        binding?.heartView?.setTimeMode(
            binding?.specDate?.timeMode,
            binding?.specDate?.datestart,
            binding?.specDate?.dateend
        )
        binding?.heartView?.setHeartData(min.toFloat(), max.toFloat(), itemList)
        binding?.tvMinRate?.text = "$min"
        binding?.tvMaxHeart?.text = "$max"
        binding?.tvAverageHeart?.text = "$average"
        binding?.heartView?.setTouchDataItem(lastItem)
        binding?.rectDataView?.setPos(((lastItem.currentDrawRect.left + lastItem.currentDrawRect.right) / 2).toFloat())
    }

    override fun onHeartInSleepResult(averageSleepHeart: Int) {
        if (averageSleepHeart <= 0) {
            binding?.tvRestingRate?.text = "--"
            return
        }
        binding?.tvRestingRate?.text = averageSleepHeart.toString()
    }

    override fun onAbnormalHeartDayResult(abnormalHeart: Int, heartList: List<HeartDataInfo>?) {
        if (heartList.isNullOrEmpty()) {
            mAbnormalAdapter.submitList(arrayListOf())
            return
        }
        mAbnormalAdapter.submitList(heartList)
    }
}