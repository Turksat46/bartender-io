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

import java.util.List;

public class itemsfororderadapter extends RecyclerView.Adapter<itemsfororderadapter.ViewHolder> {

    private String mBarID;
    private LayoutInflater mInflater;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public itemsfororderadapter(Context context, String barID){
        this.mInflater = LayoutInflater.from(context);
        this.mBarID = barID;
    }

    @NonNull
    @Override
    public itemsfororderadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itemfororderrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemsfororderadapter.ViewHolder holder, int position) {
        // Zuerst greifen Sie auf die erste Sammlung zu
        CollectionReference parentCollectionRef = db.collection("parentCollection");

// Dann greifen Sie auf die zweite (Unter-)Sammlung zu
        CollectionReference childCollectionRef = parentCollectionRef.document("parentDocumentId").collection("childCollection");
        childCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    String documentId = documentSnapshot.getId();
                    Log.e("itemsadapter", "Bei Dokument: "+documentId);
                    holder.itemNameTextView.setText(documentSnapshot.get("name").toString());
                    holder.itemcostTextView.setText(documentSnapshot.get("price").toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Behandeln Sie hier Fehler
            }
        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView;
        TextView itemcostTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemcostTextView = itemView.findViewById(R.id.itemCostTextView);
        }
    }
}
