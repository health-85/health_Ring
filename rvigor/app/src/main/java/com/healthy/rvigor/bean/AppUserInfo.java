package com.healthy.rvigor.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.healthy.rvigor.Constants;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.util.SPUtil;
import com.healthy.rvigor.util.SpConfig;
import com.alibaba.fastjson.JSON;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/14 10:33
 * @UpdateRemark:
 */
public class AppUserInfo {

    private UserInfo userInfo = null;

    public UserInfo getUserInfo() {

        if (userInfo == null) {
            String userStr = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, "");
            if (!TextUtils.isEmpty(userStr)) {
                userInfo = JSON.parseObject(userStr, UserInfo.class);
            } else {
                userInfo = new UserInfo();
                SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, new Gson().toJson(userInfo));
            }
        }
        return userInfo;
    }

    public void saveUserId(long userId) {
        UserInfo userInfo = null;
        String userStr = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, "");
        if (!TextUtils.isEmpty(userStr)) {
            userInfo = JSON.parseObject(userStr, UserInfo.class);
        } else {
            userInfo = new UserInfo();
        }
        userInfo.id = userId;
        SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, new Gson().toJson(userInfo));
    }

    public void saveUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            String userStr = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, "");
            if (!TextUtils.isEmpty(userStr)) {
                userInfo = JSON.parseObject(userStr, UserInfo.class);
            } else {
                userInfo = new UserInfo();
            }
        }
        SPUtil.saveData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, new Gson().toJson(userInfo));
    }

    //是否已经保存个人信息
    public boolean isSaveUserInfo(){
        UserInfo userInfo = null;
        String userStr = (String) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.USER_CONFIG, "");
        if (!TextUtils.isEmpty(userStr)) {
            userInfo = JSON.parseObject(userStr, UserInfo.class);
        } else {
            userInfo = new UserInfo();
        }
        if (userInfo == null) return false;
        if (!TextUtils.isEmpty(userInfo.headImg)) return true;
        if (!TextUtils.isEmpty(userInfo.username)) return true;
        if (userInfo.sex > 0) return true;
        if (userInfo.birthday > 0) return true;
        if (userInfo.height > 0) return true;
        if (userInfo.weigh > 0) return true;
        return false;
    }

    public void clearInfo() {
        userInfo = null;
    }
}
