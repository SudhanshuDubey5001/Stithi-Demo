package stithi.my.com.stithi.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import stithi.my.com.stithi.ProfilePicSetter.FragmentCreation;
import stithi.my.com.stithi.ProfilePicSetter.SliderManager;
import stithi.my.com.stithi.ProfilePicSetter.ZoomOutPageTransformer;
import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.Utils.FirebaseOperations;
import stithi.my.com.stithi.WalletTransaction.Notification;

public class ProfileActivity extends AppCompatActivity {

    private static boolean fabIconPressed = false;
    private static boolean oneTime=true;
    SliderManager manager;
    User user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    public void updateProfilePic() {
        manager.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fabset);

        if(oneTime){
            oneTime=false;
            Toast.makeText(this,"Swipe to see more pictures",Toast.LENGTH_SHORT).show();
        }

//      starting the notification service
        startService(new Intent(this,Notification.class));

//      set up the viewpager----------------------->
        ViewPager pager = findViewById(R.id.viewPagerForImages);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        manager = new SliderManager(getSupportFragmentManager());
        pager.setAdapter(manager);
        FloatingActionButton fab = findViewById(R.id.fab);

//      get the location where this activity called------------>
        if (getIntent().getStringExtra(ConstantClass.WHERETAG) == null) {
            DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            fab.hide();
        }else {
//      set up the navigation drawer if user is logged -------->
            setupNavigationDrawer();
            fab.show();
        }

//      get user object-------->
        user = getIntent().getParcelableExtra(ConstantClass.USERTAG);

//      download its profile pics--------->
        FirebaseOperations operations = new FirebaseOperations(this);
        operations.downloadPicture(user);

//      setting up details of user----------->
        getTextView(R.id.details_name).setText(user.getName());
        getTextView(R.id.details_ageNumber).setText("" + user.getAge());
        getTextView(R.id.details_email).setText(user.getEmail());
        getTextView(R.id.details_wallet).setText("" + user.getWallet());

        Button transfer = findViewById(R.id.transfer_btn);
        if(ConstantClass.auth!=null){
            transfer.setVisibility(View.VISIBLE);
        }else {
            transfer.setVisibility(View.INVISIBLE);
        }


        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCreation creation = new FragmentCreation();
                creation.clearProfilePicArray();
                ConstantClass.COUNTPICS=0;
                startActivity(new Intent(ProfileActivity.this,WalletActivity.class));
                ProfileActivity.this.finish();
            }
        });

//      fab icon for uploading images------------------------------->
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabIconPressed = true;
                if (ConstantClass.COUNTPICS < 5) {
                    Intent filepick = new Intent(Intent.ACTION_GET_CONTENT);
                    filepick.setType("image/*");
                    startActivityForResult(Intent.createChooser(filepick, "Select Pictures"), ConstantClass.REQ_CODE);
                } else {
                    Toast.makeText(ProfileActivity.this, "Sorry! Can only upload 5", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //  handle the pic uploads-------------------------->
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("my", "Request Code: " + requestCode);
        Log.d("my", "Result Code: " + resultCode);

        if (requestCode == ConstantClass.REQ_CROP &&
                resultCode == RESULT_OK &&
                data != null) {

            Log.d("my", "yahan tak aa gye bhaiya...");

            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap selectedBitmap = extras.getParcelable("data");
            // Set The Bitmap Data To ImageView
            FragmentCreation creation = new FragmentCreation();
            creation.getProfilePics(selectedBitmap);
            updateProfilePic();
            Log.d("my", "countpics----------------->" + ConstantClass.COUNTPICS);
//          perform upload----------------->
            FirebaseOperations operations = new FirebaseOperations(this);
            operations.uploadPicture(selectedBitmap, user.getEmail());

        } else if (requestCode == ConstantClass.REQ_CODE &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            Uri filePath = data.getData();
            performCrop(filePath);
        }
    }

    //  for navigation drawer-------------------------->
    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);

        navigationView = findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.viewprofiles) {
                    startActivity(new Intent(ProfileActivity.this, ViewProfiles.class));
                } else if (id == R.id.wallet) {
                    startActivity(new Intent(ProfileActivity.this, WalletActivity.class));
                } else if (id == R.id.logout) {
                    ConstantClass.auth=null;
                    startActivity(new Intent(ProfileActivity.this, ViewProfiles.class));
                }
                FragmentCreation creation = new FragmentCreation();
                creation.clearProfilePicArray();
                ConstantClass.COUNTPICS=0;
                ProfileActivity.this.finish();
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

    //  for performing crop---------------------------->
    private void performCrop(Uri contentUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 5);
            cropIntent.putExtra("aspectY", 3);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 480);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, ConstantClass.REQ_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private TextView getTextView(int id) {
        return findViewById(id);
    }

    //   clear the array when leaving the activity----------->
    @Override
    protected void onPause() {
        super.onPause();
        if (!fabIconPressed) {
            FragmentCreation creation = new FragmentCreation();
            creation.clearProfilePicArray();
            ConstantClass.COUNTPICS=0;
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentCreation creation = new FragmentCreation();
        creation.clearProfilePicArray();
        ConstantClass.COUNTPICS=0;
        startActivity(new Intent(this,ViewProfiles.class));
        this.finish();
    }
}
