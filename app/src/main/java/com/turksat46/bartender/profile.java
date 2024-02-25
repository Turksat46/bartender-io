package com.turksat46.bartender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    Uri mImageCaptureUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageView profilepictureview;
    TextView profilennametextview;

    EditText editname;
    Button savenamebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        profilennametextview = (TextView)findViewById(R.id.profilenametextview);
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            profilennametextview.setText(document.getString("name"));
                        }
                    }
                });

        editname=(EditText)findViewById(R.id.editName);
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            editname.setHint(document.getString("name"));
                        }
                    }
                });
        savenamebutton = (Button)findViewById(R.id.savenamebutton);
        savenamebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        updateData.put("name", editname.getText().toString());

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
        });

        profilepictureview = (ImageView)findViewById(R.id.profilepicture);
        profilepictureview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProfilePicture();
            }
        });
        Picasso.get().load(user.getPhotoUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                profilepictureview.setImageDrawable(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void setProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);


        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == 2
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(data.getData(), "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 3);

        }

        if (requestCode == 3
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

        }
    }
}