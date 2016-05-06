package com.rpfsoftwares.rollapass;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public static Snackbar a;
    private SharedPreferences pref;
    public static int length;
    public static boolean askPass=true;

    @Override
    protected void onResume() {
        super.onResume();
        //Ask for password when the Activity becomes visible again
        if(askPass)
            askPassword();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Tell the app to ask for a password when the Activity becomes visible again
        boolean isChangingConfigurations;
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.HONEYCOMB)
            isChangingConfigurations=!isChangingConfigurations();//this method was implemented on API 11
        else
            isChangingConfigurations=false;

        if(!isChangingConfigurations)
            askPass = true;
    }

    /**
     * method to start Activity to Validate the Master Password
     */
    private void askPassword()
    {
        Intent vp= new Intent(this, ValidatePasswordActivity.class);
        startActivityForResult(vp, ValidatePasswordActivity.VALIDATE_PASSWORD_REQUEST);
        askPass=!askPass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ask for Password
        if(askPass)
            askPassword();

        pref = getSharedPreferences("com.rpfsoftwares.rollapass", MODE_PRIVATE); //get the Shared Preferences
        boolean pvez=pref.getBoolean("pvez",true); //check if it's the first time the user runs the app

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
                    GenerateFragment.fab.hide();
                    if (!ManageFragment.FAB.isShown()) {
                        ManageFragment.FAB.show();
                    }
                }
                else
                {
                    ManageFragment.FAB.hide();
                    if(GenerateFragment.txtWebsite.getText().toString().trim().isEmpty() && GenerateFragment.fab.isShown())
                        GenerateFragment.fab.hide();
                    else
                    if(!GenerateFragment.txtWebsite.getText().toString().trim().isEmpty() && !GenerateFragment.fab.isShown())
                        GenerateFragment.fab.show();
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

        //If it's the first time the user runs the app
        if(pvez)
        {
            InputStream is = getResources().openRawResource(R.raw.db);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            int id;
            String word,linha;
            String []elementos;

            try
            {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case ValidatePasswordActivity.VALIDATE_PASSWORD_REQUEST:
            {
                if(resultCode==RESULT_OK)
                    askPass=false;//Stay on App if the password is correct
                else
                    finish(); //Close the App if the user dismissed the ValidatePasswordActivity
                break;
            }
        }
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
