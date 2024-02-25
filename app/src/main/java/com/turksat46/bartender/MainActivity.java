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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements com.turksat46.bartender.adapters.barselectionadapter.ItemClickListener, BeaconConsumer {

    //resetTableNumber = To leave the table "implement it further"

    barselectionadapter barselectionadapter;
    friendsattableadapter friendsattableadapter;
    RecyclerView barselectionrv;

    Boolean isinBar = false;

    //if isInBar = true
    Button orderButton;
    ConstraintLayout currentLocationLayout;
    TextView maintablenumbertextview;
    TextView barnameTextView;

    Button changeBarButton;

    Button tablenumberinputbutton;

    CircleImageView profileimgview;

    //is isinBar = False
    ImageView scannfcfortableimageview;
    ConstraintLayout noLocationLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> barsdata = new ArrayList<>();
    ArrayList<String> friendsattabledata = new ArrayList<>();
    ArrayList<String> uidList = new ArrayList<>();

    String selectedBarID = null;
    int selectedTablenumber = 0;

    Boolean selfselectedStore = false;

    ConstraintLayout typeablenumberlayout;
    ConstraintLayout scannfcconstraintlayout;

    Button sendTableNumberButton;
    Button leaveTableButton;

    EditText tablenumberinput;

    CardView currentOrderCard;
    CardView selectTableCard;
    CardView reservationCard;
    CardView friendsattableCard;
    RecyclerView friendsattablerv;

    //FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //checkPermission();
        createNotificationChannel();

        //sendNotification("The app is working in the background now!");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        getUser();

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i("BeaconManager", "I just saw an beacon for the first time :D");

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("BeaconManager", "I no longer see an beacon :(");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i("BeaconManager", "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

            }
        });

        beaconManager.startMonitoring(new Region("bartender.io", null, null, null));

        typeablenumberlayout = (ConstraintLayout)findViewById(R.id.typeablenumberlayout);
        friendsattableCard = (CardView)findViewById(R.id.currentusersontablecard);
        scannfcconstraintlayout = (ConstraintLayout)findViewById(R.id.scannfcconstrainlayout);
        barselectionrv = (RecyclerView) findViewById(R.id.barsselectionrecyclerview);
        noLocationLayout = (ConstraintLayout) findViewById(R.id.nolocation);
        currentLocationLayout = (ConstraintLayout) findViewById(R.id.currentlocation);
        tablenumberinput = (EditText)findViewById(R.id.tischnummereingabe);
        sendTableNumberButton = (Button)findViewById(R.id.sendtablenumberbutton);
        currentOrderCard = (CardView)findViewById(R.id.currentorder);
        selectTableCard = (CardView)findViewById(R.id.selectTableCard);
        reservationCard = (CardView)findViewById(R.id.reservation);
        friendsattablerv = (RecyclerView) findViewById(R.id.friendsattablerv);
        sendTableNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tablenumberinput.getText().length() != 0){
                    setTableNumber(Integer.valueOf(tablenumberinput.getText().toString()));
                }else{
                    Toast.makeText(MainActivity.this, "Bitte tippen Sie die Tischnummer ein!", Toast.LENGTH_LONG).show();
                }

            }
        });

        barnameTextView = (TextView) findViewById(R.id.barname);
        barnameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Bar.class);
                i.putExtra("id", selectedBarID);
                startActivity(i);
            }
        });

        profileimgview = (CircleImageView)findViewById(R.id.profileroundimageview);
        profileimgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, profile.class);
                startActivity(intent);
            }
        });

        changeBarButton = (Button) findViewById(R.id.changebarbutton);
        changeBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isinBar = false;
                selfselectedStore = false;
                selectedTablenumber = 0;
                switchUI();
            }
        });

        leaveTableButton = (Button)findViewById(R.id.leavetablebutton);
        leaveTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTableNumber();
            }
        });

        tablenumberinputbutton = (Button) findViewById(R.id.inputtablenumberbutton);
        tablenumberinputbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchtableinputtypelayout();
            }
        });
        db.collection("stores").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                barsdata.add(document.getId());
                            }
                            renewselectionBars(barsdata);
                        }
                    }
                });

        maintablenumbertextview = (TextView) findViewById(R.id.MaintablenumberTextView);
        maintablenumbertextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTableNumber();
            }
        });

        scannfcfortableimageview = (ImageView) findViewById(R.id.scannfcfortableimageview);
        scannfcfortableimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show maybe an error or let user select bar

                Toast.makeText(MainActivity.this, "Zurzeit kann kein Tisch gescannt werden. ", Toast.LENGTH_LONG).show();
            }
        });


        orderButton = (Button) findViewById(R.id.orderbutton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ordering.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        //sendBroadcastToSetTable(false);
    }

    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //sendBroadcastToSetTable(false);
    }

    private void getUser() {

        if(user == null){
            //Sign the user in, if user == null
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            checkifUserisinDatabase();
        }

        //Else load the user and place information about user
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

    private void checkifUserisinDatabase() {

    }

    public void switchtableinputtypelayout(){
        scannfcconstraintlayout.setVisibility(View.GONE);
        typeablenumberlayout.setVisibility(View.VISIBLE);
    }

    public void switchUI() {
        if (!isinBar) {
            //Zeige Bars an
            noLocationLayout.setVisibility(View.VISIBLE);
            currentLocationLayout.setVisibility(View.GONE);
            scannfcfortableimageview.setVisibility(View.VISIBLE);
            maintablenumbertextview.setVisibility(View.GONE);

        } else {
            //Zeige Barinfos an
            noLocationLayout.setVisibility(View.GONE);
            currentLocationLayout.setVisibility(View.VISIBLE);

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
    }

    // Checking Permission whether ACCESS_COARSE_LOCATION permssion is granted or not
    public void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || this.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || this.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || this.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Please accept the Permissions requested");
                builder.setMessage("In order for the app to recognize the bar you're visiting, accept the request (you don't need to!)");
                builder.setPositiveButton("OK", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.POST_NOTIFICATIONS}, 1);
                    }
                });
                builder.show();
            }
        }
    }

    private void sendNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "bartender.io-channel")
                .setSmallIcon(R.drawable.bartenderiologo)
                .setContentTitle("Bartender.io")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("bartender.io-channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void switchTableNumber(boolean state){
        if(state == false){
            scannfcfortableimageview.setVisibility(View.VISIBLE);
            maintablenumbertextview.setVisibility(View.GONE);
        }else{
            scannfcfortableimageview.setVisibility(View.GONE);
            maintablenumbertextview.setVisibility(View.VISIBLE);
            maintablenumbertextview.setText(String.valueOf(selectedTablenumber));
        }
    }

    public void getFriendsAtTable(){
        db.collection("tables").document(selectedBarID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Hier kannst du die Daten aus dem Dokument verarbeiten
                                ArrayList<String> uidList = new ArrayList<>();

                                // Beispiel: Annahme, dass das Feld "tisch" ein Array von UIDs enthält
                                Object tischValue = document.get(String.valueOf(selectedTablenumber));

                                if (tischValue instanceof ArrayList) {
                                    uidList = (ArrayList<String>) tischValue;
                                    // Jetzt enthält uidList die UIDs aus dem "tisch"-Array
                                    Log.d("Freundesliste", "UIDs aus Firebase: " + uidList.toString());
                                } else {
                                    Log.e("Freundesliste", "Fehler beim Abrufen von Daten: 'tisch' ist kein Array");
                                }
                            } else {
                                Log.d("Freundesliste", "Dokument nicht gefunden");
                            }
                        } else {
                            Log.e("Freundesliste", "Fehler beim Abrufen von Daten", task.getException());
                        }
                    }
                });
        sendBroadcastToSetTable(true);
    }
    //true um eigene UID zu setzen, false um eigene UID zu löschen
    private void sendBroadcastToSetTable(boolean b) {
        if(b == true){
            //In "tables"
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

            //In "users"


            listenforTableChanges();
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

    private void listenforTableChanges() {
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

    public void showFriendsAtTable(ArrayList<String> data){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(friendsattablerv.getContext(),1);

        RecyclerView recyclerView = findViewById(R.id.friendsattablerv);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsattableadapter = new friendsattableadapter(this, data);
        //friendsattableadapter.setClickListener(this);
        recyclerView.setAdapter(friendsattableadapter);
    }

    public void renewselectionBars(ArrayList<String> data){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(barselectionrv.getContext(),1);

        RecyclerView recyclerView = findViewById(R.id.barsselectionrecyclerview);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        barselectionadapter = new barselectionadapter(this, data);
        barselectionadapter.setClickListener(this);
        recyclerView.setAdapter(barselectionadapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        //Was passieren soll, wenn man auf Bar clickt
        //Toast.makeText(this, barsdata.get(position)+" wurde ausgewählt", Toast.LENGTH_LONG).show();
        selectedBarID = barsdata.get(position);
        isinBar = true;
        selfselectedStore = true;
        switchUI();
    }

    public void setTableNumber(int number){
            getFriendsAtTable();
            //sendBroadcastToSetTable(true, number);
            selectedTablenumber = number;
            friendsattableCard.setVisibility(View.VISIBLE);
            currentOrderCard.setVisibility(View.VISIBLE);
            selectTableCard.setVisibility(View.GONE);
            reservationCard.setVisibility(View.GONE);

            switchTableNumber(true);
            Toast.makeText(this, "Tisch "+ String.valueOf(selectedTablenumber)+ " wurde ausgewählt!", Toast.LENGTH_LONG).show();

    }

    public void resetTableNumber(){
        switchTableNumber(false);
        sendBroadcastToSetTable(false);
        friendsattableCard.setVisibility(View.GONE);
        currentOrderCard.setVisibility(View.GONE);
        selectTableCard.setVisibility(View.VISIBLE);
        reservationCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBeaconServiceConnect() {
        
    }
}