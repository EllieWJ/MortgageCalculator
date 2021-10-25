package com.swufestu.mortgagecalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter {
    private  static final String TAG="MyAdapter";
    public MyAdapter(@NonNull Context context, int resource, ArrayList<Item> data) {
        super(context, resource, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView=convertView;
        if(itemView==null){
            itemView= LayoutInflater.from(getContext()).inflate(R.layout.choice_item,parent,false);
        }
        Item item=(Item)getItem(position) ;
        TextView cho=(TextView) itemView.findViewById(R.id.one_choice);
        cho.setText(item.getChoice());
        return itemView;
    }
}
