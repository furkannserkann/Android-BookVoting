package com.furkanyilmaz.kitapoyla.admin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.items.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsersList extends BaseAdapter {


    private List<User> listUSer;
    private Context context;
    private LayoutInflater layoutInflater;

    public AdapterUsersList(List<User> listUSer, Context context) {
        this.listUSer = listUSer;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listUSer.size();
    }

    @Override
    public User getItem(int position) {
        return listUSer.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View customView = layoutInflater.inflate(R.layout.item_list_user, null);

        TextView textViewName = (TextView) customView.findViewById(R.id.itemlistuser_text_name);
        TextView textViewYetki = (TextView) customView.findViewById(R.id.itemlistuser_text_yetki);
        TextView textViewEmail = (TextView) customView.findViewById(R.id.itemlistuser_text_mail);
        CircleImageView imageViewProfile = (CircleImageView) customView.findViewById(R.id.itemlistuser_profileimage);

        textViewName.setText(getItem(position).getNameSurname());
        textViewYetki.setText(getItem(position).getAuthorit());
        textViewEmail.setText(getItem(position).getEmail());

        if (!TextUtils.isEmpty(getItem(position).getImage())) {
            Picasso.get().load(getItem(position).getImage()).resize(100, 100).centerCrop().into(imageViewProfile);
        } else {
            if (getItem(position).getGender().equals(myEnums.Gender.FEMALE.toString()))
                imageViewProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_female));
            else
                imageViewProfile.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_male));
        }

        return customView;
    }
}
