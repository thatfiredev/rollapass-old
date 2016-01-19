package com.rpfsoftwares.rollapass;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GenerateFragment extends Fragment {

    public static FloatingActionButton FAB;
    private TextView txtPassword;
    public static EditText txtWebsite,txtUsername;
    private TextInputLayout inputWebsite,inputUsername;
    private Button btnRoll;
    private DatabaseHelper db;
    private CoordinatorLayout fl;
    private int length=MainActivity.length,half;

    public GenerateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_generate, container, false);

        //Assigning the views
        fl=(CoordinatorLayout)rootView.findViewById(R.id.fl);
        FAB = (FloatingActionButton) rootView.findViewById(R.id.fab);
        txtPassword=(TextView)rootView.findViewById(R.id.txtPassword);
        txtWebsite=(EditText)rootView.findViewById(R.id.txtWebsite);
        txtUsername=(EditText)rootView.findViewById(R.id.txtUsername);
        inputWebsite=(TextInputLayout)rootView.findViewById(R.id.input_layout_website);
        inputUsername=(TextInputLayout)rootView.findViewById(R.id.input_layout_username);
        btnRoll=(Button)rootView.findViewById(R.id.btnRoll);

        db = new DatabaseHelper(getContext()); //Open the Database Connection

        half=length/2; //half the length that has been set by the user

        FAB.hide();//hide the FloatingActionButton
        //Set FAB's OnClick Listener
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create the Account
                Account account = new Account(txtWebsite.getText().toString(), txtUsername.getText().toString(), txtPassword.getText().toString());
                db.create(account);

                //Update the Manage's List
                ManageFragment.rv.setAdapter(new RVAdapter(db.read(), ManageFragment.rv));
                db.closeDB(); //Close the database connection

                final Snackbar a = Snackbar.make(fl, getString(R.string.save_confirmation), Snackbar.LENGTH_SHORT);
                a.show(); //Show the confirmation snackbar

                //Reset the layout
                txtWebsite.setText("");
                txtUsername.setText("");
                gerarPassword();
                inputWebsite.setErrorEnabled(false);
            }
        });
        btnRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerarPassword();
            }
        });
        txtWebsite.addTextChangedListener(new MyTextWatcher(txtWebsite,inputWebsite));

        gerarPassword();//Generate first password

        return rootView;
    }

    private void gerarPassword() //method to generate a random password
    {
        String password=db.getWord(getRandomLocation()); //get the first word

        while(password.length()<half || password.length()>length)
        {
            if(length==8)
                password = db.getWord(getRandomLocation()) + db.getWord(getRandomLocation());
            else if(length==12)
                password = db.getWord(getRandomLocation()) + db.getWord(getRandomLocation()) + db.getWord(getRandomLocation());
            else if(length==16)
                password = db.getWord(getRandomLocation()) + db.getWord(getRandomLocation()) + db.getWord(getRandomLocation())+ db.getWord(getRandomLocation());
            else
                password = db.getWord(getRandomLocation()) + db.getWord(getRandomLocation()) + db.getWord(getRandomLocation())+ db.getWord(getRandomLocation())+ db.getWord(getRandomLocation());
        }

        //if the password is too short, add a few random characters on the end
        while(password.length()<length)
            password=password+getRandomChar(rollDice(),rollDice(),rollDice());

        txtPassword.setText(password);

        db.closeDB();
    }

    //Getting the result of 5 dice rolls.
    public String getRandomLocation()
    {
        int [] rolls= new int[5];
        String password_location="";
        for(int i=0;i<rolls.length;i++)
        {
            rolls[i] = rollDice();
            password_location=password_location+rolls[i];
        }
        return password_location;
    }

    //Rolling the dice
    // *returns a random number from 1 to 6
    private int rollDice()
    {
        return (int)Math.ceil(Math.random()*6);
    }

    //method to get a random character from 3 dice rolls
    // *if you don't understand this, read the DiceWare guide
    public char getRandomChar(int firstRoll, int secondRoll,int thirdRoll)
    {
        char[][] a =new char[6][6];
        char retorno=' ';
        if(firstRoll==1 || firstRoll==2)
        {
        a[0][0]='A';a[0][1]='B';a[0][2]='C';a[0][3]='D';a[0][4]='E';a[0][5]='F';
        a[1][0]='G';a[1][1]='H';a[1][2]='I';a[1][3]='J';a[1][4]='K';a[1][5]='L';
        a[2][0]='M';a[2][1]='N';a[2][2]='O';a[2][3]='P';a[2][4]='Q';a[2][5]='R';
        a[3][0]='S';a[3][1]='T';a[3][2]='U';a[3][3]='V';a[3][4]='W';a[3][5]='X';
        a[4][0]='Y';a[4][1]='Z';a[4][2]='0';a[4][3]='1';a[4][4]='2';a[4][5]='3';
        a[5][0]='4';a[5][1]='5';a[5][2]='6';a[5][3]='7';a[5][4]='8';a[5][5]='9';
        }
        else if(firstRoll==3 || firstRoll==4){
        a[0][0]='a';a[0][1]='b';a[0][2]='c';a[0][3]='d';a[0][4]='e';a[0][5]='f';
        a[1][0]='g';a[1][1]='h';a[1][2]='i';a[1][3]='j';a[1][4]='k';a[1][5]='l';
        a[2][0]='m';a[2][1]='n';a[2][2]='o';a[2][3]='p';a[2][4]='q';a[2][5]='r';
        a[3][0]='s';a[3][1]='t';a[3][2]='u';a[3][3]='v';a[3][4]='w';a[3][5]='x';
        a[4][0]='y';a[4][1]='z';a[4][2]=' ';a[4][3]=' ';a[4][4]=' ';a[4][5]=' ';
        a[5][0]=' ';a[5][1]=' ';a[5][2]=' ';a[5][3]=' ';a[5][4]=' ';a[5][5]=' ';
        }
        else{
        a[0][0]='!';a[0][1]='@';a[0][2]='#';a[0][3]='$';a[0][4]='%';a[0][5]='^';
        a[1][0]='&';a[1][1]='*';a[1][2]='(';a[1][3]=')';a[1][4]='-';a[1][5]='=';
        a[2][0]='+';a[2][1]='[';a[2][2]=']';a[2][3]='{';a[2][4]='}';a[2][5]=' ';
        a[3][0]='|';a[3][1]=' ';a[3][2]=';';a[3][3]=':';a[3][4]=' ';a[3][5]='"';
        a[4][0]='<';a[4][1]='>';a[4][2]='/';a[4][3]='?';a[4][4]='.';a[4][5]=',';
        a[5][0]=' ';a[5][1]=' ';a[5][2]=' ';a[5][3]=' ';a[5][4]=' ';a[5][5]=' ';
        }
        retorno=a[thirdRoll-1][secondRoll-1];

        while(retorno==' ')
        {
            thirdRoll=rollDice();
            secondRoll=rollDice();
            retorno = a[thirdRoll-1][secondRoll-1];
        }

        return retorno;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FAB.hide();
    }

    private class MyTextWatcher implements TextWatcher {

        private EditText view;
        private TextInputLayout inputLayout;

        private MyTextWatcher(EditText view,TextInputLayout inputLayout) {
            this.view = view;
            this.inputLayout=inputLayout;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            if (view.getText().toString().trim().isEmpty()) {
                //view.getBackground().clearColorFilter();
                inputLayout.setError(getResources().getString(R.string.error));
                view.requestFocus();
                FAB.hide();
            } else {
                    view.getBackground().setColorFilter(getResources().getColor(R.color.yellow),PorterDuff.Mode.SRC_IN);
                    inputLayout.setErrorEnabled(false);
                    FAB.show();
                }
            }
        }
    }

