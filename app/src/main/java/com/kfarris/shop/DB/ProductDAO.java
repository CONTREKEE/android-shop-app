package com.kfarris.shop.DB;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kfarris.shop.Product;

import java.util.List;

@Dao
public interface ProductDAO {

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM " + AppDatabase.PRODUCT_TABLE)
    List<Product> getProductsInfo();

    @Query("SELECT * FROM " + AppDatabase.PRODUCT_TABLE + " WHERE mProductNameLowerCase = :name")
    Product getProduct(String name);

}
