package com.example.roomdb.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.roomdb.daos.LoliDAO;
import com.example.roomdb.models.Loli;

@Database(entities = {Loli.class}, version = 1)
public abstract class LoliDB extends RoomDatabase {

    private static final String DATABASE_NAME = "loli.db";

    // Singleton
    private static LoliDB instance;

    public static synchronized LoliDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LoliDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public abstract LoliDAO loliDAO();
}
