package com.healthy.rvigor.bean;

//语言类型
public class LanguageType {

    public static int LANGUAGE_SAMPLE_CHINESE = 1;
    public static int LANGUAGE_TRADITIONAL_CHINESE = 2;
    public static int LANGUAGE_ENGLISH = 3;
    public static int LANGUAGE_SPANISH = 4;
    public static int LANGUAGE_FRENCH = 5;
    public static int LANGUAGE_GERMAN = 6;
    public static int LANGUAGE_ITALIAN = 7;
    public static int LANGUAGE_JAPANESE = 8;
    public static int LANGUAGE_SWEDISH = 9;
    public static int LANGUAGE_KOREAN = 10;
    public static int LANGUAGE_PORTUGUESE = 11;
    public static int LANGUAGE_RUSSIAN = 12;
    public static int LANGUAGE_TURKISH = 13;
    public static int LANGUAGE_POLAND = 14;
    public static int LANGUAGE_MONGOLIA = 15;

    private int type;
    private String name;

    private String tip;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
