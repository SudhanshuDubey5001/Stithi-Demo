package stithi.my.com.stithi.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.Utils.FirebaseOperations;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//      signup button--------------------------------->
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseOperations operations = new FirebaseOperations(SignUpActivity.this);

//              getting texts
                String name = getTextView(R.id.input_name).getText().toString();
                int age= Integer.parseInt(getTextView(R.id.input_age).getText().toString());
                String email = getTextView(R.id.input_email).getText().toString();
                String pass= getTextView(R.id.input_password).getText().toString();

                User user=new User(name,age,email,pass, ConstantClass.INITIAL_CREDIT);
                operations.signUp(user);
            }
        });

    }

    private TextView getTextView(int id){
        return findViewById(id);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ViewProfiles.class));
        this.finish();
    }
}