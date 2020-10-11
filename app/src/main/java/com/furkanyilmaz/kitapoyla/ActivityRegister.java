package com.furkanyilmaz.kitapoyla;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.furkanyilmaz.items.User;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.custom.PhoneTextFormatter;
import com.furkanyilmaz.kitapoyla.custom.isVaild;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ActivityRegister extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    private RelativeLayout rlName, rlSurname, rlPhone, rlEmail, rlPass;
    private EditText edName, edSurname, edPhone, edEmail, edPass;
    private RadioButton radioButtonMale, radioButtonFemale;
    private CircleImageView imageViewProfile;

    private DatabaseReference firebaseDatabase;
    private FirebaseAuth auth;

    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private LinearLayout linearRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        storageReference = FirebaseStorage.getInstance().getReference();

        init();
    }

    private void init() {
        rlName = (RelativeLayout) findViewById(R.id.register_relative_name);
        rlSurname = (RelativeLayout) findViewById(R.id.register_relative_surname);
        rlPhone = (RelativeLayout) findViewById(R.id.register_relative_phonenumber);
        rlEmail = (RelativeLayout) findViewById(R.id.register_relative_email);
        rlPass = (RelativeLayout) findViewById(R.id.register_relative_password);

        edName = (EditText) findViewById(R.id.register_edittext_name);
        edSurname = (EditText) findViewById(R.id.register_edittext_surname);
        edPhone = (EditText) findViewById(R.id.register_edittext_phonenumber);
        edEmail = (EditText) findViewById(R.id.register_edittext_email);
        edPass = (EditText) findViewById(R.id.register_edittext_password);

        radioButtonMale = (RadioButton) findViewById(R.id.register_radio_male);
        radioButtonFemale = (RadioButton) findViewById(R.id.register_radio_female);

        imageViewProfile = (CircleImageView) findViewById(R.id.register_image_profile);

        linearRegister = (LinearLayout) findViewById(R.id.register_linear_register);


        edName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myStaticObj.setValidColor(ActivityRegister.this, rlName, true);
            }
        });

        edSurname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myStaticObj.setValidColor(ActivityRegister.this, rlSurname, true);
            }
        });

        edPhone.addTextChangedListener(new PhoneTextFormatter(edPhone, "+90 (###) ###-####"));
        edPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myStaticObj.setValidColor(ActivityRegister.this, rlPhone, true);
                } else {
                    if (!TextUtils.isEmpty(edPhone.getText().toString())) {
                        if (edPhone.length() == 18) {
                            myStaticObj.setValidColor(ActivityRegister.this, rlPhone, true);
                        } else {
                            myStaticObj.setValidColor(ActivityRegister.this, rlPhone, false);
                        }
                    } else {
                        myStaticObj.setValidColor(ActivityRegister.this, rlPhone, true);
                    }
                }
            }
        });

        edEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myStaticObj.setValidColor(ActivityRegister.this, rlEmail, true);
                } else {
                    if (!TextUtils.isEmpty(edEmail.getText().toString())) {
                        if (!isVaild.isEmailValid(edEmail.getText().toString())) {
                            myStaticObj.setValidColor(ActivityRegister.this, rlEmail, false);
                        } else {
                            myStaticObj.setValidColor(ActivityRegister.this, rlEmail, true);
                        }
                    } else {
                        myStaticObj.setValidColor(ActivityRegister.this, rlEmail, true);
                    }
                }
            }
        });

        edPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myStaticObj.setValidColor(ActivityRegister.this, rlPass, true);
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Profil Resmi Seçiniz"), GALLERY_PICK);
            }
        });

        linearRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegister();
            }
        });
    }

    public void onClickRegister() {
        myStaticObj.setValidColor(ActivityRegister.this, rlName, true);
        myStaticObj.setValidColor(ActivityRegister.this, rlSurname, true);
        myStaticObj.setValidColor(ActivityRegister.this, rlPhone, true);
        myStaticObj.setValidColor(ActivityRegister.this, rlEmail, true);
        myStaticObj.setValidColor(ActivityRegister.this, rlPass, true);

        boolean isEmptyName = TextUtils.isEmpty(edName.getText().toString());
        boolean isEmptySurname = TextUtils.isEmpty(edSurname.getText().toString());
        boolean isEmptyPhone = TextUtils.isEmpty(edPhone.getText().toString());
        boolean isEmptyEmail = TextUtils.isEmpty(edEmail.getText().toString());
        boolean isEmptyPass = TextUtils.isEmpty(edPass.getText().toString());

        if (isEmptyName) {
            myStaticObj.setValidColor(ActivityRegister.this, rlName, false);
        }
        if (isEmptySurname) {
            myStaticObj.setValidColor(ActivityRegister.this, rlSurname, false);
        }
        if (isEmptyPhone) {
            myStaticObj.setValidColor(ActivityRegister.this, rlPhone, false);
        }
        if (isEmptyEmail) {
            myStaticObj.setValidColor(ActivityRegister.this, rlEmail, false);
        }
        if (isEmptyPass) {
            myStaticObj.setValidColor(ActivityRegister.this, rlPass, false);
        }

        boolean isReturn = false;
        if (!isEmptyPhone) {
            if (edPhone.length() != 18) {
                myStaticObj.setValidColor(ActivityRegister.this, rlPhone, false);
                isReturn = true;
            }
        }
        if (!isEmptyEmail) {
            if (!isVaild.isEmailValid(edEmail.getText().toString())) {
                myStaticObj.setValidColor(ActivityRegister.this, rlEmail, false);
                isReturn = true;
            }
        }
        if (!isEmptyPass) {
            if (edPass.length() < 6) {
                myStaticObj.setValidColor(ActivityRegister.this, rlPass, false);
                isReturn = true;
            }
        }

        if (isReturn) return;

        if (!isEmptyName && !isEmptyPhone && !isEmptyEmail && !isEmptyPass) {
            progressDialog = new ProgressDialog(ActivityRegister.this);
            progressDialog.setTitle(getString(R.string.registering_user));
            progressDialog.setCancelable(false);
            progressDialog.show();

            String lGender = myEnums.Gender.MALE.toString();
            if (radioButtonFemale.isChecked()) {
                lGender = myEnums.Gender.FEMALE.toString();
            }

            Register(edEmail.getText().toString(), edPass.getText().toString(), edName.getText().toString() + " " + edSurname.getText().toString(), edPhone.getText().toString(), lGender);
        }
    }

    public void onClickLogin(View view) {
        finish();
    }

    private void Register(final String email, final String password, final String nameSurname, final String phone, final String gender) {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    crateUser(email, nameSurname, phone, gender);
                } else {
                    if (task.getException().getMessage().equals("The email address is already in use by another account."))
                        Toast.makeText(ActivityRegister.this, getString(R.string.usebyemail), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ActivityRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void crateUser(final String email, final String nameSurname, final String phone, final String gender) {
        String uID = auth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);

        final Map userMap = new HashMap();
        userMap.put(User.SnameSurname, nameSurname);
        userMap.put(User.Sphone, phone);
        userMap.put(User.Sgender, gender);
        userMap.put(User.Sauthorit, myEnums.Authority.KULLANICI.toString());
        userMap.put(User.Semail, email);
        userMap.put(User.SregisterTime, ServerValue.TIMESTAMP);

        if (profile_image != null) {
            final StorageReference filepath = storageReference.child("profile_images").child(auth.getUid() + ".jpg");

            UploadTask uploadTask = filepath.putBytes(profile_image);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uriImage) {
                                userMap.put(User.Simage, uriImage.toString());
                                firebaseDatabase.setValue(userMap);

                                progressDialog.hide();

                                Toast.makeText(ActivityRegister.this, getString(R.string.registration_completed), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        progressDialog.hide();
                        Toast.makeText(ActivityRegister.this, getString(R.string.upload_image_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            userMap.put(User.Simage, null);
            firebaseDatabase.setValue(userMap);
            progressDialog.hide();

            Toast.makeText(ActivityRegister.this, getString(R.string.registration_completed), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private byte[] profile_image;

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
                progressDialog = new ProgressDialog(ActivityRegister.this);
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

                imageViewProfile.setImageBitmap(thumb_bitmap);

                progressDialog.hide();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
