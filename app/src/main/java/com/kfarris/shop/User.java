package com.kfarris.shop;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kfarris.shop.DB.AppDatabase;

@Entity(tableName = AppDatabase.USER_TABLE)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int mId;

    private String mUsername;
    private String mPassword = "password";
    private int mIsAdmin;

    public User(String username, String password, int isAdmin) {
        mUsername = username;
        mPassword = password;
        mIsAdmin = isAdmin;
    }


    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getIsAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        mIsAdmin = isAdmin;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ID : " + getId() + "\n");
        sb.append("Username : " + getUsername() + "\n");
        sb.append("Password : " + getPassword() + "\n");
        sb.append("isAdmin : " + getIsAdmin());
        return sb.toString();
    }
}


