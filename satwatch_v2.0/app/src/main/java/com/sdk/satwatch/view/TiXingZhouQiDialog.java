package com.sdk.satwatch.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdk.satwatch.R;
import com.sdk.satwatch.adapter.CommonListViewAdapter;
import com.sdk.satwatch.adapter.MultitypeTypeListViewAdapter;


/**
 * 提醒周期
 */
public class TiXingZhouQiDialog  extends DialogBase {

    public TiXingZhouQiDialog(@NonNull Context context) {
        super(context);
    }

    public TiXingZhouQiDialog(@NonNull  Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TiXingZhouQiDialog(@NonNull  Context context, boolean cancelable, @Nullable  OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ListView  listview1=null;

    private MultitypeTypeListViewAdapter adapter=null;

    @Override
    protected View getSubContentView() {
        View  view=getLayoutInflater().inflate(R.layout.tixingzhouqidialog_layout,null);
        listview1=view.findViewById(R.id.listview1);
        if (listview1!=null){
              listview1.setDividerHeight(0);
              listview1.setSelector(new ColorDrawable(Color.TRANSPARENT));
              if (adapter==null){
                  initAdapter();
              }
              listview1.setAdapter(adapter);
        }
        View  cancel_button=view.findViewById(R.id.cancel_button);
        if (cancel_button!=null){
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      dismiss();
                }
            });
        }

        View  true_button=view.findViewById(R.id.true_button);
        if (true_button!=null){
            true_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       CheckItemEntity  entity=  getSelItem();
                       if ((entity!=null) && (resultEvent!=null)){
                             resultEvent.onSelectedItem(entity);
                       }
                       dismiss();
                }
            });
        }

        return view;
    }

    private   CheckItemEntity  getSelItem(){
          if (adapter!=null){
              for (int i=0;i<adapter.getCount();i++){
                     Object  object=adapter.getItem(i);
                     if (object  instanceof  CheckItemEntity){
                           CheckItemEntity entity= (CheckItemEntity) object;
                           if (entity.isChecked){
                               return entity;
                           }
                     }
              }
          }
          return  null;
    }

    /**
     * 获取所有选项
     * @return
     */
    public   int getItemsCount(){
          if (adapter!=null){
              return  adapter.getCount();
          }
          return 0;
    }


    /**
     * 获取其中的选项
     * @param i
     * @return
     */
    public   CheckItemEntity  getItem(int i){
          if (adapter!=null){
              if ((i>-1) && (i<adapter.getCount())){
                   return (CheckItemEntity) adapter.getItem(i);
              }
          }
          return null;
    }

    /**
     * 添加选项
     * @param checkItemEntity
     */
    public void Add(CheckItemEntity checkItemEntity){
              if(adapter==null){
                  initAdapter();
              }
              if (adapter!=null){
                  adapter.add(checkItemEntity);
              }
    }


    /**
     * 清除所有选项
     */
    public void clearAllItems(){
           if (adapter!=null){
                 adapter.clear();
           }else {
               initAdapter();
           }
    }

    /**
     * 选择结果回调
     */
    public   IResultEvent  resultEvent=null;


    public   static  interface  IResultEvent{
           public   void onSelectedItem(CheckItemEntity sel);
    }


    /**
     * 改变数据
     */
    public  void notifyDatasetChanged(){
        if (adapter!=null){
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetInvalidated();
            if (listview1!=null){
                listview1.requestLayout();
                listview1.invalidate();
            }
        }
    }


    private  void initAdapter(){
        if (adapter==null){
            adapter=new MultitypeTypeListViewAdapter();
            adapter.registryViewHolder(new MultitypeTypeListViewAdapter.ItemViewHolder<CheckItemEntity
                                              ,CheckItemEntity_Viewholder>(CheckItemEntity.class) {

                @Override
                public CheckItemEntity_Viewholder newViewHolder(Context context) {
                    return new CheckItemEntity_Viewholder(context);
                }
            });
        }
    }



    public  static  class   CheckItemEntity{
           public  String  title="";
           public  boolean  isChecked=false;
           public  Object  value=null;

        public CheckItemEntity(String title, boolean isChecked, Object value) {
            this.title = title;
            this.isChecked = isChecked;
            this.value = value;
        }
    }


    private static  class  CheckItemEntity_Viewholder  extends CommonListViewAdapter
                                       .BaseListViewHolder<CheckItemEntity>{

        public CheckItemEntity_Viewholder(Context con) {
            super(con);
        }


        private AppCheckBox appcheckbox=null;

        private TextView  title_text_view=null;

        @Override
        public View initItemView(Context con) {
            View  view= LayoutInflater.from(con).inflate(R.layout.checkitementity_layout,null);
            appcheckbox=view.findViewById(R.id.appcheckbox);
            title_text_view=view.findViewById(R.id.title_text_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       if (thisadp!=null){
                            for (int i=0;i< thisadp.getCount();i++){
                                   Object  curr=thisadp.getItem(i);
                                   if (curr instanceof  CheckItemEntity){
                                       CheckItemEntity  currck= (CheckItemEntity) curr;
                                       if (currck==mdata){
                                           currck.isChecked=true;
                                       }else {
                                           currck.isChecked=false;
                                       }
                                   }
                            }
                            thisadp.notifyDataSetChanged();
                       }
                }
            });
            return view;
        }

        @Override
        public void bindData(CheckItemEntity data) {
              if (appcheckbox!=null){
                  appcheckbox.setChecked(data.isChecked);
              }
              if (title_text_view!=null){
                  title_text_view.setText(data.title);
              }
        }
    }





}
