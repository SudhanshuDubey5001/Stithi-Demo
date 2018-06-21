package stithi.my.com.stithi.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.Utils.FirebaseOperations;
import stithi.my.com.stithi.ViewProfileAdapter.ViewProfileAdapter;

public class ViewProfiles extends AppCompatActivity {

    ViewProfileAdapter adapter;
    DrawerLayout drawerLayout;
    EditText searchText;
    private static boolean searchPressed=false;
    FirebaseOperations operations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profiles);
        adapter = new ViewProfileAdapter();

//      setting up navigation drawer--------->
        setupNavigationDrawer();

//      fetch all users------------------------------------------------->
        operations = new FirebaseOperations(this);
        operations.fetchProfiles(adapter);

        searchText = findViewById(R.id.search_text);
        ImageButton searchButton = findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPressed=true;
                String desiredName=searchText.getText().toString();
                operations.performSearch(desiredName,adapter);
                searchText.setText("");
                searchText.setFocusable(false);
            }
        });
    }

    public void setUpRecycler() {
//      setting up recycler view----------------------------------------->
        RecyclerView viewProfileRecycler = findViewById(R.id.recyclerView);
        viewProfileRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setUpBaseImages(this);
        viewProfileRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_showprofile);

        Toolbar toolbar = findViewById(R.id.toolbar_viewprofile);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);

        NavigationView navigationView = findViewById(R.id.nav_showProfile);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.myprofile) {
                    if (ConstantClass.auth == null) {
                        Toast.makeText(ViewProfiles.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent in = new Intent(ViewProfiles.this, ProfileActivity.class);
                        in.putExtra(ConstantClass.WHERETAG, "login_activity");
                        in.putExtra(ConstantClass.USERTAG, ConstantClass.LOGGEDIN_USER);
                        startActivity(in);
                        ViewProfiles.this.finish();
                    }
                } else if (id == R.id.profile_accountCreation) {
                    startActivity(new Intent(ViewProfiles.this, SignUpActivity.class));
                    ViewProfiles.this.finish();
                } else if (id == R.id.profile_login) {
                    if(ConstantClass.auth==null) {
                        startActivity(new Intent(ViewProfiles.this, LoginActivity.class));
                        ViewProfiles.this.finish();
                    }else {
                        Toast.makeText(ViewProfiles.this,"You are already logged in",Toast.LENGTH_SHORT).show();
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    //  for toolbar button---------------------------->
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.clearUserArray();
        adapter.clearUserPicArray();
    }

    @Override
    public void onBackPressed() {
        if(searchPressed){
            searchPressed=false;
            ViewProfileAdapter.setUserArr(ConstantClass.AllUsersArray);
            ViewProfileAdapter.setProfilePicURI(ConstantClass.AllUsersArrayPics);
            adapter.notifyDataSetChanged();
            searchText.setFocusableInTouchMode(true);
        }else {
            super.onBackPressed();
        }
    }
}
