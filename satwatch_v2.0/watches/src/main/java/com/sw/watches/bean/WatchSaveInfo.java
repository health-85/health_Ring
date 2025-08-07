package com.sw.watches.bean;

public class WatchSaveInfo {

    private String time;
    private int A;
    private int B;
    private int C;
    private int D;

    private String byteData;

    public WatchSaveInfo(String time, int A, int B, int C, int D){
        this.time = time;
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getA() {
        return A;
    }

    public void setA(int a) {
        A = a;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }

    public int getC() {
        return C;
    }

    public void setC(int c) {
        C = c;
    }

    public int getD() {
        return D;
    }

    public void setD(int d) {
        D = d;
    }

    public String getByteData() {
        return byteData;
    }

    public void setByteData(String byteData) {
        this.byteData = byteData;
    }
}
