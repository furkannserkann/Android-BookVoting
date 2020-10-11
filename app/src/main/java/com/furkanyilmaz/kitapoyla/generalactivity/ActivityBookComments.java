package com.furkanyilmaz.kitapoyla.generalactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.Comment;
import com.furkanyilmaz.items.User;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterBookList;
import com.furkanyilmaz.kitapoyla.generalactivity.Adapter.AdapterBookComments;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityBookComments extends AppCompatActivity {

    public static Book selectedBook;

    DatabaseReference drCommentsBook, drComments, drUsers;

    private ListView listView;
    private AdapterBookComments adapterBookComments;
    private List<Comment> listComments = new ArrayList<>();
    private List<User> listUser = new ArrayList<>();

    private ImageView imageBack;
    private TextView textHead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.just_back_listview);

        drCommentsBook = FirebaseDatabase.getInstance().getReference().child("CommentsBook").child(selectedBook.getKey());
        drComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        drUsers = FirebaseDatabase.getInstance().getReference().child("Users");


        init();
        firebaseDBLoad();
    }

    private void init() {
        listView = (ListView) findViewById(R.id.just_list_listview);
        adapterBookComments = new AdapterBookComments(listComments, listUser, selectedBook, ActivityBookComments.this);
        listView.setAdapter(adapterBookComments);

        imageBack = (ImageView) findViewById(R.id.justback_image_back);
        textHead = (TextView) findViewById(R.id.justback_list_head);

        textHead.setText(selectedBook.getName());
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void firebaseDBLoad() {
        drCommentsBook.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String comKey = dataSnapshot.getKey();

                drComments.child(comKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Comment localCom = dataSnapshot.getValue(Comment.class);
                        localCom.setKey(comKey);

                        drUsers.child(localCom.getUserId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User localUser = dataSnapshot.getValue(User.class);
                                localUser.setKey(localCom.getUserId());

                                listUser.add(localUser);
                                listComments.add(localCom);
                                adapterBookComments.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
}
