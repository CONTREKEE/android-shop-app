package com.kfarris.shop;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kfarris.shop.DB.AppDatabase;

@Entity(tableName = AppDatabase.PRODUCT_TABLE)
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int mId;


    private String mProductName;
    private String mProductNameLowerCase;
    private double mPrice;
    private String mLocation;
    private int mQuantity;
    private String mDescription;

    public Product(String productName, String productNameLowerCase, double price, String location, int quantity, String description) {
        mProductName = productName;
        mProductNameLowerCase = productNameLowerCase.toLowerCase();
        mPrice = price;
        mLocation = location;
        mQuantity = quantity;
        mDescription = description;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public String getProductNameLowerCase() {
        return mProductNameLowerCase;
    }

    public void setProductNameLowerCase(String productNameLowerCase) {
        mProductNameLowerCase = productNameLowerCase;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public String toString() {
        String productInfo = getProductName() + "\n" + "Price : $" + getPrice()
                + "\n" + "Shipping from : " + getLocation() +
                "\nCurrent Quantity : " + getQuantity() + "\n" +
                "Product Info : \n"
                + getDescription();
        return productInfo;
    }

}
