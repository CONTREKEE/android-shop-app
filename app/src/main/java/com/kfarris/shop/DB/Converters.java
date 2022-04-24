package com.kfarris.shop.DB;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;


public class Converters implements Serializable {

    @TypeConverter // note this annotation
    public String fromOptionValuesList(List<String> productNames) {
        if (productNames == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        String json = gson.toJson(productNames, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<String> toOptionValuesList(String productNames) {
        if (productNames == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> productCategoriesList = gson.fromJson(productNames, type);
        return productCategoriesList;
    }


}
