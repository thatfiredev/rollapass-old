package com.rpfsoftwares.rollapass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;


public class LoadingActivity extends ActionBarActivity {

    private TextView txtLoading;
    private SharedPreferences pref;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        txtLoading=(TextView)findViewById(R.id.txtLoading);

        //Get the Shared Preferences
        pref = getSharedPreferences("com.rpfsoftwares.rollapass", MODE_PRIVATE);
        boolean pvez=pref.getBoolean("pvez",true); //Check if it's the first time running the app

        db = new DatabaseHelper(LoadingActivity.this); //open the database connection

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//Keep the screen on

        if(pvez) //if it's the first time the user runs the app
        {
            txtLoading.setText(getResources().getString(R.string.loading));

            InputStream is;

            //check the phone's languange
            if(Locale.getDefault().getLanguage().equalsIgnoreCase("pt"))
                is = getResources().openRawResource(R.raw.db_pt);
            else
                is = getResources().openRawResource(R.raw.db);

            InputStreamReader inputStreamReader = new InputStreamReader(is);
            new CreateTables(db,inputStreamReader,this).execute();//call the AsyncTask to insert the words on the SQLite Database

            // it's not the first time anymore
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("pvez", false);
            edit.commit();
        }
        else
        {
            int duration = 1500;
            //Show the app logo for 1500 milliseconds before loading the Main Activity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, duration);
            db.closeDB();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //let the screen turn off
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //AsyncTask to insert the words on the SQLite Database
    public class CreateTables extends AsyncTask<String, Integer, String> {
        private DatabaseHelper db;
        private InputStreamReader is;
        private Context c;
        private String result="";
        private ProgressDialog progressDialog;

        //Constructor
        public CreateTables(DatabaseHelper db, InputStreamReader is, Context c)
        {
            this.db=db;
            this.is=is;
            this.c=c;
        }

        @Override
        protected void onPreExecute() {
            //Show the dialog
            progressDialog = new ProgressDialog(c, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(c.getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            BufferedReader bufferedReader = new BufferedReader(is);
            int id,i=0;
            String word, line;
            String [] elements;
            db.criarTabelas(); //create the tables
            db.setMaster(); //create the default Master Password ('12345')
            try {
                line = bufferedReader.readLine();

                //read the words from the raw .txt file and
                //insert it on the SQLite Table
                while (line != null && !line.equals(""))
                {
                    elements = line.split(" ");
                    id = Integer.parseInt(elements[0]);
                    word = elements[1];
                    db.createWord(id, word);
                    line = bufferedReader.readLine();

                    publishProgress((i*100)/7776); //publish progress to update the dialog's percentage

                    i++;
                    result=i+"";
                }

                bufferedReader.close();
                is.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                this.cancel(true);
            }
            db.closeDB();
            return result;
        }

        protected void onPostExecute(String a){
            //Dismiss the Dialog and prompt the user to set a Master Password
            progressDialog.dismiss();
            Intent i = new Intent(c, MasterPasswordActivity.class);
            c.startActivity(i);
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //update the dialog's percentage
            progressDialog.setProgress(values[0]);
            progressDialog.setMessage(c.getString(R.string.preparing)+String.valueOf(values[0])+"%");
        }
    }
}
