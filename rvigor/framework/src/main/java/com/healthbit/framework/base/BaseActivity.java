package com.healthbit.framework.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.healthbit.framework.bean.ActivityEventBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
* @Description:    Activity基类
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public abstract class BaseActivity extends AppCompatActivity {

    private static final int DESIGN_HEIGHT_DPI = 667;
    private static final int DESIGN_WIDTH_DPI = 375;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isScreenAdaptWithHeight() || isScreenAdaptWithWidth()) {
            screenAdaptWithDesignSize();
        }
        if (isFullScreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(getLayoutView());
        if (!isFullScreen()) {
            configureStatusBar();
        }
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    protected boolean isFullScreen() {
        return false;
    }

    protected void configureStatusBar() {
        //子类实现
    }

    /**
     *
     * @return 返回ContentView
     */
    protected abstract View getLayoutView();

    /**
     * 子类展示页面资源id
     *
     * @return 子类展示页面资源id
     */
    protected abstract int getLayoutResID();

    /**
     * 通过自有属性值判断，当样式值不为1时即为非系统默认样式。
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 1为非默认值
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 通过APP自有属性值判断，当样式值不为1时即为非系统默认样式。
     * 如果不为1，则设置为默认样式。
     * 为了屏幕样式适配，即将本项目的样式设为默认，不随系统改变而改变。
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        // 1为非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    protected boolean useEventBus() {
        return false;
    }

    /**
     * 需要以宽度为基准适配，复写此方法返回true即可
     *
     * @return
     */
    protected boolean isScreenAdaptWithWidth() {
        return false;
    }

    private void screenAdaptWithDesignSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final float targetDensity = isScreenAdaptWithHeight() ? (float) displayMetrics.heightPixels / (float) DESIGN_HEIGHT_DPI : (float) displayMetrics.widthPixels / (float) DESIGN_WIDTH_DPI;
        final float targetScaleDensity = targetDensity * (displayMetrics.scaledDensity / displayMetrics.density);
        final int targetDensityDpi = (int) (160 * targetDensity);
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaleDensity;
        displayMetrics.densityDpi = targetDensityDpi;
    }

    /**
     * 需要以高度为基准适配，复写此方法返回true即可
     *
     * @return
     */
    protected boolean isScreenAdaptWithHeight() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityEvent(ActivityEventBean eventBean) {
        if (eventBean.clazz == this.getClass()) {
            if (eventBean.operate == ActivityEventBean.CLOSE) {
                doClose();
            } else if (eventBean.operate == ActivityEventBean.REFRESH) {
                doRefresh();
            }
        }
    }

    protected void doRefresh() {

    }

    protected void doClose() {
        finish();
    }
}
