package stithi.my.com.stithi.ViewProfileAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.activities.ProfileActivity;
import stithi.my.com.stithi.activities.ViewProfiles;

public class ViewProfileAdapter extends RecyclerView.Adapter<ViewProfileAdapter.ViewProfileHolder> {

    private static ArrayList<User> userArr = new ArrayList<>();
    private static ArrayList<String> profilePicURI = new ArrayList<>();
    private Context context;

//  getter and setter-------------->
    public static void setUserArr(ArrayList<User> userArr) {
        ViewProfileAdapter.userArr = userArr;
    }
    public static void setProfilePicURI(ArrayList<String> profilePicURI) {
        ViewProfileAdapter.profilePicURI = profilePicURI;
    }
    public static ArrayList<User> getUserArr() {
        return userArr;

    }
    public static ArrayList<String> getProfilePicURI() {
        return profilePicURI;
    }

    public void setUpBaseImages(Context con) {
        for (int i = 0; i < userArr.size(); i++) {
//          it is filled with base image in order to set the correct uri when available-------->
            profilePicURI.add("https://image.ibb.co/bJ3rAJ/profile.png");
        }
    }

    public void addUser(User user) {
        userArr.add(user);
    }
    public User getUser(int index){ return userArr.get(index);}
    public int UserArrSize(){return userArr.size();}
    public void clearUserArray() {
        userArr.clear();
    }
    public void addUserPic(int index,String uri) {
        if(index==-2){
            profilePicURI.add(uri);
        }else {
            profilePicURI.set(index, uri);
        }
    }
    public String getUserPic(int index) {
        return profilePicURI.get(index);
    }
    public void clearUserPicArray() {
        profilePicURI.clear();
    }

    @NonNull
    @Override
    public ViewProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerviewsampleitem, parent, false);
        return new ViewProfileHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProfileHolder holder, int position) {
        holder.name.setText(userArr.get(position).getName());
        holder.wallet.setText("" + userArr.get(position).getWallet());
        holder.pos = position;
        holder.user = userArr.get(position);
        try{
            Glide.with(context).load(profilePicURI.get(position)).into(holder.profilePic);
        }catch (Exception ignored){
        }
    }

    @Override
    public int getItemCount() {
        return userArr.size();
    }

    //  viewholder as inner class--------------------------------->
    class ViewProfileHolder extends RecyclerView.ViewHolder {

        ImageView profilePic;
        TextView name;
        TextView wallet;
        int pos;
        User user;
        Context con;

        public ViewProfileHolder(View itemView, Context con) {
            super(itemView);
            this.con = con;
            profilePic = itemView.findViewById(R.id.profile_icon);
            name = itemView.findViewById(R.id.profile_name);
            wallet = itemView.findViewById(R.id.profile_walletCredit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(ViewProfileHolder.this.con, ProfileActivity.class);
                    in.putExtra(ConstantClass.USERTAG, ViewProfileHolder.this.user);
                    ViewProfileHolder.this.con.startActivity(in);
                    ((ViewProfiles)ViewProfileHolder.this.con).finish();
                }
            });
        }
    }
}