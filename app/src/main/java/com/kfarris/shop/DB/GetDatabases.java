package com.kfarris.shop.DB;

import android.content.Context;

import androidx.room.Room;

public class GetDatabases {

    /**
     * Sets up the user table.
     */
    public static UserDAO userDatabase(Context context) {
        UserDAO mUserDAO = Room.databaseBuilder(context, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().UserDAO();
        return mUserDAO;
    }

    /**
     * Sets up the item table.
     */
    public static ProductDAO productDatabase(Context context) {
        ProductDAO mProductDAO = Room.databaseBuilder(context, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().ProductDAO();
        return mProductDAO;
    }

}
