package com.healthy.rvigor.util;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.healthy.rvigor.R;

/**
 * ================================================
 * 描    述：图片加载工具类
 * 修订历史：
 * ================================================
 */
public class ImageUtil {

    public static void loadIcon(ImageView ivImg, int id) {
        if (ivImg == null){
            return;
        }
        Glide.with(ivImg).load(id).into(ivImg);
    }

    public static void loadImgWithKey(ImageView image, String path, int placeDrawable) {
        if (image == null){
            return;
        }
        if (TextUtils.isEmpty(path)) {
            image.setImageResource(placeDrawable);
            return;
        }
        RequestOptions options = new RequestOptions().placeholder(placeDrawable)
                .signature(new ObjectKey(path));
        Glide.with(image).load(path).apply(options).into(image);
    }

    public static void loadImgWithKey(ImageView image, int id, int placeDrawable) {
        if (image == null){
            return;
        }
        RequestOptions options = new RequestOptions().placeholder(placeDrawable)
                .signature(new ObjectKey(id + System.currentTimeMillis()));
        Glide.with(image).load(id).apply(options).into(image);
    }

    /**
     * 加载头像
     *
     * @param image
     * @param path
     */
    public static void loadImgHeadWithKey(ImageView image, String path) {
        if (image == null) return;
        RequestOptions options = new RequestOptions()
                .signature(new ObjectKey(path));
        Glide.with(image).load(path).apply(options).into(image);
    }

    //1 男性 2 女性
    public static void loadImgHeadWithKey(ImageView image, String path, boolean isMan) {
        if (image == null) return;
        RequestOptions options = new RequestOptions()
                .signature(new ObjectKey(path));
        Glide.with(image).load(path).apply(options).into(image);
    }

    public static void loadLocalImg(ImageView image, String path) {
        if (image == null){
            return;
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            RequestOptions options = new RequestOptions()
                    .signature(new ObjectKey(path)).placeholder(R.drawable.svg_man);
            Glide.with(image).load(path).apply(options).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadImg(ImageView image, String path, OnImageReadyListener listener) {
        if (image == null){
            return;
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }
        RequestOptions options = new RequestOptions();
        Glide.with(image).load(path).apply(options).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                image.setImageDrawable(resource);
                if (listener != null) {
                    listener.onImageReady();
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (listener != null) {
                    listener.onImageFail();
                }
            }
        });
    }

    public interface OnImageReadyListener {
        void onImageReady();

        void onImageFail();
    }
}
