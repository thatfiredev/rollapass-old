package com.rpfsoftwares.rollapass;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.rpfsoftwares.rollapass.dao.AccountDAO;
import com.rpfsoftwares.rollapass.dao.UserDAO;
import com.rpfsoftwares.rollapass.model.Account;
import com.rpfsoftwares.rollapass.model.User;

/**
 * Created by rosariopfernandes on 3/7/18.
 */

@Database(entities = {Account.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "roll_a_pass_db").build();
        }
        return INSTANCE;
    }

    public abstract AccountDAO accountDAO();
    public abstract UserDAO userDAO();
}
