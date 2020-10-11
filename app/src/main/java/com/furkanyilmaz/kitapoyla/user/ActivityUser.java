package com.furkanyilmaz.kitapoyla.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DecimalFormat;
import java.util.Calendar;

import com.furkanyilmaz.customfonts.DatePickerPubDate;
import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.items.Comment;
import com.furkanyilmaz.items.Publisher;
import com.furkanyilmaz.items.Sizes;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.ActivityLogin;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.AdapterBookList;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.furkanyilmaz.kitapoyla.generalactivity.ActivityBookInfo;
import com.furkanyilmaz.kitapoyla.generalactivity.Adapter.AdapterBookComments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class ActivityUser extends AppCompatActivity {

    LinearLayout linearFragMain, linearFragSearch, linearFragComment, linearFragProfile;
    SmoothBottomBar smoothBottomBar;

    CircleImageView imagePProfile;
    TextView textPName, textPGender, textPEmail, textPPhone;
    Button buttonPUpdate, buttonPLogout;

    private EditText eTextSearch;
    private ImageView imageSearch, imageSearchClear;
    private LinearLayout linearSort, linearSortFilterAyrac, linearFilter, linearSearchText, linearSearchNotFound, linearSearchHistory, linearSearchLoading, linearSearchHistoryNotFound;
    private ListView listViewSearch;

    private AdapterBookList adapterBookList;
    private List<Book> listBook = new ArrayList<>();

    private ListView listViewHistory;
    private String lastSearch = "";

    private DatabaseReference drBook, drPublishers, drCategoryList, drCategoryBook, drCommentsUser, drComments, drSizes;

    private List<Publisher> listPublisher = new ArrayList<>();
    private List<String> listPublisherString = new ArrayList<>();
    private List<String> listSelectedPublisherString = new ArrayList<>(), listSelectedPublisherStringYedek = new ArrayList<>(), listSelectedPublisherStringOpened = new ArrayList<>();
    private List<Integer> listSelectedPublisherInteger = new ArrayList<>(), listSelectedPublisherIntegerYedek = new ArrayList<>(), listSelectedPublisherIntegerOpened = new ArrayList<>();


    private List<Category> listCategory = new ArrayList<>();
    private List<String> listCategoryString = new ArrayList<>();
    private List<Integer> listSelectedCategoryInteger = new ArrayList<>(), listSelectedCategoryIntegerYedek = new ArrayList<>(), listSelectedCategoryIntegerOpened = new ArrayList<>();
    private List<String> listSelectedCategoryString = new ArrayList<>(), listSelectedCategoryStringYedek = new ArrayList<>(), listSelectedCategoryStringOpened = new ArrayList<>();

    private ListView listViewMyComments;
    private AdapterBookComments adapterBookMyComments;
    private List<Comment> listMyComments = new ArrayList<>();
    private List<Book> listMyComBook = new ArrayList<>();
    private LinearLayout linearMyComNull, linearMyComProgress;

    SharedPreferences sharedPref;
    private Integer bookSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        drBook = FirebaseDatabase.getInstance().getReference().child("Books");

        drCategoryBook = FirebaseDatabase.getInstance().getReference().child("CategoryBook").child("Category-Book");

        drCommentsUser = FirebaseDatabase.getInstance().getReference().child("CommentsUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drComments = FirebaseDatabase.getInstance().getReference().child("Comments");

        drCategoryList = FirebaseDatabase.getInstance().getReference().child("CategoryList");
        drPublishers = FirebaseDatabase.getInstance().getReference().child("PublisherList");

        drSizes = FirebaseDatabase.getInstance().getReference().child("Sizes");
        drSizes.child(Sizes.SbookSize).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookSize = Integer.valueOf(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sharedPref = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);

        firebaseDBLoad();
        init();
    }

    private void init() {
        smoothBottomBar = (SmoothBottomBar) findViewById(R.id.bottomBar);
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        linearFragMain.setVisibility(View.GONE);
                        linearFragSearch.setVisibility(View.VISIBLE);
                        linearFragComment.setVisibility(View.GONE);
                        linearFragProfile.setVisibility(View.GONE);
                        return true;
                    case 1:
                        linearFragMain.setVisibility(View.GONE);
                        linearFragSearch.setVisibility(View.GONE);
                        linearFragComment.setVisibility(View.VISIBLE);
                        linearFragProfile.setVisibility(View.GONE);
                        loadMyComments();
                        return true;
                    case 2:
                        linearFragMain.setVisibility(View.GONE);
                        linearFragSearch.setVisibility(View.GONE);
                        linearFragComment.setVisibility(View.GONE);
                        linearFragProfile.setVisibility(View.VISIBLE);
                        FragmentProfile();
                        return true;
                    default:
                        return false;
                }
            }
        });

        linearFragMain = (LinearLayout) findViewById(R.id.activityuser_linear_frag1);
        linearFragSearch = (LinearLayout) findViewById(R.id.activityuser_linear_frag2);
        linearFragComment = (LinearLayout) findViewById(R.id.activityuser_linear_frag3);
        linearFragProfile = (LinearLayout) findViewById(R.id.activityuser_linear_frag4);

        linearFragMain.setVisibility(View.GONE);
        linearFragSearch.setVisibility(View.VISIBLE);
        linearFragComment.setVisibility(View.GONE);
        linearFragProfile.setVisibility(View.GONE);

        imagePProfile = (CircleImageView) findViewById(R.id.activityuser_image_profile);
        textPName = (TextView) findViewById(R.id.activityuser_text_name);
        textPGender = (TextView) findViewById(R.id.activityuser_text_gender);
        textPEmail = (TextView) findViewById(R.id.activityuser_text_email);
        textPPhone = (TextView) findViewById(R.id.activityuser_text_phone);
        buttonPUpdate = (Button) findViewById(R.id.activityuser_button_update);
        buttonPUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUser.this, ActivityUserUpdate.class));
            }
        });
        buttonPLogout = (Button) findViewById(R.id.activityuser_button_logout);
        buttonPLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(ActivityUser.this, ActivityLogin.class));
                finish();
            }
        });

        eTextSearch = (EditText) findViewById(R.id.search_edittext_bookname);
        imageSearch = (ImageView) findViewById(R.id.search_image_search);
        linearSort = (LinearLayout) findViewById(R.id.search_linear_sort);
        linearSortFilterAyrac = (LinearLayout) findViewById(R.id.search_linear_sortfilter_ayrac);
        linearFilter = (LinearLayout) findViewById(R.id.search_linear_filter);
        linearSearchText = (LinearLayout) findViewById(R.id.search_linear_search);
        linearSearchNotFound = (LinearLayout) findViewById(R.id.search_linear_nullsearch);
        linearSearchHistoryNotFound = (LinearLayout) findViewById(R.id.search_linear_nullhistory);
        linearSearchHistory = (LinearLayout) findViewById(R.id.search_linear_history);
        linearSearchLoading = (LinearLayout) findViewById(R.id.search_linear_loading);
        listViewSearch = (ListView) findViewById(R.id.search_listview);

        linearSearchNotFound.setVisibility(View.GONE);
        linearSearchHistory.setVisibility(View.VISIBLE);
        linearSearchLoading.setVisibility(View.GONE);
        listViewSearch.setVisibility(View.GONE);

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityBookInfo.book = listBook.get(position);

                startActivity(new Intent(ActivityUser.this, ActivityBookInfo.class));
            }
        });

        linearSort.setVisibility(View.GONE);
        linearSortFilterAyrac.setVisibility(View.GONE);

        imageSearchClear = (ImageView) findViewById(R.id.search_image_clearsearchtext);
        imageSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTextSearch.setText("");
            }
        });
        eTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    imageSearchClear.setVisibility(View.GONE);
                } else {
                    imageSearchClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        adapterBookList = new AdapterBookList(listBook, ActivityUser.this);
        listViewSearch.setAdapter(adapterBookList);

        eTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchBook();
                    return true;
                }
                return false;
            }
        });
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBook();
            }
        });

        linearSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSort();
            }
        });

        linearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFilter();
            }
        });

        filterCheckList = new Boolean[6];
        Arrays.fill(filterCheckList, false);

        linearMyComNull = (LinearLayout) findViewById(R.id.mycomment_linear_nullsearch);
        linearMyComProgress = (LinearLayout) findViewById(R.id.mycomment_linear_progress);
        listViewMyComments = (ListView) findViewById(R.id.activityuser_listview_mycomments);
        adapterBookMyComments = new AdapterBookComments(listMyComments, listMyComBook, ActivityUser.this);
        listViewMyComments.setAdapter(adapterBookMyComments);
        listViewMyComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityBookInfo.book = listMyComBook.get(position);
                startActivity(new Intent(ActivityUser.this, ActivityBookInfo.class));
            }
        });


        listViewMyComments.setVisibility(View.GONE);
        linearMyComNull.setVisibility(View.GONE);
        linearMyComProgress.setVisibility(View.VISIBLE);


        listViewHistory = (ListView) findViewById(R.id.search_listview_history);

        SharedPreferences.Editor editor = sharedPref.edit();
        int num = sharedPref.getInt("num", 0);
        String[] items = new String[num];

        for (int i=0; i<num; i++) {
            items[i] = sharedPref.getString("h" + String.valueOf(i + 1), "");
        }

        for(int i = 0; i < items.length / 2; i++)
        {
            String temp = items[i];
            items[i] = items[items.length - i - 1];
            items[items.length - i - 1] = temp;
        }

        if (items.length == 0) {
            linearSearchHistoryNotFound.setVisibility(View.VISIBLE);
            listViewHistory.setVisibility(View.GONE);
        } else {
            linearSearchHistoryNotFound.setVisibility(View.GONE);
            listViewHistory.setVisibility(View.VISIBLE);

            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
            listViewHistory.setAdapter(itemsAdapter);
        }

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView lText = (TextView) view.findViewById(android.R.id.text1);
                eTextSearch.setText(lText.getText().toString());
                SearchBook();
            }
        });
    }

    private void firebaseDBLoad() {
        drPublishers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Publisher localPublisher = dataSnapshot.getValue(Publisher.class);
                localPublisher.setKey(dataSnapshot.getKey());

                listPublisher.add(localPublisher);
                listPublisherString.add(localPublisher.getName());

                Collections.sort(listPublisher, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Publisher p1 = (Publisher) o1;
                        Publisher p2 = (Publisher) o2;
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });

                Collections.sort(listPublisherString, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String p1 = (String) o1;
                        String p2 = (String) o2;
                        return p1.compareToIgnoreCase(p2);
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

        drCategoryList.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category localCategory = dataSnapshot.getValue(Category.class);
                localCategory.setKey(dataSnapshot.getKey());

                listCategory.add(localCategory);
                listCategoryString.add(localCategory.getName());

                Collections.sort(listCategory, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Category p1 = (Category) o1;
                        Category p2 = (Category) o2;
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });

                Collections.sort(listCategoryString, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String p1 = (String) o1;
                        String p2 = (String) o2;
                        return p1.compareToIgnoreCase(p2);
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

    private void loadMyComments() {
        listMyComBook.clear();
        listMyComments.clear();

        listViewMyComments.setVisibility(View.GONE);
        linearMyComNull.setVisibility(View.GONE);
        linearMyComProgress.setVisibility(View.VISIBLE);

        drCommentsUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String comKey = dataSnapshot.getKey();

                drComments.child(comKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Comment localCom = dataSnapshot.getValue(Comment.class);
                        localCom.setKey(comKey);

                        drBook.child(localCom.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Book localBook = dataSnapshot.getValue(Book.class);
                                localBook.setKey(localCom.getBookId());

                                listMyComBook.add(localBook);
                                listMyComments.add(localCom);
                                adapterBookMyComments.notifyDataSetChanged();

                                listViewMyComments.setVisibility(View.VISIBLE);
                                linearMyComNull.setVisibility(View.GONE);
                                linearMyComProgress.setVisibility(View.GONE);
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

    private boolean isSelectFilters = false;
    private void SearchBook() {
        if (!TextUtils.isEmpty(eTextSearch.getText()) || isSelectFilters) {
            linearSearchNotFound.setVisibility(View.GONE);
            linearSearchHistory.setVisibility(View.GONE);
            linearSearchLoading.setVisibility(View.VISIBLE);
            listViewSearch.setVisibility(View.GONE);

            listBook.clear();
            myStaticObj.hideKeyboard(ActivityUser.this);
            if (!TextUtils.isEmpty(eTextSearch.getText())) {
                lastSearch = eTextSearch.getText().toString();

                if (!lastSearch.equals("")) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int maxNum = 7;

                    int num = sharedPref.getInt("num", 0);
                    if (num >= maxNum) {
                        num = maxNum;

                        for (int i = 0; i < maxNum; i++) {
                            if ((i + 1) != maxNum)
                                editor.putString("h" + String.valueOf(i + 1), sharedPref.getString("h" + String.valueOf(i + 2), ""));
                            else editor.putString("h" + String.valueOf(i + 1), lastSearch);
                        }
                        //                    editor.putString("h1", sharedPref.getString("h2", ""));
                        //                    editor.putString("h2", sharedPref.getString("h3", ""));
                        //                    editor.putString("h3", lastSearch);
                    } else {
                        num++;
                        editor.putString("h" + String.valueOf(num), lastSearch);
                    }

                    editor.putInt("num", num);
                    editor.commit();
                }
            } else
                lastSearch = "";


            SearchBookLocal();
        } else {
            Toast.makeText(this, "Lütfen Kitap Adı veya Detaylı Arama Seçiniz!", Toast.LENGTH_SHORT).show();
        }
    }
    private Integer bookSizeWhile = 1;
    private void SearchBookLocal() {
        listBook.clear();
        adapterBookList.notifyDataSetChanged();

        drBook.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Book myBook = dataSnapshot.getValue(Book.class);
                myBook.setKey(dataSnapshot.getKey());


                if (!lastSearch.equals("")) {
                    if (myBook.getName().toLowerCase().replace(" ", "")
                            .indexOf(lastSearch.replace(" ", "").toLowerCase()) != -1
                            || myBook.getISBN().equals(lastSearch)) {
                        _localSearch(myBook);
                    }
                } else if (isSelectFilters) {
                    _localSearch(myBook);
                }

                if (bookSize == bookSizeWhile) {
                    if (listBook.size() == 0) {
                        linearSearchNotFound.setVisibility(View.VISIBLE);
                        linearSearchHistory.setVisibility(View.GONE);
                        linearSearchLoading.setVisibility(View.GONE);
                        listViewSearch.setVisibility(View.GONE);
                    } else {
                        linearSearchNotFound.setVisibility(View.GONE);
                        linearSearchHistory.setVisibility(View.GONE);
                        linearSearchLoading.setVisibility(View.GONE);
                        listViewSearch.setVisibility(View.VISIBLE);
                    }
                    bookSizeWhile = 1;
                }
                bookSizeWhile++;

//                if (lastSearch.equals("") || myBook.getName().indexOf(lastSearch) != -1 || myBook.getISBN().equals(lastSearch)) {
//                    _localSearch(myBook);
//                }
            }

            private void _localSearch(Book myBook) {
                boolean addBook = true;

                if (!lastSearch.equals("")) {
                    if (myBook.getName().toLowerCase().replace(" ", "")
                            .indexOf(lastSearch.toLowerCase().replace(" ", "")) == -1) {
                        addBook = false;
                    }
                }

                if (pubDate1 != null && addBook) {
                    if (pubDate1 > Integer.parseInt(myBook.getPublishDate())) {
                        addBook = false;
                    }
                }
                if (pubDate2 != null && addBook) {
                    if (pubDate2 < Integer.parseInt(myBook.getPublishDate())) {
                        addBook = false;
                    }
                }

                if (listSelectedPublisherInteger.size()>0 && addBook) {
                    for (int i=0; i<listSelectedPublisherInteger.size(); i++) {
                        if (listPublisher.get(listSelectedPublisherInteger.get(i)).getKey().equals(myBook.getPublisherId())) {
                            addBook = true;
                            break;
                        } else {
                            addBook = false;
                        }
                    }
                } else if (addBook) {

                }

                if (listCategoryBook.size() > 0  && addBook) {
                    if (listCategoryBook.indexOf(myBook.getKey()) == -1) {
                        addBook = false;
                    }
                }//else if (addBook) {
//                    addBook = false;
//                }

                Integer localPage = Integer.parseInt(myBook.getPageNumber());
                if (addBook) {
                    if (page1 != null && page2 != null) {
                        if (page1 > localPage || localPage > page2) {
                            addBook = false;
                        }
                    } else if (page1 != null && page2 == null) {
                        if (page1 > localPage) {
                            addBook = false;
                        }
                    } else if (page1 == null && page2 != null) {
                        if (localPage > page2) {
                            addBook = false;
                        }
                    }
                }

                if (addBook) {
                    boolean addStar = false;

                    float localStar = Float.parseFloat(myBook.getStarRate());
                    if (filterCheckList[0] && !addStar) {
                        if (localStar >= 4.5f) {
                            addStar = true;
                        }
                    }
                    if (filterCheckList[1] && !addStar) {
                        if (localStar >= 3.5f && localStar < 4.5f) {
                            addStar = true;
                        }
                    }
                    if (filterCheckList[2] && !addStar) {
                        if (localStar >= 2.5f && localStar < 3.5f) {
                            addStar = true;
                        }
                    }
                    if (filterCheckList[3] && !addStar) {
                        if (localStar >= 1.5f && localStar < 2.5f) {
                            addStar = true;
                        }
                    }
                    if (filterCheckList[4] && !addStar) {
                        if (localStar >= 0.5f && localStar < 1.5f) {
                            addStar = true;
                        }
                    }
                    if (filterCheckList[5] && !addStar) {
                        if (localStar < 0.5f) {
                            addStar = true;
                        }
                    }

                    if (!filterCheckList[0] && !filterCheckList[1] && !filterCheckList[2]
                            && !filterCheckList[3] && !filterCheckList[4] && !filterCheckList[5]) {
                        addStar = true;
                    }

                    addBook = addStar;
                }

                if (addBook) {
                    listBook.add(myBook);
                    adapterBookList.notifyDataSetChanged();

                    linearSort.setVisibility(View.VISIBLE);
                    linearSortFilterAyrac.setVisibility(View.VISIBLE);
                }
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

    private void FragmentProfile() {
        if (!TextUtils.isEmpty(myStaticObj.loginUser.getImage()))
            Picasso.get().load(myStaticObj.loginUser.getImage()).into(imagePProfile);
        else {
            if (myStaticObj.loginUser.getGender().equals(myEnums.Gender.FEMALE.toString()))
                imagePProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));
            else
                imagePProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));
        }

        textPName.setText(myStaticObj.loginUser.getNameSurname());
        textPGender.setText(myStaticObj.loginUser.getGender());
        textPEmail.setText(myStaticObj.loginUser.getEmail());
        textPPhone.setText(myStaticObj.loginUser.getPhone());
//        textPCommentCount.setText(myStaticObj.loginUser.get);
    }

    private void menuSort() {
        if (listBook.size() > 0) {

            final Dialog dialog = new Dialog(ActivityUser.this);
            dialog.setContentView(R.layout.popup_book_sort);


            Button buttonClose = dialog.findViewById(R.id.booksort_button_exit);

            TextView textOnerilen = dialog.findViewById(R.id.sortbook_text_onerilen);
            TextView textComAZ = dialog.findViewById(R.id.sortbook_text_comaz);
            TextView textComZA = dialog.findViewById(R.id.sortbook_text_comza);
            TextView textPuanAZ = dialog.findViewById(R.id.sortbook_text_puanaz);
            TextView textPuanZA = dialog.findViewById(R.id.sortbook_text_puanza);

            TextView[] listSortText = {textComAZ, textComZA, textPuanAZ, textPuanZA};
            for (int i=0; i < listSortText.length; i++) {
                final int k = i;
                listSortText[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sortClick(event, dialog, k, k == 0 || k == 2 ? true : false);
                        return true;
                    }
                });
            }

            textOnerilen.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        listBook.clear();
                        SearchBookLocal();
                        dialog.dismiss();
                    }
                    return true;
                }
            });
//            rlComAZ.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        Collections.sort(listBook, new Comparator() {
//                            @Override
//                            public int compare(Object o1, Object o2) {
//                                Book p1 = (Book) o1;
//                                Book p2 = (Book) o2;
//                                return p1.getCommentCount().compareToIgnoreCase(p2.getCommentCount());
//                            }
//                        });
//
//                        Collections.reverse(listBook);
//                        adapterBookList.notifyDataSetChanged();
//
//                        dialog.dismiss();
//                    }
//                    return true;
//                }
//            });
//            rlComZA.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        Collections.sort(listBook, new Comparator() {
//                            @Override
//                            public int compare(Object o1, Object o2) {
//                                Book p1 = (Book) o1;
//                                Book p2 = (Book) o2;
//                                return p1.getCommentCount().compareToIgnoreCase(p2.getCommentCount());
//                            }
//                        });
//
//                        adapterBookList.notifyDataSetChanged();
//                        dialog.dismiss();
//                    }
//                    return true;
//                }
//            });
//            rlPuanAZ.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        Collections.sort(listBook, new Comparator() {
//                            @Override
//                            public int compare(Object o1, Object o2) {
//                                Book p1 = (Book) o1;
//                                Book p2 = (Book) o2;
//                                return p1.getStarRate().compareToIgnoreCase(p2.getStarRate());
//                            }
//                        });
//
//                        Collections.reverse(listBook);
//                        adapterBookList.notifyDataSetChanged();
//                        dialog.dismiss();
//                    }
//                    return true;
//                }
//            });
//            rlPuanZA.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        Collections.sort(listBook, new Comparator() {
//                            @Override
//                            public int compare(Object o1, Object o2) {
//                                Book p1 = (Book) o1;
//                                Book p2 = (Book) o2;
//                                return p1.getStarRate().compareToIgnoreCase(p2.getStarRate());
//                            }
//                        });
//
//                        adapterBookList.notifyDataSetChanged();
//                        dialog.dismiss();
//                    }
//                    return true;
//                }
//            });


            buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.show();
        } else {
            Toast.makeText(ActivityUser.this, "Önce Arama Yapmalısınız!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sortClick(MotionEvent event, Dialog dialog, final int sortNameNum, boolean isReverse) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Collections.sort(listBook, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Book p1 = (Book) o1;
                    Book p2 = (Book) o2;

                    if (sortNameNum == 0 || sortNameNum == 1) return p1.getCommentCount().compareToIgnoreCase(p2.getCommentCount());
                    else return p1.getStarRate().compareToIgnoreCase(p2.getStarRate());
                }
            });

            if (isReverse) Collections.reverse(listBook);

            adapterBookList.notifyDataSetChanged();

            dialog.dismiss();
        }
    }

    private DialogFragment datePickerDialog1;//, datePickerDialog2;
    public static Integer pubDate1, pubDate2;

    private Integer page1, page2;
    private Boolean[] filterCheckList;

    private void menuFilter() {
        final Dialog dialog = new Dialog(ActivityUser.this);
        dialog.setContentView(R.layout.popup_book_filter);

        final CheckBox checkStar5 = dialog.findViewById(R.id.bookfiltre_check_5);
        final CheckBox checkStar4 = dialog.findViewById(R.id.bookfiltre_check_4);
        final CheckBox checkStar3 = dialog.findViewById(R.id.bookfiltre_check_3);
        final CheckBox checkStar2 = dialog.findViewById(R.id.bookfiltre_check_2);
        final CheckBox checkStar1 = dialog.findViewById(R.id.bookfiltre_check_1);
        final CheckBox checkStar0 = dialog.findViewById(R.id.bookfiltre_check_0);
        final CheckBox[] listCheck = {checkStar5, checkStar4, checkStar3, checkStar2, checkStar1, checkStar0};

        final RatingBar ratingBarStar5 = dialog.findViewById(R.id.bookfiltre_rating5);
        final RatingBar ratingBarStar4 = dialog.findViewById(R.id.bookfiltre_rating4);
        final RatingBar ratingBarStar3 = dialog.findViewById(R.id.bookfiltre_rating3);
        final RatingBar ratingBarStar2 = dialog.findViewById(R.id.bookfiltre_rating2);
        final RatingBar ratingBarStar1 = dialog.findViewById(R.id.bookfiltre_rating1);
        final RatingBar ratingBarStar0 = dialog.findViewById(R.id.bookfiltre_rating0);
        final RatingBar[] listRating = {ratingBarStar5, ratingBarStar4, ratingBarStar3, ratingBarStar2, ratingBarStar1, ratingBarStar0};


        final TextView textPubDate1 = dialog.findViewById(R.id.bookfiltre_text_date1);
        final TextView textPubDate2 = dialog.findViewById(R.id.bookfiltre_text_date2);
        final TextView textPublisher = dialog.findViewById(R.id.bookfiltre_text_publisher);
        final TextView textCategory = dialog.findViewById(R.id.bookfiltre_text_category);
        final TextView textPageMin = dialog.findViewById(R.id.bookfiltre_text_pagenumber1);
        final TextView textPageMax = dialog.findViewById(R.id.bookfiltre_text_pagenumber2);

        final Button btnClearFilter = dialog.findViewById(R.id.bookfiltre_button_clearfilter);
        final Button btnPubDate1 = dialog.findViewById(R.id.bookfiltre_button_date1);
        final Button btnPubDate2 = dialog.findViewById(R.id.bookfiltre_button_date2);
        final Button btnPublisher = dialog.findViewById(R.id.bookfiltre_button_publisher);
        final Button btnCategory = dialog.findViewById(R.id.bookfiltre_button_category);
        final Button btnExit = dialog.findViewById(R.id.bookfilter_button_exit);
        final Button btnSave = dialog.findViewById(R.id.bookfilter_button_save);

        listSelectedCategoryStringOpened = new ArrayList<>(listSelectedCategoryString);
        listSelectedCategoryIntegerOpened = new ArrayList<>(listSelectedCategoryInteger);
        listSelectedPublisherStringOpened = new ArrayList<>(listSelectedPublisherString);
        listSelectedPublisherIntegerOpened = new ArrayList<>(listSelectedPublisherInteger);

        datePickerDialog1 = new DatePickerPubDate();

        if (0 == DatePickerPubDate.selectYear) {
            DatePickerPubDate.changeDate(Calendar.getInstance().get(Calendar.YEAR));
        }

        if (filterCheckList != null) {
            for (int i = 0; i < filterCheckList.length; i++)
                listCheck[i].setChecked(filterCheckList[i]);
        }

        textCategory.setText("");
        for (int i = 0; i < listSelectedCategoryString.size(); i++) {
            textCategory.setText(textCategory.getText() + String.valueOf(listSelectedCategoryString.get(i)));

            if (i != listSelectedCategoryString.size() - 1)
                textCategory.setText(textCategory.getText().toString() + ", ");
        }

        textPublisher.setText("");
        for (int i = 0; i < listSelectedPublisherString.size(); i++) {
            textPublisher.setText(textPublisher.getText() + String.valueOf(listSelectedPublisherString.get(i)));

            if (i != listSelectedPublisherString.size() - 1)
                textPublisher.setText(textPublisher.getText().toString() + ", ");
        }

        if (page1 != null) textPageMin.setText(page1.toString());
        if (page2 != null) textPageMax.setText(page2.toString());

        DecimalFormat df = new DecimalFormat("00");
        if (pubDate1 != null) {
            textPubDate1.setText(String.valueOf(pubDate1));

            btnPubDate1.setBackgroundResource(R.drawable.blue_border_rounded_red);
            btnPubDate1.setText("Temizle");
        }
        if (pubDate2 != null) {
            textPubDate2.setText(String.valueOf(pubDate2));

            btnPubDate2.setBackgroundResource(R.drawable.blue_border_rounded_red);
            btnPubDate2.setText("Temizle");
        }


        for (int i=0; i<listRating.length; i++) {
            final int k = i;
            listRating[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (listCheck[k].isChecked()) {
                            listCheck[k].setChecked(false);
                        } else {
                            listCheck[k].setChecked(true);
                        }
                    }

                    return true;
                }
            });
        }

        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPubDate1.setText("");
                textPubDate2.setText("");

                btnPubDate1.setBackgroundResource(R.drawable.blue_border_rounded_green);
                btnPubDate1.setText("Seç");
                btnPubDate2.setBackgroundResource(R.drawable.blue_border_rounded_green);
                btnPubDate2.setText("Seç");


                textPublisher.setText("");
                textCategory.setText("");

                textPageMin.setText("");
                textPageMax.setText("");

                for(int i=0; i<listCheck.length; i++) {
                    listCheck[i].setChecked(false);
                }

                listSelectedPublisherString = new ArrayList<>();
                listSelectedPublisherInteger = new ArrayList<>();

                listSelectedCategoryInteger = new ArrayList<>();
                listSelectedCategoryString = new ArrayList<>();
            }
        });

        btnPubDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textPubDate1.getText().toString().equals("")) {
                    if (datePickerDialog1.getShowsDialog()) {
                        DatePickerPubDate.textView = dialog.findViewById(R.id.bookfiltre_text_date1);
                        DatePickerPubDate.button = btnPubDate1;
                        datePickerDialog1.show(getSupportFragmentManager(), "datePicker");
                    }
                } else {
                    textPubDate1.setText("");
                    pubDate1 = null;

                    btnPubDate1.setBackgroundResource(R.drawable.blue_border_rounded_green);
                    btnPubDate1.setText("Seç");
                }
            }
        });

        btnPubDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textPubDate2.getText().toString().equals("")) {
                    if (datePickerDialog1.getShowsDialog()) {
                        DatePickerPubDate.textView = dialog.findViewById(R.id.bookfiltre_text_date2);
                        DatePickerPubDate.button = btnPubDate2;
                        datePickerDialog1.show(getSupportFragmentManager(), "datePicker");
                    }
                } else {
                    textPubDate2.setText("");
                    pubDate2 = null;

                    btnPubDate2.setBackgroundResource(R.drawable.blue_border_rounded_green);
                    btnPubDate2.setText("Seç");
                }
            }
        });

        btnPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPublisher(textPublisher);
            }
        });

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategories(textCategory);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSelectedCategoryString = new ArrayList<>(listSelectedCategoryStringOpened);
                listSelectedCategoryInteger = new ArrayList<>(listSelectedCategoryIntegerOpened);
                listSelectedPublisherString = new ArrayList<>(listSelectedPublisherStringOpened);
                listSelectedPublisherInteger = new ArrayList<>(listSelectedPublisherIntegerOpened);

                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFilter = false;
                if (pubDate1 != null || pubDate2 != null || !textCategory.getText().toString().equals("") || !textPublisher.getText().toString().equals("")) {
                    localFilter = true;
                }

                if (!textPageMin.getText().toString().equals("")) {
                    page1 = Integer.parseInt(textPageMin.getText().toString());
                    localFilter = true;
                } else {
                    page1 = null;
                }
                if (!textPageMax.getText().toString().equals("")) {
                    page2 = Integer.parseInt(textPageMax.getText().toString());
                    localFilter = true;
                } else {
                    page2 = null;
                }

                boolean search = true;
                if (page1 != null && page2 != null) {
                    if (page1 > page2) {
                        Toast.makeText(ActivityUser.this, "1.Sayfa sayısı 2. Sayfa sayısını geçemez", Toast.LENGTH_SHORT).show();
                        search = false;
                    } else {
                        localFilter = true;
                    }
                } else if (!(page1 == null && page2 == null)) {
                    localFilter = true;
                }

                for (int i=0; i<listCheck.length; i++) {
                    filterCheckList[i] = listCheck[i].isChecked();
                    if (listCheck[i].isChecked()) {
                        localFilter = true;
                    }
                }

                if (search && localFilter) {
                    listCategoryBook.clear();

                    for (int i=0; i<listSelectedCategoryInteger.size(); i++) {
                        final boolean fin = (i+1 == listSelectedCategoryInteger.size()) ? true : false;
                        final Category localCat = listCategory.get(listSelectedCategoryInteger.get(i));

                        if (Integer.valueOf(localCat.getCount()) != 0) {
                            drCategoryBook.child(localCat.getKey()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    listCategoryBook.add(dataSnapshot.getKey().toString());

                                    if (fin && Integer.valueOf(localCat.getCount()) == categoryDonus) {
                                        isSelectFilters = localFilter;
                                        SearchBook();
                                        dialog.dismiss();
                                        categoryDonus = 0;
                                    } else if (fin) {
                                        categoryDonus++;
                                    }
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
                        } else {
                            if (fin) {
                                isSelectFilters = localFilter;
                                SearchBook();
                                dialog.dismiss();
                                categoryDonus = 0;
                            }
                        }
                    }

                    if (listSelectedCategoryInteger.size() == 0) {
                        isSelectFilters = localFilter;
                        SearchBook();
                        dialog.dismiss();
                    }
                }
                else if (!localFilter && eTextSearch.getText().toString().equals("")) {
                    // Filtre Yok ve Arama Sözcüğü Yok
                    // Burada List temizlenecek ve ilk boş haline dönecek!
                    Toast.makeText(ActivityUser.this, "Lütfen Arama Yapılacak Bilgileri Giriniz", Toast.LENGTH_SHORT).show();
                } else if (!localFilter && !eTextSearch.getText().toString().equals("")) {
                    // Filtre Yok ve Arama Sözcüğü var
                    if (textCategory.getText().toString().equals("")) {
                        listSelectedCategoryInteger = new ArrayList<>();
                        listSelectedCategoryIntegerYedek = new ArrayList<>();
                        listSelectedCategoryString = new ArrayList<>();
                        listSelectedCategoryStringYedek = new ArrayList<>();
                    }
                    if (textPublisher.getText().toString().equals("")) {
                        listSelectedPublisherString = new ArrayList<>();
                        listSelectedPublisherStringYedek = new ArrayList<>();
                        listSelectedPublisherInteger = new ArrayList<>();
                        listSelectedPublisherIntegerYedek = new ArrayList<>();
                    }

                    isSelectFilters = false;
                    SearchBook();
                    dialog.dismiss();
                }
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    private Integer categoryDonus = 0;
    boolean localFilter = false;

    private void clearFilter(TextView textPubDate1, TextView textPubDate2, Button btnPubDate1, Button btnPubDate2,
                             TextView textPublisher, TextView textCategory, TextView textPageMin, TextView textPageMax,
                             CheckBox[] listCheck) {
        textPubDate1.setText("");
        pubDate1 = null;
        btnPubDate1.setBackgroundResource(R.drawable.blue_border_rounded_green);
        btnPubDate1.setText("Seç");

        textPubDate2.setText("");
        pubDate2 = null;
        btnPubDate2.setBackgroundResource(R.drawable.blue_border_rounded_green);
        btnPubDate2.setText("Seç");

        textPublisher.setText("");
        textCategory.setText("");

        textPageMin.setText("");
        textPageMax.setText("");

        for(int i=0; i<listCheck.length; i++) {
            listCheck[i].setChecked(false);
        }

        listSelectedPublisherString = new ArrayList<>();
        listSelectedPublisherStringYedek = new ArrayList<>();
        listSelectedPublisherInteger = new ArrayList<>();
        listSelectedPublisherIntegerYedek = new ArrayList<>();

        listSelectedCategoryInteger = new ArrayList<>();
        listSelectedCategoryIntegerYedek = new ArrayList<>();
        listSelectedCategoryString = new ArrayList<>();
        listSelectedCategoryStringYedek = new ArrayList<>();
    }

    private void selectPublisher(final TextView textPublisher) {

        listSelectedPublisherIntegerYedek = new ArrayList<>(listSelectedPublisherInteger);
        listSelectedPublisherInteger.clear();

        listSelectedPublisherStringYedek = new ArrayList<>(listSelectedPublisherString);
        listSelectedPublisherString.clear();

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUser.this);
        builder.setTitle("Yayın Evleri");

        String[] localStringPub = new String[listPublisherString.size()];
        boolean[] localBoolPub = new boolean[listPublisherString.size()];


        for (int i = 0; i < listPublisherString.size(); i++) {
            localStringPub[i] = listPublisherString.get(i);

            if (!textPublisher.getText().equals("")) {
                if (listSelectedPublisherIntegerYedek.indexOf(i) != -1) {
                    localBoolPub[i] = true;
                    listSelectedPublisherInteger.add(i);
                    listSelectedPublisherString.add(listPublisherString.get(i));
                } else {
                    localBoolPub[i] = false;
                }
            }
        }

        builder.setMultiChoiceItems(localStringPub, localBoolPub, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    listSelectedPublisherString.add(listPublisherString.get(which));
                    listSelectedPublisherInteger.add(which);
                } else {
                    if (!listSelectedPublisherInteger.isEmpty())
                        listSelectedPublisherInteger.remove(listSelectedPublisherInteger.indexOf(which));
                    if (!listSelectedPublisherString.isEmpty())
                        listSelectedPublisherString.remove(listSelectedPublisherString.indexOf(listPublisherString.get(which)));
                }
            }
        });

        builder.setPositiveButton("Seç", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textPublisher.setText("");

                Collections.sort(listSelectedPublisherString, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String p1 = (String) o1;
                        String p2 = (String) o2;
                        return p1.compareToIgnoreCase(p2);
                    }
                });

                for (int i = 0; i < listSelectedPublisherString.size(); i++) {
                    textPublisher.setText(textPublisher.getText() + String.valueOf(listSelectedPublisherString.get(i)));

                    if (i != listSelectedPublisherString.size() - 1)
                        textPublisher.setText(textPublisher.getText().toString() + ", ");
                }
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listSelectedPublisherInteger = listSelectedPublisherIntegerYedek;
                listSelectedPublisherString = listSelectedPublisherStringYedek;
            }
        });

        AlertDialog localDialog = builder.create();
        localDialog.setCancelable(false);
        localDialog.setCanceledOnTouchOutside(false);
        localDialog.show();
    }

    List<String> listCategoryBook = new ArrayList<>();
    private void selectCategories(final TextView textCategories) {
        listSelectedCategoryIntegerYedek = new ArrayList<>(listSelectedCategoryInteger);
        listSelectedCategoryInteger.clear();

        listSelectedCategoryStringYedek = new ArrayList<>(listSelectedCategoryString);
        listSelectedCategoryString.clear();

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUser.this);
        builder.setTitle("Kategoriler");

        String[] localStringCat = new String[listCategoryString.size()];
        boolean[] localBoolCat = new boolean[listCategoryString.size()];

        for (int i=0; i<listCategoryString.size(); i++) {
            localStringCat[i] = listCategoryString.get(i);

            if (listSelectedCategoryIntegerYedek.indexOf(i) != -1) {
                localBoolCat[i] = true;
                listSelectedCategoryInteger.add(i);
                listSelectedCategoryString.add(listCategoryString.get(i));
            }
            else
                localBoolCat[i] = false;
        }

        builder.setMultiChoiceItems(localStringCat, localBoolCat, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    listSelectedCategoryString.add(listCategoryString.get(which));
                    listSelectedCategoryInteger.add(which);
                } else {
                    if (!listSelectedCategoryInteger.isEmpty())
                        listSelectedCategoryInteger.remove(listSelectedCategoryInteger.indexOf(which));
                    if (!listSelectedCategoryString.isEmpty())
                        listSelectedCategoryString.remove(listSelectedCategoryString.indexOf(listCategoryString.get(which)));
                }
            }
        });

        builder.setPositiveButton("Seç", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textCategories.setText("");

                Collections.sort(listSelectedCategoryString, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        String p1 = (String) o1;
                        String p2 = (String) o2;
                        return p1.compareToIgnoreCase(p2);
                    }
                });

                for (int i=0; i<listSelectedCategoryString.size(); i++) {
                    textCategories.setText(textCategories.getText() + String.valueOf(listSelectedCategoryString.get(i)));

                    if (i != listSelectedCategoryString.size() -1)
                        textCategories.setText(textCategories.getText().toString() + ", ");
                }
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listSelectedCategoryInteger = listSelectedCategoryIntegerYedek;
                listSelectedCategoryString = listSelectedCategoryStringYedek;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}