package com.sw.watches.bean;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sw.watches.bleUtil.SpBelUtil;
import com.sw.watches.bleUtil.DeviceModelUtil;


public class DeviceModule implements Parcelable {

    @RequiresApi(api = 21)
    public static final Creator<DeviceModule> CREATOR = new Creator<DeviceModule>() {
        public DeviceModule createFromParcel(Parcel parcel) {
            return new DeviceModule(parcel);
        }

        public DeviceModule[] newArray(int size) {
            return new DeviceModule[size];
        }
    };

    public int mRssi;

    public boolean isBLE;
    public boolean isCollect;
    public boolean mBeenConnected;

    public ScanResult scanResult;
    public BluetoothDevice mDevice;
    public SpBelUtil mDataMemory;

    public String mDeviceName;
    public String mServiceUUID;
    public String mReadWriteUUID;

    @SuppressLint("MissingPermission")
    public DeviceModule(BluetoothDevice device, int rssi, String deviceName, Context context, ScanResult scanResult) {
        this(deviceName, device, false, context, rssi);
        this.scanResult = scanResult;
        if (DeviceModelUtil.isDeviceModel(device.getName()) && context != null && !DeviceModelUtil.isDeviceModel(deviceName)) {
            mDataMemory = new SpBelUtil(context);
            mDataMemory.putString(device.getAddress(), deviceName);
            Log.d("AppRun" + DeviceModule.class.getSimpleName(), "修正保存乱码文字..");
        }
    }

    public DeviceModule(String deviceName, BluetoothDevice device) {
        this(deviceName, device, false, null, 10);
    }

    public DeviceModule(String deviceName, BluetoothDevice device, int rssi) {
        this(deviceName, device, false, null, rssi);
    }

    @SuppressLint("MissingPermission")
    public DeviceModule(String deviceName, BluetoothDevice bluetoothDevice, boolean bool, Context context, int rssi) {
        this.isBLE = false;
        this.isCollect = false;
        this.mDeviceName = deviceName;
        this.mDevice = bluetoothDevice;
        this.mBeenConnected = bool;
        this.mRssi = rssi;
        if (bluetoothDevice != null) {
            this.isBLE = bluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_LE;
            if (this.isBLE && context != null && (DeviceModelUtil.isDeviceModel(deviceName) || DeviceModelUtil.isDeviceModel(bluetoothDevice.getName())) &&
                    (deviceName = (new SpBelUtil(context)).getString(bluetoothDevice.getAddress())) != null) {
                this.mDeviceName = deviceName;
            }
        }
    }

    @RequiresApi(api = 21)
    public DeviceModule(Parcel parcel) {
        this.isBLE = false;
        this.isCollect = false;
        this.mDeviceName = parcel.readString();
        this.mDevice = (BluetoothDevice) parcel.readParcelable(BluetoothDevice.class.getClassLoader());
        boolean bool;
        if (parcel.readByte() != 0) {
            bool = true;
        } else {
            bool = false;
        }

        this.isBLE = bool;
        this.mRssi = parcel.readInt();
        if (parcel.readByte() != 0) {
            bool = true;
        } else {
            bool = false;
        }

        this.mBeenConnected = bool;
        this.scanResult = (ScanResult) parcel.readParcelable(ScanResult.class.getClassLoader());
        if (parcel.readByte() != 0) {
            bool = true;
        } else {
            bool = false;
        }

        this.isCollect = bool;
        this.mServiceUUID = parcel.readString();
        this.mReadWriteUUID = parcel.readString();
    }

    @RequiresApi(api = 21)
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(this.mDeviceName);
        parcel.writeParcelable(this.mDevice, flag);
        parcel.writeByte((byte) (this.isBLE ? 1 : 0));
        parcel.writeInt(this.mRssi);
        parcel.writeByte((byte) (this.mBeenConnected ? 1 : 0));
        parcel.writeParcelable(this.scanResult, flag);
        parcel.writeByte((byte) (this.isCollect ? 1 : 0));
        parcel.writeString(this.mServiceUUID);
        parcel.writeString(this.mReadWriteUUID);
    }

    public int describeContents() {
        return 0;
    }

    @SuppressLint("MissingPermission")
    public String getName() {
        if (mDeviceName != null) {
            return mDeviceName;
        } else {
            if (mDevice.getName() != null) {
                mDeviceName = mDevice.getName();
            } else {
                mDeviceName = "N/A";
            }
            return mDeviceName;
        }
    }

    @SuppressLint("MissingPermission")
    public String getOriginalName(Context context) {
        mDeviceName = getDevice().getName();
        String mac = new SpBelUtil(context).getString(getMac());
        if (isBLE && context != null && DeviceModelUtil.isDeviceModel(getDevice().getName()) && mac != null) {
            mDeviceName = mac;
        }
        if (mDeviceName == null) {
            mDeviceName = "N/A";
        }
        return mDeviceName;
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public String getMac() {
        return mDevice != null ? mDevice.getAddress() : "出错了";
    }

    public void setMessyCode(Context context) {
        String name = new SpBelUtil(context).getString(this.getMac());
        if (context != null && name != null) {
            Log.d("AppRun" + DeviceModule.class.getSimpleName(), "修正成功..");
            mDeviceName = name;
        }
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public void setUUID(String serviceUUID, String readWriteUUID) {
        if (serviceUUID != null) {
            mServiceUUID = serviceUUID;
        }
        if (readWriteUUID != null) {
            mReadWriteUUID = readWriteUUID;
        }
    }

    public void setCollectModule(Context context, String module) {
        if (mDataMemory != null) {
            mDataMemory.putCollect(getMac(), module);
        } else {
            mDataMemory = new SpBelUtil(context);
            mDataMemory.putCollect(getMac(), module);
        }
        if (TextUtils.isEmpty(module)) {
            getOriginalName(context);
            isCollect = false;
        }
    }

    public void isCollectName(Context context) {
        String collect;
        if (mDataMemory != null) {
            collect = mDataMemory.getCollect(this.getMac());
        } else {
            mDataMemory = new SpBelUtil(context);
            collect = mDataMemory.getCollect(getMac());
        }
        if (!TextUtils.isEmpty(collect)) {
            this.isCollect = true;
            this.mDeviceName = collect;
        }
    }

    public int getRssi() {
        return mRssi;
    }

    public boolean isBLE() {
        return isBLE;
    }

    public boolean isBeenConnected() {
        return mBeenConnected;
    }

    public String bluetoothType() {
        if (isBLE) {
            return "Ble蓝牙";
        } else {
            return mBeenConnected ? "已配对" : "未配对";
        }
    }

    public boolean isCollect() {
        return isCollect;
    }

    public String getReadWriteUUID() {
        return mReadWriteUUID != null ? mReadWriteUUID : "没有读写特征";
    }

    public String getServiceUUID() {
        return mServiceUUID != null ? mServiceUUID : "00001101-0000-1000-8000-00805F9B34FB";
    }
}
