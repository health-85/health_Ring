package com.healthy.rvigor.mvp.presenter

import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.healthbit.framework.mvp.BasePresenterImpl
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.mvp.contract.IUserMsgContract
import com.healthy.rvigor.util.OssUtil

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 20:35
 * @UpdateRemark:
 */
class UserMsgPresenter : BasePresenterImpl<IUserMsgContract.View/*, IUserMsgContract.Model*/>(),
    IUserMsgContract.Presenter {


    override fun ossUploadFile(path: String) {
        OssUtil.getInstance(MyApplication.instance()).uploadFile("", path, object :
            OssUtil.OSSUploadCallback {
            override fun onSuccess(
                allPath: MutableList<String>?,
                allLocalPath: MutableList<String>?
            ) {
                var uploadPath : String? = null
                if (allPath != null && allPath.size > 0) {
                    uploadPath = allPath[0]
                }
                var localPath : String? = null
                if (allLocalPath != null && allLocalPath.size > 0) {
                    localPath = allLocalPath[0]
                }
                view.uploadFileSuccess(localPath, uploadPath)
            }

            override fun onFailure(
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {

            }

            override fun onProgress(progress: Int) {

            }

        })
    }


}