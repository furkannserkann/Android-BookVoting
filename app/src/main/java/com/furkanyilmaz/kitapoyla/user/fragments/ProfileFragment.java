package com.furkanyilmaz.kitapoyla.user.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.kitapoyla.ActivityLogin;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterCategoryList;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    private DatabaseReference firebaseDBCategories, firebaseDBCategoriesBook;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseDBCategories = FirebaseDatabase.getInstance().getReference().child("CategoryList");
        firebaseDBCategoriesBook = FirebaseDatabase.getInstance().getReference().child("CategoryBook").child("Category-Book");

        init(root);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    private void init(View root) {

        TextView textView = (TextView) root.findViewById(R.id.fragment_profile_cikis);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), ActivityLogin.class));
                getActivity().finish();
            }
        });

    }

    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static ProfileFragment newInstance(int page, String title) {
        ProfileFragment fragmentFirst = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
}