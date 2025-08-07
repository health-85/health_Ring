package com.healthy.rvigor.mvp.view.activity

import android.content.Intent
import android.text.TextUtils
import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.MyApplication.Companion.instance
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivitySplashBinding
import com.healthy.rvigor.mvp.view.activity.login.LoginActivity
import com.healthy.rvigor.mvp.view.activity.main.MainActivity
import com.healthy.rvigor.mvp.view.activity.scan.ScanActivity
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig

/**
 * @Description:    SplashActivity
 * @Author:         wb
 * @CreateDate:     2024/5/5 21:53
 * @UpdateRemark:
 */
class SplashActivity : BaseMVPActivity<ActivitySplashBinding, IBasePresenter<IBaseView>>(){

    override fun getLayoutResID(): Int {
        return R.layout.activity_splash
    }

    override fun createPresenter(): IBasePresenter<IBaseView>? {
        return null
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
        startNextActivity()
    }

    private fun startNextActivity() {
        val userId = MyApplication.instance().appUserInfo.userInfo.id
        val deviceName = SPUtil.getData(instance(), SpConfig.DEVICE_NAME, "").toString()
        /*if (userId <= 0){
            startActivity(Intent(this, LoginActivity::class.java))
        }else */
        /*if (TextUtils.isEmpty(deviceName)){
            startActivity(Intent(this, ScanActivity::class.java))
        }else{*/
            startActivity(Intent(this, MainActivity::class.java))
//        }
        finish()
    }
}