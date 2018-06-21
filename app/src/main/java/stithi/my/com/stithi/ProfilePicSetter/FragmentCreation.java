package stithi.my.com.stithi.ProfilePicSetter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.Utils.ConstantClass;

public class FragmentCreation extends Fragment {

    int profileID;
    private static ArrayList<Bitmap> profilepicsArray = new ArrayList<>();

    public void getProfilePics(Bitmap image) {
        profilepicsArray.add(image);
        ConstantClass.COUNTPICS=profilepicsArray.size();
        Log.d("my","Size of array: "+profilepicsArray.size());
    }

    public void clearProfilePicArray(){
        profilepicsArray.clear();
    }

    public static FragmentCreation createInstance(int profile_id) {
        FragmentCreation f = new FragmentCreation();
        Bundle b = new Bundle();
        b.putInt(ConstantClass.PROFILE_ID, profile_id);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            profileID = args.getInt(ConstantClass.PROFILE_ID);
        } else {
            throw new RuntimeException("Pass the profile id dude!!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_pic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView picSetter = view.findViewById(R.id.setImage);
        Log.d("my","profileID: "+profileID);

        if(profileID+1<=profilepicsArray.size()){
            picSetter.setImageBitmap(profilepicsArray.get(profileID));
        }
    }
}
