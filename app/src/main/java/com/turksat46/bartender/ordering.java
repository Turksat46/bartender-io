package com.turksat46.bartender;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.turksat46.bartender.adapters.itemsfororderadapter;

public class ordering extends AppCompatActivity {

    CardView selectionCard;
    CardView scannfcCard;
    Button orderButton;
    Button cancelorderButton;

    itemsfororderadapter orderitemsAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView orderitemsrv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        orderitemsrv = findViewById(R.id.itemsrecyclerView);
        orderitemsAdapter = new itemsfororderadapter(this, "testbar");
        orderitemsrv.setLayoutManager(new LinearLayoutManager(this));
        orderitemsrv.setAdapter(orderitemsAdapter);

        selectionCard = (CardView) findViewById(R.id.selectioncard);
        selectionCard.setVisibility(View.VISIBLE);
        scannfcCard = (CardView) findViewById(R.id.ordertablenumber);
        scannfcCard.setVisibility(View.GONE);
        orderButton = (Button) findViewById(R.id.sendorderbutton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionCard.setVisibility(View.GONE);
                scannfcCard.setVisibility(View.VISIBLE);
            }
        });

        cancelorderButton = (Button) findViewById(R.id.cancelorderbutton);
        cancelorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: cancel order
                selectionCard.setVisibility(View.VISIBLE);
                scannfcCard.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Bestellung wurde abgebrochen", Toast.LENGTH_LONG).show();
    }
}