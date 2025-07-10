package com.fptu.fevent.util;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class IntegerListConverter {
    @TypeConverter
    public static List<Integer> fromString(String value) {
        List<Integer> list = new ArrayList<>();
        if (value == null || value.isEmpty()) return list;
        for (String s : value.split(",")) {
            list.add(Integer.parseInt(s.trim()));
        }
        return list;
    }

    @TypeConverter
    public static String fromList(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Integer i : list) {
            sb.append(i).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}

