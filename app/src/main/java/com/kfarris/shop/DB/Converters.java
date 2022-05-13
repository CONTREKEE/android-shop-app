package com.kfarris.shop.DB;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Converters implements Serializable{

    /**
     * I did not know how to handle a list of strings
     * when storing them in the android room database.
     * I found a source from
     * https://stackoverflow.com/questions/44986626/android-room-database-how-to-handle-arraylist-in-an-entity
     */

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
