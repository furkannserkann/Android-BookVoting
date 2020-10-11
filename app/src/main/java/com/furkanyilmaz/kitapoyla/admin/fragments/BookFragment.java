package com.furkanyilmaz.kitapoyla.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterBookList;
import com.furkanyilmaz.kitapoyla.admin.add_update.Activity_AddBook;
import com.furkanyilmaz.kitapoyla.generalactivity.ActivityBookInfo;
import com.furkanyilmaz.kitapoyla.user.ActivityUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BookFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView myListView;
    private AdapterBookList adapterBookList;
    private List<Book> listBook = new ArrayList<>();

    private DatabaseReference firebaseDatabaseAuthor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listview_fab, container, false);

        firebaseDatabaseAuthor = FirebaseDatabase.getInstance().getReference().child("Books");

        init(root);

        return root;
    }

    private void init(View root) {
        fab = (FloatingActionButton) root.findViewById(R.id.fragmentlistview_fab);

        myListView = root.findViewById(R.id.just_list_listview);
        adapterBookList = new AdapterBookList(listBook, getContext());
        myListView.setAdapter(adapterBookList);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
                ActivityBookInfo.book = listBook.get(position);
                startActivity(new Intent(getActivity(), ActivityBookInfo.class));
            }
        });

        firebaseDatabaseAuthor.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Book myBook = dataSnapshot.getValue(Book.class);
                myBook.setKey(dataSnapshot.getKey());

                listBook.add(myBook);

                Collections.sort(listBook, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Book p1 = (Book) o1;
                        Book p2 = (Book) o2;
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });

                adapterBookList.notifyDataSetChanged();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Activity_AddBook.class));
            }
        });
    }
}
