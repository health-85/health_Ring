package com.healthbit.framework.util;

import android.content.Context;
import android.graphics.*;

/**
* @Description:    绘制工具类
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public class DrawUtil {

    public static int getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public static float getTextCenterY(int centerY, Paint paint) {
        return centerY - ((paint.descent() + paint.ascent()) / 2);
    }

    public static float getTextCenterX(int left, int right, Paint paint) {
        Paint.Align align = paint.getTextAlign();
        if (align == Paint.Align.RIGHT) {
            return right;
        } else if (align == Paint.Align.LEFT) {
            return left;
        } else {
            return (right + left) / 2;
        }
    }

    /**
     * 获取多行文字高度
     *
     * @param paint
     * @return
     */
    public static int getMultiTextHeight(Paint paint, String[] values) {
        return getTextHeight(paint) * values.length;
    }

    /**
     * 获取多行文字宽度
     *
     * @param paint
     * @return
     */
    public static int getMultiTextWidth(Paint paint, String[] values) {

        int maxWidth = 0;
        for (String val : values) {
            int width = (int) paint.measureText(val);
            if (maxWidth < width) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    /**
     * 绘制.9图片
     *
     * @param canvas     画布
     * @param context    上下文
     * @param drawableID Res资源ID
     * @param rect       矩形
     */
    public static void drawPatch(Canvas canvas, Context context, int drawableID, Rect rect) {
        Bitmap bmp_9 = BitmapFactory.decodeResource(context.getResources(), drawableID);
        NinePatch ninePatch = new NinePatch(bmp_9, bmp_9.getNinePatchChunk(), null);
        ninePatch.draw(canvas, rect);
    }


    /**
     * 绘制多行文字
     *
     * @param canvas
     * @param paint
     * @param rect
     */
    public static void drawMultiText(Canvas canvas, Paint paint, Rect rect, String[] values) {
        for (int i = 0; i < values.length; i++) {
            int centerY = (int) ((rect.bottom + rect.top) / 2 + (values.length / 2f - i - 0.5) * getTextHeight(paint));
            canvas.drawText(values[values.length - i - 1], getTextCenterX(rect.left, rect.right, paint),
                    getTextCenterY(centerY, paint), paint);
        }
    }

    /**
     * 绘制单行文字
     *
     * @param canvas
     * @param paint
     * @param rect
     * @param value
     */
    public static void drawSingleText(Canvas canvas, Paint paint, Rect rect, String value) {
        canvas.drawText(value, getTextCenterX(rect.left, rect.right, paint),
                getTextCenterY(rect.centerY(), paint), paint);
    }

}
