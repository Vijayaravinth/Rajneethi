package com.software.cb.rajneethi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.models.NavigationItems;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by w7 on 10/11/2017.
 */

public class NavigationAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private List<NavigationItems> listStorage;


    public NavigationAdapter(Context context, List<NavigationItems> customizedListView) {
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;

    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = lInflater.inflate(R.layout.navigation_list_item, parent, false);

            listViewHolder.textInListView = (TextView) convertView.findViewById(R.id.textView);
            listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.imageView);
            listViewHolder.txt_count = (TextView) convertView.findViewById(R.id.txt_count);
            listViewHolder.view = (View) convertView.findViewById(R.id.view);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }

     /*   listViewHolder.txt_count.setSolidColor("#9C27B0");
        listViewHolder.txt_count.setStrokeWidth(1);
        listViewHolder.txt_count.setStrokeColor("#ffffff");*/
        listViewHolder.textInListView.setText(listStorage.get(position).getName());
        listViewHolder.imageInListView.setImageResource(listStorage.get(position).getImage());
        listViewHolder.txt_count.setText(listStorage.get(position).getCount());
        if (position == 2 || position == 6 || position == 12) {

            listViewHolder.view.setVisibility(View.VISIBLE);
        } else {

            listViewHolder.view.setVisibility(GONE);
        }

        return convertView;
    }

    static class ViewHolder {

        TextView textInListView;
        ImageView imageInListView;
        TextView txt_count;
        View view;
    }
}