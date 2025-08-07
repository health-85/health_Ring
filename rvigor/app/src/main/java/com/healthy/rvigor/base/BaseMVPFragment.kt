package com.healthy.rvigor.base

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.healthbit.framework.base.BaseDialogFragment
import com.healthbit.framework.mvp.AbsMVPCompatFragment
import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthbit.framework.util.StatusBarUtil
import com.healthbit.framework.util.ToastUtil
import com.healthy.rvigor.R
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.view.DialogLoading
import com.zhangteng.utils.ViewBindingUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 16:31
 * @UpdateRemark:
 */
abstract class BaseMVPFragment<VB : ViewBinding, P : IBasePresenter<IBaseView>> : AbsMVPCompatFragment<P>() {

    var binding: VB? = null

    var loadViewDialog: DialogLoading? = null

    private var mDisposable: CompositeDisposable? = null

    override fun getLayoutView(): View? {
        binding = ViewBindingUtils.inflate<VB>(this)
        return binding?.root ?: layoutInflater.inflate(layoutResID ?: layoutResID, null)
    }


    override fun showLoadingTextDialog(textResID: Int, time: Long) {
        showLoadingTextDialog(textResID, time, false, null)
    }

    override fun showLoadingTextDialog(
        textResID: Int,
        time: Long,
        isCancelable: Boolean,
        dialogCallback: BaseDialogFragment.BaseDialogCallback?
    ) {
        synchronized(this) {
            if (loadViewDialog == null) {
                loadViewDialog = DialogLoading.newInstance(textResID, time, isCancelable)
            }
            if (time > 0) {
                loadViewDialog?.setDialogCallback(dialogCallback)
            } else {
                loadViewDialog?.upDateTime(time.toInt())
            }
            loadViewDialog?.isCancelable = true
            loadViewDialog?.setDialogTouchOutsideCancelable(false)
            loadViewDialog?.show(parentFragmentManager, "ActivityImplLoading")
        }
    }

    override fun addDisposable(disposable: Disposable?) {
        if (mDisposable == null) {
            mDisposable = CompositeDisposable()
        }
        disposable?.let { mDisposable?.add(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposable != null) {
            mDisposable!!.dispose()
            mDisposable!!.clear()
        }
    }

    override fun hideLoadingTextDialog() {
        if (loadViewDialog != null && loadViewDialog?.isAdded == true) {
            loadViewDialog?.dismissAllowingStateLoss()
        }
    }

    override fun showWarningDialog(textResID: Int) {
        showNoNetworkView()
    }

    open fun showNoNetworkView() {
//        if (mStateLayout != null) {
//            L.d("显示错误页面");
//            mStateLayout.showNoNetworkView();
//        }
    }

    override fun showToast(msg: String?) {
        if (TextUtils.isEmpty(msg)) {
            return
        }
        ToastUtil.showToast(activity?.applicationContext, msg)
    }


}