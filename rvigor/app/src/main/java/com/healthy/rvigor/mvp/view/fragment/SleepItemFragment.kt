package com.healthy.rvigor.mvp.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.healthy.rvigor.Constants
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.bean.SleepBarBean
import com.healthy.rvigor.bean.SleepDayBean
import com.healthy.rvigor.bean.SleepItem
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.FragmentSleepItemBinding
import com.healthy.rvigor.event.WatchDataEvent
import com.healthy.rvigor.mvp.contract.ISleepItemContract
import com.healthy.rvigor.mvp.presenter.SleepItemPresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.view.SleepDataView
import com.smart.adapter.interf.SmartFragmentImpl2
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.http.GET
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/25 22:44
 * @UpdateRemark:
 */
class SleepItemFragment : BaseMVPFragment<FragmentSleepItemBinding, SleepItemPresenter>(),
    SmartFragmentImpl2<SourceBean>, ISleepItemContract.View {

    companion object {

        private var instance: SleepItemFragment? = null

        @JvmStatic
        fun getInstance(): SleepItemFragment {
            if (instance == null) {
                instance = SleepItemFragment()
            }
            return instance as SleepItemFragment
        }
    }

    private var mSourceBean: SourceBean? = null

    override fun getLayoutResID(): Int {
        return R.layout.fragment_sleep_item
    }

    override fun createPresenter(): SleepItemPresenter {
        return SleepItemPresenter()
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun initView() {
        super.initView()
        binding?.sleepChartView?.setSleepTouchListener(object :
            SleepDataView.OnSleepTouchBarListener {

            override fun onSleepTouchBar(item: SleepItem?, pos: Float) {
                setSleepTouchItem(item)
                binding?.rectDataView?.setPos(pos)
            }
        })
    }

    override fun initData(bundle: Bundle?) {
        super.initData(bundle)
        getData()
    }

    override fun initSmartFragmentData(bean: SourceBean) {
        this.mSourceBean = bean
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchDataEvent(watchDataEvent: WatchDataEvent) {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0 || watchDataEvent.time <= 0) return
        if (watchDataEvent.time == mSourceBean?.time &&
            (watchDataEvent.type == Constants.SLEEP_TYPE || watchDataEvent.type == Constants.ALL_TYPE)
        ) {
            getData()
        }
    }

    private fun getData() {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0) return
        mPresenter.querySleepData(mSourceBean?.time ?: 0)
    }

    override fun onSleepData(barBean: SleepBarBean?) {
        //睡眠分段
        if (barBean == null || barBean.sleepLength <= 0) {
            binding?.llEmptySleep?.visibility = View.VISIBLE
            binding?.clSleepView?.visibility = View.INVISIBLE
        } else {
            binding?.llEmptySleep?.visibility = View.GONE
            binding?.clSleepView?.visibility = View.VISIBLE
            setSleepStages(barBean)
        }

        //3.睡眠总时长=长睡眠时长+小睡时长，睡眠总时长≥10小时，显示偏长、6-10小时，显示正常，<6小时，显示不足
        if (barBean == null || barBean.sleepLength <= 0) {
            binding?.tvTotalSleep?.text = DateTimeUtils.parseSleepTime(
                activity, 0, 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvTotalStatus?.visibility = View.INVISIBLE
        } else {
            binding?.tvTotalSleep?.text = DateTimeUtils.parseSleepTime(
                activity, barBean.sleepLength / (1000 * 60), 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvTotalStatus?.visibility = View.VISIBLE
            val sleepHour = barBean.sleepLength / (1000 * 60 * 60)
            if (sleepHour >= 10) {
                binding?.tvTotalStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvTotalStatus?.text = resources.getString(R.string.longish)
                binding?.tvTotalStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else if (sleepHour >= 6) {
                binding?.tvTotalStatus?.text = resources.getString(R.string.normal)
                binding?.tvTotalStatus?.setTextColor(resources.getColor(R.color.color_46F79F))
                binding?.tvTotalStatus?.setBackgroundResource(R.drawable.bg_sleep_normal_status)
            } else {
                binding?.tvTotalStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvTotalStatus?.text = resources.getString(R.string.insufficient)
                binding?.tvTotalStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            }
        }

        //长睡眠
        var longSleep = 0L
        if (barBean != null && barBean.dayBeanList != null && barBean.dayBeanList.size > 0) {
            for (dayBean in barBean.dayBeanList) {
                if (dayBean.sleepLen > 0) {
                    longSleep += dayBean.sleepLen
                }
            }
        }
        if (barBean != null && longSleep > 0) {
            binding?.tvLongSleep?.text = DateTimeUtils.parseSleepTime(
                activity, longSleep / (1000 * 60), 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvLongEfficiency?.text = String.format(
                resources.getString(R.string.sleep_efficiency),
                (longSleep / barBean.sleepLength * 100f).roundToInt().toString() + "%"
            )
        } else {
            binding?.tvLongSleep?.text = DateTimeUtils.parseSleepTime(
                activity, 0, 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvLongEfficiency?.text =
                String.format(resources.getString(R.string.sleep_efficiency), "--%")
        }


        var fallPer = 0
        var deepPer = 0
        var remPer = 0
        var lightPer = 0
        if (barBean != null && barBean.getSleepLength() > 0) {
            fallPer = ((barBean.fallLength * 100f) / barBean.getSleepLength()).roundToInt()
            deepPer = ((barBean.deepLength * 100f) / barBean.getSleepLength()).roundToInt()
            remPer = ((barBean.remLength * 100f) / barBean.getSleepLength()).roundToInt()
            lightPer = 100 - fallPer - deepPer - remPer
        }

        //4.浅睡占比≥55%,显示偏高，<55%显示正常；
        if (barBean != null && barBean.lightLength > 0) {
            binding?.tvLightSleep?.text = DateTimeUtils.parseSleepTime(
                activity, barBean.lightLength / (1000 * 60), 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvLightEfficiency?.text = "$lightPer%"
            binding?.tvLightStatus?.visibility = View.VISIBLE
            if (lightPer >= 55) {
                binding?.tvLightStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvLightStatus?.text = resources.getString(R.string.slightly_higher)
                binding?.tvLightStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else {
                binding?.tvLightStatus?.setTextColor(resources.getColor(R.color.color_46F79F))
                binding?.tvLightStatus?.text = resources.getString(R.string.normal)
                binding?.tvLightStatus?.setBackgroundResource(R.drawable.bg_sleep_normal_status)
            }
        } else {
            binding?.tvLightSleep?.text = DateTimeUtils.parseSleepTime(
                activity, 0, 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvLightEfficiency?.text = "--%"
            binding?.tvLightStatus?.visibility = View.GONE
        }

        // ，深睡<20%，显示偏低，>60%，显示偏高，其余显示正常，
        if (barBean != null && barBean.deepLength > 0) {
            binding?.tvDeepSleep?.text = DateTimeUtils.parseSleepTime(
                activity, barBean.deepLength / (1000 * 60), 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvDeepStatus?.visibility = View.VISIBLE
            if (deepPer > 60) {
                binding?.tvDeepStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvDeepStatus?.text = resources.getString(R.string.slightly_higher)
                binding?.tvDeepStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else if (deepPer < 20) {
                binding?.tvDeepStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvDeepStatus?.text = resources.getString(R.string.slightly_low)
                binding?.tvDeepStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else {
                binding?.tvDeepStatus?.setTextColor(resources.getColor(R.color.color_46F79F))
                binding?.tvDeepStatus?.text = resources.getString(R.string.normal)
                binding?.tvDeepStatus?.setBackgroundResource(R.drawable.bg_sleep_normal_status)
            }
        } else {
            binding?.tvDeepSleep?.text = DateTimeUtils.parseSleepTime(
                activity, 0, 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvDeepStatus?.visibility = View.INVISIBLE
        }

        // REM<10%，显示偏低，>30%，显示偏高，其余正常；
        if (barBean != null && barBean.remLength > 0) {
            binding?.tvRemSleep?.text = DateTimeUtils.parseSleepTime(
                activity, barBean.remLength / (1000 * 60), 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvRemEfficiency?.text = "$remPer%"
            if (remPer > 30) {
                binding?.tvRemStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvRemStatus?.text = resources.getString(R.string.slightly_higher)
                binding?.tvRemStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else if (remPer < 10) {
                binding?.tvRemStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvRemStatus?.text = resources.getString(R.string.slightly_low)
                binding?.tvRemStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else {
                binding?.tvRemStatus?.setTextColor(resources.getColor(R.color.color_46F79F))
                binding?.tvRemStatus?.text = resources.getString(R.string.normal)
                binding?.tvRemStatus?.setBackgroundResource(R.drawable.bg_sleep_normal_status)
            }
            binding?.tvRemStatus?.visibility = View.VISIBLE
        } else {
            binding?.tvRemSleep?.text = DateTimeUtils.parseSleepTime(
                activity, 0, 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvRemEfficiency?.text = "--%"
            binding?.tvRemStatus?.visibility = View.GONE
        }

        // 清醒0-1次正常，≥2次偏多
        if (barBean != null && barBean.wakeCount > 0) {
            binding?.tvWakeSleep?.text = "${barBean.wakeCount}${resources.getString(R.string.time)}"
            binding?.tvWakeStatus?.visibility = View.VISIBLE
            if (barBean.wakeCount >= 2) {
                binding?.tvWakeStatus?.setTextColor(resources.getColor(R.color.color_C53B11))
                binding?.tvWakeStatus?.text = resources.getString(R.string.excessive)
                binding?.tvWakeStatus?.setBackgroundResource(R.drawable.bg_sleep_red_status)
            } else {
                binding?.tvWakeStatus?.setTextColor(resources.getColor(R.color.color_46F79F))
                binding?.tvWakeStatus?.text = resources.getString(R.string.normal)
                binding?.tvWakeStatus?.setBackgroundResource(R.drawable.bg_sleep_normal_status)
            }
        } else {
            binding?.tvWakeSleep?.text = "--"
            binding?.tvWakeStatus?.visibility = View.INVISIBLE
        }

        if (barBean != null && barBean.siestaLength > 0) {
            val day = DateTimeUtils.s_long_2_str(barBean.sleepDay, DateTimeUtils.day_format)
            var siestaStartTime = DateTimeUtils.s_str_to_long(
                day + " " + barBean.startSiestaTime,
                DateTimeUtils.day_hm_format
            )
            var siestaEndTime = DateTimeUtils.s_str_to_long(
                day + " " + barBean.endSiestaTime,
                DateTimeUtils.day_hm_format
            )
            val dayBean = SleepDayBean()
            dayBean.startTime = siestaStartTime
            dayBean.endTime = siestaEndTime
            val radio =
                ((barBean.siestaLength * 100f) / (dayBean.endTime - dayBean.startTime)).roundToInt() / 100f
            dayBean.radio = radio
            dayBean.sleepLen = barBean.siestaLength

            binding?.tvSiestaSleep?.text = DateTimeUtils.parseSleepTime(
                activity, barBean.siestaLength / (1000 * 60), 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
            binding?.tvSiestaTime?.text = "${barBean.startSiestaTime}-${barBean.endSiestaTime}"

            val siestaLen = barBean.siestaLength / (1000 * 60)
            binding?.tvSiestaLen?.text = "$siestaLen${resources.getString(R.string.minute)}"
            binding?.clEmptySiesta?.visibility = View.GONE
            binding?.llSiesta?.visibility = View.VISIBLE
        } else {
            binding?.llSiesta?.visibility = View.GONE
            binding?.clEmptySiesta?.visibility = View.VISIBLE
            binding?.tvSiestaSleep?.text = DateTimeUtils.parseSleepTime(
                activity, 0, 30, 14,
                Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
            )
        }

        var sleepItem: SleepItem? = null
        if (barBean != null && barBean?.list != null && barBean.list.size > 0) {
            sleepItem = barBean.list[0]
            setSleepTouchItem(sleepItem)
        }

    }

    private fun setSleepTouchItem(item: SleepItem?) {
        if (item == null) return
        val len = item.endTime - item.startTime
        binding?.tvSleepLen?.text = DateTimeUtils.parseTime(
            activity, len / (1000 * 60), 30, 14,
            Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
        )
        var txt = ""
        if (item.getSleepType() == SleepItem.FALL_SLEEP_TYPE) {
            txt = resources.getString(R.string.fall_asleep)
        } else if (item.getSleepType() == SleepItem.LIGHT_SLEEP_TYPE) {
            txt = resources.getString(R.string.light_sleep_view)
        } else if (item.getSleepType() == SleepItem.DEEP_SLEEP_TYPE) {
            txt = resources.getString(R.string.deep_sleep_view)
        } else if (item.getSleepType() == SleepItem.REM_SLEEP_TYPE) {
            txt = resources.getString(R.string.sleep_rem)
        } else if (item.getSleepType() == SleepItem.WAKE_SLEEP_TYPE) {
            txt = resources.getString(R.string.sober)
        }
        var startTime = DateTimeUtils.s_long_2_str(
            item.startTime,
            DateTimeUtils.hm_format
        )
        var endTime = DateTimeUtils.s_long_2_str(
            item.endTime,
            DateTimeUtils.hm_format
        )
        binding?.tvSleepTip?.text = "$txt $startTime-$endTime"

//        binding?.sleepChartView?.setTouchDataItem(item)
        binding?.rectDataView?.setRectData(0, 0, 0f)
    }

    //睡眠分段图
    private fun setSleepStages(bean: SleepBarBean?) {
        if (bean == null || binding?.sleepChartView == null) {
            binding?.sleepChartView?.cleanTouchItem()
            return
        }
        binding?.sleepChartView?.cleanTouchItem()
        var index = 0
        var len: Long = 0
        if (bean.list != null && bean.list.isNotEmpty()) {
            for (item in bean.list) {
                //1 深睡 //2 浅睡 //3 清醒 //4 入睡 //5 熬夜
                if (item.getSleepType() === SleepItem.DEEP_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.LIGHT_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.WAKE_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.FALL_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.END_SLEEP_TYPE ||
                    item.getSleepType() === SleepItem.REM_SLEEP_TYPE
                ) {
                    if (item.getSleepType() === SleepItem.FALL_SLEEP_TYPE) {
                        index++
                    }
//                    if (BuildConfig.DEBUG && item.getSleepType() == SleepItem.LIGHT_SLEEP_TYPE){
//                        item.setSleepType(SleepItem.WAKE_SLEEP_TYPE);
//                    }
                    if (item.getSleepType() !== SleepItem.END_SLEEP_TYPE) {
                        len += item.endTime - item.startTime
                    }
//                    LogUtils.i(" start 222 " + DateTimeUtils.s_long_2_str(item.startTime, DateTimeUtils.day_hm_format) + " "  +
//                            " end " + DateTimeUtils.s_long_2_str(item.endTime, DateTimeUtils.day_hm_format) + " " + item.getSleepType());
                    binding?.sleepChartView?.addDateItem(item)
                }
            }
        }
        binding?.sleepChartView?.setSleepDayBean(bean.dayBeanList)
        if (bean.startTime !== 0L && bean.endTime !== 0L) {
            binding?.sleepChartView?.setTimeRange(bean.startTime, bean.endTime)
//            LogUtils.i(" start 222 11 " + DateTimeUtils.s_long_2_str(bean.getStartTime(), DateTimeUtils.day_hm_format) + " " + len +
//                    " end " + DateTimeUtils.s_long_2_str(bean.getStartTime() + len, DateTimeUtils.day_hm_format) + " ");
        }
    }

}