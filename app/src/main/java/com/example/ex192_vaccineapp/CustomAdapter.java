package com.example.ex192_vaccineapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> nameList, details, statusList;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, ArrayList<String> nameList, ArrayList<String> details, ArrayList<String> statusList) {
        this.context = applicationContext;
        this.nameList = nameList;
        this.details = details;
        this.statusList = statusList;
        this.inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.custom_lv_layout, null);

        TextView studentNameTV = convertView.findViewById(R.id.studentNameTV);
        TextView detailsTV = convertView.findViewById(R.id.detailsTV);
        ImageView iconIV = convertView.findViewById(R.id.iconIV);

        studentNameTV.setText(nameList.get(position));
        detailsTV.setText(details.get(position));

        if (statusList.get(position).equals("Allergic student")) iconIV.setImageResource(R.drawable.minus);
        else if (statusList.get(position).equals("First vaccination was documented")) iconIV.setImageResource(R.drawable.check);
        else iconIV.setImageResource(R.drawable.doublev);

        return convertView;
    }
}
