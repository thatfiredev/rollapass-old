package com.rpfsoftwares.rollapass;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MasterPasswordActivity extends AppCompatActivity {

    private EditText txtPassword,txtRePassword;
    private FloatingActionButton fab;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_password);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        txtRePassword=(EditText)findViewById(R.id.txtRePassword);

        db=new DatabaseHelper(this); //open the database connection

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if password matches the repassword
                if(txtPassword.getText().toString().equals(txtRePassword.getText().toString()))
                {
                    new AlertDialog.Builder(MasterPasswordActivity.this)
                            .setMessage(getResources().getString(R.string.master_pass2))
                            .setNegativeButton(getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.updatemaster(txtPassword.getText().toString());
                                    db.closeDB();
                                    Intent i = new Intent(MasterPasswordActivity.this, MainActivity.class);
                                    startActivity(i);
                                    MainActivity.askPass=false;
                                    finish();
                                }
                            })
                            .show();
                }
                else
                {
                    txtPassword.setText("");
                    txtRePassword.setText("");
                    Snackbar.make(view, getResources().getString(R.string.pass_no_match), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        fab.show();
    }

}
