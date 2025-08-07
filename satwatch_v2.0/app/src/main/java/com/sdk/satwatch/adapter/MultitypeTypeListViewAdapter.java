package com.sdk.satwatch.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 多种类型视图adapter
 */
public class MultitypeTypeListViewAdapter extends CommonListViewAdapter<Object, CommonListViewAdapter.BaseListViewHolder<Object>> {

    /**
     * @param <T>
     * @param <VH>
     */
    public static abstract class ItemViewHolder<T, VH extends BaseListViewHolder<T>> {

        private Class<T> ListItemClass = null;

        public ItemViewHolder(Class<T> listItemClass) {
            ListItemClass = listItemClass;
        }

        public Class<T> getListItemClass() {
            return ListItemClass;
        }

        /**
         * viewholder新实例
         *
         * @param context
         * @return
         */
        public abstract VH newViewHolder(Context context);
    }

    /**
     * viewholder集合
     */
    private List<ItemViewHolder> itemViewHolders = new ArrayList<>();


    /**
     * 注册ViewHolder
     *
     * @param itemViewHolder
     */
    public void registryViewHolder(ItemViewHolder itemViewHolder) {
        if (!containsItemViewHolder(itemViewHolder)) {
            itemViewHolders.add(itemViewHolder);
        }
    }

    /**
     * 是否包含指定的ItemViewHolder
     *
     * @param itemViewHolder
     * @return
     */
    private boolean containsItemViewHolder(ItemViewHolder itemViewHolder) {
        if (!itemViewHolders.contains(itemViewHolder)) {
            for (int i = 0; i < itemViewHolders.size(); i++) {
                if (itemViewHolders.get(i).getListItemClass().equals(itemViewHolder.getListItemClass())) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }


    /**
     * 获取类型信息
     *
     * @param data
     * @return
     */
    private int getItemViewType1(Object data) {
        for (int i = 0; i < itemViewHolders.size(); i++) {
            if (itemViewHolders.get(i).getListItemClass().equals(data.getClass())) {
                return i;
            }
        }
        throw new RuntimeException("找不到此类型" + data.getClass().getName() + "的ViewHolder");
    }


    public MultitypeTypeListViewAdapter() {
        super();
        super.setViewHolderInitCallBack(new ViewHolderInit<Object, BaseListViewHolder<Object>>() {
            @Override
            public int getItemViewType(Object data) {
                return getItemViewType1(data);
            }

            @Override
            public int getViewTypeCount() {
                return itemViewHolders.size();
            }

            @Override
            protected BaseListViewHolder<Object> onCreateViewHolder(ViewGroup parent, int viewType) {
                return itemViewHolders.get(viewType).newViewHolder(parent.getContext());
            }
        });
    }


    @Override
    public void setViewHolderInitCallBack(ViewHolderInit<Object, BaseListViewHolder<Object>> callback) {
        throw new RuntimeException("该方法已经不可用");
    }


}
