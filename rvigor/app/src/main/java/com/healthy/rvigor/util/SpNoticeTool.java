package com.healthy.rvigor.util;


import com.healthy.rvigor.MyApplication;

public class SpNoticeTool {

    public static final String IS_PHONE_NOTICE = "is_phone_notice";
    public static final String IS_MESSAGE_NOTICE = "is_message_notice";
    public static final String IS_WEIXIN_NOTICE = "is_weixin_notice";
    public static final String IS_QQ_NOTICE = "is_qq_notice";
    public static final String IS_FACEBOOK_NOTICE = "is_facebook_notice";
    public static final String IS_TWITTER_NOTICE = "is_twitter_notice";
    public static final String IS_LINKEDLN_NOTICE = "is_linkedln_notice";
    public static final String IS_WHATSAPP_NOTICE = "is_whatsapp_notice";
    public static final String IS_INSTAGRAM_NOTICE = "is_instagram_notice";
    public static final String IS_SKYPE_NOTICE = "is_skype_notice";
    public static final String IS_YOUTUBE_NOTICE = "is_youtube_notice";
    public static final String IS_VIBER_NOTICE = "is_viber_notice";
    public static final String IS_KAKAO_NOTICE = "is_kakao_notice";
    public static final String IS_VKONTAKE_NOTICE = "is_vkontake_notice";
    public static final String IS_APPLEMAIL_NOTICE = "is_applemail_notice";
    public static final String IS_APPLECALENDAR_NOTICE = "is_applecalendar_notice";
    public static final String IS_APPLEFACE_NOTICE = "is_appleface_notice";
    public static final String IS_TIM_NOTICE = "is_tim_notice";
    public static final String IS_GAIML_NOTICE = "is_gaiml_notice";
    public static final String IS_DINGTALKPLUS_NOTICE = "is_dingtalkplus_notice";
    public static final String IS_WORKWECHAT_NOTICE = "is_workwechat_notice";
    public static final String IS_AADD_NOTICE = "is_aadd_notice";
    public static final String IS_BEIKE_NOTICE = "is_beike_notice";
    public static final String IS_LIANJIA_NOTICE = "is_lianjia_notice";
    public static final String IS_LINE_NOTICE = "is_line_notice";
    public static final String IS_MESSENGER_NOTICE = "is_messenger_notice";

    public static boolean getMessengerNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_MESSENGER_NOTICE, false);
    }

    public static boolean getLineNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_LINE_NOTICE, false);
    }

    public static boolean getLianjiaNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_LIANJIA_NOTICE, false);
    }

    public static boolean getBeikeNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_BEIKE_NOTICE, false);
    }

    public static boolean getAADDNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_AADD_NOTICE, false);
    }

    public static boolean getWorkChatNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_WORKWECHAT_NOTICE, false);
    }

    public static boolean getDingTalkNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_DINGTALKPLUS_NOTICE, false);
    }

    public static boolean getGaimlNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_GAIML_NOTICE, false);
    }

    public static boolean getTimeNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_TIM_NOTICE, false);
    }

    public static boolean getAppleFaceNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_APPLEFACE_NOTICE, false);
    }

    public static boolean getAppleCalendarNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_APPLECALENDAR_NOTICE, false);
    }

    public static boolean getAppleMailNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_APPLEMAIL_NOTICE, false);
    }

    public static boolean getVkontakeNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_VKONTAKE_NOTICE, false);
    }

    public static boolean getKakaoNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_KAKAO_NOTICE, false);
    }

    public static boolean getViberNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_VIBER_NOTICE, false);
    }

    public static boolean getYouTubeNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_YOUTUBE_NOTICE, false);
    }

    public static boolean getSkypeNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_SKYPE_NOTICE, false);
    }

    public static boolean getInstagramMNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_INSTAGRAM_NOTICE, false);
    }

    public static boolean getWhatsappNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_WHATSAPP_NOTICE, false);
    }

    public static boolean getLinkedLnNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_LINKEDLN_NOTICE, false);
    }

    public static boolean getTwitterNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_TWITTER_NOTICE, false);
    }

    public static boolean getFacebookNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_FACEBOOK_NOTICE, false);
    }

    public static boolean getQQNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_QQ_NOTICE, false);
    }

    public static boolean getWeixinNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_WEIXIN_NOTICE, false);
    }

    public static boolean getPhoneNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_PHONE_NOTICE, false);
    }

    public static boolean getSMSNoticeEnable(){
        return (boolean) SPUtil.getData(MyApplication.Companion.instance(), SpConfig.IS_MESSAGE_NOTICE, false);
    }

}
