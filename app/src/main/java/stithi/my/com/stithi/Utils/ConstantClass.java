package stithi.my.com.stithi.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import stithi.my.com.stithi.User.User;

public class ConstantClass {

    public static int COUNTPICS = 0;

    public static FirebaseAuth auth=null;

    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

    public static final String USERTAG = "UserObject";

    public static final int INITIAL_CREDIT = 50;

    public static String USERNAME;

    public static final int REQ_CODE = 5;

    public static final String PROFILE_ID = "profile_id";

    public static final int REQ_CROP=6;

    public static final long ONE_MEGABYTE = 1024 * 1024;

    public static String WHERETAG="wherefrom";

    public static User LOGGEDIN_USER;

    public static ArrayList<User> AllUsersArray;
    public static ArrayList<String> AllUsersArrayPics;
}
