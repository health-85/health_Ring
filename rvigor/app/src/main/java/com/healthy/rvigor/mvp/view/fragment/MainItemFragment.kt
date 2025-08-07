package com.healthy.rvigor.mvp.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.LinearInterpolator
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.SleepBarBean
import com.healthy.rvigor.bean.SleepDayBean
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.FragmentMainItemBinding
import com.healthy.rvigor.event.WatchDataEvent
import com.healthy.rvigor.event.WatchSyncEvent
import com.healthy.rvigor.mvp.contract.IMainItemContract
import com.healthy.rvigor.mvp.presenter.MainItemPresenter
import com.healthy.rvigor.mvp.view.activity.health.HeartActivity
import com.healthy.rvigor.mvp.view.activity.health.MindActivity
import com.healthy.rvigor.mvp.view.activity.health.OxActivity
import com.healthy.rvigor.mvp.view.activity.main.MainActivity
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.WatchBeanUtil
import com.smart.adapter.interf.SmartFragmentImpl2
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Calendar

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/17 18:11
 * @UpdateRemark:
 */
class MainItemFragment : BaseMVPFragment<FragmentMainItemBinding, MainItemPresenter>(),
    IMainItemContract.View,
    SmartFragmentImpl2<SourceBean>, View.OnClickListener {

    companion object {
        private var mMindTime = 0L
        private var instance: MainItemFragment? = null

        @JvmStatic
        fun getInstance(): MainItemFragment {
            if (instance == null) {
                instance = MainItemFragment()
            }
            return instance as MainItemFragment
        }
    }

    private var mSourceBean: SourceBean? = null

    //心理时间
//    private var mMindTime = 0L
    private var mAverageTire = 0f
    private var mAveragePressure = 0f

    override fun getLayoutResID(): Int {
        return R.layout.fragment_main_item
    }

    override fun createPresenter(): MainItemPresenter {
        return MainItemPresenter()
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun initData(bundle: Bundle?) {
        super.initData(bundle)
        mMindTime = 0
        getData()
    }

    override fun initView() {
        super.initView()
        binding?.refresh?.setOnRefreshListener {
            binding?.refresh?.isRefreshing = false
            MyApplication.instance().watchSyncUtils.watchSync()
        }
        binding?.imgSleepRight?.setOnClickListener(this@MainItemFragment)
        binding?.imgMotionRight?.setOnClickListener(this@MainItemFragment)
        binding?.imgMindRight?.setOnClickListener(this@MainItemFragment)
        binding?.imgOxRight?.setOnClickListener(this@MainItemFragment)
        binding?.imgHeartRight?.setOnClickListener(this@MainItemFragment)
    }

    override fun initSmartFragmentData(bean: SourceBean) {
        this.mSourceBean = bean
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchSyncEvent(watchSyncEvent: WatchSyncEvent) {
        binding?.refresh?.isRefreshing = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchDataEvent(watchDataEvent: WatchDataEvent) {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0 || watchDataEvent.time <= 0) return
        LogUtils.i(
            " onWatchDataEvent type ${watchDataEvent.type} " +
                    "mSourceBean?.time ${
                        DateTimeUtils.s_long_2_str(
                            mSourceBean?.time ?: 0L,
                            DateTimeUtils.f_format
                        )
                    } " +
                    "watchDataEvent.time ${
                        DateTimeUtils.s_long_2_str(
                            watchDataEvent.time,
                            DateTimeUtils.f_format
                        )
                    }"
        )
        if (watchDataEvent.time == mSourceBean?.time &&
            (watchDataEvent.type == Constants.MAIN_TYPE || watchDataEvent.type == Constants.ALL_TYPE)
        ) {
            getData()
        }
    }

    fun getData() {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0) return
        //获取昨天的数据
        mPresenter.getLastDayData(mSourceBean?.time ?: 0)
        //获取睡眠
        mPresenter.getSleepData(mSourceBean?.time ?: 0, false)
        //获取运动
        mPresenter.getMotionData(mSourceBean?.time ?: 0, false)
        //获取心理
        mPresenter.getMindData(mSourceBean?.time ?: 0, false)
        //获取心率
        mPresenter.getHeartData(mSourceBean?.time ?: 0, false)
        //获取血氧
        mPresenter.getOxData(mSourceBean?.time ?: 0, false)
    }

    override fun onMotionData(
        itemList: List<MainViewItem>?,
        lastItem: MainViewItem?,
        min: Int,
        max: Int,
        totalStep: Int
    ) {
        val targetStep: Int =
            SPUtil.getData(activity, SpConfig.TARGET_STEP, Constants.DEFAULT_TARGET_STEP) as Int

        if (itemList.isNullOrEmpty()) {
            binding?.motionSeekbar?.min = 0f
            binding?.motionSeekbar?.max = targetStep.toFloat()
            binding?.motionSeekbar?.setProgress(0f)
            binding?.tvStartStep?.text =
                String.format(resources.getString(R.string.sort_step_tip), "--", "--")
            return
        }

        binding?.motionView?.setMotionData(min.toFloat(), max.toFloat(), itemList)

        binding?.motionSeekbar?.max = targetStep.toFloat()
        binding?.motionSeekbar?.min = 0f
        if (totalStep < targetStep) {
            var step = (targetStep - totalStep).toString()
            val time = DateTimeUtils.parseTimeS(activity, WatchBeanUtil.getStepTime(totalStep))
            binding?.motionSeekbar?.setProgress(totalStep.toFloat())
            var originalString = resources.getString(R.string.sort_step_tip)
            var msg = String.format(originalString, step, time)
            binding?.tvStartStep?.text = AppUtils.getBlodBigTxt(msg, originalString, step, 30)
        } else {
            binding?.motionSeekbar?.setProgress(targetStep.toFloat())
            binding?.tvStartStep?.text = resources.getString(R.string.achieved_step)
        }
    }

    override fun onTireData(lastItem: MainViewItem?, average: Float) {
        if (lastItem == null) {
            binding?.tvFatigue?.text = "--"
            return
        }
        if (lastItem.time >= mMindTime) {
            mMindTime = lastItem.time
        }
        if (lastItem.data <= 29) {
            binding?.tvFatigue?.text = resources.getString(R.string.normal)
        } else if (lastItem.data <= 59) {
            binding?.tvFatigue?.text = resources.getString(R.string.slight)
        } else if (lastItem.data <= 79) {
            binding?.tvFatigue?.text = resources.getString(R.string.moderate)
        } else {
            binding?.tvFatigue?.text = resources.getString(R.string.severe)
        }
        mAverageTire = average
        setMindData()
    }

    override fun onPressureData(lastItem: MainViewItem?, average: Float) {
        if (lastItem == null) {
            binding?.tvStress?.text = "--"
            return
        }
        if (lastItem.time >= mMindTime) {
            mMindTime = lastItem.time
        }
        if (lastItem.data <= 29) {
            binding?.tvStress?.text = resources.getString(R.string.relax)
        } else if (lastItem.data <= 59) {
            binding?.tvStress?.text = resources.getString(R.string.normal)
        } else if (lastItem.data <= 79) {
            binding?.tvStress?.text = resources.getString(R.string.slight)
        } else {
            binding?.tvStress?.text = resources.getString(R.string.high_pressure)
        }
        mAveragePressure = average
        setMindData()
    }

    override fun onEmotionData(lastItem: MainViewItem?, average: Float) {
        if (lastItem == null) {
            binding?.tvEmotional?.text = "--"
            return
        }
        if (lastItem.data <= 33) {
            binding?.tvEmotional?.text = resources.getString(R.string.emotion_downcast)
        } else if (lastItem.data <= 66) {
            binding?.tvEmotional?.text = resources.getString(R.string.emotion_calmness)
        } else {
            binding?.tvEmotional?.text = resources.getString(R.string.emotion_excite)
        }
    }

    override fun onHeartData(
        itemList: List<MainViewItem>?,
        lastItem: MainViewItem?,
        min: Int,
        max: Int,
        average: Int
    ) {
        if (lastItem == null) {
            binding?.tvRate?.text = "--"
            binding?.tvRateTime?.text = ""
            binding?.heartView?.setHeartData(0f, 200f, null)
            return
        }
        binding?.heartView?.setHeartData(min.toFloat(), max.toFloat(), itemList)
        binding?.tvRate?.text = lastItem.data.toInt().toString()
        binding?.tvRateTime?.text =
            DateTimeUtils.s_long_2_str(lastItem.time, DateTimeUtils.hm_format)
    }

    override fun onOxData(
        itemList: List<MainViewItem>?,
        lastItem: MainViewItem?,
        min: Int,
        max: Int,
        average: Int
    ) {
        if (lastItem == null) {
            binding?.tvOx?.text = "--"
            binding?.tvOxTime?.text = ""
            binding?.oxView?.setOxData(60f, 100f, null)
            return
        }
        binding?.oxView?.setOxData(min.toFloat(), 100f, itemList)
        binding?.tvOx?.text = lastItem.data.toInt().toString()
        binding?.tvOxTime?.text = DateTimeUtils.s_long_2_str(lastItem.time, DateTimeUtils.hm_format)
    }

    override fun onSleepData(barBean: SleepBarBean?) {
        if (barBean == null) {
            binding?.tvSleepLen?.text = "--"
            binding?.tvStartEndTime?.text = "--"
            binding?.tvQualityScore?.text = "--"
            binding?.clSleepRisk?.visibility = View.GONE
            binding?.barSleepScore?.setPercentData(0, LinearInterpolator())
            return
        }
        var sleepLen = 0L
        if (barBean.getSleepLength() > 0) {
            var startTime = ""
            var endTime = ""
            val calendar = Calendar.getInstance()
            if (barBean.dayBeanList != null && barBean.dayBeanList.size > 0) {
                barBean.dayBeanList.forEach {
                    calendar.timeInMillis = it.startTime
                    var time = calendar.get(Calendar.HOUR_OF_DAY)
                    if (time >= 20 || time < 8) {
                        sleepLen += it.sleepLen
                        if (TextUtils.isEmpty(startTime)) {
                            startTime =
                                DateTimeUtils.s_long_2_str(it.startTime, DateTimeUtils.hm_format)
                        }
                        endTime = DateTimeUtils.s_long_2_str(it.endTime, DateTimeUtils.hm_format)
                        LogUtils.i(" time $time startTime ${DateTimeUtils.s_long_2_str(it.startTime, DateTimeUtils.hm_format)} endTime $endTime")
                    }
                }
            } else {
                sleepLen = barBean.getSleepLength()
                startTime = DateTimeUtils.s_long_2_str(barBean.startTime, DateTimeUtils.hm_format)
                endTime = DateTimeUtils.s_long_2_str(barBean.endTime, DateTimeUtils.hm_format)
            }
            binding?.tvStartEndTime?.text = "$startTime-$endTime"
        } else {
            binding?.tvStartEndTime?.text = "${barBean.startSiestaTime}-${barBean.endSiestaTime}"
        }

        val sleepTime = /*(barBean.getSleepLength() + barBean.siestaLength)*/ sleepLen / (1000 * 60)
        binding?.tvSleepLen?.text = DateTimeUtils.parseTime(
            activity, sleepTime, 30, 14,
            Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
        )
        binding?.tvQualityScore?.text = barBean.totalScore.toString()
        binding?.barSleepScore?.setPercentData(barBean.totalScore, LinearInterpolator())
        if (barBean.totalScore >= 85) {
            binding?.barSleepScore?.setArcColor(Color.parseColor("#25E5B6"))
        } else if (barBean.totalScore in 65..84) {
            binding?.barSleepScore?.setArcColor(Color.parseColor("#FFCD51"))
        } else {
            binding?.barSleepScore?.setArcColor(Color.parseColor("#BF3131"))
        }

        binding?.clSleepRisk?.visibility = View.VISIBLE

        /*当深睡时长≤1小时，显示深睡眠严重不足，1-2小时，显示深睡眠待加强；≥2小时，深睡眠充足；*/
        var msg = ""
        val deepLen = barBean.deepLength / (1000 * 60 * 60).toFloat()
        if (deepLen < 1f) {
            msg = resources.getString(R.string.sleep_low_deep_sleep)
        } else if (deepLen in 1f..2f) {
            msg = resources.getString(R.string.sleep_strength_deep_sleep)
        } else {
            msg = resources.getString(R.string.sleep_sufficient_deep_sleep)
        }
        binding?.tvDeepSleep?.text = msg

    }

    /*静息心率>70，显示静心心率过高，夜间恢复不好；55-70，显示静息心率正常，十分健康。≤55，显示静息心率偏低*/
    override fun onSleepHeartRate(rate: Int) {
        if (rate <= 0) {
            binding?.clSleepHeart?.visibility = View.GONE
            binding?.imgDeepSleepLine?.visibility = View.GONE
            return
        }
        binding?.clSleepHeart?.visibility = View.VISIBLE
        binding?.imgDeepSleepLine?.visibility = View.VISIBLE
        if (rate > 70) {
            binding?.tvSleepHeart?.text = resources.getString(R.string.sleep_high_heart)
        } else if (rate > 55) {
            binding?.tvSleepHeart?.text = resources.getString(R.string.sleep_normal_heart)
        } else {
            binding?.tvSleepHeart?.text = resources.getString(R.string.sleep_low_heart)
        }
    }

    /*当最低血氧≤94%时，显示疑似呼吸窘迫，当最低血氧≥95%时，显示睡眠呼吸平稳*/
    override fun onSleepOx(ox: Int) {
        if (ox <= 0) {
            binding?.clSleepOx?.visibility = View.GONE
            return
        }
        binding?.clSleepOx?.visibility = View.VISIBLE
        if (ox <= 94) {
            binding?.tvSleepOx?.text =
                String.format(resources.getString(R.string.sleep_low_ox), ox)
        } else {
            binding?.tvSleepOx?.text =
                String.format(resources.getString(R.string.sleep_normal_ox), ox)
        }
    }

    //昨天表现
    private var mSleepScore = -1
    private var mMotionScore = -1f
    private var mHeartScore = -1f
    private var mOxScore = -1f
    private var mLastPressure = -1f
    private var mLastTire = -1f

    private var lockObject = Object()

    override fun onLastSleepScore(sleepScore: Int) {
        mSleepScore = sleepScore
        setLastResult()
    }

    override fun onLastMotionScore(motionScore: Float) {
        mMotionScore = motionScore
        setLastResult()
    }

    override fun onLastHeartScore(heartScore: Float) {
        mHeartScore = heartScore
        setLastResult()
    }

    override fun onLastOxScore(oxScore: Float) {
        mOxScore = oxScore
        setLastResult()
    }

    override fun onLastAveragePressure(averagePressure: Float) {
        mLastPressure = averagePressure
        setLastResult()
    }

    override fun onLastAverageTire(averageTire: Float) {
        mLastTire = averageTire
        setLastResult()
    }

    /*
     极差：0-50分
     欠佳：51-60
     一般：61-80
     不错：81-90
     优秀：91-100
     表现得分 = (睡眠得分 × 0.3) + (运动得分 × 0.3) + (心率得分 × 0.1) + (血氧得分 × 0.1) + (心理健康得分 × 0.2)*/
    private fun setLastResult() {
        synchronized(lockObject) {
            LogUtils.i(
                " setLastResult time ${
                    DateTimeUtils.s_long_2_str(
                        mSourceBean?.time ?: 0,
                        DateTimeUtils.f_format
                    )
                } " +
                        "mSleepScore $mSleepScore mMotionScore $mMotionScore mHeartScore $mHeartScore " +
                        "mOxScore $mOxScore mLastPressure $mLastPressure mLastTire $mLastTire"
            )
            if ((mSleepScore == -1 || mMotionScore == -1f || mHeartScore == -1f || mOxScore == -1f || mLastPressure == -1f || mLastTire == -1f)
                || (mSleepScore == 0 && mMotionScore == 0f && mHeartScore == 0f && mOxScore == 0f && mLastPressure == 0f && mLastTire == 0f)
            ) {
                binding?.barPerformance?.setPercentData(0)
                binding?.tvPerformanceLevel?.text = "--"
                binding?.imgPerformanceFace?.visibility = View.GONE
                binding?.tvPerformanceRemark?.visibility = View.GONE
                return
            }

            binding?.imgPerformanceFace?.visibility = View.VISIBLE
            binding?.tvPerformanceRemark?.visibility = View.VISIBLE

            var score =
                mSleepScore * 0.3f + mMotionScore * 0.3f + mHeartScore * 0.1 + mOxScore * 0.1 +
                        (100 - 0.6f * mLastPressure - 0.4f * mLastTire) * 0.2f
            if (score <= 0) {
                score = 0.0
            } else if (score >= 100) {
                score = 100.0
            }
            if (score <= 50) {
                binding?.barPerformance?.setArcColor(Color.parseColor("#BF3131"))
                binding?.imgPerformanceFace?.setImageResource(R.mipmap.ic_face_1)
                binding?.tvPerformanceLevel?.text = resources.getString(R.string.range)
                binding?.tvPerformanceRemark?.text = resources.getString(R.string.range_tip)
            } else if (score <= 60) {
                binding?.barPerformance?.setArcColor(Color.parseColor("#E55F25"))
                binding?.imgPerformanceFace?.setImageResource(R.mipmap.ic_face_2)
                binding?.tvPerformanceLevel?.text = resources.getString(R.string.poor)
                binding?.tvPerformanceRemark?.text = resources.getString(R.string.poor_tip)
            } else if (score <= 80) {
                binding?.barPerformance?.setArcColor(Color.parseColor("#FFCD51"))
                binding?.imgPerformanceFace?.setImageResource(R.mipmap.ic_face_3)
                binding?.tvPerformanceLevel?.text = resources.getString(R.string.commonly)
                binding?.tvPerformanceRemark?.text = resources.getString(R.string.commonly_tip)
            } else if (score <= 90) {
                binding?.barPerformance?.setArcColor(Color.parseColor("#46F79F"))
                binding?.imgPerformanceFace?.setImageResource(R.mipmap.ic_face_4)
                binding?.tvPerformanceLevel?.text = resources.getString(R.string.not_bad)
                binding?.tvPerformanceRemark?.text = resources.getString(R.string.not_bad_tip)
            } else {
                binding?.barPerformance?.setArcColor(Color.parseColor("#25E5B6"))
                binding?.imgPerformanceFace?.setImageResource(R.mipmap.ic_face_5)
                binding?.tvPerformanceLevel?.text = resources.getString(R.string.excellent)
                binding?.tvPerformanceRemark?.text = resources.getString(R.string.excellent_tip)
            }
            binding?.barPerformance?.setPercentData(score.toInt())
            LogUtils.i(" sweepAngle score $score ")
        }
    }

    //心理状态数值=0.6 * 平均压力数值 + 0.4 *平均疲劳数值
    //正常：0-50
    //轻度失调：51-80
    //严重障碍：81-100
    private fun setMindData() {
        if (mMindTime <= 0) {
            binding?.tvMindTime?.text = ""
            binding?.tvMindState?.text = "--"
            binding?.tvMindMsg?.text = ""
            binding?.imgMindStatus?.setImageResource(R.drawable.svg_mind_mild)
            return
        }
        binding?.tvMindTime?.text = DateTimeUtils.s_long_2_str(mMindTime, DateTimeUtils.hm_format)
//        if (mAverageTire <= 0 || mAveragePressure <= 0) return
        var mind = 0.6f * mAveragePressure + 0.4 * mAverageTire
        if (mind <= 50) {
            binding?.imgMindStatus?.setImageResource(R.drawable.svg_mind_normal)
            binding?.tvMindState?.text = resources.getString(R.string.normal)
            binding?.tvMindMsg?.text = resources.getString(R.string.normal_tip)
        } else if (mind <= 80) {
            binding?.imgMindStatus?.setImageResource(R.drawable.svg_mind_mild)
            binding?.tvMindState?.text = resources.getString(R.string.mild_disorder)
            binding?.tvMindMsg?.text = resources.getString(R.string.mild_disorder_tip)
        } else {
            binding?.imgMindStatus?.setImageResource(R.drawable.svg_mind_serious)
            binding?.tvMindState?.text = resources.getString(R.string.serious_obstacle)
            binding?.tvMindMsg?.text = resources.getString(R.string.serious_obstacle_tip)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_sleep_right -> {
                if (activity is MainActivity) {
                    (activity as MainActivity).showFragment(Constants.SLEEP_FRAGMENT_TAG)
                }
            }

            R.id.img_motion_right -> {
                if (activity is MainActivity) {
                    (activity as MainActivity).showFragment(Constants.MOTION_FRAGMENT_TAG)
                }
            }

            R.id.img_mind_right -> {
                MindActivity.startMindActivity(activity, mSourceBean?.time ?: 0)
            }

            R.id.img_ox_right -> {
                OxActivity.startOxActivity(activity, mSourceBean?.time ?: 0)
            }

            R.id.img_heart_right -> {
                HeartActivity.startHeartActivity(activity, mSourceBean?.time ?: 0)
            }
        }
    }

}