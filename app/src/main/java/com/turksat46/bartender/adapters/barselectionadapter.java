package com.turksat46.bartender.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.turksat46.bartender.R;

import java.util.List;

public class barselectionadapter extends RecyclerView.Adapter<barselectionadapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public barselectionadapter(Context context, List<String> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public barselectionadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.barselectionrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull barselectionadapter.ViewHolder holder, int position) {



        String barname = mData.get(position);
        db.collection("stores").document(barname).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Document found in the offline cache
                                    DocumentSnapshot document = task.getResult();
                                    Log.d("Barname", "Cached document data: " + document.getData());
                                    holder.myTextView.setText(document.getString("name"));
                                    Picasso.get().load(document.getString("img")).placeholder(R.drawable.bartenderiologo).into(holder.logo);
                                    holder.progressBar.setMax(document.getDouble("maxvisitor").intValue());
                                    holder.progressBar.setProgress(document.getDouble("currentvisitor").intValue());
                                }
                            }
                        });
        //holder.myTextView.setText(barname);
        Log.w("Barname", barname);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myTextView;
        ImageView logo;

        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.rowbanrnametextView);
            logo = itemView.findViewById(R.id.rowlogoview);
            progressBar = itemView.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
