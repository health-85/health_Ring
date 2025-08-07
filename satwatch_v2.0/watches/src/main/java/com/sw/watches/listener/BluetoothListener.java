package com.sw.watches.listener;

public interface BluetoothListener {

    void showByteArray(byte[] arrayOfByte, String str);

    void blueConnectFail(String str, String exception);

    void blueConnectSuccess(String str);

    void available(boolean bool);

    void disconnectedAddress(String address);

    void writeLength(int length);

    void log(String clazzName, String msg, String tag);

    void readLength(int len);
}
