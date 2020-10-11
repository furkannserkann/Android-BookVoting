package com.furkanyilmaz.kitapoyla.generalactivity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.furkanyilmaz.customfonts.TextView_Poppins_Regular;
import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.Comment;
import com.furkanyilmaz.items.User;
import com.furkanyilmaz.kitapoyla.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterBookComments extends BaseAdapter {
    private List<Comment> listComment;
    private List<User> listUser;
    private List<Book> listBook;
    private Book selectBook;
    private Context context;
    private LayoutInflater layoutInflater;

//    DatabaseReference firebaseDatabase;

    public AdapterBookComments(List<Comment> listComment, List<User> listUser, Book selectBook, Context context) {
        this.listComment = listComment;
        this.listUser = listUser;
        this.selectBook = selectBook;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("BookList");
    }

    public AdapterBookComments(List<Comment> listComment, List<Book> listBook, Context context) {
        this.listComment = listComment;
        this.listBook = listBook;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("BookList");
    }

    @Override
    public int getCount() {
        return listComment.size();
    }

    @Override
    public Comment getItem(int position) {
        return listComment.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View customView = layoutInflater.inflate(R.layout.item_list_comment, null);

        final TextView textPubDate = customView.findViewById(R.id.itemlistcom_text_date);
        final TextView textBookName = customView.findViewById(R.id.itemlistcom_text_bookname);
        final TextView textBookISBN = customView.findViewById(R.id.itemlistcom_text_bookisbn);
        final TextView textComment = customView.findViewById(R.id.itemlistcom_text_comment);

        final RatingBar ratingBar = customView.findViewById(R.id.itemlistcom_ratingbar);

        textPubDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(getItem(position).getPublishDate())));

        if (listUser != null) {
            textBookName.setText(listUser.get(position).getNameSurname());
            textBookISBN.setText(""/*selectBook.getISBN()*/);
        } else if (listBook != null) {
            textBookName.setText(listBook.get(position).getName());
            textBookISBN.setText("ISBN: " + listBook.get(position).getISBN());
        }
        textComment.setText(getItem(position).getComment());

        if (!TextUtils.isEmpty(getItem(position).getStarNum())) {
            ratingBar.setRating(Float.parseFloat(getItem(position).getStarNum()));
        }

        return customView;
    }
}