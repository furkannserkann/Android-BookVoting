package com.furkanyilmaz.kitapoyla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.furkanyilmaz.items.User;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.admin.AdminActivity;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.furkanyilmaz.kitapoyla.user.ActivityUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivitySplash extends AppCompatActivity {

    private ImageView imageViewLogo;
    private boolean isAnimEnd = false, isAunt = false, isLoginActivity;

    private DatabaseReference firebaseDatabaseUsers;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        firebaseDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        init();

        if (auth != null) {
            if (auth.getUid() != null) {
                firebaseDatabaseUsers.child(Objects.requireNonNull(auth.getUid())).addValueEventListener(new ValueEventListener() {
                    @Override

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myStaticObj.loginUser = dataSnapshot.getValue(User.class);
                        myStaticObj.loginUser.setKey(dataSnapshot.getKey());

                        isAunt = true;
                        isLoginActivity = false;
                        localStartActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                        finish();
                    }
                });
            } else {
                isAunt = true;
                isLoginActivity = true;
                localStartActivity();
            }
        } else {
            isAunt = true;
            isLoginActivity = true;
            localStartActivity();
        }
    }

    private void init() {
        imageViewLogo = (ImageView) findViewById(R.id.splash_logo);

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(2500);
        animation1.setStartOffset(500);

        animation1.setFillAfter(true);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // pass it visible before starting the animation
                imageViewLogo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {    }
            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimEnd = true;
                localStartActivity();
            }
        });
        imageViewLogo.startAnimation(animation1);
    }

    private void localStartActivity() {
        if (isAnimEnd && isAunt) {
            if (isLoginActivity) {
                startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                finish();
            } else {
                if (myStaticObj.loginUser.getAuthorit().equals(myEnums.Authority.ADMIN.toString())) {
                    startActivity(new Intent(ActivitySplash.this, AdminActivity.class));
                    finish();
                }
                else if (myStaticObj.loginUser.getAuthorit().equals(myEnums.Authority.KULLANICI.toString())) {
                    startActivity(new Intent(ActivitySplash.this, ActivityUser.class));
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}