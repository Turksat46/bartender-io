package com.turksat46.bartender;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class reservation extends AppCompatActivity {

    CircleImageView profileImgView;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        profileImgView = (CircleImageView)findViewById(R.id.profileroundimageview);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        getUserData();

    }

    private void getUserData() {
        Picasso.get().load(user.getPhotoUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                profileImgView.setImageBitmap(bitmap);
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