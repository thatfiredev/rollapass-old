package com.rpfsoftwares.rollapass;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ValidatePasswordActivity extends AppCompatActivity {
    static final int VALIDATE_PASSWORD_REQUEST=1;
    private FloatingActionButton fab;
    private EditText txtPassword;
    private DatabaseHelper db;
    private int failedAttempts=0;
    private SharedPreferences pref;
    private CoordinatorLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_password);

        setResult(Activity.RESULT_CANCELED);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtPassword=(EditText) findViewById(R.id.txtPassword);
        cl= (CoordinatorLayout) findViewById(R.id.cl);

        pref = getSharedPreferences("com.rpfsoftwares.rollapass", MODE_PRIVATE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = new DatabaseHelper(ValidatePasswordActivity.this);
                String master=db.getMasterPassword();
                if(txtPassword.getText().toString().equals(master))
                {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                else
                {
                    if(failedAttempts >=pref.getInt("dattempts",3))
                    {
                        db= new DatabaseHelper(ValidatePasswordActivity.this);
                        for(int i=0;i<db.getAccountCount();i++)
                            db.delete(i);
                        Toast.makeText(getApplicationContext(),getString(R.string.erase_confirmation),Toast.LENGTH_LONG).show();
                        db.closeDB();
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                    else
                    {
                        Snackbar.make(cl,R.string.pass_wrong,Snackbar.LENGTH_LONG).show();
                        txtPassword.setText("");
                        db.closeDB();
                    }
                    failedAttempts++;
                }
            }
        });
    }
}
