package com.fptu.fevent.util;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
public class PermissionConverter {

    @TypeConverter
    public static String fromList(List<Integer> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<Integer> toList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<Integer>>(){}.getType());
    }
}
