package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.software.cb.rajneethi.R;

import java.util.ArrayList;

/**
 * Created by DELL on 30-01-2018.
 */

public class EventListAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> list;

    public EventListAdapter(@NonNull Context context, ArrayList<String> list) {
        super(context, -1);
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.adapter_evet_list,parent);
     //   TextView txtName = (TextView) v.findViewById(R.id.txtEventList);

      //  txtName.setText(list.get(position));

        return v;
    }
}
