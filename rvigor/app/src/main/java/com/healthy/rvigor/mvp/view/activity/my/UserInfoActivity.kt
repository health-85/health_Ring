package com.healthy.rvigor.mvp.view.activity.my

import android.Manifest
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityUserInfoBinding
import com.healthy.rvigor.mvp.contract.IUserInfoContract
import com.healthy.rvigor.mvp.presenter.UserInfoPresenter
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.ImageUtil
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.MyGlideEngine
import com.healthy.rvigor.view.BirthDialog
import com.healthy.rvigor.view.ConfirmTipsView
import com.healthy.rvigor.view.NickDialog
import com.healthy.rvigor.view.RuleDialogView
import com.healthy.rvigor.view.SexDialog
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/16 9:35
 * @UpdateRemark:
 */
class UserInfoActivity : BaseMVPActivity<ActivityUserInfoBinding, UserInfoPresenter>(),
    IUserInfoContract.View, View.OnClickListener {

    override fun getLayoutResID(): Int {
        return R.layout.activity_user_info
    }

    override fun createPresenter(): UserInfoPresenter {
        return UserInfoPresenter()
    }

    override fun initView() {
        super.initView()

        val userInfo = MyApplication.instance().appUserInfo.userInfo
        if (!TextUtils.isEmpty(userInfo.headImg)) {
            ImageUtil.loadLocalImg(binding?.imgInfo, userInfo.headImg)
        }
        if (!TextUtils.isEmpty(userInfo.username)) {
            binding?.lbNick?.labelValue = userInfo.username
        }
        if (userInfo.sex > 0) {
            var msg = ""
            if (userInfo.sex == Constants.MALE) {
                msg = resources.getString(R.string.male)
            } else if (userInfo.sex == Constants.FEMALE) {
                msg = resources.getString(R.string.female)
            } else if (userInfo.sex == Constants.SECRECY) {
                msg = resources.getString(R.string.secrecy)
            }
            binding?.lbSex?.labelValue = msg

        }
        if (userInfo.birthday > 0) {
            binding?.lbBirthday?.labelValue =
                DateTimeUtils.s_long_2_str(userInfo.birthday, DateTimeUtils.day_format)
        }
        if (userInfo.height > 0) {
            binding?.lbHeight?.labelValue = "${userInfo.height}cm"
        }
        if (userInfo.weigh > 0) {
            binding?.lbWeight?.labelValue = "${userInfo.weigh}kg"
        }

        binding?.imgInfo?.setOnClickListener(this@UserInfoActivity)
        binding?.lbNick?.setOnClickListener(this@UserInfoActivity)
        binding?.lbSex?.setOnClickListener(this@UserInfoActivity)
        binding?.lbBirthday?.setOnClickListener(this@UserInfoActivity)
        binding?.lbHeight?.setOnClickListener(this@UserInfoActivity)
        binding?.lbWeight?.setOnClickListener(this@UserInfoActivity)
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_info -> {
                requestPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it) {
                            takePhoto()
                        } else {
                            ConfirmTipsView(activity).showDialog(
                                resources.getString(R.string.tip_allow_permission),
                                resources.getString(R.string.cancel),
                                resources.getString(R.string.open)
                            ) { _, isConfirm ->
                                if (isConfirm) {
                                    AppUtils.goIntentSetting(this@UserInfoActivity)
                                }
                            }
                        }
                    }
            }

            R.id.lb_nick -> {
                var nickName = MyApplication.instance().appUserInfo.userInfo.username
                NickDialog(activity).showDialog(
                    nickName
                ) { dialog, value ->
                    binding?.lbNick?.labelValue = value
                    MyApplication.instance().appUserInfo.userInfo.username = value
                    MyApplication.instance().appUserInfo.saveUserInfo(MyApplication.instance().appUserInfo.userInfo)
                }
            }

            R.id.lb_sex -> {
                var sex = MyApplication.instance().appUserInfo.userInfo.sex
                SexDialog(activity).showDialog(
                    sex
                ) { dialog, value, msg ->
                    binding?.lbSex?.labelValue = msg
                    MyApplication.instance().appUserInfo.userInfo.sex = value
                    MyApplication.instance().appUserInfo.saveUserInfo(MyApplication.instance().appUserInfo.userInfo)
                }
            }

            R.id.lb_birthday -> {
                var birth = MyApplication.instance().appUserInfo.userInfo.birthday
                if (birth <= 0) birth = System.currentTimeMillis()
                BirthDialog(activity).showDialog(
                    birth
                ) { dialog, value ->
                    MyApplication.instance().appUserInfo.userInfo.birthday = value
                    MyApplication.instance().appUserInfo.saveUserInfo(MyApplication.instance().appUserInfo.userInfo)
                    val str = DateTimeUtils.s_long_2_str(value, DateTimeUtils.day_format)
                    binding?.lbBirthday?.labelValue = str
                }
            }

            R.id.lb_weight -> {
                var weight = MyApplication.instance().appUserInfo.userInfo.weigh
                if (weight <= 0) weight = 50f
                RuleDialogView(activity).showWeightDialog(
                    10f,
                    250f,
                    weight,
                ) { dialog, value ->
                    binding?.lbWeight?.labelValue = "${value}kg"
                    MyApplication.instance().appUserInfo.userInfo.weigh = value
                    MyApplication.instance().appUserInfo.saveUserInfo(MyApplication.instance().appUserInfo.userInfo)
                }
            }

            R.id.lb_height -> {
                var height = MyApplication.instance().appUserInfo.userInfo.height
                if (height <= 0) height = 170
                RuleDialogView(activity).showHeightDialog(
                    70f,
                    240f,
                    height.toFloat(),
                ) { _, value ->
                    binding?.lbHeight?.labelValue = "${value.toInt()}cm"
                    MyApplication.instance().appUserInfo.userInfo.height = value.toInt()
                    MyApplication.instance().appUserInfo.saveUserInfo(MyApplication.instance().appUserInfo.userInfo)
                }
            }
        }
    }

    //选择图片
    private fun takePhoto() {
        PictureSelector.create(this@UserInfoActivity)
            .openGallery(SelectMimeType.ofImage())
            .setMaxSelectNum(1)
            .setImageEngine(MyGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    if (result != null && result.size > 0) {
                        val path = result[0].realPath
                        if (!TextUtils.isEmpty(path)) {
                            ImageUtil.loadLocalImg(binding?.imgInfo, path)
                            MyApplication.instance().appUserInfo.userInfo.headImg = path
                            MyApplication.instance().appUserInfo.saveUserInfo(MyApplication.instance().appUserInfo.userInfo)
                            LogUtils.i("绝对路径：$path")
                        } else {
                            Log.e("OnActivityResult", "path: null")
                        }
                    }
                }

                override fun onCancel() {}
            })
    }

}