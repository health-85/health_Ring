package com.healthy.rvigor.util;

import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;

/**
 * json  Array
 */
public class JsonArrayUtils {

    private JSONArray jsonArray = null;

    public JsonArrayUtils(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JsonArrayUtils(String jsonarraystr) {
        try {
            if (TextUtils.isEmpty(jsonarraystr)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(jsonarraystr);
            }
        } catch (JSONException e) {
            jsonArray = new JSONArray();
        } catch (Exception e) {
            jsonArray = new JSONArray();
        }
    }

    public JSONArray toJsonArray() {
        return jsonArray;
    }

    public JsonArrayUtils put(Object value) {
        if (jsonArray != null) {
            jsonArray.put(value);
        }
        return this;
    }

    public JsonArrayUtils putJsonUtils(JsonUtils value) {
        if (value != null) {
            if (jsonArray != null) {
                jsonArray.put(value.tojson());
            }
        }
        return this;
    }

    /**
     * 长度
     *
     * @return
     */
    public int length() {
        if (jsonArray != null) {
            return jsonArray.length();
        }
        return 0;
    }


    public String getString(int index) {
        if (jsonArray != null) {
            try {
                Object obj = jsonArray.get(index);
                if (obj instanceof String) {
                    return (String) obj;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public int getInt(int index) {
        if (jsonArray != null) {
            try {
                Object obj = jsonArray.get(index);
                if (obj instanceof Integer) {
                    return (Integer) obj;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public JsonUtils getJsonObject(int index) {
        if (jsonArray != null) {
            try {
                return new JsonUtils(jsonArray.getJSONObject(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JsonUtils("");
    }

}
