package com.sdk.satwatch.adapter;

import android.text.Html;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.sdk.satwatch.R;

import java.util.List;

public class MeasureHeartAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MeasureHeartAdapter(List<String> data) {
        super(R.layout.item_measure_heart, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tvEcgMsg = helper.getView(R.id.tv_ecg_msg);
        tvEcgMsg.setText(Html.fromHtml(item));
    }
}
