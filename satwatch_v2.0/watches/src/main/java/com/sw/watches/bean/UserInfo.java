package com.sw.watches.bean;

public class UserInfo {
    public int userHeight;

    public int userWeight;

    public int age;

    public Boolean sex;

    public UserInfo(int userHeight, int userWeight, int age, boolean sex) {
        setUserHeight(userHeight);
        setUserWeight(userWeight);
        setAge(age);
        setSex(sex);
    }

    public int getUserHeight() {
        return this.userHeight;
    }

    public void setUserHeight(int userHeight) {
        this.userHeight = userHeight;
    }

    public int getUserWeight() {
        return this.userWeight;
    }

    public void setUserWeight(int userWeight) {
        this.userWeight = userWeight;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Boolean getSex() {
        return this.sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }
}