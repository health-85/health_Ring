package com.sdk.satwatch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sdk.satwatch.util.CommonFuc;

/**
 * checkBox
 */
public class AppCheckBox  extends View {

    public AppCheckBox(Context context) {
        super(context);
        init();
    }

    public AppCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AppCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint paint=new Paint();

    private   int  split=0;

    private  int R=0;

    private void init(){
        split= CommonFuc.diptopx(getContext(),2);
        paint.setColor(0xffFB8B2F);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(CommonFuc.diptopx(getContext(),2));
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        postInvalidate();
    }

    private boolean  checked=false;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        R=getMeasuredWidth()/2;
        if (R>(getMeasuredHeight()/2)){
              R=getMeasuredHeight()/2;
        }
        R= (int) (R-paint.getStrokeWidth());
        if (R<0){
            R=0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        if (!checked){
            paint.setColor(0xffaaaaaa);
        }else {
            paint.setColor(0xffFB8B2F);
        }
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,R,paint);
        if (checked){
            paint.setStyle(Paint.Style.FILL);
            int subr= (int) (R-split-paint.getStrokeWidth());
            if (subr<0){
                subr=0;
            }
            canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,subr,paint);
        }
    }
}
