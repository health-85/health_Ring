package com.healthy.rvigor.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.healthbit.framework.http.Client;
import com.healthbit.framework.util.SharedPreUtil;
import com.healthy.rvigor.MyApplication;
import com.sw.watches.bean.HeartInfo;
import com.sw.watches.bean.HeartListInfo;
import com.sw.watches.bean.MotionInfo;
import com.sw.watches.bean.PoHeartInfo;
import com.sw.watches.bean.SleepInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/5 22:03
 * @UpdateRemark:
 */
public class UserManager {

    private static UserManager mInstance;

    private static String token = "";
    private static String mobile = "";
    private static LoginBean loginBean = null;
    private static UserInfo userInfoBean = null;

    private UserManager() {

    }

    public static UserManager getInstance() {
        if (mInstance == null) {
            synchronized (UserManager.class) {
                if (mInstance == null) {
                    mInstance = new UserManager();
                }
            }
        }
        return mInstance;
    }

    public void saveMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return;
        }
        UserManager.mobile = mobile;
        SharedPreUtil.putString(MyApplication.Companion.instance(), "sp_mobile", mobile);
    }

    public String getMobile() {
        if (TextUtils.isEmpty(mobile)) {
            mobile = SharedPreUtil.getString(MyApplication.Companion.instance(), "sp_mobile", "");
        }
        return mobile;
    }

    public void saveToken(String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        UserManager.token = token;
        SharedPreUtil.putString(MyApplication.Companion.instance(), "sp_token", token);
    }

//    public String getToken() {
//        ResLoginBean resLoginBean = UserManager.getInstance().getLoginBean();
//        if (resLoginBean != null){
//            boolean flag = (System.currentTimeMillis() - resLoginBean.getLoginTimestamp()) > ((resLoginBean.getExpires_in() - 60) * 1000);
//            if (flag){
//                if ((System.currentTimeMillis() - resLoginBean.getLoginTimestamp()) > ((resLoginBean.getRefresh_expires_in() - 60) * 1000)){
//                    TokenInvalidEventBean bean = new TokenInvalidEventBean();
//                    bean.setTokenInvalid(true);
//                    EventBus.getDefault().post(bean);
//                }else {
//                    // 刷新token
//                    ResLoginBean resLoginBean1 = getSyncBusiness(resLoginBean);
//                    if (resLoginBean1 != null && !TextUtils.isEmpty(resLoginBean1.getAccess_token())){
//                        UserManager.getInstance().saveToken(Constants.TOKEN_PREFIX + resLoginBean1.getAccess_token());
//                        UserManager.getInstance().saveLoginBean(resLoginBean1);
//                        token = Constants.TOKEN_PREFIX + resLoginBean1.getAccess_token();
//                    }else {
//                        TokenInvalidEventBean bean = new TokenInvalidEventBean();
//                        bean.setTokenInvalid(true);
//                        EventBus.getDefault().post(bean);
//                    }
//                }
//            }else {
//                token = SharedPreUtil.getString(MyApplication.Companion.instance(), "sp_token", "");
//            }
//        }
//        return token;
//    }
//    public void saveLoginBean(ResLoginBean bean) {
//        long loginTimestamp = System.currentTimeMillis();
//        bean.setLoginTimestamp(loginTimestamp);
//        loginBean = bean;
//        String jsonString = JSON.toJSONString(bean);
//        SharedPreferences settings = MyApplication.Companion.instance().getSharedPreferences("login_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("login_bean", jsonString);
//        editor.apply();
//    }
//
//    public ResLoginBean getLoginBean() {
//        if (loginBean == null) {
//            SharedPreferences settings = MyApplication.Companion.instance().getSharedPreferences("login_info", Context.MODE_PRIVATE);
//            String settingsString = settings.getString("login_bean", null);
//            loginBean = JSON.parseObject(settingsString, ResLoginBean.class);
//            if (loginBean == null) {
//                loginBean = new ResLoginBean();
//            }
//        }
//        return loginBean;
//    }
//
//    public void saveUserInfoBean(UserInfoBean bean) {
//        userInfoBean = bean;
//        String jsonString = JSON.toJSONString(bean);
//        SharedPreferences settings = MyApplication.Companion.instance().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("user_info_bean", jsonString);
//        editor.apply();
//    }
//
//    public UserInfoBean getUserInfoBean() {
//        SharedPreferences settings = MyApplication.Companion.instance().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        String settingsString = settings.getString("user_info_bean", null);
//        userInfoBean = JSON.parseObject(settingsString, UserInfoBean.class);
//        if (userInfoBean == null) {
//            userInfoBean = new UserInfoBean();
//        }
//        return userInfoBean;
//    }
//
//    public void clearLoginBean() {
//        loginBean = null;
//        SharedPreferences settings = MyApplication.Companion.instance().getSharedPreferences("login_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("login_bean", "{}");
//        editor.commit();
//    }
//    public void clearUserInfoBean() {
//        userInfoBean = null;
//        SharedPreferences settings = MyApplication.Companion.instance().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("user_info_bean", "{}");
//        editor.commit();
//    }
//
//    public static boolean checkIsLogin() {
//        return !TextUtils.isEmpty(UserManager.getInstance().getToken()) &&
//                UserManager.getInstance().getLoginBean() != null;
//    }
//
//    public void clearInfo() {
//        token = "";
//        mobile = "";
//        SharedPreUtil.putString(MyApplication.Companion.instance(), "sp_token", "");
//        UserManager.getInstance().clearLoginBean();
//        UserManager.getInstance().clearUserInfoBean();
//    }

}
