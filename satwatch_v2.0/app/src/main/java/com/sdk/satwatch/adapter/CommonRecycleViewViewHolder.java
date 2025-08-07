package com.sdk.satwatch.adapter;

import android.content.Context;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;


public abstract class CommonRecycleViewViewHolder<T>  extends RecyclerView.ViewHolder {

	public CommonRecycleViewViewHolder(View itemV) {
		super(itemV);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 返回当前选项
	 * @return
	 */
	public final  View  getItemView() {
		return this.itemView;
	}

	/**
	 * 当前绑定数据的位置
	 */
	public int currentPosition=-1;




	/**
	 * 绑定当前数据
	 * @param data
	 */
	public abstract void BindData(T data);

	/**
	 * 数据位置索引发生改变
	 */
	public abstract void  onDataPositionChanged();
}
