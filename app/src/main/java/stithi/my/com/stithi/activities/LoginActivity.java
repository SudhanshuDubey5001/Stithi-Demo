package stithi.my.com.stithi.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import stithi.my.com.stithi.R;
import stithi.my.com.stithi.User.User;
import stithi.my.com.stithi.Utils.ConstantClass;
import stithi.my.com.stithi.Utils.FirebaseOperations;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//      signup link------->
        getTextView(R.id.link_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                LoginActivity.this.finish();
            }
        });


//      login button--------------------------------->
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseOperations operations = new FirebaseOperations(LoginActivity.this);

//              getting texts
                String email = getTextView(R.id.input_email_login).getText().toString();
                String pass= getTextView(R.id.input_password_login).getText().toString();

                User user=new User(email,pass);
                operations.login(user,true);
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
