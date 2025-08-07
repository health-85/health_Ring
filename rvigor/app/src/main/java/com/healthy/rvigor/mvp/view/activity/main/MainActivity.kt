package com.healthy.rvigor.mvp.view.activity.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.databinding.ActivityMainBinding
import com.healthy.rvigor.mvp.contract.IMainContract
import com.healthy.rvigor.mvp.presenter.MainPresenter
import com.healthy.rvigor.mvp.view.fragment.MainFragment
import com.healthy.rvigor.mvp.view.fragment.MotionFragment
import com.healthy.rvigor.mvp.view.fragment.MyFragment
import com.healthy.rvigor.mvp.view.fragment.SleepFragment
import com.healthy.rvigor.util.LogUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


class MainActivity : BaseMVPActivity<ActivityMainBinding, MainPresenter>(), IMainContract.View {

    private var mSupportFragmentManager: FragmentManager? = null

    private var mainFragment: MainFragment? = null

    private var sleepFragment: SleepFragment? = null

    private var motionFragment: MotionFragment? = null

    private var myFragment: MyFragment? = null

    private var mSaveFragment: WeakReference<Fragment>? = null


    override fun getLayoutResID(): Int {
        return R.layout.activity_main
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun initView() {
        super.initView()
        initListener()
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun initData(intent: Intent?) {
        super.initData(intent)
    }

    override fun onResume() {
        super.onResume()
        if (MyApplication.instance().bleUtils.getConnectionWatch() == null) {
//        MyApplication.instance().watchSyncUtils.reConnectDevice()
            MyApplication.instance().watchSyncUtils.reStartScanDevice()
        }
    }

    override fun onSaveFragmentInstanceState(savedInstanceState: Bundle?) {
        super.onSaveFragmentInstanceState(savedInstanceState)
        val fm = this.supportFragmentManager
        var fragment: Fragment? = null
        if (savedInstanceState != null) {
            fragment = fm.getFragment(
                savedInstanceState,
                Constants.FRAGMENT_TAG
            )
        }
        LogUtils.i(" onSaveFragmentInstanceState fragment $fragment")
        if (fragment == null) {
            mainFragment = MainFragment()
            hideOthersFragment(mainFragment, Constants.MAIN_FRAGMENT_TAG)
        } else {
            hideOthersFragment(fragment, fragment.tag)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mSaveFragment != null) {
            mSaveFragment?.get()?.let {
                supportFragmentManager.putFragment(
                    outState, Constants.FRAGMENT_TAG,
                    it
                )
            }
        }
    }

    private fun hideOthersFragment(showFragment: Fragment?, tag: String?) {
        var showFragment: Fragment? = showFragment ?: return
        try {
            if (mSupportFragmentManager == null) {
                mSupportFragmentManager = this.supportFragmentManager
            }
            val transaction = mSupportFragmentManager?.beginTransaction()
            var isAdd = false
            if (mSupportFragmentManager?.fragments != null && (mSupportFragmentManager?.fragments?.size
                    ?: 0) > 0
            ) {
                for (fragment in mSupportFragmentManager?.fragments!!) {
                    if (TextUtils.equals(fragment.tag, tag)) {
                        isAdd = true
                        showFragment = fragment
                        transaction?.show(fragment)
                    } else {
                        transaction?.hide(fragment)
                    }
                }
            }
            if (!isAdd) {
                transaction?.add(R.id.fragment_content, showFragment!!, tag)
            }
            transaction?.commit()
            addWeakFragment(showFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addWeakFragment(showFragment: Fragment?) {
        if (mSaveFragment == null) {
            mSaveFragment = WeakReference(showFragment)
        } else {
            mSaveFragment!!.clear()
            mSaveFragment = WeakReference(showFragment)
        }
    }

    private fun initListener() {
        binding?.rgMenu?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbt_status -> {
                    if (mainFragment == null) {
                        mainFragment = MainFragment()
                        hideOthersFragment(mainFragment, Constants.MAIN_FRAGMENT_TAG)
                    } else {
                        hideOthersFragment(mainFragment, Constants.MAIN_FRAGMENT_TAG)
                    }
                }

                R.id.rbt_sleep -> {
                    if (sleepFragment == null) {
                        sleepFragment = SleepFragment()
                        hideOthersFragment(sleepFragment, Constants.SLEEP_FRAGMENT_TAG)
                    } else {
                        hideOthersFragment(sleepFragment, Constants.SLEEP_FRAGMENT_TAG)
                    }
                }

                R.id.rbt_motion -> {
                    if (motionFragment == null) {
                        motionFragment = MotionFragment()
                        hideOthersFragment(motionFragment, Constants.MOTION_FRAGMENT_TAG)
                    } else {
                        hideOthersFragment(motionFragment, Constants.MOTION_FRAGMENT_TAG)
                    }
                }

                R.id.rbt_mine -> {
                    if (myFragment == null) {
                        myFragment = MyFragment()
                        hideOthersFragment(myFragment, Constants.MY_FRAGMENT_TAG)
                    } else {
                        hideOthersFragment(myFragment, Constants.MY_FRAGMENT_TAG)
                    }
                }
            }
        }
    }

    fun showFragment(tag: String?) {
        if (TextUtils.equals(tag, Constants.MAIN_FRAGMENT_TAG)) {
            binding?.rbtStatus?.isChecked = true
        } else if (TextUtils.equals(tag, Constants.SLEEP_FRAGMENT_TAG)) {
            binding?.rbtSleep?.isChecked = true
        } else if (TextUtils.equals(tag, Constants.MOTION_FRAGMENT_TAG)) {
            binding?.rbtMotion?.isChecked = true
        } else if (TextUtils.equals(tag, Constants.MY_FRAGMENT_TAG)) {
            binding?.rbtMine?.isChecked = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MyApplication.instance().bleUtils.getConnectionWatch() != null) {
            MyApplication.instance().bleUtils.disConnecting()
        }
        MyApplication.instance().bleUtils.stopScan()
    }

}