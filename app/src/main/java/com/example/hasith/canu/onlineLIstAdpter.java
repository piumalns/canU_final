package com.example.hasith.canu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class onlineLIstAdpter extends ArrayAdapter<onlineLIst> {

    ArrayList<onlineLIst> onlinelist;
    Context context;
    int resource;
    public onlineLIstAdpter( Context context, int resource, ArrayList<onlineLIst> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.onlinelist = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.customonline,null,true);
        }

        onlineLIst online = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.profilPiconline);
        TextView nametextView = (TextView) convertView.findViewById(R.id.name);
        TextView machine = (TextView) convertView.findViewById(R.id.machenicalType);

        nametextView.setText(online.getName());
        machine.setText(online.getMachineType());
        Picasso.get().load(online.getImage()).into(imageView);
        return convertView;
    }
}
