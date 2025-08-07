package com.healthy.rvigor.mvp.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.healthbit.framework.util.DeviceUtil
import com.healthy.rvigor.Constants
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.FragmentMotionItemBinding
import com.healthy.rvigor.event.WatchDataEvent
import com.healthy.rvigor.mvp.contract.IMotionItemContract
import com.healthy.rvigor.mvp.presenter.MotionItemPresenter
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.WatchBeanUtil
import com.healthy.rvigor.view.MotionStepView
import com.healthy.rvigor.view.OxDataView
import com.healthy.rvigor.view.SpecDateSelectedView
import com.smart.adapter.interf.SmartFragmentImpl2
import com.smart.adapter.util.ScreenUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.http.GET
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/26 15:13
 * @UpdateRemark:
 */
class MotionItemFragment : BaseMVPFragment<FragmentMotionItemBinding, MotionItemPresenter>(),
    SmartFragmentImpl2<SourceBean>, IMotionItemContract.View, View.OnClickListener {

    companion object {

        private var instance: MotionItemFragment? = null

        @JvmStatic
        fun getInstance(): MotionItemFragment {
            if (instance == null) {
                instance = MotionItemFragment()
            }
            return instance as MotionItemFragment
        }
    }

    private var mSourceBean: SourceBean? = null

    override fun getLayoutResID(): Int {
        return R.layout.fragment_motion_item
    }

    override fun createPresenter(): MotionItemPresenter {
        return MotionItemPresenter()
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun initData(bundle: Bundle?) {
        super.initData(bundle)
        getData()
    }

    override fun initView() {
        super.initView()

        binding?.stepView?.setOnItemTouchListener(object : MotionStepView.OnItemTouchListener {
            override fun onItemTouchListener(item: MainViewItem?, pos: Float) {
                if (item == null || item.data <= 0) {
                    binding?.tvStep?.text = "--"
                    binding?.tvStepTime?.text = ""
                } else {
                    binding?.tvStep?.text = item.data.toInt().toString()
                    binding?.tvStepTime?.text = item.showTimeString
                }
                binding?.rectDataView?.setPos(pos)
            }
        })
    }

    override fun initSmartFragmentData(bean: SourceBean) {
        this.mSourceBean = bean
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchDataEvent(watchDataEvent: WatchDataEvent) {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0 || watchDataEvent.time <= 0) return
        if (watchDataEvent.time == mSourceBean?.time &&
            (watchDataEvent.type == Constants.MOTION_TYPE || watchDataEvent.type == Constants.ALL_TYPE)
        ) {
            getData()
        }
    }

    fun getData() {
        if (mSourceBean == null) return
        mPresenter.queryMotionData(mSourceBean?.time ?: 0)
//        mPresenter.getStrengthData(mSourceBean?.time ?: 0)
    }

    override fun onClick(v: View?) {

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
        if (lastItem == null) {
            binding?.tvTotalStep?.text = "--"
            binding?.tvStepTarget?.text = "/$targetStep"
            binding?.tvCalorie?.text = "--"
            binding?.tvDistance?.text = "--"
            binding?.barStepPer?.setPercentData(0, LinearInterpolator())
            binding?.barCaloriePer?.setPercentData(0, LinearInterpolator())
            binding?.barDistancePer?.setPercentData(0, LinearInterpolator())
            binding?.stepView?.setMotionData(0f, max.toFloat(), itemList)
            binding?.tvStep?.text = "--"
            binding?.tvStepTime?.text = ""
            binding?.stepView?.setTouchDataItem(null)
            return
        }

        var totalCalorie = WatchBeanUtil.byteToCalorie(totalStep)
        var totalKm = WatchBeanUtil.byteToKm(totalStep)

        var testCalorie = WatchBeanUtil.byteToCalorie(targetStep)
        var testKm = WatchBeanUtil.byteToKm(targetStep)

        LogUtils.i(" onMotionData totalStep $totalStep totalCalorie $totalCalorie totalKm $totalKm testCalorie $testCalorie testKm $testKm")

        binding?.tvTotalStep?.text = totalStep.toString()
        binding?.tvStepTarget?.text = "/$targetStep"
        binding?.tvCalorie?.text = totalCalorie.times(10).roundToInt().div(10f).toString()
        binding?.tvCalorieTarget?.text = "/500"
        binding?.tvDistance?.text = totalKm.times(100).roundToInt().div(100f).toString()
        binding?.tvDistanceTarget?.text = "/5"

        var stepPer = (totalStep / targetStep.toFloat() * 100)
        if (stepPer > 100) stepPer = 100f
        if (stepPer <= 0) stepPer = 0f
        binding?.barStepPer?.setPercentData(stepPer)

        var caloriePer = (totalCalorie / 500f * 100)
        if (caloriePer > 100) caloriePer = 100f
        if (caloriePer <= 0) caloriePer = 0f
        binding?.barCaloriePer?.setPercentData(caloriePer)

        var kmPer = (totalKm / 5f * 100)
        if (kmPer > 100) kmPer = 100f
        if (kmPer <= 0) kmPer = 0f
        binding?.barDistancePer?.setPercentData(kmPer)

        binding?.stepView?.setMotionData(0f, max.toFloat(), itemList)
        binding?.tvStep?.text = lastItem.data.roundToInt().toString()
        binding?.tvStepTime?.text = lastItem.showTimeString
        binding?.stepView?.setTouchDataItem(lastItem)

        binding?.rectDataView?.postDelayed({
            binding?.rectDataView?.setPos(0f)
        }, 500)

    }

    override fun onStrengthData(low: Int, middle: Int, high: Int) {
        if (low == 0 && middle == 0 && high == 0) {
            binding?.clStrength?.visibility = View.GONE
            return
        }
        binding?.clStrength?.visibility = View.VISIBLE

        var total = low + middle + high
        if (total <= 30) {
            binding?.tvStrengthStatus?.text = resources.getString(R.string.to_enhanced)
            binding?.tvStrengthTip?.text = resources.getString(R.string.to_enhanced_tip)
        } else {
            binding?.tvStrengthStatus?.text = ""
            binding?.tvStrengthTip?.text = resources.getString(R.string.to_enhanced_reach_tip)
        }

        binding?.tvActiveTime?.text = DateTimeUtils.parseTime(
            activity, total.toLong(), 30, 14,
            Color.parseColor("#FFFFFF"), Color.parseColor("#7C7D8C")
        )

        var width =
            activity?.let { ScreenUtils.getScreenWidth(it) - DeviceUtil.dip2px(activity, 180f) }
                ?: DeviceUtil.dip2px(activity, 180f)

        var lowPer = if (low > 0) low / total.toFloat() else 0f
        if (lowPer <= 0) lowPer = 0.1f
        var lowParams = binding?.imgLowStrength?.layoutParams
        lowParams?.width = (lowPer * width).roundToInt()
        binding?.imgLowStrength?.layoutParams = lowParams
        binding?.tvLowStrengthTime?.text = DateTimeUtils.parseTimeEnS(activity, low)

        var middlePer = if (middle > 0) middle / total.toFloat() else 0f
        if (middlePer <= 0) middlePer = 0.1f
        var middleParams = binding?.imgMiddleStrength?.layoutParams
        middleParams?.width = (middlePer * width).roundToInt()
        binding?.imgMiddleStrength?.layoutParams = middleParams
        binding?.tvMiddleStrengthTime?.text = DateTimeUtils.parseTimeEnS(activity, middle)

        var highPer = if (high > 0) high / total.toFloat() else 0f
        if (highPer <= 0) highPer = 0.1f
        var highParams = binding?.imgHighStrength?.layoutParams
        highParams?.width = (highPer * width).roundToInt()
        binding?.imgHighStrength?.layoutParams = highParams
        binding?.tvHighStrengthTime?.text = DateTimeUtils.parseTimeEnS(activity, high)
    }
}