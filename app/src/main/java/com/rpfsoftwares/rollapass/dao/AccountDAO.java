package com.rpfsoftwares.rollapass.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Update;

import com.rpfsoftwares.rollapass.model.Account;

import java.util.List;

/**
 * Created by rosariopfernandes on 3/7/18.
 */

@Dao
public interface AccountDAO {

    @Insert
    void createAccount(Account account);

    @Query("SELECT * FROM Account")
    LiveData<List<Account>> readAccounts();

    @Update
    void updateAccount(Account account);

    @Delete
    void deleteAccount(Account account);
}
