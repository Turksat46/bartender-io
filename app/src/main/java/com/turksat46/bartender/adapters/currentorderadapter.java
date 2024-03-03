package com.turksat46.bartender.adapters;

import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.turksat46.bartender.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class currentorderadapter extends RecyclerView.Adapter<currentorderadapter.ViewHolder> {

    private LayoutInflater mInflater;

    List<Map<String, Object>> itemsList = new ArrayList<>();

    public currentorderadapter(Context context, List<Map<String, Object>> list){
        this.mInflater = LayoutInflater.from(context);
        itemsList = list;
    }

    @NonNull
    @Override
    public currentorderadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.ordereditemrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> json = itemsList.get(position);

        String name = (String) json.get("name");
        Object cost = (Object) json.get("price");
        Object size = (Object)json.get("size");


        holder.itemnameTextView.setText(name);
        holder.itemcostTextView.setText(String.valueOf(cost) + " €");
        holder.beveragesizeTextView.setText(String.valueOf(size));
    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemnameTextView;
        TextView itemcostTextView;
        TextView beveragesizeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemnameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemcostTextView = itemView.findViewById(R.id.itemCostTextView);
            beveragesizeTextView = itemView.findViewById(R.id.baveragesizeTextView);
        }
    }

}
