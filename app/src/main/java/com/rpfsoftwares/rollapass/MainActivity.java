package com.rpfsoftwares.rollapass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private Toolbar mToolbar;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public static Snackbar a;

    SharedPreferences pref;

    public static int length;
    private EditText txtPassword;
    private DatabaseHelper db;

    int tentativas=0;

    public static boolean askPass=true;

    private boolean isDialogShowing=false;

    @Override
    protected void onResume() {
        super.onResume();
        if(askPass)
        {
            mos();
        }
        else
        {
            askPass=true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isDialogShowing)
            askPass=false;
        else
            askPass=true;
    }

    //dialog to ask for the Master Password
    private void mos()
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.edittext,null);
        txtPassword=(EditText)dialogView.findViewById(R.id.txtMasterPassword);
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(R.string.master_pass3)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db = new DatabaseHelper(MainActivity.this);
                        String Master=db.getMasterPassword();
                        if(txtPassword.getText().toString().equals(Master))
                        {
                            dialog.dismiss();
                            isDialogShowing=false;
                        }
                        else
                        {
                            tentativas++;
                            if(tentativas>=pref.getInt("dattempts",3))
                            {
                                for(int i=0;i<7776;i++)
                                    db.delete(i);
                                Toast.makeText(MainActivity.this,getString(R.string.erase_confirmation),Toast.LENGTH_LONG);
                                db.closeDB();
                                isDialogShowing=false;
                                finish();
                            }
                            else
                            {
                                db.closeDB();
                                mos();
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isDialogShowing=false;
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
                isDialogShowing=true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("com.rpfsoftwares.rollapass", MODE_PRIVATE); //get the Shared Preferences
        boolean pvez=pref.getBoolean("pvez",true); //check if it's the first time running the app

        length=pref.getInt("plength",8); //get chosen length

        DatabaseHelper db = new DatabaseHelper(MainActivity.this); //open the database connection

        //load the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position==1) {
                    GenerateFragment.FAB.hide();
                    if (!ManageFragment.FAB.isShown()) {
                        ManageFragment.FAB.show();
                    }
                }
                else
                {
                    ManageFragment.FAB.hide();
                    if(GenerateFragment.txtWebsite.getText().toString().trim().isEmpty() && GenerateFragment.FAB.isShown())
                        GenerateFragment.FAB.hide();
                    else
                    if(!GenerateFragment.txtWebsite.getText().toString().trim().isEmpty() && !GenerateFragment.FAB.isShown())
                        GenerateFragment.FAB.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //Set up the TabLayout
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GenerateFragment(), getResources().getString(R.string.generate));
        adapter.addFragment(new ManageFragment(), getResources().getString(R.string.manage));
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        InputStream is = getResources().openRawResource(R.raw.db);
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        int id;
        String word,linha;
        String []elementos;

        if(pvez)
        {
            try {
                linha = bufferedReader.readLine();
                while (linha != null && !linha.equals(""))
                {
                    elementos = linha.split(" ");
                    id = Integer.parseInt(elementos[0]);
                    word = elementos[1];
                    db.createWord(id, word);
                    linha = bufferedReader.readLine();
                }
                bufferedReader.close();
                inputStreamReader.close();
                is.close();

                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("pvez", false);
                edit.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        db.closeDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i= new Intent(MainActivity.this,PreferencesActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
