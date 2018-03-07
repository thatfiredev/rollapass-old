package com.rpfsoftwares.rollapass.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.rpfsoftwares.rollapass.AppDatabase;
import com.rpfsoftwares.rollapass.model.Account;

import java.util.List;

/**
 * Created by rosariopfernandes on 3/7/18.
 */

public class AccountViewModel extends AndroidViewModel{
    private LiveData<List<Account>> accounts;

    public AccountViewModel(Application application)
    {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        accounts = db.accountDAO().readAccounts();
    }

    public LiveData<List<Account>> getAccounts() {
        return accounts;
    }
}
