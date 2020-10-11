package com.furkanyilmaz.kitapoyla.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.furkanyilmaz.items.Book;
import com.furkanyilmaz.items.myEnums;
import com.furkanyilmaz.kitapoyla.ActivityLogin;
import com.furkanyilmaz.kitapoyla.R;
import com.furkanyilmaz.kitapoyla.admin.fragments.BookFragment;
import com.furkanyilmaz.kitapoyla.custom.myStaticObj;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView textName, textEmail;
    private CircleImageView imageProfile;

    private NavigationView navigationView;
    private NavController navController;
    private FirebaseAuth auth;
    private DrawerLayout drawerLayout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_users,
                R.id.nav_users, R.id.nav_books, R.id.nav_categories,
                R.id.nav_publisher, R.id.nav_logout)
                .setDrawerLayout(drawerLayout).build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        init();
    }

    private void init() {
        View navHandler =navigationView.getHeaderView(0);

        textName = (TextView) navHandler.findViewById(R.id.adminmain_slide_name);
        textEmail = (TextView) navHandler.findViewById(R.id.adminmain_slide_email);
        imageProfile = (CircleImageView) navHandler.findViewById(R.id.adminmain_slide_image);

        if (myStaticObj.loginUser != null) {
            textName.setText(myStaticObj.loginUser.getNameSurname());
            textEmail.setText(myStaticObj.loginUser.getEmail());

            if (!TextUtils.isEmpty(myStaticObj.loginUser.getImage())) {
                Picasso.get().load(myStaticObj.loginUser.getImage()).resize(100, 100).centerCrop().into(imageProfile);
            } else {
                if (myStaticObj.loginUser.getGender().equals(myEnums.Gender.FEMALE.toString()))
                    imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));
                else imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));
            }
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();

                    drawerLayout.closeDrawer(GravityCompat.START);

                    startActivity(new Intent(AdminActivity.this, ActivityLogin.class));
                    finish();
                } else if (id == R.id.nav_users || id == R.id.nav_books || id == R.id.nav_categories || id == R.id.nav_publisher) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(id);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}