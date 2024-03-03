package com.turksat46.bartender.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.turksat46.bartender.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class itemsfororderadapter extends RecyclerView.Adapter<itemsfororderadapter.ViewHolder> {

    private LayoutInflater mInflater;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Map<String, Object>> drinksList = new ArrayList<>();

    private itemsfororderadapter.ItemClickListener mClickListener;

    public itemsfororderadapter(Context context, List<Map<String, Object>> list){
        this.mInflater = LayoutInflater.from(context);
        drinksList = list;
    }

    @NonNull
    @Override
    public itemsfororderadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemfororderrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemsfororderadapter.ViewHolder holder, int position) {
        Map<String, Object> json = drinksList.get(position);
        String name = (String) json.get("name");
        Object cost = (Object) json.get("price");
        Object size = (Object)json.get("size");
        holder.itemNameTextView.setText(name);
        holder.itemcostTextView.setText(String.valueOf(cost) + " €");
        holder.baveragesizeTextView.setText(String.valueOf(size));
    }

    @Override
    public int getItemCount() {
        return drinksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemNameTextView;
        TextView itemcostTextView;
        TextView baveragesizeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemcostTextView = itemView.findViewById(R.id.itemCostTextView);
            baveragesizeTextView = itemView.findViewById(R.id.baveragesizeTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(itemsfororderadapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
