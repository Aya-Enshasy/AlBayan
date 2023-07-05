package com.ayaenshasy.AlBayan.database;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

public class BooleanMapConverter {

    @TypeConverter
    public String fromMap(Map<String, Boolean> map) {
        if (map == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @TypeConverter
    public Map<String, Boolean> toMap(String json) {
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
