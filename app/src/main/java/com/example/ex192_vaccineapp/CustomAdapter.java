package com.example.ex192_vaccineapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> nameList, statusList;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, ArrayList<String> nameList, ArrayList<String> statusList) {
        this.context = applicationContext;
        this.nameList = nameList;
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
        TextView statusTV = convertView.findViewById(R.id.statusTv);

        studentNameTV.setText(nameList.get(position));
        statusTV.setText(statusList.get(position));

        if (statusList.get(position).equals("Allergic student")) statusTV.setTextColor(Color.GRAY);
        else if (statusList.get(position).equals("First vaccination was documented")) statusTV.setTextColor(Color.rgb(204, 204, 0));
        else statusTV.setTextColor(Color.GREEN);

        return convertView;
    }
}
