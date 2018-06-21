package stithi.my.com.stithi.User;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private int age = 0;
    private String email;

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    private String pass;
    private int wallet;

    public User(String name, int age, String email, int wallet) {
        this.name = name;
        this.age=age;
        this.email=email;
        this.wallet = wallet;
    }

    public User(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public User(String name, int age, String email, String pass, int wallet) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.pass = pass;
        this.wallet = wallet;
    }

    protected User(Parcel in) {
        name = in.readString();
        age = in.readInt();
        email = in.readString();
        pass = in.readString();
        wallet = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public int getWallet() {
        return wallet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeString(email);
        dest.writeString(pass);
        dest.writeInt(wallet);
    }
}
