package com.furkanyilmaz.kitapoyla.admin.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBookList extends BaseAdapter {
    private List<Book> listBook;
    private Context context;
    private LayoutInflater layoutInflater;

    DatabaseReference firebaseDatabase;

    public AdapterBookList(List<Book> listBook, Context context) {
        this.listBook = listBook;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("BookList");
    }

    @Override
    public int getCount() {
        return listBook.size();
    }

    @Override
    public Book getItem(int position) {
        return listBook.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View customView = layoutInflater.inflate(R.layout.item_list_book, null);

        final TextView textViewName = customView.findViewById(R.id.itemlistbook_text_name);
        final TextView textViewISBN = customView.findViewById(R.id.itemlistbook_text_isbn);
        final TextView textViewComment = customView.findViewById(R.id.itemlistbook_text_comment);
        final ImageView imageViewProfile = customView.findViewById(R.id.itemlistbook_profileimage);
        final RatingBar ratingBar = customView.findViewById(R.id.ratingBar);

        textViewName.setText(getItem(position).getName());
        textViewISBN.setText(getItem(position).getISBN());

        if (!TextUtils.isEmpty(getItem(position).getCommentCount())) {
            textViewComment.setText(getItem(position).getCommentCount() + " Yorum");
        } else {
            textViewComment.setText("0 Yorum");
        }

        if (!TextUtils.isEmpty(getItem(position).getStarRate())) {
            ratingBar.setRating(Float.parseFloat(getItem(position).getStarRate()));
        }

        Picasso.get()
                .load(getItem(position).getImage())
                .resize(100, 100)
                .error(R.drawable.ic_default_book_2)
                .placeholder(R.drawable.ic_default_book_2)
                .centerCrop().into(imageViewProfile);

        return customView;
    }
}