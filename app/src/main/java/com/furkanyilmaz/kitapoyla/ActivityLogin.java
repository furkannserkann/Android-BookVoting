package com.furkanyilmaz.kitapoyla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.furkanyilmaz.items.User;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.admin.AdminActivity;
import com.furkanyilmaz.kitapoyla.custom.isVaild;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.furkanyilmaz.kitapoyla.user.ActivityUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private EditText editTextEmail, editTextPassword;
    private RelativeLayout rlEmailValid, rlPassValid;

    private DatabaseReference firebaseDatabaseUsers, firebaseDatabaseYetkiler;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseDatabaseYetkiler = FirebaseDatabase.getInstance().getReference().child("Yetkiler");
        auth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        rlEmailValid = (RelativeLayout) findViewById(R.id.login_relative_email);
        rlPassValid = (RelativeLayout) findViewById(R.id.login_relative_password);

        editTextEmail = (EditText) findViewById(R.id.login_edittext_email);
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, true);
                } else {
                    if (!TextUtils.isEmpty(editTextEmail.getText().toString())) {
                        if (!isVaild.isEmailValid(editTextEmail.getText().toString())) {
                            myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, false);
                        } else {
                            myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, true);
                        }
                    } else {
                        myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, true);
                    }
                }
            }
        });

        editTextPassword = (EditText) findViewById(R.id.login_edittext_password);
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myStaticObj.setValidColor(ActivityLogin.this, rlPassValid, true);
            }
        });
    }

    public void onClickRegister(View view) {
        startActivity(new Intent(ActivityLogin.this, ActivityRegister.class));
    }

    public void onClickLogin(View view) {
        myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, true);
        myStaticObj.setValidColor(ActivityLogin.this, rlPassValid, true);

        boolean isEmptyEmail = TextUtils.isEmpty(editTextEmail.getText().toString());
        boolean isEmptyPass = TextUtils.isEmpty(editTextPassword.getText().toString());

        if (isEmptyEmail) {
            myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, false);
        }
        if (isEmptyPass) {
            myStaticObj.setValidColor(ActivityLogin.this, rlPassValid, false);
        }

        boolean isReturn = false;
        if (!isEmptyEmail) {
            if (!isVaild.isEmailValid(editTextEmail.getText().toString())) {
                myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, false);
                isReturn = true;
            }
        }
        if (!isEmptyPass) {
            if (editTextPassword.length() < 6) {
                myStaticObj.setValidColor(ActivityLogin.this, rlPassValid, false);
                isReturn = true;
            }
        }

        if (isReturn) return;

        if (!isEmptyEmail && !isEmptyPass) {
            // Giriş İşlemleri
            progressDialogLogin = new ProgressDialog(ActivityLogin.this);
            progressDialogLogin.setMessage("Giriş Yapılıyor...");
            progressDialogLogin.setCancelable(false);
            progressDialogLogin.setCanceledOnTouchOutside(false);
            progressDialogLogin.show();

            mLoginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
        }
    }
    ProgressDialog progressDialogLogin;

    public void onClickForgotPassword(View view) {
        myStaticObj.setValidColor(ActivityLogin.this, rlPassValid, true);

        if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
            myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, false);
        } else if (!isVaild.isEmailValid(editTextEmail.getText().toString())) {
            myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, false);
        } else if (isVaild.isEmailValid(editTextEmail.getText().toString())) {
            myStaticObj.setValidColor(ActivityLogin.this, rlEmailValid, true);
            // Şifremi Unuttum İşlemleri
        }
    }

    private void mLoginUser(final String _email, final String _pass) {
        auth.signInWithEmailAndPassword(_email, _pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseDatabaseUsers.child(Objects.requireNonNull(auth.getUid())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            myStaticObj.loginUser = dataSnapshot.getValue(User.class);
                            myStaticObj.loginUser.setKey(dataSnapshot.getKey());

                            if (myStaticObj.loginUser.getAuthorit().equals(myEnums.Authority.ADMIN.toString())) {
                                startActivity(new Intent(ActivityLogin.this, AdminActivity.class));
                                finish();
                            }
                            else if (myStaticObj.loginUser.getAuthorit().equals(myEnums.Authority.KULLANICI.toString())) {
                                startActivity(new Intent(ActivityLogin.this, ActivityUser.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ActivityLogin.this, "Giriş Bilgileri Yanlış!", Toast.LENGTH_SHORT).show();
                            progressDialogLogin.dismiss();
                        }
                    });


//
                } else {
                    Toast.makeText(ActivityLogin.this, "Giriş Bilgileri Yanlış!", Toast.LENGTH_SHORT).show();
                    progressDialogLogin.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
