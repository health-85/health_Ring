package com.healthy.rvigor.mvp.view.activity.login

import android.Manifest
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityUserMsgBinding
import com.healthy.rvigor.mvp.contract.IUserMsgContract
import com.healthy.rvigor.mvp.presenter.UserMsgPresenter
import com.healthy.rvigor.util.ImageUtil
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.MyGlideEngine
import com.healthy.rvigor.util.OssUtil
import com.healthy.rvigor.util.OssUtil.OSSUploadCallback
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 20:27
 * @UpdateRemark:
 */
class UserMsgActivity : BaseMVPActivity<ActivityUserMsgBinding, UserMsgPresenter>(),
    IUserMsgContract.View, View.OnClickListener {

    override fun getLayoutResID(): Int {
        return R.layout.activity_user_msg
    }

    override fun createPresenter(): UserMsgPresenter {
        return UserMsgPresenter()
    }

    override fun initView() {
        super.initView()
        binding?.imgBack?.setOnClickListener(this@UserMsgActivity)
        binding?.imgNick?.setOnClickListener(this@UserMsgActivity)
        binding?.tvBirthSel?.setOnClickListener(this@UserMsgActivity)
        binding?.tvHeightSel?.setOnClickListener(this@UserMsgActivity)
        binding?.tvWeightSel?.setOnClickListener(this@UserMsgActivity)
        binding?.tvVerify?.setOnClickListener(this@UserMsgActivity)
        binding?.tvSkip?.setOnClickListener(this@UserMsgActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                finish()
            }

            R.id.img_nick -> {
                requestPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        takePhoto()
                    }
            }

            R.id.tv_birth_sel -> {

            }

            R.id.tv_height_sel -> {
            }

            R.id.tv_weight_sel -> {

            }

            R.id.tv_verify -> {

            }

            R.id.tv_skip -> {
                finish()
            }
        }
    }

    //选择图片
    private fun takePhoto(){
        PictureSelector.create(this@UserMsgActivity)
            .openGallery(SelectMimeType.ofImage())
            .setMaxSelectNum(1)
            .setImageEngine(MyGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    if (result != null && result.size > 0) {
                        val path = result[0].realPath
                        if (!TextUtils.isEmpty(path)) {
                            LogUtils.i("绝对路径：$path")
                            mPresenter.ossUploadFile(path)
                        } else {
                            Log.e("OnActivityResult", "path: null")
                        }
                    }
                }

                override fun onCancel() {}
            })
    }

    override fun uploadFileSuccess(localPath: String?, ossImg: String?) {
        ImageUtil.loadLocalImg(binding?.imgNick, localPath)
    }

}