package com.sw.watches.bean;

public class MeetingInfo {
    public int MeetingYear;

    public int MeetingMonth;

    public int MeetingDay;

    public int MeetingHour;

    public int MeetingMin;

    public boolean MeetingEnable;

    public MeetingInfo(int MeetingYear, int MeetingMonth, int MeetingDay, int MeetingHour, int MeetingMin, boolean MeetingEnable) {
        setMeetingYear(MeetingYear);
        setMeetingMonth(MeetingMonth);
        setMeetingDay(MeetingDay);
        setMeetingHour(MeetingHour);
        setMeetingMin(MeetingMin);
        setMeetingEnable(MeetingEnable);
    }

    public int getMeetingYear() {
        return this.MeetingYear;
    }

    public void setMeetingYear(int MeetingYear) {
        this.MeetingYear = MeetingYear;
    }

    public int getMeetingMonth() {
        return this.MeetingMonth;
    }

    public void setMeetingMonth(int MeetingMonth) {
        this.MeetingMonth = MeetingMonth;
    }

    public int getMeetingDay() {
        return this.MeetingDay;
    }

    public void setMeetingDay(int MeetingDay) {
        this.MeetingDay = MeetingDay;
    }

    public int getMeetingHour() {
        return this.MeetingHour;
    }

    public void setMeetingHour(int MeetingHour) {
        this.MeetingHour = MeetingHour;
    }

    public int getMeetingMin() {
        return this.MeetingMin;
    }

    public void setMeetingMin(int MeetingMin) {
        this.MeetingMin = MeetingMin;
    }

    public boolean getMeetingEnable() {
        return this.MeetingEnable;
    }

    public void setMeetingEnable(boolean MeetingEnable) {
        this.MeetingEnable = MeetingEnable;
    }

    public String toString() {
        return "MeetingInfo{MeetingYear=" + this.MeetingYear + ", MeetingMonth=" + this.MeetingMonth + ", MeetingDay=" + this.MeetingDay + ", MeetingHour=" + this.MeetingHour + ", MeetingMin=" + this.MeetingMin + ", MeetingEnable=" + this.MeetingEnable + '}';
    }
}
