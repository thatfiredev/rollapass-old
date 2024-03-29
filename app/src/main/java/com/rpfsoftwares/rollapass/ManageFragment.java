package com.rpfsoftwares.rollapass;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Rosario on 23-Oct-15.
 */
public class ManageFragment extends Fragment {
    public static RecyclerView rv;
    private LinearLayoutManager llm;
    private DatabaseHelper db;
    public static FloatingActionButton fab;
    private CoordinatorLayout fl;
    private EditText txtPassword,txtWebsite,txtUsername;
    private Object clipboard;

    public ManageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_manage, container, false);

        rv = (RecyclerView)rootView.findViewById(R.id.rv);
        fl = (CoordinatorLayout) rootView.findViewById(R.id.fl);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        rv.setHasFixedSize(true);

        fab.hide();
        clipboard =getActivity().getSystemService(Context.CLIPBOARD_SERVICE); //getting the clipboard service

        //Show the Add Existing Account Dialog
        // on Fab's Click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView=inflater.inflate(R.layout.dialog_novo,null);
                txtPassword=(EditText)dialogView.findViewById(R.id.txtPassword);
                txtWebsite=(EditText)dialogView.findViewById(R.id.txtWebsite);
                txtUsername=(EditText)dialogView.findViewById(R.id.txtUsername);
                new AlertDialog.Builder(rootView.getContext())
                        .setMessage(R.string.add)
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Account account = new Account(txtWebsite.getText().toString(), txtUsername.getText().toString(), txtPassword.getText().toString());
                                db.create(account);
                                ManageFragment.rv.setAdapter(new RVAdapter(db.read(), ManageFragment.rv, clipboard));
                                db.closeDB();
                                final Snackbar a = Snackbar.make(fl, getString(R.string.save_confirmation), Snackbar.LENGTH_SHORT);
                                a.show();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        db = new DatabaseHelper(getContext()); //open the database onnection

        //loading accounts on the interface
        if(db.read().size()>0)
            rv.setAdapter(new RVAdapter(db.read(), rv, clipboard));

        db.closeDB(); //close the database connection

        //Hide the FloatingActionButton when user scrolls down
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0)
                    fab.hide();
                else
                    fab.show();
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fab.hide();
    }
}

