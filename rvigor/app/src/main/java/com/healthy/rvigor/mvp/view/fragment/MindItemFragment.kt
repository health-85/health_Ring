package com.healthy.rvigor.mvp.view.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.healthy.rvigor.Constants
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.bean.MainViewItem
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.FragmentMindItemBinding
import com.healthy.rvigor.event.WatchDataEvent
import com.healthy.rvigor.mvp.contract.IMindItemContract
import com.healthy.rvigor.mvp.presenter.MindItemPresenter
import com.healthy.rvigor.view.HeartDataView
import com.smart.adapter.interf.SmartFragmentImpl2
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/7/28 13:38
 * @UpdateRemark:
 */
class MindItemFragment : BaseMVPFragment<FragmentMindItemBinding, MindItemPresenter>(),
    SmartFragmentImpl2<SourceBean>, IMindItemContract.View {

    companion object {
        private var mAverageTire = 0f
        private var mAveragePressure = 0f
        private var instance: MindItemFragment? = null

        @JvmStatic
        fun getInstance(): MindItemFragment {
            if (instance == null) {
                instance = MindItemFragment()
            }
            return instance as MindItemFragment
        }
    }

    private var mSourceBean: SourceBean? = null

    override fun getLayoutResID(): Int {
        return R.layout.fragment_mind_item
    }

    override fun createPresenter(): MindItemPresenter {
        return MindItemPresenter()
    }

    override fun initView() {
        super.initView()
    }

    override fun initData(bundle: Bundle?) {
        super.initData(bundle)
        getData()
    }

    override fun initSmartFragmentData(bean: SourceBean) {
        this.mSourceBean = bean
    }

    override fun useEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchDataEvent(watchDataEvent: WatchDataEvent) {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0 || watchDataEvent.time <= 0) return
        if (watchDataEvent.time == mSourceBean?.time &&
            (watchDataEvent.type == Constants.MIND_TYPE || watchDataEvent.type == Constants.ALL_TYPE)
        ) {
            getData()
        }
    }

    private fun getData() {
        if (mSourceBean == null || (mSourceBean?.time ?: 0) <= 0) return
        mAveragePressure = -1f
        mAverageTire = -1f
        mPresenter.getMindData(mSourceBean?.time ?: 0)
    }

    override fun onTireData(
        itemList: MutableList<MainViewItem>?,
        lastItem: MainViewItem?,
        average: Float
    ) {
        mAverageTire = average
        binding?.tireView?.setStyle(HeartDataView.DataStyle.Cub_LINE)
        binding?.tireView?.setMindData(0f, 100f, itemList)
        binding?.tireView?.setTouchDataItem(lastItem)
        if (average <= 0f) {
            binding?.tvTire?.text = "--"
            binding?.tvTireStatus?.text = ""
            binding?.tvTireMsg?.text = ""
        } else {
            binding?.tvTire?.text = average.roundToInt().toString()
            if (average <= 25) {
                binding?.tvTireStatus?.text = resources.getString(R.string.normal)
                binding?.tvTireMsg?.text = resources.getString(R.string.tire_normal_tip)
            } else if (average <= 50) {
                binding?.tvTireStatus?.text = resources.getString(R.string.slight)
                binding?.tvTireMsg?.text = resources.getString(R.string.tire_slight_tip)
            } else if (average <= 90) {
                binding?.tvTireStatus?.text = resources.getString(R.string.intermediate)
                binding?.tvTireMsg?.text = resources.getString(R.string.tire_intermediate_tip)
            } else if (average <= 100) {
                binding?.tvTireStatus?.text = resources.getString(R.string.over)
                binding?.tvTireMsg?.text = resources.getString(R.string.tire_excessive_tip)
            }
        }
        setMindView()
    }

    override fun onPressureData(
        itemList: MutableList<MainViewItem>?,
        lastItem: MainViewItem?,
        average: Float
    ) {
        mAveragePressure = average
        binding?.pressureView?.setStyle(HeartDataView.DataStyle.BAR)
        binding?.pressureView?.setMindData(0f, 100f, itemList)
        binding?.pressureView?.setTouchDataItem(lastItem)
        if (average <= 0f) {
            binding?.tvPressure?.text = "--"
            binding?.tvPressureStatus?.text = ""
            binding?.tvPressureMsg?.text = ""
        } else {
            binding?.tvPressure?.text = average.roundToInt().toString()
            if (average <= 25) {
                binding?.tvPressureStatus?.text = resources.getString(R.string.relax)
                binding?.tvPressureMsg?.text = resources.getString(R.string.pressure_relax_tip)
            } else if (average <= 50) {
                binding?.tvPressureStatus?.text = resources.getString(R.string.mild)
                binding?.tvPressureMsg?.text = resources.getString(R.string.pressure_mild_tip)
            } else if (average <= 90) {
                binding?.tvPressureStatus?.text = resources.getString(R.string.higher)
                binding?.tvPressureMsg?.text = resources.getString(R.string.pressure_higher_tip)
            } else if (average <= 100) {
                binding?.tvPressureStatus?.text = resources.getString(R.string.extremely_high)
                binding?.tvPressureMsg?.text =
                    resources.getString(R.string.pressure_extremely_high_tip)
            }
        }
        setMindView()
    }

    override fun onEmotionData(
        itemList: MutableList<MainViewItem>?,
        lastItem: MainViewItem?,
        average: Float
    ) {
        binding?.emotionView?.setStyle(HeartDataView.DataStyle.LINE)
        binding?.emotionView?.setMindData(0f, 100f, itemList)
        binding?.emotionView?.setTouchDataItem(lastItem)
        if (average <= 0f) {
            binding?.tvEmotion?.text = "--"
            binding?.tvEmotionStatus?.text = ""
            binding?.tvEmotionMsg?.text = ""
        } else {
            binding?.tvEmotion?.text = average.roundToInt().toString()
            if (average <= 33) {
                binding?.tvEmotionStatus?.text = resources.getString(R.string.passive)
                binding?.tvEmotionMsg?.text = resources.getString(R.string.emotion_passive_tip)
            } else if (average <= 66) {
                binding?.tvEmotionStatus?.text = resources.getString(R.string.steady)
                binding?.tvEmotionMsg?.text = resources.getString(R.string.emotion_steady_tip)
            } else {
                binding?.tvEmotionStatus?.text = resources.getString(R.string.vigorous)
                binding?.tvEmotionMsg?.text = resources.getString(R.string.emotion_vigorous_tip)
            }
        }
    }

    private fun setMindView() {
        if (mAveragePressure < 0 || mAverageTire < 0) {
            binding?.tvMindStatus?.text = "--"
            binding?.tvMindResult?.text = ""
            return
        }
        var mind = 0.6f * mAveragePressure + 0.4 * mAverageTire
        if (mind <= 50) {
            binding?.imgMindBg?.setImageResource(R.mipmap.ic_mind_normal)
            binding?.tvMindStatus?.text = resources.getString(R.string.normal)
            binding?.tvMindResult?.text = resources.getString(R.string.normal_tip)
        } else if (mind <= 80) {
            binding?.imgMindBg?.setImageResource(R.mipmap.ic_mild_disorder)
            binding?.tvMindStatus?.text = resources.getString(R.string.mild_disorder)
            binding?.tvMindResult?.text = resources.getString(R.string.mild_disorder_tip)
        } else {
            binding?.imgMindBg?.setImageResource(R.mipmap.ic_mild_disorder)
            binding?.tvMindStatus?.text = resources.getString(R.string.serious_obstacle)
            binding?.tvMindResult?.text = resources.getString(R.string.serious_obstacle_tip)
        }
    }
}