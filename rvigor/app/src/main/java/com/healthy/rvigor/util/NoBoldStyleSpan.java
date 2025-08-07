package com.healthy.rvigor.util;

import android.graphics.Paint;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

/**
 * 非粗体
 */
public class NoBoldStyleSpan extends StyleSpan {

    public NoBoldStyleSpan(int style) {
        super(style);
    }

    public NoBoldStyleSpan(@NonNull Parcel src) {
        super(src);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
         ds.setFakeBoldText(false);
         ds.setStyle(Paint.Style.FILL);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.setFakeBoldText(false);
        paint.setStyle(Paint.Style.FILL);
    }
}
