package com.rpfsoftwares.rollapass;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;


public class PreferencesActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private PreferencesAdapter adapter;
    private ListView list;
    private PreferencesActivity CustomListView=null;
    private ArrayList<PreferencesList> CustomListViewValuesArr= new ArrayList<PreferencesList>();
    private SharedPreferences pref;
    private Resources res;
    private DatabaseHelper db;
    private CoordinatorLayout fl;
    private EditText txtPassword;
    String []attempts;

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

        CustomListView = this;
        setListData();
        res =getResources();
        list= (ListView)findViewById(R.id.lstPreferences);
        adapter=new PreferencesAdapter( CustomListView, CustomListViewValuesArr, res);
        list.setAdapter(adapter);
        MainActivity.askPass=false;
    }

    public void setListData()
    {
        CustomListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences1),
                ""+pref.getInt("plength",8)));
        CustomListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences2),
                getResources().getString(R.string.preferences3)));

        int attemp=pref.getInt("dattempts",3);
        if(attemp>999)
            CustomListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences4),
                    attempts[0]));
        else
        CustomListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences4),
                getResources().getString(R.string.preferences5) + " "+attemp+ " "+getResources().getString(R.string.attempts)));

        CustomListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences6),
                getResources().getString(R.string.preferences7)));
        CustomListViewValuesArr.add(new PreferencesList(getResources().getString(R.string.preferences8),
                getResources().getString(R.string.version)));
    }
    public void onItemClick(int mPosition)
    {
        PreferencesList tempValues = (PreferencesList ) CustomListViewValuesArr.get(mPosition);
        switch (mPosition)
        {
            case 0://Password Length
                final String []tamanhos=new String[4];
                tamanhos[0]=""+8;
                tamanhos[1]=""+12;
                tamanhos[2]=""+16;
                tamanhos[3]=""+24;
                ArrayAdapter Len = new ArrayAdapter<String>(PreferencesActivity.this,R.layout.list_black_text,R.id.list_content,tamanhos);
                new AlertDialog.Builder(PreferencesActivity.this)
                        //.setTitle(getResources().getString(R.string.preferences1))
                .setAdapter(Len,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putInt("plength", Integer.parseInt(tamanhos[which]));
                                edit.commit();
                                //Log.e("cumprimento definido", "" + pref.getInt("plength", 8));
                                MainActivity.length= Integer.parseInt(tamanhos[which]);
                                CustomListViewValuesArr.get(0).setSubtitle(tamanhos[which]);
                                adapter=new PreferencesAdapter( CustomListView, CustomListViewValuesArr, res);
                                list.setAdapter(adapter);
                            }
                        })
                .show();
                break;
            case 1://Change Master Password
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView=inflater.inflate(R.layout.edittext,null);
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
                                    Intent in= new Intent(PreferencesActivity.this,MasterPasswordActivity.class);
                                    startActivity(in);
                                    finish();
                                }
                                else
                                {
                                    Snackbar.make(fl,getString(R.string.pass_wrong), Snackbar.LENGTH_LONG).show();
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
                final int [] att= new int[4];
                att[0]=99999;
                att[1]=3;
                att[2]=5;
                att[3]=10;
                ArrayAdapter Atte = new ArrayAdapter<String>(PreferencesActivity.this,R.layout.list_black_text,R.id.list_content,attempts);
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
                                            CustomListViewValuesArr.get(2).setSubtitle(attempts[which]);
                                        else
                                            CustomListViewValuesArr.get(2).setSubtitle(getResources().getString(R.string.preferences5) + " "+attempts[which]);
                                        adapter=new PreferencesAdapter( CustomListView, CustomListViewValuesArr, res);
                                        list.setAdapter(adapter);
                                    }
                                })
                        .show();
                break;
            case 3: //Export Saved Passwords
                inflater = this.getLayoutInflater();
                dialogView=inflater.inflate(R.layout.edittext,null);
                txtPassword=(EditText)dialogView.findViewById(R.id.txtMasterPassword);
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage(R.string.master_pass3)
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.e("Pass ",txtPassword.getText().toString());
                                db = new DatabaseHelper(PreferencesActivity.this);
                                String Master=db.getMasterPassword();
                                //Log.e("Pass set",Master);
                                if(txtPassword.getText().toString().equals(Master))
                                {
                                    dialog.dismiss();
                                    exportSavedPasswords();
                                }
                                else
                                {
                                    Snackbar.make(fl,getString(R.string.pass_wrong), Snackbar.LENGTH_LONG).show();
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


    public void exportSavedPasswords(){
        if(Environment.getExternalStorageState().equalsIgnoreCase("mounted"))
        {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), "roll_a_pass");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File ficheiro = new File(root, new SimpleDateFormat("dd-MM-yyyy_HH:mm").format(new Date())+".txt");
                FileWriter writer = new FileWriter(ficheiro);
                db = new DatabaseHelper(this);
                writer.append(db.export());
                writer.flush();
                writer.close();
                db.closeDB();
                Snackbar.make(fl, getString(R.string.password_exported), Snackbar.LENGTH_LONG);
                //Log.e("Erro","Exported");
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(fl, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
        else
            Snackbar.make(fl, getString(R.string.unmounted), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
