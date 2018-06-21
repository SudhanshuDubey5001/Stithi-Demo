package stithi.my.com.stithi.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import stithi.my.com.stithi.ProfilePicSetter.FragmentCreation;
import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.ViewProfileAdapter.ViewProfileAdapter;
import stithi.my.com.stithi.WalletTransaction.AllUserAdapter;
import stithi.my.com.stithi.activities.LoginActivity;
import stithi.my.com.stithi.activities.ProfileActivity;
import stithi.my.com.stithi.activities.SignUpActivity;
import stithi.my.com.stithi.activities.ViewProfiles;
import stithi.my.com.stithi.activities.WalletActivity;

public class FirebaseOperations {

    private KProgressHUD hud;
    private AlertDialog.Builder dialog;
    private Context context;
    private DatabaseReference ref;
    private User user;
    private boolean dialogEnable;

    //  setting up the context and hud when making an object--------------------------------->
    public FirebaseOperations(Context context) {
        this.context = context;
        ref = FirebaseDatabase.getInstance().getReference("Users");
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(3)
                .setLabel("Uploading")
                .setCancellable(true);
        dialog = new AlertDialog.Builder(context);
    }

    //  sign up-------------------------------------------------->
    public void signUp(User user) {
        if (validate(user, true)) {
            this.user = user;
            hud.setLabel("Creating Account").show();

//           setting auth var null---->
            ConstantClass.auth = null;
            ConstantClass.auth = FirebaseAuth.getInstance();

            ConstantClass.auth.createUserWithEmailAndPassword(user.getEmail(), user.getPass())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("my", "SignUp successful!!!");
                                dialog.setTitle("Success")
                                        .setMessage("Account Created")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                          writing all data to database first--->
                                                ref.push().setValue(FirebaseOperations.this.user);
                                                dialog.dismiss();
                                                hud.dismiss();
//                                          started login service after account creation to avoid login just after signup-->
                                                login(FirebaseOperations.this.user, false);
                                            }
                                        }).show();
                            } else {
                                Log.d("my", "Signup failed :(:(");
                                dialog.setTitle("Failed")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                hud.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    });
        }
    }

    //  login function----------------------------------->
    public void login(User user, boolean dialogEnable) {
        if (validate(user, false)) {
            this.dialogEnable = dialogEnable;
            this.user = user;
            hud.setLabel("Logging in").show();
//          setting auth var null---->
            ConstantClass.auth = null;
            ConstantClass.auth = FirebaseAuth.getInstance();

            ConstantClass.auth.signInWithEmailAndPassword(user.getEmail(), user.getPass())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("my", "Login process started....\n");
                            if (task.isSuccessful()) {
                                Log.d("my", "Login successful!!!");

//                              fetch all the User data of logged in person---------->
                                Query query = ref.orderByValue();
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (snapshot.child("email").getValue().toString().equals(FirebaseOperations.this.user.getEmail())) {
                                                String name = snapshot.child("name").getValue().toString();
                                                int age = Integer.parseInt(snapshot.child("age").getValue().toString());
                                                String email = snapshot.child("email").getValue().toString();
                                                String pass = snapshot.child("pass").getValue().toString();
                                                int wallet = Integer.parseInt(snapshot.child("wallet").getValue().toString());
                                                FirebaseOperations.this.user = new User(name, age, email, pass, wallet);
                                                ConstantClass.LOGGEDIN_USER = FirebaseOperations.this.user;
                                                ConstantClass.USERNAME = name;
                                                hud.dismiss();

                                                if (FirebaseOperations.this.dialogEnable) {
                                                    dialog.setTitle("Success")
                                                            .setMessage("You successfully logged in")
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    Intent in = new Intent(context, ProfileActivity.class);
                                                                    in.putExtra(ConstantClass.WHERETAG, "login_activity");
                                                                    in.putExtra(ConstantClass.USERTAG, FirebaseOperations.this.user);
                                                                    context.startActivity(in);
                                                                    LoginActivity a = (LoginActivity) context;
                                                                    a.finish();
                                                                    Log.d("my", "User: " + FirebaseOperations.this.user.getName());
                                                                }
                                                            }).show();
                                                } else {
                                                    Intent in = new Intent(context, ProfileActivity.class);
                                                    in.putExtra(ConstantClass.WHERETAG, "login_activity");
                                                    in.putExtra(ConstantClass.USERTAG, FirebaseOperations.this.user);
                                                    context.startActivity(in);
                                                    SignUpActivity a = (SignUpActivity) context;
                                                    a.finish();
                                                    Log.d("my", "User: " + FirebaseOperations.this.user.getName());
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Log.d("my", "Login failed :(");
                                dialog.setTitle("Failed")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                hud.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    });
        }
    }

    private boolean validate(User user, boolean IsSignUp) {
        dialog.setTitle("Error")
                .setMessage("Cannot leave blank")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (IsSignUp) {
            if (user.getName().equals("") ||
                    user.getAge() == 0 ||
                    user.getEmail().equals("") ||
                    user.getPass().equals("")) {
                Log.d("my", "[Validation error]\n");
                dialog.show();
                return false;
            }
        } else {
            if (user.getEmail().equals("") || user.getPass().equals("")) {
                Log.d("my", "[Validation error]\n");
                dialog.show();
                return false;
            }
        }
        Log.d("my", "[Validation successful]\n");
        return true;
    }

    public void uploadPicture(Bitmap bitmap, String key) {
        hud.setLabel("Uploading");
        StorageReference storage = FirebaseStorage.getInstance()
                .getReference("Profile_pic")
                .child(key)
                .child("Pic " + ConstantClass.COUNTPICS);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert bitmap != null;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataByte = baos.toByteArray();

        UploadTask uploadTask = storage.putBytes(dataByte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                hud.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    public void downloadPicture(User user) {
        for (int i = 1; i <= 5; i++) {
            StorageReference storage = FirebaseStorage.getInstance()
                    .getReference("Profile_pic")
                    .child(user.getEmail())
                    .child("Pic " + i);

            storage.getBytes(ConstantClass.ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    FragmentCreation creation = new FragmentCreation();
                    creation.getProfilePics(bitmap);
                    ProfileActivity p = (ProfileActivity) context;
                    p.updateProfilePic();
                }
            });
        }
    }

    private int index = 0;
    private int noOfUsers;
    ViewProfileAdapter adapter;
    private HashMap<String, Bitmap> hm = new HashMap<>();
    private ArrayList<String> orderOfusers = new ArrayList<>();

    public void fetchProfiles(ViewProfileAdapter adapter) {
        this.adapter = adapter;
        hud.setLabel("Wait").show();
        Query query = ConstantClass.ref.orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOfUsers = (int) dataSnapshot.getChildrenCount();
                Log.d("my", "No of user: " + noOfUsers);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                  get name and wallet credit-------------------------------------->
                    String name = snapshot.child("name").getValue().toString();
                    int age = Integer.parseInt(snapshot.child("age").getValue().toString());
                    String email = snapshot.child("email").getValue().toString();
                    int wallet = Integer.parseInt(snapshot.child("wallet").getValue().toString());

                    Log.d("my", "Name: " + name);
                    Log.d("my", "Wallet: " + wallet);
                    Log.d("my", "------------");

                    FirebaseOperations.this.adapter.addUser(new User(name, age, email, wallet));
                    orderOfusers.add(email);
                    FirebaseOperations.this.adapter.notifyDataSetChanged();

//                  get profile pics
                    StorageReference storageReference = FirebaseStorage.
                            getInstance().getReference("Profile_pic")
                            .child(email)
                            .child("Pic 1");

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("my", "Uri: " + uri);

//                          fetch email from URL------------------------------->
                            int startIndex = uri.toString().indexOf("Profile_pic");
                            startIndex += 14;
                            int endIndex = uri.toString().indexOf("%2FPic");
                            String email = uri.toString().substring(startIndex, endIndex);
                            email = email.replace("%40", "@");
                            Log.d("my", "URL se email: " + email);

//                          get index of email in recyclerView array--------->
                            int i = getIndex(orderOfusers, email);
                            Log.d("my", "--------------------------index:  " + i);

//                          replace the downloaded image with base image------------->
                            FirebaseOperations.this.adapter.addUserPic(i, uri.toString());
                            FirebaseOperations.this.adapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });

                }
                ((ViewProfiles) context).setUpRecycler();
                hud.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //  to get index by providing email------------------------------->
    private int getIndex(ArrayList<String> set, String value) {
        for (int i = 0; i < set.size(); i++) {
            Log.d("my", "Emtry no. " + i + ": " + set.get(i));
            if (set.get(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }


    //   for wallet------------------------------------------------------>
    private AllUserAdapter allUserAdapter;

    public void getAllUsers(AllUserAdapter adapter) {
        allUserAdapter = adapter;
        ConstantClass.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    User user = new User(name, 0, email, 0);
                    allUserAdapter.addUsers(user);
                    Log.d("my", "Wallet name------: " + name);
                    Log.d("my", "Wallet email-----: " + email);
                }
                ((WalletActivity) context).displayAllUsers();
                allUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    String wallet_email;
    int wallet_amount;

    public void performTransaction(int amount, String email) {
        wallet_email = email;
        wallet_amount = amount;
        dialog.setTitle("Confirm")
                .setMessage(amount + " credit will be deducted from your account")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transactCredit();
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    //  wallet transaction---------------------------------------------->
    private MediaPlayer mp;

    private void transactCredit() {
        hud.setLabel("Transferring").show();
        mp = MediaPlayer.create(context, R.raw.success);
        ConstantClass.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int new_sender_wallet = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("email").getValue().toString().equals(wallet_email)) {

                        int reciever_wallet = Integer.parseInt(snapshot.child("wallet").getValue().toString());
                        int new_reciever_credit = reciever_wallet + wallet_amount;
                        Log.d("my", "New reciever credit: " + new_reciever_credit);
                        new_sender_wallet = ConstantClass.LOGGEDIN_USER.getWallet() - wallet_amount;
                        Log.d("my", "New sender credit: " + new_reciever_credit);
                        ConstantClass.LOGGEDIN_USER.setWallet(new_sender_wallet);

//                      writing changes to reciever database-------------------------------->
                        ConstantClass.ref.child(snapshot.getKey()).child("wallet").setValue(new_reciever_credit);
                        break;
                    }
                }
//              writing changes to user database------------------------------>
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("email").getValue().toString().equals(ConstantClass.LOGGEDIN_USER.getEmail())) {
                        ConstantClass.ref.child(snapshot.getKey()).child("wallet").setValue(new_sender_wallet);
                        ((WalletActivity) context).setTextView(new_sender_wallet);
                        break;
                    }
                }

                mp.start();
                hud.dismiss();
                dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Successful")
                        .setMessage("Transfer Completed")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mp.release();
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Failed")
                        .setMessage("Check internet connection")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                hud.dismiss();
            }
        });
    }

    //  for sending notification------------------------------------>
    public void receiveNotification() {
        if (ConstantClass.auth != null) {
            ConstantClass.ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("email").getValue().toString().equals(ConstantClass.LOGGEDIN_USER.getEmail())) {
                            if (ConstantClass.LOGGEDIN_USER.getWallet() != Integer.parseInt(snapshot.child("wallet").getValue().toString())) {
                                int newCredit = Integer.parseInt(snapshot.child("wallet").getValue().toString());

                                ConstantClass.LOGGEDIN_USER.setWallet(newCredit);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                        .setContentTitle("Stithi")
                                        .setContentText("You're credit has been updated to " + ConstantClass.LOGGEDIN_USER.getWallet())
                                        .setSmallIcon(R.mipmap.chaticon)
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setStyle(new NotificationCompat.BigTextStyle());

                                showNotification(builder);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showNotification(NotificationCompat.Builder builder) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("my", "New notification------> :D");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            assert manager != null;
            manager.notify(NotificationManager.IMPORTANCE_HIGH, builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("bu", "notification", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
        } else {
            assert manager != null;
            manager.notify(0, builder.build());
        }
    }

    private String desiredName;
    private ViewProfileAdapter searchAdpater;

    public void performSearch(String desiredName, ViewProfileAdapter adapter) {
        hud.setLabel("Searching").show();
        searchAdpater = adapter;
        this.desiredName = desiredName;
        Query query = ConstantClass.ref.orderByValue();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue().toString().toLowerCase();
                    Log.d("my", "Name!!!!!!!!!!--->:" + name);
                    int matchesFound = 0;

                    for (int i = 0; i < FirebaseOperations.this.desiredName.length(); i++) {
                        Log.d("my", "Char!!!!!!!!!!---> " + (i + 1) + " :" + FirebaseOperations.this.desiredName.charAt(i));
                        if (name.length() > i) {
                            if (FirebaseOperations.this.desiredName.charAt(i) == name.charAt(i)) {
                                matchesFound++;
                                if (matchesFound == FirebaseOperations.this.desiredName.length()) {
                                    Log.d("my", "Aa gaye remove karne!!!!!!!!!!!!!!!!!!!!!");
                                    String emailOfFoundElement = snapshot.child("email").getValue().toString();
                                    Log.d("my", "Found element!!!!!!!!!!!!!!!!!!!!!> >>>>>" + emailOfFoundElement);
                                    Log.d("my", "Length!!!!!!!!!!!!!!!!!!!!!> >>>>>" + FirebaseOperations.this.desiredName);

                                    for (int k = 0; k < searchAdpater.UserArrSize(); k++) {
                                        if (searchAdpater.getUser(k).getEmail().contains(emailOfFoundElement)) {
//                                          for saving a copy-------------->
                                            ConstantClass.AllUsersArray=new ArrayList<>(ViewProfileAdapter.getUserArr());
                                            ConstantClass.AllUsersArrayPics=new ArrayList<>(ViewProfileAdapter.getProfilePicURI());

                                            User foundUser = searchAdpater.getUser(k);
                                            String foundUri = searchAdpater.getUserPic(k);

                                            searchAdpater.clearUserArray();
                                            searchAdpater.clearUserPicArray();

                                            searchAdpater.addUser(foundUser);
                                            searchAdpater.addUserPic(-2, foundUri);
                                            break;
                                        }
                                    }
                                    Log.d("my", "Matches!!!!!!!!!!---> " + matchesFound);
                                }
                            }
                        }
                    }
                }
                searchAdpater.notifyDataSetChanged();
                hud.dismiss();
                Toast.makeText(context,"Press back to search more",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("my", "Oups");
                hud.dismiss();
            }
        });
    }
}
