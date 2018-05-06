package com.example.raghvendrapandey.appshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword,mReferral;
    private Button mCreateBtn;
    private TextView mLogin;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mRootref;
    private Toolbar mToolbar;

    List<String> InviteList=new ArrayList();
    String inviteId,referral_code,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mLogin=(TextView)findViewById(R.id.code1);
        mAuth=FirebaseAuth.getInstance();
        //.....extract data get from Launch Activity........
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            referral_code = extras.getString("referral");
            inviteId = extras.getString("invite");
            //time = extras.getString("time");
            }
        else


            //......ActionBar.....
           // mToolbar = (Toolbar) findViewById(R.id.sign_appBar);
        //setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Account");

        //.........Android View.........

        mDisplayName = (TextInputLayout) findViewById(R.id.register_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.register_email);
        mPassword = (TextInputLayout) findViewById(R.id.register_password);
        mReferral = (TextInputLayout) findViewById(R.id.register_referral);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);
        if(referral_code!=null)
            mLogin.setText(referral_code);
        else
            mLogin.setText("You need to find referral link");
        //.....initialize progress dialog in this Activity........
        mRegProgress=new ProgressDialog(this);


        //.....when create Account button is clicked...........

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //.......get text from Inputtext layout........
                String display_name = mDisplayName.getEditText().getText().toString().trim();
                String email = mEmail.getEditText().getText().toString().trim();
                String password = mPassword.getEditText().getText().toString().trim();
                String referral = mReferral.getEditText().getText().toString().trim();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)||!TextUtils.isEmpty(referral)){
                    mRegProgress.setMessage("Please wait while we create your account!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    if(isNetworkAvailable()) {
                        if (referral.equals(referral_code))
                            register_user(display_name, email, password, referral);

                        else {
                            Toast.makeText(SignUpActivity.this, "Invalid referral code", Toast.LENGTH_LONG).show();
                            mRegProgress.hide();
                        }
                    }else{
                        Toast.makeText(SignUpActivity.this, "Network is not Available", Toast.LENGTH_SHORT).show();
                        mRegProgress.hide();
                    }

                }
                else{
                    Toast.makeText(SignUpActivity.this, "Form can not be empty", Toast.LENGTH_SHORT).show();
                }




            }
        });


    }






    //.......User Register..........
    private void register_user(final String display_name, final String email, String password, final String referral) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    //.....pusing data in firebase database..........
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Video_Users").child(referral).child(uid);
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    // String current_data = "myReferral/"+
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("device_token", device_token);
                    userMap.put("email", email);
                    userMap.put("inviteId", inviteId);
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });
                    DatabaseReference myRoot=FirebaseDatabase.getInstance().getReference().child("Referral_code").child(uid).child("code");
                    myRoot.setValue("VIDEOBB-"+generateString(4));


                } else {

                    mRegProgress.hide();
                    Toast.makeText(SignUpActivity.this, "Cannot Sign in. Please check the form and try again." , Toast.LENGTH_LONG).show();

                }

            }
        });
    }
    //........checking Internet connection.............
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static String generateString(int n) {
        double i;
        char word;
        String word1="";

        for (int j = 1; j <= 4; j++) {
            i =  5 * Math.random() + (int) 'a';

            word = (char) i;
            word1+=word;

        }
        return word1.toUpperCase();

    }




}
