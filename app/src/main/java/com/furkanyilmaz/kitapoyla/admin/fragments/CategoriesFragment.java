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

import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterCategoryList;
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

public class CategoriesFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView myListView;
    private List<Category> listCategory  = new ArrayList<>();
    private AdapterCategoryList adapterCategoryList;

    private DatabaseReference firebaseDBCategories, firebaseDBCategoriesBook;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listview_fab, container, false);

        firebaseDBCategories = FirebaseDatabase.getInstance().getReference().child("CategoryList");
        firebaseDBCategoriesBook = FirebaseDatabase.getInstance().getReference().child("CategoryBook").child("Category-Book");

        init(root);

        return root;
    }

    private void init(View root) {
        myListView = (ListView) root.findViewById(R.id.just_list_listview);
        fab = (FloatingActionButton) root.findViewById(R.id.fragmentlistview_fab);


        adapterCategoryList = new AdapterCategoryList(listCategory, getContext());
        myListView.setAdapter(adapterCategoryList);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View myView, int position, long id) {
                showInfoAlertDialog(position);
            }
        });

        firebaseDBCategories.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category localCategory = dataSnapshot.getValue(Category.class);
                localCategory.setKey(dataSnapshot.getKey());

                firebaseDBCategoriesBook.child(localCategory.getKey());
//                firebaseDBCategoriesBook

                listCategory.add(localCategory);
//                listCategory.get(listCategory.size()).setListBook();

                Collections.sort(listCategory, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Category p1 = (Category) o1;
                        Category p2 = (Category) o2;
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });

                adapterCategoryList.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (int i=0; i<listCategory.size(); i++) {
                    if (listCategory.get(i).getKey().equals(dataSnapshot.getKey())) {
                        Category mc = dataSnapshot.getValue(Category.class);
                        listCategory.get(i).setName(mc.getName());

                        Collections.sort(listCategory, new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                Category p1 = (Category) o1;
                                Category p2 = (Category) o2;
                                return p1.getName().compareToIgnoreCase(p2.getName());
                            }
                        });
                        adapterCategoryList.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i=0; i<listCategory.size(); i++) {
                    if (listCategory.get(i).getKey().equals(dataSnapshot.getKey())) {
                        listCategory.remove(i);

                        adapterCategoryList.notifyDataSetChanged();
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
        dialog.setContentView(R.layout.popup_add_category);

        Button btnKaydet = (Button) dialog.findViewById(R.id.popupcategory_button_save);
        final Button btnIptal = (Button) dialog.findViewById(R.id.popupcategory_button_exit);
        final RelativeLayout relativeLayoutName = (RelativeLayout) dialog.findViewById(R.id.popupcategory_relative_name);

        final TextView textHead = (TextView) dialog.findViewById(R.id.popupcategory_text_head);
        final TextView textViewName = (TextView) dialog.findViewById(R.id.popupcategory_edittext_name);
        textHead.setText(getContext().getString(R.string.add_new_category));


        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isThere = false;

                if (!TextUtils.isEmpty(textViewName.getText())) {
                    if (!textViewName.getText().equals("")) {
                        String name = textViewName.getText().toString();
                        for (Category category : listCategory) {
                            if (category.getName().equals(name)) {
                                isThere = true;
                                break;
                            }
                        }

                        if (!isThere) {
                            final HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(Category.Sname, name);
                            userMap.put(Category.Scount, "0");

                            firebaseDBCategories.child(firebaseDBCategories.push().getKey()).setValue(userMap);
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
        final Category selectedCategory = listCategory.get(position);
        dialog.setContentView(R.layout.popup_info_category);

        final Button btnKaydet = (Button) dialog.findViewById(R.id.popupcategory_button_save);
        final Button btnRemove = (Button) dialog.findViewById(R.id.popupcategory_button_remove);
        final Button btnIptal = (Button) dialog.findViewById(R.id.popupcategory_button_exit);
        final RelativeLayout relativeLayoutName = (RelativeLayout) dialog.findViewById(R.id.popupcategory_relative_name);

        final TextView textViewBookCount = (TextView) dialog.findViewById(R.id.popupcategory_textview_bookcount);
        final TextView textViewName = (TextView) dialog.findViewById(R.id.popupcategory_edittext_name);
        textViewName.setText(selectedCategory.getName());
        textViewBookCount.setText("Kayıtlı Kitap Sayısı: " + selectedCategory.getCount());

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isThere = false;

                if (!TextUtils.isEmpty(textViewName.getText())) {
                    if (!textViewName.getText().equals("")) {
                        String name = textViewName.getText().toString();
                        for (Category category : listCategory) {
                            if (category.getName().equals(name) && !selectedCategory.getName().equals(category.getName())) {
                                isThere = true;
                                break;
                            }
                        }

                        if (!isThere) {
                            firebaseDBCategories.child(selectedCategory.getKey()).child(Category.Sname).setValue(name);
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
                if (!TextUtils.isEmpty(selectedCategory.getCount())) {
                    if (Integer.parseInt(selectedCategory.getCount()) > 0) {
                        Toast.makeText(getContext(), "Kayıtlı Kitap Bulunuyor\nKategori Silinemez!", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseDBCategories.child(selectedCategory.getKey()).removeValue();
                        dialog.dismiss();
                    }
                } else {
                    firebaseDBCategories.child(selectedCategory.getKey()).removeValue();
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