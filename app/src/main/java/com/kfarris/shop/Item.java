package com.kfarris.shop;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kfarris.shop.DB.AppDatabase;

@Entity(tableName = AppDatabase.ITEM_TABLE)
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int mId;


    private String mItemName;
    private String mItemNameLowerCase;
    private double mPrice;
    private String mLocation;
    private int mQuantity;
    private String mDescription;

    public Item(String itemName, String itemNameLowerCase, double price, String location, int quantity, String description) {
        mItemName = itemName;
        mItemNameLowerCase = itemNameLowerCase.toLowerCase();
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

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    public String getItemNameLowerCase() {
        return mItemNameLowerCase;
    }

    public void setItemNameLowerCase(String itemNameLowerCase) {
        mItemNameLowerCase = itemNameLowerCase;
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
        String itemInfo = getItemName() + "\n" + "Price : $" + getPrice()
                + "\n" + "Shipping from : " + getLocation() +
                "\nCurrent Quantity : " + getQuantity() + "\n" +
                "Item Info : \n"
                + getDescription();
        return itemInfo;
    }

}
