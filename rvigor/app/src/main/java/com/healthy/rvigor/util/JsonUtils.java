package com.healthy.rvigor.util;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * jsonutils
 */
public class JsonUtils {

    private JSONObject jsonObject = null;

    public JsonUtils(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    /**
     * json对象
     *
     * @param jsonstr
     */
    public JsonUtils(String jsonstr) {
        try {
            this.jsonObject = new JSONObject(jsonstr);
        } catch (JSONException e) {
            this.jsonObject = new JSONObject();
        }
    }

    /**
     * @return
     */
    public boolean isNullObject() {
        return (jsonObject == null);
    }

    /**
     * 获取该json的拥有者
     */
    protected JsonUtils ownner = null;

    /**
     * 获取拥有者
     *
     * @return
     */
    public JsonUtils getOwnner() {
        return ownner;
    }

    public JsonUtils put(String key, Object value) {
        if (jsonObject != null) {
            try {
                if (value != null) {
                    if (value instanceof JsonUtils) {
                        this.putJsonObject(key, (JsonUtils) value);
                        return this;
                    }
                }
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public JsonUtils put(String key, int value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public JsonUtils put(String key, boolean value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public JsonUtils put(String key, double value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }


    public JsonUtils put(String key, long value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * @param key
     * @param jsonUtils
     * @return
     */
    public JsonUtils putJsonObject(String key, JsonUtils jsonUtils) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, jsonUtils.tojson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * 获取文本
     *
     * @param name
     * @return
     */
    public Object get(String name) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return jsonObject.get(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * put
     *
     * @param key
     * @param array
     */
    public JsonUtils putJsonArray(String key, JSONArray array) {
        return this.put(key, array);
    }

    /**
     * put
     *
     * @param key
     * @param arrayUtils
     */
    public JsonUtils putJsonArray(String key, JsonArrayUtils arrayUtils) {
        return this.put(key, arrayUtils.toJsonArray());
    }


    /**
     * 获取jsonobject成员
     *
     * @param name
     * @return
     */
    public JsonUtils getJsonObject(String name) {
        Object sub = get(name);
        if ((sub != null) && (sub instanceof JSONObject)) {
            JsonUtils jsonUtils = new JsonUtils((JSONObject) sub);
            jsonUtils.ownner = this;
            return jsonUtils;
        }
        return null;
    }

    /**
     * 获取jsonArray对象
     *
     * @param name
     * @return
     */
    public JsonArrayUtils getJsonArray(String name) {
        Object sub = get(name);
        if ((sub != null) && (sub instanceof JSONArray)) {
            JsonArrayUtils jsonUtils = new JsonArrayUtils((JSONArray) sub);
            return jsonUtils;
        }
        return new JsonArrayUtils(new JSONArray());
    }

    /**
     * @param name
     * @return
     */
    public String getString(String name) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    Object value = jsonObject.get(name);
                    if (value != null) {
                        if (value instanceof String) {
                            return TextUtils.isEmpty((String)value) ? "" : (String) value;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @param name
     * @param defaulValue
     * @return
     */
    public String getString(String name, String defaulValue) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    Object value = jsonObject.get(name);
                    if (value != null) {
                        if (value instanceof String) {
                            return TextUtils.isEmpty((String)value) ? "" : (String) value;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return TextUtils.isEmpty((String)defaulValue) ? "" : (String) defaulValue;
    }

    public int getInt(String name, int defaultvalue) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return jsonObject.getInt(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultvalue;
    }

    public long getLong(String name, long defaultvalue) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return jsonObject.getLong(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultvalue;
    }

    /**
     * 获取boolean数据
     *
     * @param name
     * @return
     */
    public boolean getBoolean(String name) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return jsonObject.getBoolean(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取boolean数据
     *
     * @param name
     * @return
     */
    public boolean getBoolean(String name, boolean defaultvalue) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return jsonObject.getBoolean(name);
                }
            } catch (JSONException e) {
                return defaultvalue;
            }
        }
        return defaultvalue;
    }

    /**
     * @param name
     * @return
     */
    public boolean hasName(String name) {
        if (jsonObject != null) {
            return jsonObject.has(name);
        }
        return false;
    }

    /**
     * 获取Double数据
     *
     * @param name
     * @return
     */
    public double getDouble(String name, double defaultvalue) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return jsonObject.getDouble(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultvalue;
    }

    /**
     * @param name
     * @param defaultvalue
     * @return
     */
    public float getFloat(String name, float defaultvalue) {
        if (jsonObject != null) {
            try {
                if (jsonObject.has(name)) {
                    return (float) jsonObject.getDouble(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defaultvalue;
    }


    /**
     * 返回json 对象
     *
     * @return
     */
    public JSONObject tojson() {
        if (jsonObject != null) {
            return jsonObject;
        } else {
            return new JSONObject();
        }
    }
}
