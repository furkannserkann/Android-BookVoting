package com.furkanyilmaz.kitapoyla.admin.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.furkanyilmaz.items.Category;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class AdapterCategoryList extends BaseAdapter {
    private List<Category> listCategory;
    private Context context;
    private LayoutInflater layoutInflater;

    DatabaseReference firebaseDatabase;

    public AdapterCategoryList(List<Category> listCategory, Context context) {
        this.listCategory = listCategory;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("CategoryList");
    }

    @Override
    public int getCount() {
        return listCategory.size();
    }

    @Override
    public Category getItem(int position) {
        return listCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View customView = layoutInflater.inflate(R.layout.item_list_category, null);

        final TextView textViewName = (TextView) customView.findViewById(R.id.itemlistcategory_text_name);
        final TextView textViewCount = (TextView) customView.findViewById(R.id.itemlistcategory_text_bookcount);

        textViewName.setText(getItem(position).getName());
        textViewCount.setText("(" + getItem(position).getCount() + " Kitap)");

        return customView;
    }
}