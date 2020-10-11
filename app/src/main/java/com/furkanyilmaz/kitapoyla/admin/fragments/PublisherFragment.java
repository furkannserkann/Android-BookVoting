package com.furkanyilmaz.kitapoyla.admin.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import com.furkanyilmaz.items.Publisher;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterPublisherList;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class PublisherFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView myListView;
    private List<Publisher> listPublisher  = new ArrayList<>();
    private AdapterPublisherList adapterPblisherList;

    private DatabaseReference firebaseDBPublisher;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listview_fab, container, false);

        firebaseDBPublisher = FirebaseDatabase.getInstance().getReference().child("PublisherList");

        init(root);

        return root;
    }

    private void init(View root) {
        myListView = (ListView) root.findViewById(R.id.just_list_listview);
        fab = (FloatingActionButton) root.findViewById(R.id.fragmentlistview_fab);


        adapterPblisherList = new AdapterPublisherList(listPublisher, getContext());
        myListView.setAdapter(adapterPblisherList);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
                showInfoAlertDialog(position);
            }
        });

        firebaseDBPublisher.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Publisher localPublisher = dataSnapshot.getValue(Publisher.class);
                localPublisher.setKey(dataSnapshot.getKey());

                listPublisher.add(localPublisher);

                Collections.sort(listPublisher, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Publisher p1 = (Publisher) o1;
                        Publisher p2 = (Publisher) o2;
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });

                adapterPblisherList.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (int i=0; i<listPublisher.size(); i++) {
                    if (listPublisher.get(i).getKey().equals(dataSnapshot.getKey())) {
                        Publisher mc = dataSnapshot.getValue(Publisher.class);
                        listPublisher.get(i).setName(mc.getName());

                        Collections.sort(listPublisher, new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                Publisher p1 = (Publisher) o1;
                                Publisher p2 = (Publisher) o2;
                                return p1.getName().compareToIgnoreCase(p2.getName());
                            }
                        });
                        adapterPblisherList.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i=0; i<listPublisher.size(); i++) {
                    if (listPublisher.get(i).getKey().equals(dataSnapshot.getKey())) {
                        listPublisher.remove(i);

                        adapterPblisherList.notifyDataSetChanged();
                    }
                }
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
                showAddAlertDialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showAddAlertDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_add_publisher);


        Button btnKaydet = (Button) dialog.findViewById(R.id.popupcategory_button_save);
        final Button btnIptal = (Button) dialog.findViewById(R.id.popupcategory_button_exit);
        final RelativeLayout relativeLayoutName = (RelativeLayout) dialog.findViewById(R.id.popupcategory_relative_name);


        final TextView textViewHead = (TextView) dialog.findViewById(R.id.popuppublisher_text_head);
        final TextView textViewName = (TextView) dialog.findViewById(R.id.popupcategory_edittext_name);
        textViewHead.setText(getContext().getString(R.string.add_new_publisher));

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isThere = false;

                if (!TextUtils.isEmpty(textViewName.getText())) {
                    if (!textViewName.getText().equals("")) {
                        String name = textViewName.getText().toString();
                        for (Publisher publisher : listPublisher) {
                            if (publisher.getName().equals(name)) {
                                isThere = true;
                                break;
                            }
                        }

                        if (!isThere) {
                            final HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(Publisher.Sname, name);
                            userMap.put(Publisher.Scount, "0");
                            firebaseDBPublisher.child(firebaseDBPublisher.push().getKey()).setValue(userMap);
                        } else {
                            myStaticObj.setValidColor(getContext(), relativeLayoutName, false);
                        }
                    }
                }

                if (!isThere)
                    dialog.dismiss();

            }
        });

        btnIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void showInfoAlertDialog(final int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_info_publisher);
        final Publisher selectedPublisher = listPublisher.get(position);

        final Button btnKaydet = (Button) dialog.findViewById(R.id.popuppublisher_button_save);
        final Button btnRemove = (Button) dialog.findViewById(R.id.popuppublisher_button_remove);
        final Button btnIptal = (Button) dialog.findViewById(R.id.popuppublisher_button_exit);
        final RelativeLayout relativeLayoutName = (RelativeLayout) dialog.findViewById(R.id.popuppublisher_relative_name);

        final TextView textViewBookCount = (TextView) dialog.findViewById(R.id.popuppublisher_textview_bookcount);
        final TextView textViewName = (TextView) dialog.findViewById(R.id.popuppublisher_edittext_name);
        textViewName.setText(selectedPublisher.getName());
        textViewBookCount.setText("Kayıtlı Kitap Sayısı: " + selectedPublisher.getCount());

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isThere = false;

                if (!TextUtils.isEmpty(textViewName.getText())) {
                    if (!textViewName.getText().equals("")) {
                        String name = textViewName.getText().toString();
                        for (Publisher publisher : listPublisher) {
                            if (publisher.getName().equals(name) && !selectedPublisher.getName().equals(publisher.getName())) {
                                isThere = true;
                                break;
                            }
                        }

                        if (!isThere) {
                            final HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(Publisher.Sname, name);
                            firebaseDBPublisher.child(selectedPublisher.getKey()).setValue(userMap);
                        } else {
                            myStaticObj.setValidColor(getContext(), relativeLayoutName, false);
                        }
                    }
                }

                if (!isThere)
                    dialog.dismiss();

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectedPublisher.getCount())) {
                    if (Integer.parseInt(selectedPublisher.getCount()) > 0) {
                        Toast.makeText(getContext(), "Kayıtlı Kitap Bulunuyor\nYayın Evi Silinemez!", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseDBPublisher.child(selectedPublisher.getKey()).removeValue();
                        dialog.dismiss();
                    }
                } else {
                    firebaseDBPublisher.child(selectedPublisher.getKey()).removeValue();
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


        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

}