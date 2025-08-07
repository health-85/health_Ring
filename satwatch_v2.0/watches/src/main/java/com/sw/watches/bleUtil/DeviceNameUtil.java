package com.sw.watches.bleUtil;

public class DeviceNameUtil {

    public static final short len1 = 2;
    public static final short len2 = 3;
    public static final short len3 = 9;

    public DeviceNameUtil() {

    }

    public static byte[] parseDeviceName(short paramShort, byte[] byteArray) {
        int i = 0;
        int j = byteArray.length;
        while (i < j) {
            try {
                byte b = byteArray[i];
                if (byteArray[i + 1] == (byte) paramShort) {
                    int k;
                    byte[] arrayOfByte = new byte[k = b - 1];
                    for (b = 0; b < k; b = (byte) (b + 1))
                        arrayOfByte[b] = byteArray[i + 2 + b];
                    return arrayOfByte;
                }
                if ((i += b + 1) >= byteArray.length)
                    return null;
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }
}
