package com.kfarris.shop.DB;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kfarris.shop.Item;

import java.util.List;

@Dao
public interface ItemDAO {

    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM " + AppDatabase.ITEM_TABLE)
    List<Item> getItemInfo();

    @Query("SELECT * FROM " + AppDatabase.ITEM_TABLE + " WHERE mItemNameLowerCase = :name")
    Item getItem(String name);

}
