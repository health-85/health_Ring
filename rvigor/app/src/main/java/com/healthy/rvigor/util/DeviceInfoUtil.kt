package com.healthy.rvigor.util

import android.text.TextUtils
import com.healthy.rvigor.MyApplication.Companion.instance
import com.sw.watches.bean.DeviceInfo
import org.greenrobot.eventbus.EventBus

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/15 10:50
 * @UpdateRemark:
 */
class DeviceInfoUtil {

    companion object{

        fun saveDeviceInfo(deviceName : String, deviceCode : Int, battery : Int){
            if (!TextUtils.isEmpty(deviceName)) {
                SPUtil.saveData(
                    instance(),
                    SpConfig.DEVICE_VERSION_NAME,
                    deviceName
                )
            }
            if (deviceCode > 0) {
                SPUtil.saveData(
                    instance(),
                    SpConfig.DEVICE_VERSION_CODE,
                    deviceCode
                )
            }
            if (battery > 0) {
                SPUtil.saveData(instance(), SpConfig.DEVICE_BATTERY, battery)
            }

            val deviceName = SPUtil.getData(
                instance(),
                SpConfig.DEVICE_VERSION_NAME,
                ""
            ) as String
            val deviceCode = SPUtil.getData(
                instance(),
                SpConfig.DEVICE_VERSION_CODE,
                0
            ) as Int
            val battery = SPUtil.getData(
                instance(),
                SpConfig.DEVICE_BATTERY,
                0
            ) as Int

            val deviceInfo = DeviceInfo()
            deviceInfo.setDeviceBattery(battery)
            deviceInfo.setDeviceVersionName(deviceName)
            deviceInfo.setDeviceVersionNumber(deviceCode)
            EventBus.getDefault().post(deviceInfo)
        }
    }
}