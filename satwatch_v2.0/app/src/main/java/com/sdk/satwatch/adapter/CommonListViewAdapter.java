package com.sdk.satwatch.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;


public class CommonListViewAdapter<T,VH extends CommonListViewAdapter.BaseListViewHolder<T>> extends BaseAdapter {

    /**
     * 数据集合
     */
    private LinkedList<T>  datas=new LinkedList<T>();

    public CommonListViewAdapter(){

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    /**
     * 添加数据
     * @param data
     */
    public  void  add(T data){
        datas.addLast(data);
    }

    /**
     * 是否包含
     * @param data
     * @return
     */
    public  boolean container(T data){
        return datas.contains(data);
    }


    /**
     * 添加数据
     * @param index
     * @param data
     */
    public  void  addAt(int index,T data){
         datas.add(index,data);
    }

    /**
     * 移除数据
     * @param data
     */
    public  boolean remove(T data){
       return datas.remove(data);
    }

    /**
     * 清空数据
     */
    public void clear(){
        datas.clear();
    }

    public T removeAt(int index){
        return datas.remove(index);
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


     private   ViewHolderInit<T ,VH>   mcallback=null;

    /**
     * 设置ViewHolder初始化回调
     * @param callback
     */
     public void setViewHolderInitCallBack(ViewHolderInit<T ,VH>   callback){
         mcallback=callback;
     }



    @Override
    public int getItemViewType(int position) {
        T data=datas.get(position);
        if (mcallback!=null){
           return  mcallback.getItemViewType(data);
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
         if (mcallback!=null){
             return  mcallback.getViewTypeCount();
         }
         return 1;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View  V=null;
        BaseListViewHolder<T> vh= null;
        T data=datas.get(position);
        if (convertView==null){
            if (mcallback==null){
                throw  new RuntimeException("初始化回调不能为空");
            }

           vh =  mcallback.onCreateViewHolder(parent,mcallback.getItemViewType(data));
           if (vh!=null) {
               vh.thisadp=this;
               vh.itemV = vh.initItemView(parent.getContext());
               if (vh.itemV == null) {
                   throw new RuntimeException("itemView不能为空");
               }
               vh.itemV.setTag(vh);
           }else{
               throw new RuntimeException("ViewHolder不能为空");
           }
        }else {
            vh = (BaseListViewHolder<T>) convertView.getTag();
        }

        if (vh!=null){
            V=vh.getItemView();
            vh.position=position;
            vh.mdata=data;
            vh.bindData(data);
        }
        return V;
    }


    public static abstract class  ViewHolderInit<T ,VH extends BaseListViewHolder<T>>{

        /**
         * ViewType的数量
         * @return
         */
        public int getViewTypeCount(){
                return 1;
            }

        /**
         *返回实例的ViewType
         * @param data
         * @return
         */
           public int getItemViewType(T data){
               return 0;
           }

        /**
         * 初始化ViewHolder实例
         * @param parent
         * @param viewType
         * @return
         */
        protected  abstract VH  onCreateViewHolder(ViewGroup parent, int viewType);

    }

    /**
     * 基类ViewHolder
     * @param <T>  数据模板
     */
    public static abstract class BaseListViewHolder<T>  {

        public   View itemV=null;

        public BaseListViewHolder(Context con){

        }

        /**
         * 初始化itemView
         * @param con
         * @return
         */
        public abstract  View initItemView(Context con);

        /**
         * 当前位置信息
         */
        protected  int position=0;

        /**
         * 当前绑定的数据对象
         */
        protected T mdata=null;

        /**
         * 获取当前Viewholder的rootView
         * @return
         */
        public final View  getItemView(){
            return itemV;
        }

        /**
         * 当前adapter
         */
        protected  CommonListViewAdapter  thisadp=null;

        /**
         * 绑定数据
         * @param data
         */
        public abstract void bindData(T data);
    }


    public  static  class RecycleViewViewHolderForListViewHolder<T> extends CommonRecycleViewViewHolder<T> {

        private BaseListViewHolder<T> listViewHolder=null;

        public RecycleViewViewHolderForListViewHolder(BaseListViewHolder<T> listViewHolder, Context context) {
            super(listViewHolder.initItemView(context));
            listViewHolder.itemV=this.getItemView();
            this.listViewHolder=listViewHolder;
        }

        @Override
        public void BindData(T data) {
            listViewHolder.position=currentPosition;
            listViewHolder.mdata=data;
            listViewHolder.bindData(data);
        }

        @Override
        public void onDataPositionChanged() {
             listViewHolder.position=currentPosition;
        }
    }


}
