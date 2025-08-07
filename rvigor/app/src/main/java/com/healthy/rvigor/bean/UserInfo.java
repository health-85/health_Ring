package com.healthy.rvigor.bean;

public class UserInfo {

    public long id;

    public long birthday;
    /**
     * 慢性疾病名称
     */
    public String chronicDiseaseName="";

    public int height;
    /**
     * 是否有其他慢性疾病
     */
    public String isChronicDisease="";
    /**
     * 是否患有糖尿病
     */
    public String isDiabetes="";
    /**
     * 是否有心脏病
     */
    public String isHeartDisease="";
    /**
     * 高血压
     */
    public String isHypertension="";
    /**
     * 是否吸烟
     */
    public String isSmoke="";

    public String phone="";

    public int sex;

    public String username="";

    public float weigh;

    public int stated;

    public String updated="";

    public String created="";

    public String stepLength1="";

    public String stepLength2="";

    public String headImg="";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getHeight() {
        return height;
    }

    public String getChronicDiseaseName() {
        return chronicDiseaseName;
    }

    public void setChronicDiseaseName(String chronicDiseaseName) {
        this.chronicDiseaseName = chronicDiseaseName;
    }

    public String getIsChronicDisease() {
        return isChronicDisease;
    }

    public void setIsChronicDisease(String isChronicDisease) {
        this.isChronicDisease = isChronicDisease;
    }

    public String getIsDiabetes() {
        return isDiabetes;
    }

    public void setIsDiabetes(String isDiabetes) {
        this.isDiabetes = isDiabetes;
    }

    public String getIsHeartDisease() {
        return isHeartDisease;
    }

    public void setIsHeartDisease(String isHeartDisease) {
        this.isHeartDisease = isHeartDisease;
    }

    public String getIsHypertension() {
        return isHypertension;
    }

    public void setIsHypertension(String isHypertension) {
        this.isHypertension = isHypertension;
    }

    public String getIsSmoke() {
        return isSmoke;
    }

    public void setIsSmoke(String isSmoke) {
        this.isSmoke = isSmoke;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getWeigh() {
        return weigh;
    }

    public void setWeigh(float weigh) {
        this.weigh = weigh;
    }

    public int getStated() {
        return stated;
    }

    public void setStated(int stated) {
        this.stated = stated;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getStepLength1() {
        return stepLength1;
    }

    public void setStepLength1(String stepLength1) {
        this.stepLength1 = stepLength1;
    }

    public String getStepLength2() {
        return stepLength2;
    }

    public void setStepLength2(String stepLength2) {
        this.stepLength2 = stepLength2;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
