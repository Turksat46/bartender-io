package com.turksat46.bartender.backend;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Backend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BackendManager {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    String temp;

    public BackendManager(){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public void setUserBarID(String selectedBarID){
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
                                updateData.put("atbarid", selectedBarID);

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

    public void setUserTableID(int tablenumber){
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
                                updateData.put("attableid", tablenumber);

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

    public void leaveTable(){
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
                                updateData.put("attableid", null);

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

    public void leaveBar(){
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
                                updateData.put("atbarid", null);

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

    public String getCurrentSelectedBar(){
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            temp = document.getString("atbarid").toString();
                        }

                    }
                });
        return temp;
    }

    public String getCurrentTableNumber(){
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            temp = document.getString("attableid").toString();
                        }

                    }
                });
        return temp;
    }

    public boolean checkifinBar(){
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            temp = document.getString("atbarid").toString();
                        }

                    }
                });
        if(temp == ""){
            return false;
        }else{
            return true;
        }
    }

    public boolean checkIfLeavingAvailable(){
        return true;
    }

}
