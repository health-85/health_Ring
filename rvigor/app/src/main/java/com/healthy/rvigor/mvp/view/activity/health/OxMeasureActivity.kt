package com.healthy.rvigor.mvp.view.activity.health

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.text.TextUtils
import android.view.View
import android.view.animation.LinearInterpolator
import com.google.gson.Gson
import com.healthbit.framework.util.StatusBarUtil
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.bean.BoBean
import com.healthy.rvigor.dao.executor.InsertMeSpoExecutor
import com.healthy.rvigor.databinding.ActivityOxMeasureBinding
import com.healthy.rvigor.mvp.presenter.OxMeasurePresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.watch.IWatchFunctionDataCallBack
import com.healthy.rvigor.watch.WatchBase
import com.smart.adapter.util.ScreenUtils
import com.sw.watches.bean.SpoData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/22 21:30
 * @UpdateRemark:   血氧测量
 */
class OxMeasureActivity : BaseMVPActivity<ActivityOxMeasureBinding, OxMeasurePresenter>(),
    View.OnClickListener {

    //血氧数据
    private var mOxData = 0
    private var mOxDataTime = ""

    //测量结果
    private var isMeasureResult = false

    //测量时间
    private var mMeasureTime = 25

    //倒计时
    private var mTimeDispose: Disposable? = null

    //动画
    private var mAnimatorSet: AnimatorSet? = null

    override fun getLayoutResID(): Int {
        return R.layout.activity_ox_measure
    }

    override fun createPresenter(): OxMeasurePresenter {
        return OxMeasurePresenter()
    }

    override fun initView() {
        super.initView()
        StatusBarUtil.setStatusBarByPureColor(this, R.color.color_000001)
        MyApplication.instance().bleUtils.registryWatchFunctionDataCallBack(
            watchFunctionDataCallBack
        )
        //开始测量血氧
        mAnimatorSet = AnimatorSet()
        startMeasure()

        binding?.tvMeasureAgain?.setOnClickListener(this@OxMeasureActivity)

//        val list = mutableListOf<Int>()
//        list.add(R.mipmap.ic_ox_anim)
//        list.add(R.mipmap.ic_ox_anim)
//        val mAsqBannerAdapter = OxBannerAdapter(list)
//        binding?.bannerAnim?.setAdapter(mAsqBannerAdapter)
    }

    //开始测量血氧
    private fun startMeasure() {
        isMeasureResult = false
        MyApplication.instance().bleUtils.getConnectionWatch()?.measureOx()
        binding?.tvOx?.text = "--"
        binding?.tvOxTime?.text = "00:$mMeasureTime"
        startTimeCount()
        binding?.tvMeasureAgain?.visibility = View.GONE
        binding?.tvOxTime?.visibility = View.VISIBLE
        binding?.tvMeasureTip?.visibility = View.VISIBLE
        startAnim()
    }

    //开始到计时
    private fun startTimeCount() {
        mTimeDispose?.dispose()
        mTimeDispose = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                var showTime = ""
                var time = mMeasureTime - it
                if (time > 0) {
                    if (time >= 10) {
                        showTime = "00:$time"
                    } else {
                        showTime = "00:0$time"
                    }
                    binding?.tvOxTime?.text = showTime
                } else {
                    mTimeDispose?.dispose()
                    binding?.tvOxTime?.text = "00:00"
                    if (isMeasureResult) {
                        //更新首页数据
                        val boBean = BoBean()
                        boBean.boDate = mOxDataTime
                        boBean.boRate = mOxData.toString()
                        EventBus.getDefault().post(boBean)
                        SPUtil.saveData(
                            MyApplication.instance().applicationContext,
                            SpConfig.MEASURE_OX_DATA,
                            Gson().toJson(boBean)
                        )
                        //插入血氧数据
                        val spoData = SpoData(mOxDataTime, mOxData, 0)
                        val watch = MyApplication.instance().bleUtils.getConnectionWatch()
                        val insertMeSpoExecutor = InsertMeSpoExecutor(
                            if (watch != null) watch.deviceName else "",
                            if (watch != null) watch.deviceMacAddress else "",
                            spoData
                        )
                        MyApplication.instance().appDaoManager?.ExecuteDBAsync(insertMeSpoExecutor)

                        binding?.tvMeasureAgain?.visibility = View.VISIBLE
                        binding?.tvMeasureTip?.visibility = View.GONE
                        binding?.tvOxTime?.visibility = View.GONE

                    } else {
                        MeasureFailActivity.startMeasureFailActivity(
                            this@OxMeasureActivity,
                            MeasureFailActivity.RETRY_OX_MEASURE
                        )
                    }

                    mAnimatorSet?.cancel()
                }
            }
    }

    private fun startAnim() {

        val width = ScreenUtils.getScreenWidth(this@OxMeasureActivity)

        val params = binding?.imgMeasureAnim?.layoutParams
        params?.width = width * 2
        binding?.imgMeasureAnim?.layoutParams = params

        val params2 = binding?.imgMeasureAnim2?.layoutParams
        params2?.width = width * 2
        binding?.imgMeasureAnim2?.layoutParams = params2

        val animator =
            ObjectAnimator.ofFloat(
                binding?.imgMeasureAnim,
                "translationX",
                0f,
                -width.toFloat() * 2
            )
        // 设置动画时长
        animator?.duration = 4000
        // 设置重复次数为ValueAnimator.INFINITE代表无限循环
        animator?.repeatCount = ValueAnimator.INFINITE
        // 设置重复模式为循环
        animator?.repeatMode = ValueAnimator.RESTART
        // 设置插值器为线性，保持动画速度恒定
        animator?.interpolator = LinearInterpolator()

        val animator2 =
            ObjectAnimator.ofFloat(
                binding?.imgMeasureAnim2,
                "translationX",
                0f,
                -width.toFloat() * 2
            )
        // 设置动画时长
        animator2?.duration = 4000
        // 设置重复次数为ValueAnimator.INFINITE代表无限循环
        animator2?.repeatCount = ValueAnimator.INFINITE
        // 设置重复模式为循环
        animator2?.repeatMode = ValueAnimator.RESTART
        // 设置插值器为线性，保持动画速度恒定
        animator2?.interpolator = LinearInterpolator()

        mAnimatorSet?.playTogether(animator, animator2)
        mAnimatorSet?.start()
    }

    private val watchFunctionDataCallBack =
        object : IWatchFunctionDataCallBack {
            override fun WatchDataArrived(watch: WatchBase?, functionName: String?, bean: Any?) {
                if (TextUtils.equals(functionName, "MeasureOx")) {
                    if (bean is Int) {
                        isMeasureResult = true
                        mOxData = bean
                        mOxDataTime = DateTimeUtils.s_long_2_str(
                            System.currentTimeMillis(),
                            DateTimeUtils.f_format
                        )
                        binding?.tvOx?.text = bean.toString()
                    }
                }
            }
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_measure_again -> {
                startMeasure()
            }
        }
    }

    override fun onDestroy() {
        mTimeDispose?.dispose()
        mTimeDispose = null
        mAnimatorSet?.cancel()
        mAnimatorSet = null
        MyApplication.instance().bleUtils.unRegistryWatchFunctionDataCallBack(
            watchFunctionDataCallBack
        )
        super.onDestroy()
    }
}