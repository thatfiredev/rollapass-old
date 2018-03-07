package com.rpfsoftwares.rollapass.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by rosariopfernandes on 3/7/18.
 */

@Entity
public class User {
    @PrimaryKey
    private int userId;
    private String masterPassword;

    public User() {
    }

    public User(int userId, String masterPassword) {
        this.userId = userId;
        this.masterPassword = masterPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }
}
