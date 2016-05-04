package com.rpfsoftwares.rollapass;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.GoogleApiAvailability;

public class AboutActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private CoordinatorLayout cl;
    private Button btnNotices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Setting the fab and Layout
        fab=(FloatingActionButton)findViewById(R.id.fab);
        cl=(CoordinatorLayout)findViewById(R.id.fl);
        btnNotices=(Button)findViewById(R.id.btnNotices);

        //Timer used to show the fab after 500 milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.show();
            }
        }, 500);

        //Setting the fab's onClick Listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"rosariofernandes@live.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT   , getString(R.string.mail_body));
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.mail_title)));
                } catch (android.content.ActivityNotFoundException e) {
                    Snackbar.make(cl,getString(R.string.mail_error),Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnNotices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSource= new Intent(AboutActivity.this,LegalNoticesActivity.class);
                startActivity(openSource);
            }
        });
    }
}