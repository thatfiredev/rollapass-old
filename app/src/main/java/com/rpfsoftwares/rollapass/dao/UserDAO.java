package com.rpfsoftwares.rollapass.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rpfsoftwares.rollapass.model.User;

/**
 * Created by rosariopfernandes on 3/7/18.
 */

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void newUser(User user);

    @Query("SELECT * FROM User WHERE userId = 1 " +
            "AND masterPassword = :password")
    User getCurrentUser(String password);
}
