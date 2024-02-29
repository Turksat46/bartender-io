package com.turksat46.bartender;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.turksat46.bartender.adapters.barselectionadapter;
import com.turksat46.bartender.adapters.friendsattableadapter;
import com.turksat46.bartender.backend.BackendManager;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements com.turksat46.bartender.adapters.barselectionadapter.ItemClickListener, BeaconConsumer {


    //UI Variablen
    barselectionadapter barselectionadapter;
    friendsattableadapter friendsattableadapter;
    RecyclerView barselectionrv;
    RecyclerView friendsattablerv;

    ConstraintLayout currentLocationLayout;
    ConstraintLayout noLocationLayout;
    ConstraintLayout typeablenumberlayout;
    ConstraintLayout scannfclayout;

    CircleImageView profileimgview;


    Button orderButton;
    Button changeBarButton;
    Button tablenumberinputbutton;
    Button sendTableNumberButton;
    Button leaveTableButton;
    Button reservationButton;

    CardView currentOrderCard;
    CardView selectTableCard;
    CardView reservationCard;
    CardView friendsattableCard;

    TextView barnameTextView;
    TextView maintablenumbertextview;

    ImageView scannfcfortableimageview;


    EditText tablenumberinput;

    //Interne Variablen
    String selectedBarID = null;
    int selectedTablenumber = 0;
    ArrayList<String> barsdata = new ArrayList<>();
    ArrayList<String> uidList = new ArrayList<>();
    ArrayList<String> friendsattabledata = new ArrayList<>();


    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        profileimgview = (CircleImageView)findViewById(R.id.profileroundimageview);


        typeablenumberlayout = (ConstraintLayout)findViewById(R.id.typeablenumberlayout);
        friendsattableCard = (CardView)findViewById(R.id.currentusersontablecard);
        scannfclayout = (ConstraintLayout)findViewById(R.id.scannfcconstrainlayout);
        barselectionrv = (RecyclerView) findViewById(R.id.barsselectionrecyclerview);
        noLocationLayout = (ConstraintLayout) findViewById(R.id.nolocation);
        currentLocationLayout = (ConstraintLayout) findViewById(R.id.currentlocation);
        tablenumberinput = (EditText)findViewById(R.id.tischnummereingabe);
        currentOrderCard = (CardView)findViewById(R.id.currentorder);
        selectTableCard = (CardView)findViewById(R.id.selectTableCard);
        reservationCard = (CardView)findViewById(R.id.reservation);
        friendsattablerv = (RecyclerView) findViewById(R.id.friendsattablerv);
        barnameTextView = (TextView) findViewById(R.id.barname);
        profileimgview = (CircleImageView)findViewById(R.id.profileroundimageview);
        changeBarButton = (Button) findViewById(R.id.changebarbutton);
        leaveTableButton = (Button)findViewById(R.id.leavetablebutton);
        sendTableNumberButton = (Button)findViewById(R.id.sendtablenumberbutton);
        tablenumberinputbutton = (Button) findViewById(R.id.inputtablenumberbutton);
        maintablenumbertextview = (TextView) findViewById(R.id.MaintablenumberTextView);
        scannfcfortableimageview = (ImageView) findViewById(R.id.scannfcfortableimageview);
        reservationButton = (Button)findViewById(R.id.reservationbutton);
        orderButton = (Button) findViewById(R.id.orderbutton);

        changeBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectedBar();
            }
        });

        profileimgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, profile.class);
                startActivity(intent);
            }
        });

        leaveTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveTable();
            }
        });
        
        tablenumberinputbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToManualTableInputUI();
            }
        });

        sendTableNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidTableInput();
            }
        });

        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, reservation.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Den User erstmal anmelden und Daten einsehen
        handleUser();
        //Dann prüfen, ob der Nutzer schon an einem Tisch sitzt
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.getString("atbarid").isEmpty()){
                                //Starte den ganz normalen Prozedur
                                startNormalProcedure();
                            }else{
                                selectedBarID = document.getString("atbarid");
                                selectedTablenumber = Integer.valueOf(document.getString("attableid"));
                                userisAlreadyatTable();
                            }
                        }
                    }
                });
    }

    private void checkValidTableInput() {
        //TODO: check if table number is valid
        if(tablenumberinput.getText().length() != 0){
            enterTable(Integer.valueOf(tablenumberinput.getText().toString()));
        }else{
            Toast.makeText(MainActivity.this, "Bitte tippen Sie die Tischnummer ein!", Toast.LENGTH_LONG).show();
        }
    }

    private void enterTable(int tablenumber) {
        selectedTablenumber = tablenumber;

        updatefromTable(true);
        friendsattableCard.setVisibility(View.VISIBLE);
        currentOrderCard.setVisibility(View.VISIBLE);
        selectTableCard.setVisibility(View.GONE);
        reservationCard.setVisibility(View.GONE);
    }

    private void changeToManualTableInputUI() {
        scannfclayout.setVisibility(View.GONE);
        typeablenumberlayout.setVisibility(View.VISIBLE);
    }

    private void leaveTable() {
        //TODO: Check if user hasn't paid yet
        updatefromTable(false);
        //resetBarVariable();
        setUItoTableView(false);
    }

    private void resetBarVariable() {
        selectedBarID = null;
        selectedTablenumber = 0;
    }

    //Boolean true is for entering and false for leaving table
    private void updatefromTable(boolean b) {
        if(b == true){
        db.collection("tables")  // Ersetze "deineSammlung" durch den Namen deiner Firestore-Sammlung
                .document(selectedBarID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Hier kannst du die Daten aus dem Dokument verarbeiten


                                // Beispiel: Annahme, dass das Feld "tisch" ein Array von UIDs enthält
                                Object tischValue = document.get(String.valueOf(selectedTablenumber));

                                if (tischValue instanceof ArrayList) {
                                    uidList = (ArrayList<String>) tischValue;
                                    // Füge deine eigene UID zur Liste hinzu
                                    uidList.add(user.getUid());

                                    // Aktualisiere das Dokument in Firebase mit der aktualisierten Liste
                                    db.collection("tables")
                                            .document(selectedBarID)
                                            .update(String.valueOf(selectedTablenumber), uidList)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> updateTask) {
                                                    if (updateTask.isSuccessful()) {
                                                        Log.d("updateTable", "Eigene UID erfolgreich hinzugefügt");
                                                        friendsattabledata = uidList;
                                                        updateUserTable(true);
                                                        showFriendsAtTable(friendsattabledata);
                                                    } else {
                                                        Log.e("updateTable", "Fehler beim Aktualisieren der Daten", updateTask.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.e("updateTable", "Fehler beim Abrufen von Daten: 'tisch' ist kein Array");
                                }
                            } else {
                                Log.d("updateTable", "Dokument nicht gefunden");
                            }
                        } else {
                            Log.e("updateTable", "Fehler beim Abrufen von Daten", task.getException());
                        }
                    }
                });

    }else{
        //Vom Tisch löschen
        db.collection("tables")
                .document(selectedBarID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                // Annahme: Das Feld "tisch" ist ein Array von UIDs
                                Object tischValue = document.get(String.valueOf(selectedTablenumber));

                                if (tischValue instanceof ArrayList) {
                                    uidList = (ArrayList<String>) tischValue;

                                    // Entferne die UID aus der Liste
                                    uidList.remove(user.getUid());

                                    // Aktualisiere das Dokument in Firebase mit der aktualisierten Liste
                                    db.collection("tables")
                                            .document(selectedBarID)
                                            .update(String.valueOf(selectedTablenumber), uidList)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> updateTask) {
                                                    if (updateTask.isSuccessful()) {
                                                        Log.d("löscheAusTisch", "UID erfolgreich aus der Liste entfernt");
                                                        updateUserTable(false);

                                                    } else {
                                                        Log.e("löscheAusTisch", "Fehler beim Aktualisieren der Daten", updateTask.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.e("löscheAusTisch", String.valueOf(selectedTablenumber)+ " ist kein Array!");
                                }
                            } else {
                                Log.d("löscheAusTisch", "Dokument nicht gefunden");
                            }
                        } else {
                            Log.e("löscheAusTisch", "Fehler beim Abrufen von Daten", task.getException());
                        }
                    }
                });


    }


}

    private void listenForTableChanges() {
        db.collection("tables")
                .document(selectedBarID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@NonNull DocumentSnapshot documentSnapshot, @NonNull FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Tischüberwachung", "Fehler beim Empfangen von Echtzeitdaten", error);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            // Beispiel: Annahme, dass das Feld "tisch" ein Array von UIDs enthält
                            Object tischValue = documentSnapshot.get(String.valueOf(selectedTablenumber));

                            if (tischValue instanceof ArrayList) {
                                uidList = (ArrayList<String>) tischValue;
                                friendsattabledata = uidList;
                                showFriendsAtTable(friendsattabledata);
                                // Jetzt enthält uidList die UIDs aus dem "tisch"-Array
                                Log.d("Tischüberwachung", "Aktualisierte UIDs aus Firebase: " + uidList.toString());
                            } else {
                                Log.e("Tischüberwachung", "Fehler beim Verarbeiten von Echtzeitdaten: 'tisch' ist kein Array");
                            }
                        } else {
                            Log.d("Tischüberwachung", "Dokument nicht gefunden oder leer");
                        }
                    }
                });
    }

    private void showFriendsAtTable(ArrayList<String> data) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(friendsattablerv.getContext(),1);
        RecyclerView recyclerView = findViewById(R.id.friendsattablerv);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsattableadapter = new friendsattableadapter(this, data);
        //friendsattableadapter.setClickListener(this);
        recyclerView.setAdapter(friendsattableadapter);
    }

    private void updateUserTable(boolean b) {
        if(b == true){
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Log-Aussage, um die abgerufenen Daten anzuzeigen
                                    Log.d("MyApp", "Abgerufene Daten: " + document.getData());

                                    // Hier kannst du nur den Namen aktualisieren
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("atbarid",selectedBarID);
                                    updateData.put("attableid", String.valueOf(selectedTablenumber));

                                    db.collection("users").document(user.getUid()).update(updateData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> updateTask) {
                                                    if (updateTask.isSuccessful()) {
                                                        Log.d("MyApp", "Name erfolgreich aktualisiert!");
                                                    } else {
                                                        Log.e("MyApp", "Fehler beim Aktualisieren des Namens", updateTask.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d("MyApp", "Dokument existiert nicht");
                                }
                            } else {
                                Log.e("MyApp", "Fehler beim Abrufen der Daten", task.getException());
                            }
                        }
                    });
        }else{
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Log-Aussage, um die abgerufenen Daten anzuzeigen
                                    Log.d("MyApp", "Abgerufene Daten: " + document.getData());

                                    // Hier kannst du nur den Namen aktualisieren
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("atbarid","");
                                    updateData.put("attableid", "");

                                    db.collection("users").document(user.getUid()).update(updateData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> updateTask) {
                                                    if (updateTask.isSuccessful()) {
                                                        Log.d("MyApp", "Name erfolgreich aktualisiert!");
                                                    } else {
                                                        Log.e("MyApp", "Fehler beim Aktualisieren des Namens", updateTask.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d("MyApp", "Dokument existiert nicht");
                                }
                            } else {
                                Log.e("MyApp", "Fehler beim Abrufen der Daten", task.getException());
                            }
                        }
                    });
        }
    }

    private void startNormalProcedure() {
        setBarsRecyclerView();
    }

    private void setBarsRecyclerView() {
        //Daten von der Datenbank abrufen
        db.collection("stores").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                barsdata.add(document.getId());
                            }
                            showBarsInRV(barsdata);
                        }
                    }
                });
    }

    private void showBarsInRV(ArrayList<String> barsdata) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(barselectionrv.getContext(),1);

        RecyclerView recyclerView = findViewById(R.id.barsselectionrecyclerview);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        barselectionadapter = new barselectionadapter(this, barsdata);
        barselectionadapter.setClickListener(this);
        recyclerView.setAdapter(barselectionadapter);
    }

    private void userisAlreadyatTable() {
        setUItoTableView(true);
    }

    private void setUItoTableView(Boolean b) {
        if(b == true){
            updateBartextViewInfo();
            noLocationLayout.setVisibility(View.GONE);
            currentLocationLayout.setVisibility(View.VISIBLE);
            friendsattableCard.setVisibility(View.VISIBLE);
            currentOrderCard.setVisibility(View.VISIBLE);
            selectTableCard.setVisibility(View.GONE);
            reservationCard.setVisibility(View.GONE);
            switchTableNumberCard();
        }else{
            updateBartextViewInfo();
            noLocationLayout.setVisibility(View.GONE);
            currentLocationLayout.setVisibility(View.VISIBLE);
            friendsattableCard.setVisibility(View.GONE);
            currentOrderCard.setVisibility(View.GONE);
            selectTableCard.setVisibility(View.VISIBLE);
            reservationCard.setVisibility(View.VISIBLE);
            switchTableNumberCard();
        }

    }

    private void switchTableNumberCard() {
        if(selectedTablenumber == 0){
            scannfcfortableimageview.setVisibility(View.VISIBLE);
            maintablenumbertextview.setVisibility(View.GONE);
        }else{
            scannfcfortableimageview.setVisibility(View.GONE);
            maintablenumbertextview.setVisibility(View.VISIBLE);
            maintablenumbertextview.setText(String.valueOf(selectedTablenumber));
        }
    }

    private void updateBartextViewInfo() {
        db.collection("stores").document(selectedBarID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            barnameTextView.setText(document.getString("name"));
                        }
                    }
                });
    }

    private void handleUser() {
        if(user == null || mAuth.getCurrentUser() == null){
            //Sign the user in, if user == null
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        Picasso.get().load(user.getPhotoUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                profileimgview.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        selectedBarID = barsdata.get(position);
        selectedBar();
    }

    private void selectedBar() {
        noLocationLayout.setVisibility(View.GONE);
        currentLocationLayout.setVisibility(View.VISIBLE);
        updateBartextViewInfo();
    }

    private void deselectedBar(){
        selectedBarID = null;
        noLocationLayout.setVisibility(View.VISIBLE);
        currentLocationLayout.setVisibility(View.GONE);
        scannfcfortableimageview.setVisibility(View.VISIBLE);
        maintablenumbertextview.setVisibility(View.GONE);
    }

    @Override
    public void onBeaconServiceConnect() {

    }
}