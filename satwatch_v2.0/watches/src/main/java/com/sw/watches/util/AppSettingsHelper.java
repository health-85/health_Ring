package com.sw.watches.util;

import android.content.Context;
import android.text.TextUtils;

import com.realsil.sdk.core.base.BaseSharedPrefes;
import com.realsil.sdk.dfu.model.DfuConfig;
import com.realsil.sdk.dfu.support.view.ProgressView;

public class AppSettingsHelper extends BaseSharedPrefes {

    private final static String KEY_WORK_MODE_PROMPT = "switch_dfu_work_mode_prompt";
    private final static String KEY_UPLOAD_FILE_PROMPT = "switch_dfu_upload_file_prompt";
    private final static String KEY_BANK_LINK = "switch_dfu_backlink";
    private final static String KEY_DFU_SUCCESS_HINT = "switch_dfu_success_hint";
    private final static String KEY_DFU_FIXED_IMAGE_FILE = "switch_dfu_fixed_image_file";
    private final static String KEY_RTK_SELECT_FILE_TYPE = "rtk_select_file_type";
    private final static String KEY_RTK_FILE_LOCATION = "rtk_file_location";
    private final static String KEY_OTA_CHANNEL = "rtk_last_ota_channel";
    private final static String KEY_RTK_PROGRESS_TYPE = "rtk_progress_type";

    public AppSettingsHelper(Context context) {
        super(context);
    }

    public AppSettingsHelper(Context context, String s) {
        super(context, s);
    }


    public volatile static AppSettingsHelper instance;

    public static synchronized AppSettingsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppSettingsHelper(context);
        }
        return instance;
    }

    public boolean isWorkModePromptEnabled() {
        if (!contains(KEY_WORK_MODE_PROMPT)) {
            set(KEY_WORK_MODE_PROMPT, false);
            return false;
        }
        return getBoolean(KEY_WORK_MODE_PROMPT, false);
    }

    public boolean isUploadFilePromptEnabled() {
        if (!contains(KEY_UPLOAD_FILE_PROMPT)) {
            set(KEY_UPLOAD_FILE_PROMPT, false);
            return false;
        }

        return getBoolean(KEY_UPLOAD_FILE_PROMPT, false);
    }

    public boolean isDfuBankLinkEnabled() {
        if (!contains(KEY_BANK_LINK)) {
            set(KEY_BANK_LINK, false);
            return false;
        }

        return getBoolean(KEY_BANK_LINK, false);
    }

    /**
     * it's recommended to turn on for bbpro ic.
     */
    public boolean isDfuSuccessHintEnabled() {
        if (!contains(KEY_DFU_SUCCESS_HINT)) {
            set(KEY_DFU_SUCCESS_HINT, false);
            return false;
        }
        return getBoolean(KEY_DFU_SUCCESS_HINT, false);
    }

    public boolean isFixedImageFileEnabled() {
        if (!contains(KEY_DFU_FIXED_IMAGE_FILE)) {
            set(KEY_DFU_FIXED_IMAGE_FILE, false);
            return false;
        }

        return getBoolean(KEY_DFU_FIXED_IMAGE_FILE, false);
    }

    public String getSelectFileType() {
        String value = getString(KEY_RTK_SELECT_FILE_TYPE, null);
        if (TextUtils.isEmpty(value)) {
            set(KEY_RTK_SELECT_FILE_TYPE, "*/*");
            return "*/*";
        } else {
            return value;
        }
    }

    public int getFileLocation() {
        String value = getString(KEY_RTK_FILE_LOCATION, null);
        if (TextUtils.isEmpty(value)) {
            set(KEY_RTK_FILE_LOCATION, DfuConfig.FILE_LOCATION_SDCARD + "");
            return DfuConfig.FILE_LOCATION_SDCARD;
        } else {
            return Integer.parseInt(value);
        }
    }

    public int progressType() {
        String value = getString(KEY_RTK_PROGRESS_TYPE, null);
        if (TextUtils.isEmpty(value)) {
            set(KEY_RTK_PROGRESS_TYPE, ProgressView.PROGRESS_TYPE_OVERALL + "");
            return ProgressView.PROGRESS_TYPE_OVERALL;
        } else {
            return Integer.parseInt(value);
        }
    }

    public int getLastOtaChannel() {
        return getInt(KEY_OTA_CHANNEL, DfuConfig.CHANNEL_TYPE_GATT);
    }

    public void setLastOtaChannel(int channel) {
        set(KEY_OTA_CHANNEL, channel);
    }
}
