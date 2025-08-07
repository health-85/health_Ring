package com.healthy.rvigor.mvp.view.activity.login

import android.Manifest
import android.content.Intent
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityLoginBinding
import com.healthy.rvigor.mvp.contract.ILoginContract
import com.healthy.rvigor.mvp.presenter.LoginPresenter
import com.healthy.rvigor.mvp.view.activity.main.MainActivity
import com.healthy.rvigor.mvp.view.activity.scan.ScanActivity
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.util.MyClickableSpan
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig

/**
 * @Description:    登录
 * @Author:         wb
 * @CreateDate:     2024/5/5
 * @UpdateRemark:   无
 * @Version:        1.0
 */
class LoginActivity : BaseMVPActivity<ActivityLoginBinding, LoginPresenter>(), ILoginContract.View,
    View.OnClickListener {

    override fun getLayoutResID(): Int {
        return R.layout.activity_login
    }

    override fun createPresenter(): LoginPresenter {
        return LoginPresenter()
    }

    override fun initView() {
        super.initView()

        val locale = context.resources.configuration.locale
        if (TextUtils.equals(locale.language, "en")) {
            binding?.imgLoginTip?.setImageResource(R.drawable.svg_login_title_en)
        } else {
            binding?.imgLoginTip?.setImageResource(R.drawable.svg_login_title)
        }

        //字体高亮
        val userAgreement = resources.getString(R.string.user_agreement)
        val privacyPolicy = resources.getString(R.string.privacy_policy)
        val spannableString: SpannableString = AppUtils.highlights(
            binding?.tvPolicy?.text.toString(),
            arrayOf(userAgreement, privacyPolicy),
            "#FFFFFF",
            object : MyClickableSpan.OnMyClickListener{
                override fun onViewClick(target: String?, widget: View) {
                    if (TextUtils.equals(target, userAgreement)){

                    }else if (TextUtils.equals(target, privacyPolicy)){

                    }
                }
            }
        )
        binding?.tvPolicy?.text = spannableString

        binding?.imgCheckPolicy?.setOnClickListener(this@LoginActivity)
        binding?.tvAreaCode?.setOnClickListener(this@LoginActivity)
        binding?.tvLogin?.setOnClickListener(this@LoginActivity)
        binding?.clTourist?.setOnClickListener(this@LoginActivity)
        binding?.clMail?.setOnClickListener(this@LoginActivity)
    }


    override fun onFailed(e: String?) {

    }

    override fun onLoginSuccess() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_check_policy -> {
                binding?.imgCheckPolicy?.isSelected = binding?.imgCheckPolicy?.isSelected != true
            }

            R.id.tv_area_code -> {
//                requestPermission(Manifest.permission.READ_PHONE_STATE)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe {
//                        val areaCode = AppUtils.getCountryIso(this@LoginActivity)
//                        LogUtils.i(areaCode)
//                    }
            }

            R.id.cl_mail -> {

            }

            R.id.cl_tourist -> {
                MyApplication.instance().appUserInfo.saveUserId(Constants.TOURIST_USER_ID)
                val deviceName =
                    SPUtil.getData(MyApplication.Companion.instance(), SpConfig.DEVICE_NAME, "") as String
                if (TextUtils.isEmpty(deviceName)){
                    startActivity(Intent(this@LoginActivity, ScanActivity::class.java))
                }else{
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
                finish()
            }

            R.id.tv_login -> {
                if (binding?.imgCheckPolicy?.isSelected == true) {
                    showToast(resources.getString(R.string.agree_policy))
                    return
                }
                val phone: String = binding?.etPhone?.text.toString().trim()
                if (phone.length != 11) {
                    showToast(resources.getString(R.string.phone_number_11))
                    return
                }
                mPresenter.getCode(phone)
            }
        }
    }
}