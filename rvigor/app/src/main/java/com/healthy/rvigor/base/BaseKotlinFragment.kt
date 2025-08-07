package com.healthy.rvigor.base

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.view.CustomLoadingDialog
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.disposables.Disposable

abstract class BaseKotlinFragment<VB : ViewBinding> : Fragment() {

    private var pd: CustomLoadingDialog? = null

    private val mDisposable = CompositeDisposable()

    protected var binding: VB? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB

    abstract fun initView()

    abstract fun initData()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    open fun addDisposable(disposable: Disposable?) {
        if (disposable != null) {
            mDisposable.add(disposable)
        }
    }

    /**
     * 显示加载框
     */
    fun showProgressDialog() {
        if (pd == null) {
            pd = CustomLoadingDialog(activity, R.style.CustomLoadingDialog);
        }
        if (pd?.isShowing == false) {
            pd?.showMsg(null);
        }
    }

    /**
     * 隐藏加载框
     */
    fun dismissProgressDialog() {
        if (pd?.isShowing == true) {
            pd?.dismiss();
        }
    }

    protected open fun setStartBarColor(color: Int) {
        ImmersionBar.with(this)
            .statusBarColor(color) //状态栏颜色，不写默认透明色
            .statusBarDarkFont(true) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
            .fitsSystemWindows(true)
            .keyboardEnable(true)
            .init()
    }

    fun emptyData(msg: String?): View? {
        var msg = msg
        if (TextUtils.isEmpty(msg)) {
            msg = resources.getString(R.string.no_data)
        }
        val view = LayoutInflater.from(activity).inflate(R.layout.empty_data, null, false)
        val msgTv: TextView = view.findViewById(R.id.tv_empty_msg)
        msgTv.text = msg
        return view
    }

    open fun showToast(msg: String?) {
        Toast.makeText(MyApplication.instance(), msg, Toast.LENGTH_SHORT).show()
    }

}