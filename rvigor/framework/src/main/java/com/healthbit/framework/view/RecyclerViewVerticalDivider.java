package com.healthbit.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by zxy on 2019/4/10.
 * RecyclerView 横向item的分割线，第一个item顶部有分割线
 */
public class RecyclerViewVerticalDivider extends DividerItemDecoration {

    private Drawable drawable;
    private final Rect mBounds = new Rect();

    public RecyclerViewVerticalDivider(Context context) {
        super(context, DividerItemDecoration.VERTICAL);
    }

    @Override
    public void setDrawable(@NonNull Drawable drawable) {
        super.setDrawable(drawable);
        this.drawable = drawable;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        c.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            c.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        if (parent.getChildCount() > 0) {
            View child = parent.getChildAt(0);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int top = mBounds.top + Math.round(ViewCompat.getTranslationY(child));
            int bottom = top + drawable.getIntrinsicHeight();
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(c);
        }
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
            int top = bottom - drawable.getIntrinsicHeight();
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(c);
        }
        c.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childPosition = parent.getChildAdapterPosition(view);
//        int last = parent.getAdapter().getItemCount() - 1;
        if (childPosition == 0) {
            outRect.set(0, drawable.getIntrinsicHeight(), 0, drawable.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, 0, drawable.getIntrinsicHeight());
        }
    }
}
