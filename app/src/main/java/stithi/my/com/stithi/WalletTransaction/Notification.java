package stithi.my.com.stithi.WalletTransaction;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;

import stithi.my.com.stithi.Utils.FirebaseOperations;

public class Notification extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseOperations operations = new FirebaseOperations(this);
        operations.receiveNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
