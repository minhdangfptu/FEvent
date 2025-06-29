package com.fptu.fevent.util;

import androidx.room.TypeConverter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PermissionConverter {

    @TypeConverter
    public String fromList(List<String> list) {
        return new JSONArray(list).toString();  // lưu dưới dạng ["a", "b"]
    }

    @TypeConverter
    public List<String> toList(String json) {
        List<String> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
