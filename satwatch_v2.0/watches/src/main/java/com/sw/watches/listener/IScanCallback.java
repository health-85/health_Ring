package com.sw.watches.listener;

import com.sw.watches.bean.DeviceModule;

public interface IScanCallback {

    void addDeviceModule();

    void addDeviceModule(DeviceModule deviceModule);
}
