package com.rpfsoftwares.rollapass;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PreferencesAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    PreferencesList tempValues=null;

    public PreferencesAdapter(Activity a, ArrayList d,Resources resLocal) {
        activity = a;
        data=d;
        res = resLocal;
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView title,subtitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.prefitem, null);
            holder = new ViewHolder();
            holder.title= (TextView) vi.findViewById(R.id.txtTitle);
            holder.subtitle = (TextView) vi.findViewById(R.id.txtSubtitle);
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();
            tempValues=null;
            tempValues = ( PreferencesList ) data.get( position );
            holder.title.setText(tempValues.getTitle());
            holder.subtitle.setText(tempValues.getSubtitle());
            vi.setOnClickListener(new OnItemClickListener( position ));
        return vi;
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            PreferencesActivity sct = (PreferencesActivity)activity;
            sct.onItemClick(mPosition);
        }
    }
}