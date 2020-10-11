package com.furkanyilmaz.kitapoyla.admin.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.furkanyilmaz.items.User;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.ActivityRegister;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterUsersList;
import com.furkanyilmaz.kitapoyla.admin.add_update.Activity_AddBook;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class UsersFragment extends Fragment {
    private static final int GALLERY_PICK = 1;


    private ListView myListView;
    private AdapterUsersList adapterUsersList;
    private List<User> listUser = new ArrayList<>();

    private DatabaseReference firebaseDatabaseUsers;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        firebaseDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        View root = inflater.inflate(R.layout.just_listview, container, false);

        myListView = root.findViewById(R.id.just_list_listview);
        adapterUsersList = new AdapterUsersList(listUser, getContext());
        myListView.setAdapter(adapterUsersList);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
                showMyCustomAlertDialog(listUser.get(position));
            }
        });

        loadUser();

        return root;
    }

    private void loadUser() {
        listUser.clear();

        firebaseDatabaseUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User myUser = dataSnapshot.getValue(User.class);
                myUser.setKey(dataSnapshot.getKey());

                listUser.add(myUser);

                Collections.sort(listUser, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        User p1 = (User) o1;
                        User p2 = (User) o2;
                        return p1.getNameSurname().compareToIgnoreCase(p2.getNameSurname());
                    }
                });

                adapterUsersList.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                return;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                return;
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ImageView imageProfile;
    @SuppressLint("SetTextI18n")
    public void showMyCustomAlertDialog(final User localUser) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_add_user);

        Button btnKaydet = (Button) dialog.findViewById(R.id.popupuseradd_button_save);
        final Button btnIptal = (Button) dialog.findViewById(R.id.popupuseradd_button_exit);

        imageProfile = (ImageView) dialog.findViewById(R.id.userinfo_image_profile);

        final TextView textViewName = (TextView) dialog.findViewById(R.id.register_edittext_name_surname);
        final TextView textViewPhone = (TextView) dialog.findViewById(R.id.register_edittext_phonenumber);
        TextView textViewEmail = (TextView) dialog.findViewById(R.id.register_edittext_email);
        final TextView textViewAuthority = (TextView) dialog.findViewById(R.id.register_edittext_authority);

        final RadioButton radioButtonMale = (RadioButton) dialog.findViewById(R.id.register_radio_male);
        RadioButton radioButtonFemale = (RadioButton) dialog.findViewById(R.id.register_radio_female);

        textViewName.setText(localUser.getNameSurname());
        textViewPhone.setText(localUser.getPhone());
        textViewEmail.setText(localUser.getEmail());
        textViewAuthority.setText(localUser.getAuthorit());

        if (!TextUtils.isEmpty(localUser.getImage())) {
            Picasso.get().load(localUser.getImage()).resize(100, 100).centerCrop().into(imageProfile);
        } else {
            if (localUser.getGender().equals(myEnums.Gender.FEMALE.toString()))
                imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));
            else
                imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));
        }

        if (localUser.getGender().equals(myEnums.Gender.MALE.toString())){
            radioButtonMale.setActivated(true);
            radioButtonFemale.setActivated(false);
        } else {
            radioButtonMale.setActivated(false);
            radioButtonFemale.setActivated(true);
        }

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEmptyName = TextUtils.isEmpty(textViewName.getText().toString());
                boolean isEmptyPhone = TextUtils.isEmpty(textViewPhone.getText().toString());

                boolean isReturn = false;
                if (!isEmptyPhone) {
                    if (textViewPhone.length() != 18) {
                        isReturn = true;
                    }
                }

                if (isEmptyName || isEmptyPhone || isReturn) {
                    Toast.makeText(getActivity(), "Lütfen Eksik Bilgileri Doldurunuz!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String lGender = radioButtonMale.isChecked() ? myEnums.Gender.MALE.toString() : myEnums.Gender.FEMALE.toString();
                    String lAuthorit = textViewAuthority.getText().equals(myEnums.Authority.ADMIN.toString()) ? myEnums.Authority.ADMIN.toString() : myEnums.Authority.KULLANICI.toString();

                    firebaseDatabaseUsers.child(localUser.getKey()).child(User.Sgender).setValue(lGender);
                    firebaseDatabaseUsers.child(localUser.getKey()).child(User.Sauthorit).setValue(lAuthorit);
                    firebaseDatabaseUsers.child(localUser.getKey()).child(User.Sphone).setValue(textViewPhone.getText().toString());
                    firebaseDatabaseUsers.child(localUser.getKey()).child(User.SnameSurname).setValue(textViewName.getText().toString());

                    Toast.makeText(getActivity(), "Kayıt İşlemi Tamamlandı!", Toast.LENGTH_SHORT).show();
                    loadUser();
                    dialog.dismiss();
                }
            }
        });

        btnIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

}