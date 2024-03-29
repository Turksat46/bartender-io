package com.turksat46.bartender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.turksat46.bartender.adapters.currentorderadapter;
import com.turksat46.bartender.adapters.itemsfororderadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ordering extends AppCompatActivity implements itemsfororderadapter.ItemClickListener {

    String selectedBarID;
    int tableNumber;

    Boolean confirmedOrder = false;

    CardView selectionCard;
    CardView scannfcCard;
    Button orderButton;
    Button cancelorderButton;
    TextView tableNumberTextView;
    TextView ordertotaltextview;

    itemsfororderadapter orderitemsAdapter;
    currentorderadapter currentOrderAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView orderitemsrv;
    RecyclerView currentorderrv;

    NfcAdapter nfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    List<Map<String, Object>> drinksList = new ArrayList<>();
    List<Map<String, Object>> selectedList = new ArrayList<>();


    double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            selectedBarID = extras.getString("barID");
            tableNumber = extras.getInt("tableID");
        }else{
            //TODO: Gib eine Fehlermeldung aus
        }


        tableNumberTextView = (TextView)findViewById(R.id.MaintablenumberTextView);
        tableNumberTextView.setText(String.valueOf(tableNumber));
        selectionCard = (CardView) findViewById(R.id.selectioncard);
        selectionCard.setVisibility(View.VISIBLE);
        scannfcCard = (CardView) findViewById(R.id.ordertablenumber);
        scannfcCard.setVisibility(View.GONE);
        ordertotaltextview = (TextView)findViewById(R.id.ordertotaltextview);
        orderButton = (Button) findViewById(R.id.sendorderbutton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkOrder();
            }
        });

        cancelorderButton = (Button) findViewById(R.id.cancelorderbutton);
        cancelorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: cancel order
                confirmedOrder = false;
                selectionCard.setVisibility(View.VISIBLE);
                scannfcCard.setVisibility(View.GONE);

            }
        });
        handleIntent(getIntent());
        setupForegroundDispatch(ordering.this, nfcAdapter);
        getItems();
    }

    private void getItems() {
        CollectionReference drinksRef = db.collection("items/testbar/drinks");
        drinksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    for (DocumentSnapshot document : snapshot) {
                        // Verarbeiten Sie jedes Dokument
                        Log.d("Firestore", document.getData().toString());
                        Map<String, Object> item = new HashMap<>();
                        item.put("name", document.get("name"));
                        item.put("price", document.get("price"));
                        item.put("size", document.get("size"));
                        drinksList.add(item);
                    }
                    //Call for drawing on rv
                    drawonrv(drinksList);
                } else {
                    Log.d("Firestore", "Fehler beim Abrufen der Dokumente", task.getException());
                }
            }
        });

    }

    private void drawonrv(List<Map<String, Object>> drinksList) {
        orderitemsrv = findViewById(R.id.itemsrecyclerView);
        orderitemsrv.setLayoutManager(new LinearLayoutManager(this));
        orderitemsrv.setAdapter(orderitemsAdapter);
        orderitemsAdapter = new itemsfororderadapter(this, drinksList);
        orderitemsAdapter.setClickListener(this);
        orderitemsrv.setAdapter(orderitemsAdapter);
    }

    private void checkOrder() {
        //TODO:check order
        confirmedOrder = true;
        selectionCard.setVisibility(View.GONE);
        scannfcCard.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.e("handleIntent", "Intent called");
        String action = intent.getAction();
        String type = intent.getType();
        Log.e("handleIntent", "Type: " + type);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.e("NFC", "Tag is ndef!");

            if (MIME_TEXT_PLAIN.equals(type)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    if(confirmedOrder){
                        String uuid = new NdefReaderTask().execute(tag).get();
                        Log.e("UUID", uuid);
                        checkRightNFCTag(uuid);
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Log.d("NFC", "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.e("NFC", "Tag is not ndef!");
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    private void checkRightNFCTag(String uuid) {
        db.collection("tables").document(selectedBarID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Object uuids = document.get("uuids");
                            ArrayList<String> list = (ArrayList<String>) uuids;
                            if(list.contains(uuid)){
                                order();
                            }else{
                                Toast.makeText(getApplicationContext(), "Falscher Tisch!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void order() {
        //TODO: Schick die Bestellung rüber zur Activity
        Toast.makeText(this, "Weiterleitung zur Bezahlung...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ordering.this, payment.class);
        intent.putExtra("barID", selectedBarID);
        intent.putExtra("tableID", tableNumber);
        intent.putExtra("total", totalPrice);
        startActivity(intent);
    }

    private void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        nfcAdapter.disableForegroundDispatch(ordering.this);
        Toast.makeText(this, "Bestellung wurde abgebrochen", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        selectedList.add(drinksList.get(position));
        drawselecteditems();
        addtototalprice();
    }

    private void addtototalprice() {

        for(Map<String, Object> item : selectedList) {
            if (item.containsKey("price")) {
                Object priceObj = item.get("price");
                String pricetemp = String.valueOf(priceObj);
                Double price = Double.valueOf(pricetemp);
                totalPrice += price;
                ordertotaltextview.setText("Gesamt: "+String.valueOf(totalPrice)+" €");
            }
        }

        Log.e("TotalPrice", String.valueOf(totalPrice));
    }

    private void drawselecteditems() {
        currentorderrv = findViewById(R.id.currentorderrecyclerView);
        currentorderrv.setLayoutManager(new LinearLayoutManager(this));
        currentOrderAdapter = new currentorderadapter(this, selectedList);
        currentorderrv.setAdapter(currentOrderAdapter);
    }
}