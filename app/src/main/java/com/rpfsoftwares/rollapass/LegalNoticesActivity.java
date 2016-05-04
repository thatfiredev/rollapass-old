package com.rpfsoftwares.rollapass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;

public class LegalNoticesActivity extends AppCompatActivity {

    private TextView txtOpenSource;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_notices);

        //find views  by ID
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtOpenSource=(TextView)findViewById(R.id.txtOpenSource);

        //Set up the Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set the License Text
        txtOpenSource.setText(GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(LegalNoticesActivity.this));
    }
}
