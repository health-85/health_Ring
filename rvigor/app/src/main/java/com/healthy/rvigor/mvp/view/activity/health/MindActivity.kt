package com.healthy.rvigor.mvp.view.activity.health

import android.app.Activity
import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.healthy.rvigor.Constants
import com.healthy.rvigor.MyApplication
import com.healthy.rvigor.R
import com.healthy.rvigor.adapter.DayAdapter
import com.healthy.rvigor.base.BaseMVPActivity
import com.healthy.rvigor.bean.SourceBean
import com.healthy.rvigor.databinding.ActivityMindBinding
import com.healthy.rvigor.mvp.contract.IMindContract
import com.healthy.rvigor.mvp.presenter.MindPresenter
import com.healthy.rvigor.mvp.view.fragment.MainFragment
import com.healthy.rvigor.mvp.view.fragment.MindItemFragment
import com.healthy.rvigor.util.DateTimeUtils
import com.healthy.rvigor.util.LogUtils
import com.healthy.rvigor.util.SPUtil
import com.healthy.rvigor.util.SpConfig
import com.healthy.rvigor.view.GalleryLayoutManager
import com.smart.adapter.SmartViewPager2Adapter
import com.smart.adapter.interf.OnRefreshLoadMoreListener
import com.smart.adapter.interf.SmartFragmentTypeExEntity
import java.util.Date

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/7/28 13:09
 * @UpdateRemark:   心理健康
 */
class MindActivity : BaseMVPActivity<ActivityMindBinding, MindPresenter>(), IMindContract.View{

    companion object {
        fun startMindActivity(activity: Activity?, time: Long) {
            if (activity == null || time == 0L) return
            val intent = Intent(activity, MindActivity::class.java)
            intent.putExtra(Constants.EXTRA, time)
            activity.startActivity(intent)
        }
    }

    //日期
    private val mDayAdapter by lazy { DayAdapter() }

    //数据
    private val mPageAdapter by lazy {
        SmartViewPager2Adapter(this, binding!!.viewPager)
            .setOffscreenPageLimit(3)
            .addFragment(1, MindItemFragment::class.java)
    }

    override fun getLayoutResID(): Int {
        return R.layout.activity_mind
    }

    override fun createPresenter(): MindPresenter {
        return MindPresenter()
    }

    override fun initView() {
        super.initView()
        //Tab日期数据
        val galleryLayoutManager = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        galleryLayoutManager.attach(binding?.rvDay, 0)
        galleryLayoutManager.setOnItemSelectedListener { recyclerView, item, position ->
            if (position < 0) return@setOnItemSelectedListener
            binding?.viewPager?.currentItem = position
        }
        binding?.rvDay?.adapter = mDayAdapter
        mDayAdapter.addOnItemChildClickListener(
            R.id.cl_day
        ) { adapter, view, position ->
            if (position < 0) return@addOnItemChildClickListener
            binding?.viewPager?.currentItem = position
        }

        //数据列表
        binding?.viewPager?.adapter = mPageAdapter
        mPageAdapter.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(smartAdapter: SmartViewPager2Adapter) {
                LogUtils.i(" PageAdapter onLoadMore")
            }

            override fun onRefresh(smartAdapter: SmartViewPager2Adapter) {
                LogUtils.i(" PageAdapter onRefresh")
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