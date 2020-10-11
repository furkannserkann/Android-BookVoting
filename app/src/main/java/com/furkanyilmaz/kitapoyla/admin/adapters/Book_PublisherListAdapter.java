package com.furkanyilmaz.kitapoyla.admin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.furkanyilmaz.items.Publisher;
import com.furkanyilmaz.kitapoyla.R;

import java.util.List;

public class Book_PublisherListAdapter extends ArrayAdapter<Publisher> {
    private final Context context;
    private final List<Publisher> userList;

    public Book_PublisherListAdapter(@NonNull Context context, @NonNull List<Publisher> objects) {
        super(context, R.layout.item_list_category, objects);
        userList = objects;
        this.context = context;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_list_category, parent, false);

        TextView bookCount = rowView.findViewById(R.id.itemlistcategory_text_bookcount);
        TextView userName = rowView.findViewById(R.id.itemlistcategory_text_name);
        Publisher user = userList.get(position);

        userName.setText(user.getName());
        bookCount.setVisibility(View.GONE);

        return rowView;
    }
}