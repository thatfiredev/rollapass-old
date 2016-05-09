package com.rpfsoftwares.rollapass;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.GameViewHolder>{
    List<Account> accounts;
    RecyclerView rv;
    Object clipboard;

    public RVAdapter(List<Account> accounts, RecyclerView rv, Object clipboard)
    {
        this.accounts=accounts;
        this.rv=rv;
        this.clipboard=clipboard;
    }

    private View.OnClickListener show(final GameViewHolder personViewHolder, final int i)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle the password's visibility
                if(personViewHolder.txtPassword.getText().toString().equalsIgnoreCase("********")) {
                    personViewHolder.txtPassword.setText(accounts.get(i).getPassword());
                    personViewHolder.btnShow.setImageDrawable(rv.getResources().getDrawable(R.drawable.ic_visibility_off));
                }
                else
                {
                    personViewHolder.txtPassword.setText("********");
                    personViewHolder.btnShow.setImageDrawable(rv.getResources().getDrawable(R.drawable.ic_visibility));
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(final GameViewHolder personViewHolder,final int i) {
        //int id=accounts.get(i).getId();
        personViewHolder.txtWebsite.setText(accounts.get(i).getWebsite());
        personViewHolder.txtUsername.setText(accounts.get(i).getUsername());

        personViewHolder.btnShow.setOnClickListener(show(personViewHolder, i));

        personViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            Account undo= accounts.get(i);
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage(v.getResources().getString(R.string.delete_prompt))
                        .setCancelable(false)
                        .setNegativeButton(v.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(v.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Delete the account from the SQLite database
                                final DatabaseHelper db = new DatabaseHelper(v.getContext());
                                db.delete(accounts.get(i).getId());

                                //update the interface
                                ManageFragment.rv.setAdapter(new RVAdapter(db.read(), rv, clipboard));

                                //show the confirmation snackbar
                                final Snackbar a = Snackbar.make(rv, v.getResources().getString(R.string.delete_confirmation), Snackbar.LENGTH_LONG);
                                a.setAction(v.getResources().getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.create(undo);
                                        a.dismiss();
                                        rv.setAdapter(new RVAdapter(db.read(), rv, a));
                                        db.closeDB();
                                    }
                                });
                                a.show();
                            }
                        })
                        .show();
            }
        });


        personViewHolder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                            RVAdapter.this.clipboard;
                    clipboard.setText(accounts.get(i).getPassword());
                } else {
                    try {
                        android.content.ClipboardManager clipboard =
                                (android.content.ClipboardManager) RVAdapter.this.clipboard;
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Password", accounts.get(i).getPassword());
                        clipboard.setPrimaryClip(clip);
                    }
                    catch (Exception e)
                    {}
                }
                final Snackbar a = Snackbar.make(rv, v.getResources().getString(R.string.copy_confirmation), Snackbar.LENGTH_SHORT);
                a.show();
            }
        });
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_account, viewGroup, false);
        return new GameViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class GameViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView txtWebsite,txtUsername,txtPassword;
        ImageView btnShow,btnDelete,btnCopy;

        GameViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            txtWebsite = (TextView)itemView.findViewById(R.id.txtWebsite);
            txtUsername = (TextView)itemView.findViewById(R.id.txtUsername);
            txtPassword = (TextView)itemView.findViewById(R.id.txtPassword);
            btnShow = (ImageView)itemView.findViewById(R.id.btnShow);
            btnDelete = (ImageView)itemView.findViewById(R.id.btnDelete);
            btnCopy = (ImageView)itemView.findViewById(R.id.btnCopy);
        }
    }

}

