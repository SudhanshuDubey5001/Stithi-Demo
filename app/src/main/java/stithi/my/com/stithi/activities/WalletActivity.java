package stithi.my.com.stithi.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.Utils.FirebaseOperations;
import stithi.my.com.stithi.WalletTransaction.AllUserAdapter;

public class WalletActivity extends AppCompatActivity {

    AllUserAdapter adapter=new AllUserAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        setTextView(ConstantClass.LOGGEDIN_USER.getWallet());

        FirebaseOperations operations = new FirebaseOperations(this);
        operations.getAllUsers(adapter);
    }

    public void displayAllUsers(){
        RecyclerView recyclerView=findViewById(R.id.wallet_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public int getInputCredit(){
        try {
            int inputcredit = Integer.parseInt(getTextView(R.id.wallet_amount_input).getText().toString());
            if (ConstantClass.LOGGEDIN_USER.getWallet() >= inputcredit) {
                return inputcredit;
            } else {
                Toast.makeText(this, "You don't have sufficient credit", Toast.LENGTH_SHORT).show();
            }
        }catch (NumberFormatException e){
            Toast.makeText(this,"Cannot leave blank",Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

    private TextView getTextView(int id){
        return findViewById(id);
    }

    public void setTextView(int credit){
        getTextView(R.id.wallet_display).setText(""+credit);
        getTextView(R.id.wallet_amount_input).setText("");
    }

    @Override
    public void onBackPressed() {
        adapter.clearArray();
        Intent in = new Intent(this,ProfileActivity.class);
        in.putExtra(ConstantClass.USERTAG,ConstantClass.LOGGEDIN_USER);
        in.putExtra(ConstantClass.WHERETAG,"here");
        startActivity(in);
        this.finish();
    }
}
