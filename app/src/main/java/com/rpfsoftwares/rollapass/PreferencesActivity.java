package com.rpfsoftwares.rollapass;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PreferencesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private Toolbar mToolbar;
    private PreferencesAdapter adapter;
    private ListView list;
    private PreferencesActivity customListView =null;
    private ArrayList<PreferencesList> customListViewValuesArr = new ArrayList<PreferencesList>();
    private SharedPreferences pref;
    private Resources res;
    private DatabaseHelper db;
    private CoordinatorLayout fl;
    private EditText txtPassword;
    private String []attempts;

    //Used by the Drive API
    private GoogleApiClient mGoogleApiClient;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        fl=(CoordinatorLayout)findViewById(R.id.fl);
        attempts=getResources().getStringArray(R.array.attempts);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle(getResources().getString(R.string.action_settings));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getSharedPreferences("com.rpfsoftwares.rollapass", MODE_PRIVATE);

        customListView = this;
        setListData();
        res =getResources();
        list= (ListView)findViewById(R.id.lstPreferences);
        adapter=new PreferencesAdapter(customListView, customListViewValuesArr, res);
        list.setAdapter(adapter);
        MainActivity.askPass=false;

        //Autorize Roll a Pass to use Google Drive
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void setListData()
    {
        customListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences1),
                ""+pref.getInt("plength",8)));
        customListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences2),
                getResources().getString(R.string.preferences3)));

        int attemp=pref.getInt("dattempts",3);
        if(attemp>999)
            customListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences4),
                    attempts[0]));
        else
        customListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences4),
                getResources().getString(R.string.preferences5) + " "+attemp+ " "+getResources().getString(R.string.attempts)));

        customListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences6),
                getResources().getString(R.string.preferences7)));
        customListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences8),
                getResources().getString(R.string.version)));
    }

    public void onItemClick(int mPosition)
    {
        switch (mPosition)
        {
            case 0://Password Length
                final String []tamanhos=new String [] {8+"", 12+"", 16+"", 24+""};
                ArrayAdapter Len = new ArrayAdapter<String>(PreferencesActivity.this,
                        R.layout.list_black_text,R.id.list_content,tamanhos);
                new AlertDialog.Builder(PreferencesActivity.this)
                        //.setTitle(getResources().getString(R.string.preferences1))
                .setAdapter(Len,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putInt("plength", Integer.parseInt(tamanhos[which]));
                                edit.commit();
                                customListViewValuesArr.get(0).setSubtitle(tamanhos[which]);
                                adapter=new PreferencesAdapter(customListView,
                                        customListViewValuesArr, res);
                                list.setAdapter(adapter);
                            }
                        })
                .show();
                break;
            case 1://Change Master Password
                View dialogView=getLayoutInflater().inflate(R.layout.edittext, null);
                txtPassword=(EditText)dialogView.findViewById(R.id.txtMasterPassword);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage(R.string.master_pass3)
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db = new DatabaseHelper(PreferencesActivity.this);
                                String Master=db.getMasterPassword();
                                if(txtPassword.getText().toString().equals(Master))
                                {
                                    Intent in= new Intent(PreferencesActivity.this,
                                            MasterPasswordActivity.class);
                                    startActivity(in);
                                    finish();
                                }
                                else
                                {
                                    Snackbar.make(fl,getString(R.string.pass_wrong), Snackbar.LENGTH_LONG)
                                            .show();
                                    db.closeDB();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case 2: //Delete All Data
                final int [] att= new int[] { 99999, 3, 5,  10};
                ArrayAdapter Atte = new ArrayAdapter<String>(PreferencesActivity.this,
                        R.layout.list_black_text,R.id.list_content,attempts);
                new AlertDialog.Builder(PreferencesActivity.this)
                        //.setTitle(getResources().getString(R.string.preferences1))
                        .setAdapter(Atte,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences.Editor edit = pref.edit();
                                        edit.putInt("dattempts", att[which]);
                                        edit.commit();
                                        if(which==0)
                                            customListViewValuesArr.get(2)
                                                    .setSubtitle(attempts[which]);
                                        else
                                            customListViewValuesArr.get(2)
                                                    .setSubtitle(getResources().getString(R.string.preferences5) + " "+attempts[which]);
                                        adapter=new PreferencesAdapter(customListView,
                                                customListViewValuesArr, res);
                                        list.setAdapter(adapter);
                                    }
                                })
                        .show();
                break;
            case 3: //Export Saved Passwords
                dialogView=getLayoutInflater().inflate(R.layout.edittext,null);
                txtPassword=(EditText)dialogView.findViewById(R.id.txtMasterPassword);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage(R.string.master_pass3)
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db = new DatabaseHelper(PreferencesActivity.this);
                                String master=db.getMasterPassword();
                                if(txtPassword.getText().toString().equals(master))
                                {
                                    String []optionsArray=getResources()
                                            .getStringArray(R.array.export_options);
                                    ArrayAdapter optionsAdapter= new ArrayAdapter<String>(PreferencesActivity.this,
                                            R.layout.list_black_text,R.id.list_content,optionsArray);
                                    new AlertDialog.Builder(PreferencesActivity.this)
                                            .setAdapter(optionsAdapter, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(which==0)
                                                        exportSavedPasswords();
                                                    else
                                                    {
                                                        mGoogleApiClient.connect();
                                                        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                                                .setResultCallback(contentsCallback);
                                                    }
                                                }
                                            })
                                            .show();
                                    dialog.dismiss();
                                }
                                else
                                {
                                    Snackbar.make(fl,getString(R.string.pass_wrong), Snackbar.LENGTH_LONG)
                                            .show();
                                    db.closeDB();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case 4://About
                Intent i= new Intent(PreferencesActivity.this,AboutActivity.class);
                startActivity(i);
                break;
        }
    }

    //Export Passwords to Internal Storage
    public void exportSavedPasswords(){
        if(Environment.getExternalStorageState().equalsIgnoreCase("mounted"))
        {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), "roll_a_pass");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File ficheiro = new File(root, new SimpleDateFormat("dd-MM-yyyy_HH:mm")
                        .format(new Date())+".txt");
                FileWriter writer = new FileWriter(ficheiro);
                db = new DatabaseHelper(this);
                writer.append(db.export());
                writer.flush();
                writer.close();
                db.closeDB();
                Snackbar.make(fl, getString(R.string.password_exported), Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(fl, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
        else
            Snackbar.make(fl, getString(R.string.unmounted), Snackbar.LENGTH_LONG).show();
    }

    //Used to export passwords to Drive
    ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        new AlertDialog.Builder(PreferencesActivity.this)
                                .setMessage("Connection wasnt successful: "+result.getStatus().getStatusMessage())
                                .setCancelable(true)
                                .show();
                        return;
                    }
                    final DriveContents driveContents=result.getDriveContents();

                    //Perform I/O off the UI thread
                    new Thread() {
                        @Override
                        public void run()
                        {
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer =  new OutputStreamWriter(outputStream);

                            db= new DatabaseHelper(PreferencesActivity.this);
                            try
                            {
                                writer.write(db.export());
                                writer.flush();
                                writer.close();
                            }
                            catch(IOException e)
                            {
                                Log.e("Couldn't Export",e.getMessage());
                            }
                            db.closeDB();

                            MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                    .setTitle("RAP_"+new SimpleDateFormat(getString(R.string.dateformat)).format(new Date())+".txt")
                                    .setViewed(false)
                                    .setMimeType("text/plain")
                                    .build();

                            //create file on root folder
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, metadataChangeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };


    final ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess())
                    {
                        Snackbar.make(fl, getString(R.string.password_not_exported), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Snackbar.make(fl, getString(R.string.password_exported), Snackbar.LENGTH_LONG).show();
                    }
                };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage("IntentSender "+e.getMessage())
                        .setCancelable(true)
                        .show();
            }
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
        }
    }

    //Disconnect when the Activity is no longer visible to the user
    @Override
    protected void onPause() {
        if(mGoogleApiClient!=null)
            mGoogleApiClient.disconnect();
        super.onPause();
    }
}
