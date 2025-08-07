package com.healthy.rvigor.base

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.healthbit.framework.base.BaseDialogFragment.BaseDialogCallback
import com.healthbit.framework.mvp.AbsMVPActivity
import com.healthbit.framework.mvp.IBasePresenter
import com.healthbit.framework.mvp.IBaseView
import com.healthbit.framework.util.StatusBarUtil
import com.healthbit.framework.util.ToastUtil
import com.healthy.rvigor.R
import com.healthy.rvigor.bean.TokenInvalidEventBean
import com.healthy.rvigor.util.AppUtils
import com.healthy.rvigor.view.DialogLoading
import com.zhangteng.utils.ViewBindingUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseMVPActivity<VB : ViewBinding, P : IBasePresenter<IBaseView>> :
    AbsMVPActivity<P>() {

    var binding: VB? = null

    var loadViewDialog: DialogLoading? = null

    private var mDisposable: CompositeDisposable? = null

    override fun getLayoutView(): View? {
        binding = ViewBindingUtils.inflate<VB>(this)
        return binding?.root ?: layoutInflater.inflate(layoutResID ?: layoutResID, null)
    }

    override fun configureStatusBar() {
        super.configureStatusBar()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(AppUtils.localeAttachBaseContext(newBase))
    }

    override fun showLoadingTextDialog(textResID: Int, time: Long) {
        showLoadingTextDialog(textResID, time, false, null)
    }

    override fun showLoadingTextDialog(
        textResID: Int,
        time: Long,
        isCancelable: Boolean,
        dialogCallback: BaseDialogCallback?
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
            loadViewDialog?.show(supportFragmentManager, "ActivityImplLoading")
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

    protected open fun setSwipeStyle(view: SwipeRefreshLayout?) {
        view?.setColorSchemeColors(ContextCompat.getColor(context, R.color.color_main_50be91))
    }

    override fun useEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun tokenInvalid(bean: TokenInvalidEventBean) {
        hideLoadingTextDialog()
    }

    /**
     * 获得栈中最顶层的Activity
     *
     * @param context
     * @return
     */
    open fun getTopActivity(context: Context): String? {
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = manager.getRunningTasks(1)
        return if (runningTaskInfos != null) {
            runningTaskInfos[0].topActivity.toString()
        } else {
            null
        }
    }

    open fun showContentView() {
//        if (mStateLayout != null) {
//            mStateLayout.showContentView();
//        }
    }

    open fun showNoNetworkView() {
//        if (mStateLayout != null) {
//            L.d("显示错误页面");
//            mStateLayout.showNoNetworkView();
//        }
    }

    open fun refreshClick() {}

    open fun loginClick() {}

    override fun showToast(msg: String?) {
        if (TextUtils.isEmpty(msg)) {
            return
        }
        ToastUtil.showToast(activity.applicationContext, msg)
    }

}