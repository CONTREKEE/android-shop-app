package com.kfarris.shop.DB;

import android.content.Context;

import androidx.room.Room;

public class GetDatabases {

    /**
     * Returns UserDAO.
     * @param context
     * @return
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
     * Returns ProductDAO.
     * @param context
     * @return
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
