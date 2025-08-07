package com.healthy.rvigor.mvp.view.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.adapter.DayAdapter
import com.healthy.rvigor.base.BaseMVPFragment
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.FragmentMineBinding
import com.healthy.rvigor.databinding.FragmentSleepBinding
import com.healthy.rvigor.event.WatchDataEvent
import com.healthy.rvigor.mvp.contract.IMineContract
import com.healthy.rvigor.mvp.contract.ISleepContract
import com.healthy.rvigor.mvp.presenter.MinePresenter
import com.healthy.rvigor.mvp.presenter.SleepPresenter
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.view.GalleryLayoutManager
import com.smart.adapter.SmartViewPager2Adapter
import com.smart.adapter.interf.OnRefreshLoadMoreListener
import com.smart.adapter.interf.SmartFragmentTypeExEntity
import org.greenrobot.eventbus.EventBus
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/10 16:51
 * @UpdateRemark:
 */
class SleepFragment : BaseMVPFragment<FragmentSleepBinding, SleepPresenter>(), ISleepContract.View{

    companion object {
        const val TAG = "SleepFragment"
    }

    private var mCurDay = 0L

    //日期
    private val mDayAdapter by lazy { DayAdapter() }

    //数据
    private val mPageAdapter by lazy {
        SmartViewPager2Adapter(this, binding!!.viewPager)
            .setOffscreenPageLimit(3)
            .addFragment(1, SleepItemFragment::class.java)
//            .addData(
//                SourceBean(
//                    0,
//                    1,
//                    DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time
//                )
//            )
    }

    override fun getLayoutResID(): Int {
        return R.layout.fragment_sleep
    }

    override fun createPresenter(): SleepPresenter {
        return SleepPresenter()
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
//        mDayAdapter.add(DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time)
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
                watchDataEvent.type = Constants.SLEEP_TYPE
                EventBus.getDefault().post(watchDataEvent)

                LogUtils.i(
                    " ${MainFragment.TAG} SourceBean ${
                        DateTimeUtils.s_long_2_str(
                            bean.time,
                            DateTimeUtils.f_format
                        )
                    } position $position"
                )
            }
        })

        addPagerData()
    }

//    //添加数据
//    private fun addPagerData() {
//        if (mPageAdapter.itemCount <= 0) return
//        val bean: SourceBean = mPageAdapter.getItem(0) as SourceBean
//        val index = bean.id - 1
//        val produceSize = 30
//
//        val curTime = DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis()))
//        val dayList = mutableListOf<Long>()
//        var newDats = mutableListOf<SmartFragmentTypeExEntity>()
//        for (i in index - (produceSize - 1)..index) {
//            val time = DateTimeUtils.AddDay(curTime, i).time
//            dayList.add(time)
//            newDats.add(SourceBean(i, 1, time))
//            LogUtils.i(" productDatas $i ")
//        }
//        mPageAdapter.addFrontData(newDats)
//        mDayAdapter.addAll(0, dayList)
//    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.i(" onHiddenChanged $hidden")
        val day = DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time
        val curDay = SPUtil.getData(MyApplication.instance(), SpConfig.IS_SLEEP_CURRENT_DAY, 0L)
        if (curDay != day) {
            addPagerData()

        }
    }

    //添加数据
    private fun addPagerData() {

        val day = DateTimeUtils.getDateTimeDatePart(Date(System.currentTimeMillis())).time
        SPUtil.saveData(MyApplication.instance(), SpConfig.IS_SLEEP_CURRENT_DAY, day)

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
}