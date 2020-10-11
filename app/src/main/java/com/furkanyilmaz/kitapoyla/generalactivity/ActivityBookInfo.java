package com.furkanyilmaz.kitapoyla.generalactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.items.Comment;
import com.furkanyilmaz.items.Publisher;
import com.furkanyilmaz.items.User;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.add_update.Activity_AddBook;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ActivityBookInfo extends AppCompatActivity {

    public static Book book;
    private Book selectBook;

    private ImageView imageBack, imageBook;
    private RatingBar ratingBar;
    private TextView textComRate, textCommentCount;
    private TextView textISBN, textName, textPublishDate, textPublisher, textPageNumber, textCategori, textAuthor, textInfo;
    private Button buttonComments, buttonToComment, buttonDelete;

    private DatabaseReference drBook, drUsers, drComments, drBookComments, drUserComments, drUserBookComment;
    private DatabaseReference drPublishers, drCategoryList, drBookCategory, drCategoryBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        selectBook = book;

        drUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        drBook = FirebaseDatabase.getInstance().getReference().child("Books");
        drComments = FirebaseDatabase.getInstance().getReference().child("Comments");
        drBookComments = FirebaseDatabase.getInstance().getReference().child("CommentsBook");
        drUserComments = FirebaseDatabase.getInstance().getReference().child("CommentsUser");

        drUserBookComment = FirebaseDatabase.getInstance().getReference().child("CommentUserBook");

        drBookCategory = FirebaseDatabase.getInstance().getReference().child("CategoryBook").child("Book-Category");
        drCategoryBook = FirebaseDatabase.getInstance().getReference().child("CategoryBook").child("Category-Book");

        drPublishers = FirebaseDatabase.getInstance().getReference().child("PublisherList");
        drCategoryList = FirebaseDatabase.getInstance().getReference().child("CategoryList");

        init();
    }

    private int catDonus = 0;

    @SuppressLint("SetTextI18n")
    private void init() {
        imageBack = (ImageView) findViewById(R.id.bookinfo_image_back);

        imageBook = (ImageView) findViewById(R.id.bookinfo_image);
        ratingBar = (RatingBar) findViewById(R.id.bookinfo_ratingbar);
        textComRate = (TextView) findViewById(R.id.bookinfo_text_comRate);
        textCommentCount = (TextView) findViewById(R.id.bookinfo_text_commentcount);

        textISBN = (TextView) findViewById(R.id.bookinfo_text_isbn);
        textName = (TextView) findViewById(R.id.bookinfo_text_name);
        textPublishDate = (TextView) findViewById(R.id.bookinfo_text_publishdate);
        textPublisher = (TextView) findViewById(R.id.bookinfo_text_publisher);
        textPageNumber = (TextView) findViewById(R.id.bookinfo_text_pagenumber);
        textCategori = (TextView) findViewById(R.id.bookinfo_text_category);
        textAuthor = (TextView) findViewById(R.id.bookinfo_text_author);
        textInfo = (TextView) findViewById(R.id.bookinfo_text_info);

        buttonComments = (Button) findViewById(R.id.bookinfo_button_comments);
        buttonToComment = (Button) findViewById(R.id.bookinfo_button_tocomment);
        buttonDelete = (Button) findViewById(R.id.bookinfo_button_delete);
        if (myStaticObj.loginUser.getAuthorit().equals(myEnums.Authority.ADMIN.toString())) {
            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    catDonus = 0;
                    drBookCategory.child(selectBook.getKey()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            final String catKey = dataSnapshot.getKey();
                            catDonus++;

                            drCategoryBook.child(catKey).child(selectBook.getKey()).removeValue();
                            drCategoryList.child(catKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Category lCat = dataSnapshot.getValue(Category.class);
                                    drCategoryList.child(catKey).child(Category.Scount).setValue(String.valueOf(Integer.valueOf(lCat.getCount()) - 1));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            if (catDonus == Integer.valueOf(selectBook.getCategoryCount())) {
                                drBookCategory.child(selectBook.getKey()).removeValue();
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



                    onBackPressed();
                }
            });
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        Picasso.get().load(selectBook.getImage()).resize(100, 100).error(R.drawable.ic_default_book_2).placeholder(R.drawable.ic_default_book_2).centerCrop().into(imageBook);

        ratingBar.setRating(Float.parseFloat(selectBook.getStarRate()));
        textComRate.setText(selectBook.getStarRate());
        textCommentCount.setText((TextUtils.isEmpty(selectBook.getCommentCount()) ? "0" : selectBook.getCommentCount()) + " Yorum");

        textISBN.setText(selectBook.getISBN());
        textName.setText(selectBook.getName());
        textPublishDate.setText(selectBook.getPublishDate());

        textPageNumber.setText(selectBook.getPageNumber());

        textAuthor.setText(selectBook.getAuthors());
        textInfo.setText(Html.fromHtml(selectBook.getInformation()));

        buttonComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityBookComments.selectedBook = selectBook;
                startActivity(new Intent(ActivityBookInfo.this, ActivityBookComments.class));
            }
        });

        buttonToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toComment();
            }
        });

        firebaseDBLoad();
        isComment();

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void firebaseDBLoad() {
        drBookCategory.child(selectBook.getKey()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String catKey = dataSnapshot.getKey();

                drCategoryList.child(catKey).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Category localCategory = dataSnapshot.getValue(Category.class);

                        if (textCategori.getText().toString().equals("")) {
                            textCategori.setText(localCategory.getName());
                        } else {
                            textCategori.setText(textCategori.getText() + ", " + localCategory.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        return;
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

        drPublishers.child(selectBook.getPublisherId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Publisher localPublisher = dataSnapshot.getValue(Publisher.class);

                textPublisher.setText(localPublisher.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
    }

    private boolean isCom = false;
    private String comKey;
    private void isComment() {
        drUserBookComment.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(selectBook.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (!TextUtils.isEmpty(dataSnapshot.getValue().toString())) {
                        buttonToComment.setText("Yorumu Düzenle");
                        buttonToComment.setTextSize(15);

                        comKey = dataSnapshot.getValue().toString().substring(1, dataSnapshot.getValue().toString().length()-6);
                        isCom = true;
                    }
                } catch (NullPointerException exception) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    Dialog dialog;
    String toComId;
    private boolean isSaveClickable = false;
    private boolean isSaveClick = false;

    private String defaultStars;

    private void toComment() {
        dialog = new Dialog(ActivityBookInfo.this);
        dialog.setContentView(R.layout.popup_to_comment);

        final RatingBar localRatingBar = (RatingBar) dialog.findViewById(R.id.popuptocom_ratingbar);


        final Button btnKaydet = (Button) dialog.findViewById(R.id.popuptocom_button_tocomment);
        final Button btnIptal = (Button) dialog.findViewById(R.id.popuptocom_button_exit);

        final TextView textCom = (TextView) dialog.findViewById(R.id.popuptocom_edittext_comment);

        if (!isCom) {
            toComId = drComments.push().getKey();
            isSaveClickable = true;
        } else {
            toComId = comKey;
            drComments.child(toComId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Comment localCom = dataSnapshot.getValue(Comment.class);
                    localCom.setKey(toComId);

                    btnKaydet.setText("Kaydet");

                    textCom.setText(localCom.getComment());
                    localRatingBar.setRating(Float.parseFloat(localCom.getStarNum()));
                    defaultStars = String.valueOf(Float.parseFloat(localCom.getStarNum()));

                    isSaveClickable = true;
                    if (isSaveClick) {
                        isSaveClick = false;
                        toCommentSaveCom(textCom, localRatingBar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                toCommentSaveCom(textCom, localRatingBar);
            }
        });

        btnIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void toCommentSaveCom(TextView textCom, RatingBar localRatingBar) {
        if (isSaveClickable) {
            boolean isThere = false;

            if (!textCom.getText().toString().equals("") && localRatingBar.getRating() != 0.0f) {
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (!isCom) {
                    final Map bookComMap = new HashMap();
                    bookComMap.put(Comment.sBookId, selectBook.getKey());
                    bookComMap.put(Comment.sUserId, userId);
                    bookComMap.put(Comment.sPublishDate, ServerValue.TIMESTAMP);
                    bookComMap.put(Comment.sComment, textCom.getText().toString());
                    bookComMap.put(Comment.sStarNum, String.valueOf(localRatingBar.getRating()));

                    drBookComments.child(selectBook.getKey()).child(toComId).setValue("true");
                    drUserComments.child(userId).child(toComId).setValue(true);
                    drComments.child(toComId).setValue(bookComMap);

                    float liveRate = (Float.parseFloat(selectBook.getStarRate()) * Float.parseFloat(selectBook.getCommentCount())) + localRatingBar.getRating();
                    liveRate = liveRate / (Integer.parseInt(selectBook.getCommentCount()) + 1);

                    selectBook.setCommentCount(String.valueOf(Integer.parseInt(selectBook.getCommentCount()) + 1));
                    selectBook.setStarRate(String.format("%.1f", liveRate).replace(",", "."));

                    drBook.child(selectBook.getKey()).child(Book.ScommentCount).setValue(selectBook.getCommentCount());
                    drBook.child(selectBook.getKey()).child(Book.SstarRate).setValue(selectBook.getStarRate());

                    ratingBar.setRating(Float.parseFloat(selectBook.getStarRate()));
                    textComRate.setText(selectBook.getStarRate());
                    textCommentCount.setText((TextUtils.isEmpty(selectBook.getCommentCount()) ? "0" : selectBook.getCommentCount()) + " Yorum");

                    drUserBookComment.child(userId).child(selectBook.getKey()).child(toComId).setValue("true");
                    //                    myStaticObj.loginUser.setComCount(String.valueOf(Integer.parseInt(myStaticObj.loginUser.getComCount()) + 1));
                    //                    drUser.child(userId).child(User.ScomCount).setValue(myStaticObj.loginUser.getComCount());
                } else {
                    drComments.child(toComId).child(Comment.sComment).setValue(textCom.getText().toString());
                    drComments.child(toComId).child(Comment.sStarNum).setValue(String.valueOf(localRatingBar.getRating()));

                    float liveRate = ((Float.parseFloat(selectBook.getStarRate()) * Float.parseFloat(selectBook.getCommentCount())) - Float.parseFloat(defaultStars)) + localRatingBar.getRating();
                    liveRate = liveRate / (Integer.parseInt(selectBook.getCommentCount()));

                    selectBook.setStarRate(String.format("%.1f", liveRate).replace(",", "."));
                    ratingBar.setRating(Float.parseFloat(selectBook.getStarRate()));
                    textComRate.setText(selectBook.getStarRate());

                    drBook.child(selectBook.getKey()).child(Book.SstarRate).setValue(selectBook.getStarRate());
                }

                Toast.makeText(ActivityBookInfo.this, "Yorum İçin Teşekkürler :)", Toast.LENGTH_SHORT).show();

            } else {
                isThere = true;
                Toast.makeText(ActivityBookInfo.this, "Eksik Yerleri Doldurunuz!", Toast.LENGTH_SHORT).show();
            }

            if (!isThere) dialog.dismiss();
        } else {
            Toast.makeText(ActivityBookInfo.this, "Lütfen Bekleyiniz!", Toast.LENGTH_SHORT).show();
            isSaveClick = true;
        }
    }
}