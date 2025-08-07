package com.healthy.rvigor.mvp.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.healthy.rvigor.BuildConfig
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.adapter.DayAdapter
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.FragmentMainBinding
import com.healthy.rvigor.event.WatchBindEvent
import com.healthy.rvigor.event.WatchDataEvent
import com.healthy.rvigor.event.WatchSyncEvent
import com.healthy.rvigor.mvp.contract.IMainFragmentContract
import com.healthy.rvigor.mvp.presenter.MainFragmentPresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.view.GalleryLayoutManager
import com.smart.adapter.SmartViewPager2Adapter
import com.smart.adapter.interf.OnRefreshLoadMoreListener
import com.smart.adapter.interf.SmartFragmentTypeExEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.internal.notify
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 16:40
 * @UpdateRemark:
 */
class MainFragment : BaseMVPFragment<FragmentMainBinding, MainFragmentPresenter>(),
    IMainFragmentContract.View {

    companion object {
        const val TAG = "MainFragment"
    }

    private var mProgress = 0

    //进度条
    private var mBarDis: Disposable? = null

    //日期
    private val mDayAdapter by lazy { DayAdapter() }

    //数据
    private val mPageAdapter by lazy {
        SmartViewPager2Adapter(this, binding!!.viewPager)
            .setOffscreenPageLimit(3)
            .addFragment(1, MainItemFragment::class.java)
//            .addData(
//                SourceBean(
//                    0,
//                    1,
//                    DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time
//                )
//            )
    }

    override fun getLayoutResID(): Int {
        return R.layout.fragment_main
    }

    override fun createPresenter(): MainFragmentPresenter {
        return MainFragmentPresenter()
    }

    override fun initView() {
        super.initView()
        //Tab日期数据
        val galleryLayoutManager = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        galleryLayoutManager.attach(binding?.rvDay, 0)
        galleryLayoutManager.setOnItemSelectedListener { recyclerView, item, position ->
//            if (mDayAdapter.items == null || mDayAdapter.items.size < position){
//                return@setOnItemSelectedListener
//            }
//            val time = mDayAdapter.getItem(position)
//            LogUtils.i(" $TAG setOnItemSelectedListener $position time ${DateTimeUtils.s_long_2_str(
//                time!!, DateTimeUtils.f_format)}")
            if (position < 0) return@setOnItemSelectedListener
            binding?.viewPager?.currentItem = position
        }
        binding?.rvDay?.adapter = mDayAdapter

        mDayAdapter.addOnItemChildClickListener(
            R.id.cl_day
        ) { adapter, view, position ->
//            if (mDayAdapter.items == null || mDayAdapter.items.size < position){
//                return@addOnItemChildClickListener
//            }
//            val time = mDayAdapter.getItem(position)
//            LogUtils.i(" $TAG addOnItemChildClickListener $position time ${DateTimeUtils.s_long_2_str(
//                time!!, DateTimeUtils.f_format)}")
            if (position < 0) return@addOnItemChildClickListener
            binding?.viewPager?.currentItem = position
        }

        //数据列表
        binding?.viewPager?.adapter = mPageAdapter
        mPageAdapter.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(smartAdapter: SmartViewPager2Adapter) {
                LogUtils.i(" PageAdapter onLoadMore")
//                binding?.viewPager?.postDelayed({
//                    addPagerData()
//                }, 1000)
            }

            override fun onRefresh(smartAdapter: SmartViewPager2Adapter) {
                LogUtils.i(" PageAdapter onRefresh")
//                binding?.viewPager?.postDelayed({
//                    addPagerData()
//                }, 1000)

            }
        })
        binding?.viewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val bean: SourceBean = mPageAdapter.getItem(position) as SourceBean
                mDayAdapter.selDay = bean.time
                try {
                    binding?.viewPager?.postDelayed({
                        mDayAdapter.notifyDataSetChanged()
                    }, 100)
                }catch (e : Exception){
                    e.printStackTrace()
                }
                galleryLayoutManager.curSelectedPosition = position

                //更新页面数据
                val watchDataEvent = WatchDataEvent()
                watchDataEvent.time = bean.time
                watchDataEvent.type = Constants.MAIN_TYPE
                EventBus.getDefault().post(watchDataEvent)

                LogUtils.i(
                    " $TAG SourceBean ${
                        DateTimeUtils.s_long_2_str(
                            bean.time,
                            DateTimeUtils.f_format
                        )
                    } position $position"
                )
            }
        })

        addPagerData()

        //设置顶部手表
        setTopDevice()
    }

    override fun initData(bundle: Bundle?) {
        super.initData(bundle)
    }

    override fun onResume() {
        super.onResume()
        val watchBase = MyApplication.instance().bleUtils.getConnectionWatch()
        binding?.imgRing?.visibility = if (watchBase != null) View.VISIBLE else View.GONE
        binding?.tvNoDevice?.visibility = if (watchBase == null) View.VISIBLE else View.GONE
        setTopDevice()
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.i(" onHiddenChanged $hidden")
        if (!hidden) {
            setTopDevice()
        }
        val day = DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time
        val curDay = SPUtil.getData(MyApplication.instance(), SpConfig.IS_MAIN_CURRENT_DAY, 0L)
        if (curDay != day) {
            addPagerData()

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchEvent(watchStatus: WatchBindEvent?) {
        //手表状态
        setTopDevice()
    }

    //添加数据
    private fun addPagerData() {
        val day = DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time
        SPUtil.saveData(MyApplication.instance(), SpConfig.IS_MAIN_CURRENT_DAY, day)

        mDayAdapter.submitList(arrayListOf())
        mDayAdapter.add(day)
        mPageAdapter.addFrontData(
            SourceBean(
                0,
                1,
                day
            )
        )

        val bean: SourceBean = mPageAdapter.getItem(0) as SourceBean
        val index = bean.id - 1

//        val index = 0
        val produceSize = 31

        val curTime = DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis()))
        val dayList = mutableListOf<Long>()
        var newDats = mutableListOf<SmartFragmentTypeExEntity>()
        for (i in index - (produceSize - 1)..index) {
            val time = DateTimeUtils.AddDay(curTime, i).time
            dayList.add(time)
            newDats.add(SourceBean(i, 1, time))
            LogUtils.i(" productDatas $i ")
        }
        mPageAdapter.addFrontData(newDats)
        mDayAdapter.addAll(0, dayList)
    }

    //设置顶部手表
    private fun setTopDevice() {
        val watchBase = MyApplication.instance().bleUtils.getConnectionWatch()
        if (watchBase != null) {
            binding?.tvNoDevice?.visibility = View.GONE
            binding?.imgRing?.visibility = View.VISIBLE
        } else {
            binding?.tvNoDevice?.visibility = View.VISIBLE
            binding?.imgRing?.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWatchSyncEvent(watchSyncEvent: WatchSyncEvent) {
        LogUtils.i(" onWatchSyncEvent progress ${watchSyncEvent.progress} mProgress $mProgress ")
        if (watchSyncEvent.progress == 0) {
            mProgress = 0
            binding?.tvProgress?.visibility = View.VISIBLE
        }
        if (watchSyncEvent.progress == 100 || mProgress >= 100) {
            mProgress = 100
            binding?.tvProgress?.text = "100%"
            mBarDis?.dispose()
            mBarDis = Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    LogUtils.i(" ISyncEvent accept ")
                    binding?.tvProgress?.visibility = View.GONE
                }
        } else {
            mProgress += watchSyncEvent.progress
            if (mProgress >= 100) {
                mProgress = 99
            }
            binding?.tvProgress?.text = "$mProgress%"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBarDis != null) {
            mBarDis?.dispose()
            mBarDis = null
        }
    }
}