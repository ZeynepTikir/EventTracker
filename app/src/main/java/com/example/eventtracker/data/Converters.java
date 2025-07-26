package com.example.eventtracker.data;

import androidx.room.TypeConverter;

public class Converters {

    // boolean dizisini String olarak kaydet (örneğin: "true,false,true,false,false,true,false")
    @TypeConverter
    public static String fromBooleanArray(boolean[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    // String'i boolean dizisine dönüştür
    @TypeConverter
    public static boolean[] toBooleanArray(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] items = data.split(",");
        boolean[] array = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            array[i] = Boolean.parseBoolean(items[i]);
        }
        return array;
    }

}
