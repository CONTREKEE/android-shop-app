package com.kfarris.shop.DB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.kfarris.shop.Item;
import com.kfarris.shop.User;

@Database(entities = {User.class, Item.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "iBay.db";
    public static final String USER_TABLE = "user_table";
    public static final String ITEM_TABLE = "item_table";

    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract UserDAO UserDAO();
    public abstract ItemDAO ItemDAO();

    public static AppDatabase getInstance(Context context) {

        if (instance == null) {

            synchronized (LOCK) {

                if (instance == null) {

                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();

                }

            }

        }

        return instance;

    }

}
