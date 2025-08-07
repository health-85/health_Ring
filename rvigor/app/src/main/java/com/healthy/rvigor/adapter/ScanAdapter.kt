package com.healthy.rvigor.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.healthy.rvigor.R

/**
 * @Description:    Rvigor
 * @Author:         wb
 * @CreateDate:     2024/5/9 19:34
 * @UpdateRemark:
 */
class ScanAdapter : BaseQuickAdapter<BluetoothDevice, QuickViewHolder>(){

    var bindName : String? = null

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: BluetoothDevice?) {
        holder.setText(R.id.tv_device_name, item?.name ?: "")
        holder.setGone(R.id.tv_device_state, TextUtils.equals(item?.name, bindName))
        holder.setGone(R.id.img_progress, !TextUtils.equals(item?.name, bindName))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_scan_device, parent)
    }



}