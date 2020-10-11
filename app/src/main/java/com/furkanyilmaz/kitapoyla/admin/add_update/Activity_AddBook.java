package com.furkanyilmaz.kitapoyla.admin.add_update;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.furkanyilmaz.customfonts.DatePickerFragment;
import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.items.Publisher;
import com.furkanyilmaz.items.Sizes;
import com.furkanyilmaz.items.User;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.adapters.Book_PublisherListAdapter;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class Activity_AddBook extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    private ProgressDialog progressDialog;
    private byte[] profile_image;

    private DialogFragment datePickerDialog;

    private DatabaseReference drPublishers, drCategoryList;
    private List<Publisher> listPublisher = new ArrayList<>();
    private List<Category> listCategory = new ArrayList<>();
    List<String> listCategoryString = new ArrayList<>();
    List<Integer> listSelectedCategoryInteger = new ArrayList<>(), listSelectedCategoryIntegerYedek = new ArrayList<>();
    List<String> listSelectedCategoryString = new ArrayList<>(), listSelectedCategoryStringYedek = new ArrayList<>();

    private DatabaseReference firebaseDatabaseBook, firebaseDBCategory, firebaseDBPublisher, drSizes;
    private FirebaseAuth auth;

    private ImageView imageViewBook;
    private StorageReference storageReference;

    private TextView textISBN, textName, textPublishDate, textPublisher, textPageNumber, textAuthor, textInformation, textCategory;
    private RelativeLayout relativeImage, relativeISBN, relativeName, relativePublisher, relativePageNumber, relativeAuthor, relativeInformation, relativeCategory;
    private LinearLayout linearPublishDate;
    private Publisher selectPublisher;

    private Button buttonSave, buttonSelectImage;

    List<String> listBookName = new ArrayList<>();
    List<String> listBookISBN = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addupdate_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.add_book));


        auth = FirebaseAuth.getInstance();
        firebaseDatabaseBook = FirebaseDatabase.getInstance().getReference().child("Books");
        firebaseDBCategory = FirebaseDatabase.getInstance().getReference().child("CategoryBook");
        firebaseDBPublisher = FirebaseDatabase.getInstance().getReference().child("PublisherBook");

        drCategoryList = FirebaseDatabase.getInstance().getReference().child("CategoryList");
        drPublishers = FirebaseDatabase.getInstance().getReference().child("PublisherList");

        drSizes = FirebaseDatabase.getInstance().getReference().child("Sizes");

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDBLoad();

        init();
    }

    private Integer bookSize;
    private void firebaseDBLoad() {
        drPublishers.addChildEventListener(new ChildEventListener() {
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
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (int i = 0; i < listPublisher.size(); i++) {
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
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < listPublisher.size(); i++) {
                    if (listPublisher.get(i).getKey().equals(dataSnapshot.getKey())) {
                        listPublisher.remove(i);
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

        firebaseDatabaseBook.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Book localBook = dataSnapshot.getValue(Book.class);
                localBook.setKey(dataSnapshot.getKey());

                listBookName.add(localBook.getName());
                listBookISBN.add(localBook.getISBN());
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

        drSizes.child(Sizes.SbookSize).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookSize = Integer.valueOf(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {

        textISBN = (TextView) findViewById(R.id.addbook_text_isbn);
        textName = (TextView) findViewById(R.id.addbook_text_name);
        textPublishDate = (TextView) findViewById(R.id.addbook_text_publishdate);
        textPublisher = (TextView) findViewById(R.id.addbook_text_publisher);
        textPageNumber = (TextView) findViewById(R.id.addbook_text_pagenumber);
        textAuthor = (TextView) findViewById(R.id.addbook_text_author);
        textInformation = (TextView) findViewById(R.id.addbook_text_info);
        textCategory = (TextView) findViewById(R.id.addbook_text_category);

        buttonSave = (Button) findViewById(R.id.addbook_button_save);
        buttonSelectImage = (Button) findViewById(R.id.addbook_button_selecimage);

        relativeISBN = (RelativeLayout) findViewById(R.id.addbook_relative_isbn);
        relativeImage = (RelativeLayout) findViewById(R.id.addbook_relative_image);
        relativeName = (RelativeLayout) findViewById(R.id.addbook_relative_name);
        relativePublisher = (RelativeLayout) findViewById(R.id.addbook_relative_publisher);
        relativePageNumber = (RelativeLayout) findViewById(R.id.addbook_relative_pagenumber);
        relativeAuthor = (RelativeLayout) findViewById(R.id.addbook_relative_author);
        relativeInformation = (RelativeLayout) findViewById(R.id.addbook_relative_info);
        relativeCategory = (RelativeLayout) findViewById(R.id.addbook_relative_category);
        linearPublishDate = (LinearLayout) findViewById(R.id.addbook_relative_publishdate);


        datePickerDialog = new DatePickerFragment();
        DatePickerFragment.changeDate(Calendar.getInstance().get(Calendar.YEAR));
//        textPublishDate.setInputType(InputType.TYPE_NULL);

        linearPublishDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePickerDialog.getShowsDialog()) {
                    datePickerDialog.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });
//        textPublishDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    if (datePickerDialog.getShowsDialog()) {
//                        datePickerDialog.show(getSupportFragmentManager(), "datePicker");
//                    }
//                }
//            }
//        });

        textPublisher.setInputType(InputType.TYPE_NULL);
        textPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPublisher();
            }
        });
        textPublisher.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectPublisher();
                }
            }
        });

        relativeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSelectedCategoryIntegerYedek = new ArrayList<>(listSelectedCategoryInteger);
                listSelectedCategoryInteger.clear();

                listSelectedCategoryStringYedek = new ArrayList<>(listSelectedCategoryString);
                listSelectedCategoryString.clear();

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_AddBook.this);
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
                        textCategory.setText("");

                        Collections.sort(listSelectedCategoryString, new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                String p1 = (String) o1;
                                String p2 = (String) o2;
                                return p1.compareToIgnoreCase(p2);
                            }
                        });

                        for (int i=0; i<listSelectedCategoryString.size(); i++) {
                            textCategory.setText(textCategory.getText() + String.valueOf(listSelectedCategoryString.get(i)));

                            if (i != listSelectedCategoryString.size() -1)
                                textCategory.setText(textCategory.getText().toString() + ", ");
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
        });

        imageViewBook = (ImageView) findViewById(R.id.addbook_image);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Kitabın Resmini Seçiniz"), GALLERY_PICK);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStaticObj.setValidColor(Activity_AddBook.this, relativeImage, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativeISBN, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativeName, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativePageNumber, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativeAuthor, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativeInformation, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativePublisher, true);
                myStaticObj.setValidColor(Activity_AddBook.this, relativeCategory, true);
                myStaticObj.setValidColor(Activity_AddBook.this, linearPublishDate, true);

                boolean isISBN = TextUtils.isEmpty(textISBN.getText().toString());
                boolean isName = TextUtils.isEmpty(textName.getText().toString());
                boolean isPageNumber = TextUtils.isEmpty(textPageNumber.getText().toString());
                boolean isAuthor = TextUtils.isEmpty(textAuthor.getText().toString());
                boolean isInfo = TextUtils.isEmpty(textInformation.getText().toString());
                boolean isPublisher = selectPublisher == null;
                boolean isCategory = listSelectedCategoryString.size() == 0;
                boolean isPublishDate = TextUtils.isEmpty(textPublishDate.getText());

                boolean isSave = true;

                if (profile_image == null) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeImage, false);
                }

                if (isISBN) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeISBN, false);
                } else if (listBookISBN.indexOf(textISBN.getText().toString()) != -1) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeISBN, false);
                }

                if (isName) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeName, false);
                } else if (listBookName.indexOf(textName.getText().toString()) != -1) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeName, false);
                }

                if (isPageNumber) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativePageNumber, false);
                }
                if (isAuthor) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeAuthor, false);
                }
                if (isInfo) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeInformation, false);
                }
                if (isPublisher) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativePublisher, false);
                }
                if (isCategory) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, relativeCategory, false);
                }
                if (isPublishDate) {
                    isSave = false;
                    myStaticObj.setValidColor(Activity_AddBook.this, linearPublishDate, false);
                }


                if (isSave) {
                    saveBook(textISBN.getText().toString(),
                            textName.getText().toString(),
                            textPublishDate.getText().toString(),
                            textPageNumber.getText().toString(),
                            textInformation.getText().toString(),
                            textAuthor.getText().toString());
                } else {
                    Toast.makeText(Activity_AddBook.this, "Lütfen Bilgilerin Tamamını Doldurunuz!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectPublisher() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Activity_AddBook.this);
        builderSingle.setTitle(getString(R.string.publisher));

        final ArrayAdapter arrayAdapter = new Book_PublisherListAdapter(getApplicationContext(), listPublisher);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = ((Publisher) arrayAdapter.getItem(which)).getName();
                textPublisher.setText(strName);

                selectPublisher = ((Publisher) arrayAdapter.getItem(which));
            }
        });

        builderSingle.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                progressDialog = new ProgressDialog(Activity_AddBook.this);
                progressDialog.setTitle("Profil Resmi Ayarlanıyor!");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                File thumb_filePath = new File(resultUri.getPath());


                Bitmap thumb_bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(100).compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                profile_image = baos.toByteArray();

                imageViewBook.setImageBitmap(thumb_bitmap);

                progressDialog.hide();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void saveBook(String ISBN, String Name, final String PublishDate, String Page, String Info, String Author) {
        progressDialog = new ProgressDialog(Activity_AddBook.this);
        progressDialog.setTitle(getString(R.string.completing_the_registration_process));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String uID = firebaseDatabaseBook.push().getKey();
        final HashMap<String, String> bookMap = new HashMap<>();

        bookMap.put(Book.SISBN, ISBN);
        bookMap.put(Book.Sname, Name);
        bookMap.put(Book.SpublishDate, PublishDate);
        bookMap.put(Book.SpublisherId, selectPublisher.getKey());
        bookMap.put(Book.SpageNumber, Page);
        bookMap.put(Book.Sinformation, Info);
        bookMap.put(Book.Sauthors, Author);
        bookMap.put(Book.SstarRate, "0");
        bookMap.put(Book.ScommentCount, "0");
        bookMap.put(Book.ScategoryCount, String.valueOf(listSelectedCategoryInteger.size()));

        if (profile_image != null) {
            final StorageReference filepath = storageReference.child("book_images").child(uID + ".jpg");

            UploadTask uploadTask = filepath.putBytes(profile_image);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uriImage) {
                                bookMap.put(User.Simage, uriImage.toString());
                                firebaseDatabaseBook.child(uID).setValue(bookMap);

                                for (int i=0; i<listSelectedCategoryInteger.size(); i++) {
                                    // Category-Book Child
                                    String categoryKey = listCategory.get(listSelectedCategoryInteger.get(i)).getKey();
                                    firebaseDBCategory.child("Category-Book").child(categoryKey).child(uID).setValue("true");

                                    for (int k = 0; k < listCategory.size(); k++) {
                                        if (listCategory.get(k).getKey().equals(categoryKey)) {
                                            listCategory.get(k).setCount(String.valueOf(Integer.parseInt(listCategory.get(k).getCount())+1));

                                            drCategoryList.child(listCategory.get(k).getKey()).child(Category.Scount).setValue(listCategory.get(k).getCount());
                                            break;
                                        }
                                    }
                                    // Book-Category Child
                                    firebaseDBCategory.child("Book-Category").child(uID).child(categoryKey).setValue("true");
                                }

                                firebaseDBPublisher.child("Publisher-Book").child(selectPublisher.getKey()).child(uID).setValue("true");
                                firebaseDBPublisher.child("Book-Publisher").child(uID).child(selectPublisher.getKey()).setValue("true");
                                selectPublisher.setCount(String.valueOf(Integer.parseInt(selectPublisher.getCount())+1));
                                drPublishers.child(selectPublisher.getKey()).child(Publisher.Scount).setValue(selectPublisher.getCount());
                                if (bookSize != null) {
                                    bookSize++;
                                    drSizes.child(Sizes.SbookSize).setValue(bookSize);
                                }

                                progressDialog.hide();

                                Toast.makeText(Activity_AddBook.this, getString(R.string.registration_completed), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        progressDialog.hide();
                        Toast.makeText(Activity_AddBook.this, getString(R.string.upload_image_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

