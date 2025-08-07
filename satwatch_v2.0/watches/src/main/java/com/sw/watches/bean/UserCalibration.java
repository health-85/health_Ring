package com.sw.watches.bean;

public class UserCalibration {
    public int UserCalibrationHR;

    public int UserCalibrationSBP;

    public int UserCalibrationDBP;

    public UserCalibration(int UserCalibrationHR, int UserCalibrationSBP, int UserCalibrationDBP) {
        setUserCalibrationHR(UserCalibrationHR);
        setUserCalibrationSBP(UserCalibrationSBP);
        setUserCalibrationDBP(UserCalibrationDBP);
    }

    public UserCalibration() {}

    public int getUserCalibrationHR() {
        return this.UserCalibrationHR;
    }

    public void setUserCalibrationHR(int UserCalibrationHR) {
        this.UserCalibrationHR = UserCalibrationHR;
    }

    public int getUserCalibrationSBP() {
        return this.UserCalibrationSBP;
    }

    public void setUserCalibrationSBP(int UserCalibrationSBP) {
        this.UserCalibrationSBP = UserCalibrationSBP;
    }

    public int getUserCalibrationDBP() {
        return this.UserCalibrationDBP;
    }

    public void setUserCalibrationDBP(int UserCalibrationDBP) {
        this.UserCalibrationDBP = UserCalibrationDBP;
    }
}
