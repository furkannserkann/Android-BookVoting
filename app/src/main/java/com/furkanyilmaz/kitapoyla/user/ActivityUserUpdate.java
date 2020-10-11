package com.furkanyilmaz.kitapoyla.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.furkanyilmaz.items.User;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.custom.PhoneTextFormatter;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ActivityUserUpdate extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    private ImageView imageBack;
    private CircleImageView imageProfile;
    private EditText textName, textPhone;
    private TextView textEmail;
    private RadioButton radioBay, radioBayan;
    private Button buttonSelectImage, buttonClose, buttonSave, buttonChangePass;

    private RelativeLayout rlName, rlPhone;

    private FirebaseAuth auth;
    private DatabaseReference firebaseDatabase;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private byte[] profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        imageBack = (ImageView) findViewById(R.id.userupdate_image_back);
        imageProfile = (CircleImageView) findViewById(R.id.userupdate_image_profile);

        textName = (EditText) findViewById(R.id.userupdate_text_name);
        textPhone = (EditText) findViewById(R.id.userupdate_text_phone);
        textEmail = (TextView) findViewById(R.id.userupdate_text_email);

        radioBay = (RadioButton) findViewById(R.id.userupdate_radio_male);
        radioBayan = (RadioButton) findViewById(R.id.userupdate_radio_female);

        buttonSelectImage = (Button) findViewById(R.id.userupdate_button_selectimage);
        buttonClose = (Button) findViewById(R.id.userupdate_button_close);
        buttonSave = (Button) findViewById(R.id.userupdate_button_save);
        buttonChangePass = (Button) findViewById(R.id.userupdate_button_changepass);

        rlName = (RelativeLayout) findViewById(R.id.userupdate_relative_name);
        rlPhone = (RelativeLayout) findViewById(R.id.userupdate_relative_phone);

        if (!TextUtils.isEmpty(myStaticObj.loginUser.getImage()))
            Picasso.get().load(myStaticObj.loginUser.getImage()).into(imageProfile);
        else {
            if (myStaticObj.loginUser.getGender().equals(myEnums.Gender.FEMALE.toString()))
                imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));
            else imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));
        }
        textName.setText(myStaticObj.loginUser.getNameSurname());
        textPhone.setText(myStaticObj.loginUser.getPhone());
        textEmail.setText(myStaticObj.loginUser.getEmail());

        if (myStaticObj.loginUser.getGender().equals(myEnums.Gender.MALE.toString())) {
            radioBay.setChecked(true);
        } else {
            radioBayan.setChecked(true);
        }

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Profil Resmi Seçiniz"), GALLERY_PICK);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.sendPasswordResetEmail(myStaticObj.loginUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ActivityUserUpdate.this, myStaticObj.loginUser.getEmail() + "\nAdresine şifre sıfırlama bağlantısı gönderildi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        textName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myStaticObj.setValidColor(ActivityUserUpdate.this, rlName, true);
            }
        });

        textPhone.addTextChangedListener(new PhoneTextFormatter(textPhone, "+90 (###) ###-####"));
        textPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, true);
                } else {
                    if (!TextUtils.isEmpty(textPhone.getText().toString())) {
                        if (textPhone.length() == 18) {
                            myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, true);
                        } else {
                            myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, false);
                        }
                    } else {
                        myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, true);
                    }
                }
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void saveUser() {
        myStaticObj.setValidColor(ActivityUserUpdate.this, rlName, true);
        myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, true);

        boolean isEmptyName = TextUtils.isEmpty(textName.getText().toString());
        boolean isEmptyPhone = TextUtils.isEmpty(textPhone.getText().toString());

        boolean isReturn = false;

        if (isEmptyName) {
            myStaticObj.setValidColor(ActivityUserUpdate.this, rlName, false);
            isReturn = true;
        }
        if (isEmptyPhone) {
            myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, false);
            isReturn = true;
        }

        if (!isEmptyPhone) {
            if (textPhone.length() != 18) {
                myStaticObj.setValidColor(ActivityUserUpdate.this, rlPhone, false);
                isReturn = true;
            }
        }

        if (isReturn) return;

        progressDialog = new ProgressDialog(ActivityUserUpdate.this);
        progressDialog.setTitle("Bilgiler Güncelleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String lGender = myEnums.Gender.MALE.toString();
        if (radioBayan.isChecked()) {
            lGender = myEnums.Gender.FEMALE.toString();
        }

        updateUser(textName.getText().toString(), textPhone.getText().toString(), lGender);
    }

    private void updateUser(final String nameSurname, final String phone, final String gender) {
        String uID = myStaticObj.loginUser.getKey();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);

        if (profile_image != null) {
            final StorageReference filepath = storageReference.child("profile_images").child(uID + ".jpg");

            UploadTask uploadTask = filepath.putBytes(profile_image);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uriImage) {
                                firebaseDatabase.child(User.SnameSurname).setValue(nameSurname);
                                firebaseDatabase.child(User.Sphone).setValue(phone);
                                firebaseDatabase.child(User.Sgender).setValue(gender);
                                firebaseDatabase.child(User.Simage).setValue(uriImage.toString());

                                progressDialog.hide();

                                Toast.makeText(ActivityUserUpdate.this, "Bilgiler Güncellendi", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });

                    } else {
                        progressDialog.hide();
                        Toast.makeText(ActivityUserUpdate.this, "Profil Resmi Yüklenemedi!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            firebaseDatabase.child(User.SnameSurname).setValue(nameSurname);
            firebaseDatabase.child(User.Sphone).setValue(phone);
            firebaseDatabase.child(User.Sgender).setValue(gender);

            progressDialog.hide();

            Toast.makeText(ActivityUserUpdate.this, "Bilgiler Güncellendi", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(ActivityUserUpdate.this);
                progressDialog.setTitle("Profil Resmi Ayarlanıyor!");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                File thumb_filePath = new File(resultUri.getPath());


                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(100)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                profile_image = baos.toByteArray();

                imageProfile.setImageBitmap(thumb_bitmap);

                progressDialog.hide();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}