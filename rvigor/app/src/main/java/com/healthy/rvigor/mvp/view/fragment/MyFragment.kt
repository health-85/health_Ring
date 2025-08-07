package com.healthy.rvigor.mvp.view.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.databinding.FragmentMineBinding
import com.healthy.rvigor.event.WatchBindEvent
import com.healthy.rvigor.mvp.contract.IMineContract
import com.healthy.rvigor.mvp.presenter.MinePresenter
import com.healthy.rvigor.mvp.view.activity.login.LoginActivity
import com.healthy.rvigor.mvp.view.activity.my.UserInfoActivity
import com.healthy.rvigor.mvp.view.activity.scan.ScanActivity
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.util.ImageUtil
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.util.WatchBeanUtil
import com.healthy.rvigor.view.ConfirmTipsView
import com.sw.watches.bean.DeviceInfo
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 16:52
 * @UpdateRemark:
 */
class MyFragment : BaseMVPFragment<FragmentMineBinding, MinePresenter>(), IMineContract.View,
    View.OnClickListener {

    override fun getLayoutResID(): Int {
        return R.layout.fragment_mine
    }

    override fun createPresenter(): MinePresenter {
        return MinePresenter()
    }

    override fun initView() {
        super.initView()

        setViewStatus()

        binding?.tvLoginRegister?.setOnClickListener(this@MyFragment)
        binding?.clUserInfo?.setOnClickListener(this@MyFragment)
        binding?.clDevice?.setOnClickListener(this@MyFragment)
        binding?.llNoDevice?.setOnClickListener(this@MyFragment)

        binding?.labelFind?.setOnClickListener {
            val watchBase = MyApplication.instance().bleUtils.getConnectionWatch()
            watchBase?.findDevice()
        }
    }

    override fun initData(bundle: Bundle?) {
        super.initData(bundle)
    }

    override fun useEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConfigInfo(deviceInfo: DeviceInfo?) {
        //设置设备状态
        setDeviceStatus()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchEvent(watchStatus: WatchBindEvent?) {
        //手表状态
        setWatchStatus(watchStatus?.watchStatus)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.i(" onHiddenChanged $hidden")
        if (isHidden) {
            setViewStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtils.i(" onResume ")
        setViewStatus()
    }

    private fun setViewStatus() {
        //设置设备状态
        setDeviceStatus()
        //手表状态
        val watchStatus = MyApplication.instance().watchSyncUtils.watchStatus
        setWatchStatus(watchStatus)
        //设置我的状态
        setMyStatus()
    }

    //设置设备状态
    private fun setDeviceStatus() {
        val deviceVersionName =
            SPUtil.getData(MyApplication.instance(), SpConfig.DEVICE_VERSION_NAME, "") as String
        if (!TextUtils.isEmpty(deviceVersionName)) {
            binding?.labelDeviceUpgrade?.setLabelHint("V$deviceVersionName")
        }
        val battery = SPUtil.getData(MyApplication.instance(), SpConfig.DEVICE_BATTERY, 0) as Int
        if (battery > 0) {
            binding?.tvBattery?.text =
                String.format(resources.getString(R.string.my_battery), "$battery%")
            binding?.imgBatteryBar?.setBatteryPer(battery.toFloat())
        }else{
            binding?.imgBatteryBar?.setBatteryPer(0f)
        }
        val deviceName =
            SPUtil.getData(MyApplication.instance(), SpConfig.DEVICE_NAME, "").toString()
        if (!TextUtils.isEmpty(deviceName)) {
            binding?.tvDeviceName?.text = deviceName
        }
        val deviceMac =
            SPUtil.getData(MyApplication.instance(), SpConfig.DEVICE_ADDRESS, "").toString()
        if (!TextUtils.isEmpty(deviceMac)) {
            binding?.tvDeviceMac?.text =
                String.format(resources.getString(R.string.my_mac), deviceMac)
        }

        binding?.llNoDevice?.visibility =
            if (!TextUtils.isEmpty(deviceName)) View.GONE else View.VISIBLE
        binding?.clDeviceMsg?.visibility =
            if (!TextUtils.isEmpty(deviceName)) View.VISIBLE else View.GONE
    }

    //手表状态
    private fun setWatchStatus(watchStatus: Int?) {
        var msg = resources.getString(R.string.connecting)
        val watchBase = MyApplication.instance().bleUtils.getConnectionWatch()
        if (watchBase != null){
            msg = resources.getString(R.string.connected)
        }else{
            when (watchStatus) {
                Constants.WATCH_SCANNING -> {
                    msg = resources.getString(R.string.scanning)
                }

                Constants.WATCH_SCAN_STOP -> {
//                    msg = resources.getString(R.string.scan_stop)
                    msg = resources.getString(R.string.connecting)
                }

                Constants.WATCH_CONNECTING -> {
                    msg = resources.getString(R.string.connecting)
                }

                Constants.WATCH_CONNECTED -> {
                    msg = resources.getString(R.string.connected)
                }

                Constants.WATCH_CONNECT_FAIL -> {
                    msg = resources.getString(R.string.connect_failed)
                }

                Constants.WATCH_DISCONNECT -> {
                    msg = resources.getString(R.string.no_connected)
                }
            }
        }
        binding?.tvStatus?.text = msg
        LogUtils.i(" watchStatus $watchStatus msg $msg ")
    }

    //设置我的状态
    private fun setMyStatus() {
        if (MyApplication.instance().appUserInfo.isSaveUserInfo) {
            binding?.tvLoginRegister?.visibility = View.GONE
            binding?.clUserInfo?.visibility = View.VISIBLE
        } else {
            binding?.tvLoginRegister?.visibility = View.VISIBLE
            binding?.clUserInfo?.visibility = View.GONE
        }
        val userInfo = MyApplication.instance().appUserInfo.userInfo
        if (!TextUtils.isEmpty(userInfo.headImg)) {
            ImageUtil.loadLocalImg(binding?.imgMy, userInfo.headImg)
        }
        if (!TextUtils.isEmpty(userInfo.username)) {
            binding?.tvName?.text = userInfo.username
        }
        var age = AppUtils.fromStringToInteger(AppUtils.getAge(userInfo.birthday), 0)
        if (age > 0) {
            binding?.tvAge?.text = "$age${resources.getString(R.string.age)}"
        } else {
            binding?.tvAge?.text = ""
        }
        var sexDrawable: Drawable? = null
        if (userInfo.sex == Constants.MALE) {
            sexDrawable = activity?.let { ContextCompat.getDrawable(it, R.drawable.svg_male) }
        } else if (userInfo.sex == Constants.FEMALE) {
            sexDrawable = activity?.let { ContextCompat.getDrawable(it, R.drawable.svg_female) }
        }
        if (sexDrawable != null) {
            sexDrawable.setBounds(0, 0, sexDrawable.intrinsicWidth, sexDrawable.intrinsicHeight)
            binding?.tvAge?.setCompoundDrawables(sexDrawable, null, null, null)
        }
        if (age <= 0 && sexDrawable == null) {
            binding?.tvAge?.visibility = View.GONE
        } else {
            binding?.tvAge?.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_login_register -> {
//                startActivity(Intent(activity, UserInfoActivity::class.java))
                startActivity(Intent(activity, LoginActivity::class.java))
            }

            R.id.cl_user_info -> {
                startActivity(Intent(activity, UserInfoActivity::class.java))
            }

            R.id.ll_no_device -> {
                MyApplication.instance().bleUtils.disConnecting()
                startActivity(Intent(activity, ScanActivity::class.java))
            }

            R.id.cl_device -> {
                val deviceName = SPUtil.getData(context, SpConfig.DEVICE_NAME, "").toString()
                if (!TextUtils.isEmpty(deviceName)) {
                    ConfirmTipsView(activity).showDialog(
                        resources.getString(R.string.unbind_current_device),
                        resources.getString(R.string.cancel),
                        resources.getString(R.string.sure)
                    ) { _, isConfirm ->
                        if (isConfirm){
                            WatchBeanUtil.unBindDevice()
                            setDeviceStatus()
                        }
                    }
                } else {
                    startActivity(Intent(context, ScanActivity::class.java))
                }
            }
        }
    }

}