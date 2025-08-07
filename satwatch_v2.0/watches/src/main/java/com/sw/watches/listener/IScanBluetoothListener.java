package com.sw.watches.listener;

import com.sw.watches.bean.DeviceModule;

public interface IScanBluetoothListener {

    void updateList(DeviceModule deviceModule);

    void updateEnd();

    void updateMessyCode(DeviceModule deviceModule);
}

