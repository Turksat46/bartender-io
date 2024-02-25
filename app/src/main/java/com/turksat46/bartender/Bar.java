package com.turksat46.bartender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class Bar extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CardView bartitlecard;

    String BarID;
    String imgurl;
    String barName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bartitlecard = (CardView) findViewById(R.id.bartitlecard);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            BarID = extras.getString("id");
            showBarInfo();
        }else{
            showError();
        }
    }

    private void showBarInfo(){
        db.collection("stores").document(BarID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Picasso.get().load(document.getString("img")).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    bartitlecard.setBackground(new BitmapDrawable(bitmap));
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }
                    }
                });
    }

    private void showError(){

    }
}