package com.sw.watches.bluetooth;

import com.sw.watches.bean.AlarmInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ParseWatchesData {

    /**
     * 解析不提醒数据
     *
     * @param str
     * @return
     */
    public static String parseUnNotifyMsg(String str) {
        int i = 14;
        char[] arrayOfChar = str.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (byte b1 = 0; b1 < arrayOfChar.length; ++b1) {
            byte[] arrayOfByte = String.valueOf(arrayOfChar[b1]).getBytes();
            i += arrayOfByte.length;
            if (i > 100)
                break;
            builder.append(String.valueOf(arrayOfChar[b1]));
        }
        return builder.toString();
    }

    /**
     * 解析提醒数据
     *
     * @param str
     * @return
     */
    public static String parseNotifyMsg(String str) {
        int i = 14;
        char[] arrayOfChar = str.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (byte b1 = 0; b1 < arrayOfChar.length; ++b1) {
            byte[] arrayOfByte = String.valueOf(arrayOfChar[b1]).getBytes();
            i += arrayOfByte.length;
            if (i > 250)
                break;
            builder.append(String.valueOf(arrayOfChar[b1]));
        }
        return builder.toString();
    }

    public static String byteFormatDate(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes == null || bytes.length <= 0)
            return null;
        for (byte b1 = 0; b1 < bytes.length; b1++) {
            String str = Integer.toHexString(bytes[b1] & 0xFF);
            if (str.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    public static String byteToString(byte b) {
        return Integer.toHexString(b & 0xFF);
    }

    public static String byteToDate(byte[] bytes) {
        //年
        int year = (bytes[13] & 0x7E) >> 1;
        //月
        String month;
        int monthInt = (bytes[13] & 0x1) << 3 | (bytes[14] & 0xE0) >> 5;
        if (monthInt < 10) {
            month = "0" + monthInt;
        } else {
            month = "" + monthInt;
        }
        //日
        String day;
        int dayInt = bytes[14] & 0x1F;
        if (dayInt < 10) {
            day = "0" + dayInt;
        } else {
            day = "" + dayInt;
        }
        return "20" + year + "-" + month + "-" + day;
    }

    public static int[] byteToIntArray(byte b) {
        int[] arrayOfInt = new int[8];
        String str = getBigInterString(new byte[]{b}, 2);
        for (int i = str.length() - 1; i >= 0; i--) {
            int j = i + 8 - str.length();
            arrayOfInt[j] = Integer.valueOf(str.substring(i, i + 1)).intValue();
        }
        return arrayOfInt;
    }

    public static boolean[] parseCheckBoolean(byte b) {

        boolean[] booleans = new boolean[8];
        String str = getBigInterString(new byte[]{b}, 2);
        for (int i = str.length() - 1; i >= 0; --i) {
            if (Integer.valueOf(str.substring(i, i + 1)) == 1) {
                booleans[i + 8 - str.length()] = true;
            } else {
                booleans[i + 8 - str.length()] = false;
            }
        }
        return booleans;
    }

    public static int parseCheckInt(boolean[] booleans) {
        int start = 0;
        for (byte i = 7; i >= 0; --i) {
            if (booleans[i]) {
                start = (int) (start + Math.pow(2.0D, (7 - i)));
            }
        }
        return start;
    }

    /**
     * 返回一个字符串，其中包含以基数表示的BigInteger的字符串。
     *
     * @param magnitude
     * @param radix
     * @return
     */
    public static String getBigInterString(byte[] magnitude, int radix) {
        return (new BigInteger(1, magnitude)).toString(radix);
    }

    public static float byteToData(float var0, float var1, int var2) {
        return (float) ((double) var1 * 1.036D * (double) var0 * 0.32D * (double) var2 * 1.0E-5D);
    }

    public static float byteToMile(float user_height, int totalStep) {
        return (float) (user_height * 0.41F * (float) totalStep * 0.00001F * 0.62F);
    }

    public static float byteToCalorie(float user_height, float user_weight, int totalStep) {
        return (float) (user_weight * 1.036F * user_height * (float) totalStep * 0.41F * 0.00001F);
    }

    public static float byteToKm(float user_height, int totalStep) {
        return (float) (user_height * 0.41F * (float) totalStep * 0.00001F);
    }

    public static int parseTwoTime(String time1, String time2) {

        int hour;
        String[] time1Array = time1.split(":");
        if (Integer.valueOf(time1Array[0]) >= 20 && Integer.valueOf(time1Array[0]) <= 24) {
            hour = Integer.valueOf(time1Array[0]);
        } else {
            hour = Integer.valueOf(time1Array[0]) + 24;
        }
        int min = Integer.valueOf(time1Array[1]);

        String[] time2Array = time2.split(":");
        int hour2 = Integer.valueOf(time2Array[0]) >= 20 && Integer.valueOf(time2Array[0]) <= 24 ? Integer.valueOf(time2Array[0]) : Integer.valueOf(time2Array[0]) + 24;
        int min2 = Integer.valueOf(time2Array[1]);
        if ((min += hour2 * 60 + min2 - hour * 60) < 0) {
            min = 0;
        }
        return min;
    }

    public static String stringToDate(String dateStr) {
        String str = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(dateStr);
            Calendar.getInstance().setTime(date);
            Calendar.getInstance().add(Calendar.DATE, 1);
            str = simpleDateFormat.format(Calendar.getInstance().getTime());
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return str;
    }

    public static int getAvgData(List<Integer> list) {
        int avg = 0;
        int temp = 0;
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i = (byte) (i + 1)) {
                temp += ((Integer) list.get(i)).intValue();
            }
            avg = temp / list.size();
        }
        return avg;
    }

    public static int getInfoSBP(int infoHR, int heartInfoHR, int infoSBP) {
        return infoHR == 0 ? 0 : (int) ((((double) infoHR * 0.0318D + 5.12D) / 0.05852D) - (((double) heartInfoHR * 0.0318D + 5.12D) / 0.05852D) + infoSBP);
    }

    public static int getInfoDBP(int infoHR, int infoSBP, int heartInfoSBP) {
        return infoHR == 0 ? 0 : (int) (((((double) infoHR * 0.0318D + 5.12D) / 0.05852D) - (((double) infoSBP * 0.0318D + 5.12D) / 0.05852D) + heartInfoSBP) * 0.4D / 0.62D);
    }

    public static ArrayList<AlarmInfo> stringToAlarmList(String alarmData) {
        ArrayList<AlarmInfo> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = (new JSONObject(alarmData)).getJSONArray("alarm_data");
            byte b1 = 0;
            while (b1 < jSONArray.length()) {
                JSONObject jSONObject = jSONArray.getJSONObject(b1);
                int alarmId = jSONObject.getInt("id");
                int alarmHour = jSONObject.getInt("hour");
                int alarmMin = jSONObject.getInt("min");
                int data = jSONObject.getInt("data");
                arrayList = addAlarmInfo(arrayList, new AlarmInfo(alarmId, alarmHour, alarmMin, data));
                b1 = (byte) (b1 + 1);
            }
        } catch (JSONException jSONException) {
            jSONException.printStackTrace();
        }
        return arrayList;
    }

    public static ArrayList<AlarmInfo> addAlarmInfo(ArrayList<AlarmInfo> infoArrayList, AlarmInfo info) {
        for (byte b = 0; b < 5; ++b) {
            int alarmId = getlAarmId(infoArrayList, b);
            if (alarmId != -1) {
                info.setAlarmId(alarmId);
                infoArrayList.add(info);
                break;
            }
        }
        return infoArrayList;
    }

    public static int getlAarmId(List<AlarmInfo> infoList, int i) {
        int alarmId = -1;
        if (infoList.size() == 0) {
            alarmId = 1;
        } else {
            for (byte b = 0; b < infoList.size(); alarmId = i) {
                if (infoList.get(b).getAlarmId() == i) {
                    alarmId = -1;
                    break;
                }
                ++b;
            }
        }
        return alarmId;
    }

    public static ArrayList<AlarmInfo> removeAlarmInfo(ArrayList<AlarmInfo> arrayList, int position) {
        arrayList.remove(position);
        return arrayList;
    }

    public static ArrayList<AlarmInfo> setAlarmInfo(ArrayList<AlarmInfo> infoArrayList, AlarmInfo alarmInfo, int i) {
        alarmInfo.setAlarmId((infoArrayList.get(i)).getAlarmId());
        infoArrayList.set(i, alarmInfo);
        return infoArrayList;
    }

    public static String alarmInfoListToJson(ArrayList<AlarmInfo> infoArrayList) {
        String str = "";
        if (infoArrayList.size() != 0) {
            JSONArray jsonArray = new JSONArray();
            for (byte b = 0; b < infoArrayList.size(); b = (byte) (b + 1)) {
                AlarmInfo info = infoArrayList.get(b);
                HashMap<Object, Object> map = new HashMap<>();
                map.put("id", info.getAlarmId());
                map.put("data", info.getAlarmData());
                map.put("hour", info.getAlarmHour());
                map.put("min", info.getAlarmMin());
                jsonArray.put(new JSONObject(map));
            }

            HashMap<Object, Object> dataMap = new HashMap<>();
            dataMap.put("alarm_data", jsonArray);
            str = (new JSONObject(dataMap)).toString();
        }
        return str;
    }

}