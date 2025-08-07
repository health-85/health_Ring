package com.healthy.rvigor.mvp.view.activity.health

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.google.gson.Gson
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.bean.HrBean
import com.healthy.rvigor.dao.executor.InsertMeHeartExecutor
import com.healthy.rvigor.databinding.ActivityHeartMeasureBinding
import com.healthy.rvigor.mvp.contract.IHeartMeasureContract
import com.healthy.rvigor.mvp.presenter.HeartMeasurePresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.watch.IWatchFunctionDataCallBack
import com.healthy.rvigor.watch.WatchBase
import com.sw.watches.bean.HeartInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/24 20:32
 * @UpdateRemark:
 */
class HeartMeasureActivity : BaseMVPActivity<ActivityHeartMeasureBinding, HeartMeasurePresenter>(),
    IHeartMeasureContract.View, View.OnClickListener {

    //测量心率是否成功
    var isMeasureResult = false

    var mRate = 0
    var mRateTime = 0L

    var mMeasureTime = 20L

    private var mTimeDispose: Disposable? = null

    override fun getLayoutResID(): Int {
        return R.layout.activity_heart_measure
    }

    override fun createPresenter(): HeartMeasurePresenter {
        return HeartMeasurePresenter()
    }

    override fun initView() {
        super.initView()
        MyApplication.instance().bleUtils.registryWatchFunctionDataCallBack(
            watchFunctionDataCallBack
        )
        binding?.tvAgainMeasure?.setOnClickListener(this@HeartMeasureActivity)
        startMeasure()
    }

    private fun startMeasure() {
        isMeasureResult = false
        binding?.tvHeart?.text = "--"
        binding?.tvAgainMeasure?.visibility = View.GONE
        binding?.tvHeartTip?.visibility = View.VISIBLE
        MyApplication.instance().bleUtils.getConnectionWatch()?.measureHeart()
        //开始到计时
        startTimeCount()
        startAnim()
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_again_measure -> {
                startMeasure()
            }
        }
    }

    //开始到计时
    private fun startTimeCount() {
        mTimeDispose?.dispose()
        mTimeDispose = Observable.timer(mMeasureTime, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mTimeDispose?.dispose()
                binding?.tvHeartTip?.visibility = View.INVISIBLE
                binding?.tvAgainMeasure?.visibility = View.VISIBLE
                binding?.imgHeart?.clearAnimation()
                MyApplication.instance().bleUtils.getConnectionWatch()?.stopMeasureHeart()
                if (isMeasureResult) {
                    //更新首页数据
                    if (mRateTime <= 0){
                        mRateTime = System.currentTimeMillis()
                    }
                    val hrBean = HrBean()
                    hrBean.heartRate = mRate
                    hrBean.heartTime = DateTimeUtils.s_long_2_str(
                        mRateTime,
                        DateTimeUtils.f_format
                    )
                    EventBus.getDefault().post(hrBean)
                    SPUtil.saveData(
                        MyApplication.instance(),
                        SpConfig.MEASURE_HEART_DATA,
                        Gson().toJson(hrBean)
                    )
                    //插入体温数据
                    val watch = MyApplication.instance().bleUtils.getConnectionWatch()
                    val insertMeHeartExecutor = InsertMeHeartExecutor(
                        if (watch != null) watch.deviceName else "",
                        if (watch != null) watch.deviceMacAddress else "",
                        mRate, mRateTime
                    )
                    MyApplication.instance().appDaoManager?.ExecuteDBAsync(insertMeHeartExecutor)
                }

                if (!isMeasureResult) {
                    MeasureFailActivity.startMeasureFailActivity(
                        this@HeartMeasureActivity,
                        MeasureFailActivity.RETRY_HEART_MEASURE
                    )
                    finish()
                }
            }
    }

    private fun startAnim() {
        val scaleMinAnimation = ScaleAnimation(
            1.1f, 0.8f, 1.1f, 0.8f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleMinAnimation.duration = 1000
        scaleMinAnimation.repeatCount = -1
        scaleMinAnimation.repeatMode = Animation.RESTART
        binding?.imgHeart?.startAnimation(scaleMinAnimation)

    }

    private val watchFunctionDataCallBack =
        object : IWatchFunctionDataCallBack {
            override fun WatchDataArrived(watch: WatchBase?, functionName: String?, bean: Any?) {
                if (TextUtils.equals(functionName, "HeartInfo")) {
                    if (bean is HeartInfo) {
                        if (bean.HeartInfoHR > 0) {
                            mRate = bean.HeartInfoHR
                            mRateTime = System.currentTimeMillis()
                            isMeasureResult = true
                            binding?.tvHeart?.text = bean.HeartInfoHR.toString()
                        }
                    }
                }
            }
        }

    override fun onDestroy() {
        mTimeDispose?.dispose()
        MyApplication.instance().bleUtils.unRegistryWatchFunctionDataCallBack(
            watchFunctionDataCallBack
        )
        super.onDestroy()

    }
}