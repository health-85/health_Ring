package com.healthy.rvigor.mvp.contract

import com.healthbit.framework.mvp.IBaseModel
import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/6 20:33
 * @UpdateRemark:
 */
interface IUserMsgContract {

    interface View : IBaseView {
        fun uploadFileSuccess(localPath : String?, ossImg : String?)
    }

    interface Presenter : IBasePresenter<IBaseView> {
        fun ossUploadFile(path : String)
    }


}