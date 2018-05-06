package com.example.raghvendrapandey.appshare;

import android.app.ProgressDialog;
import android.content.Intent;
import java.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LaunchActivity extends AppCompatActivity {

    String inviteId;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mRootref;
    private ProgressDialog pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String s=getIntent().getData().toString();
        pg=new ProgressDialog(this);
       // Toast.makeText(this, s, Toast.LENGTH_LONG).show();


        //............get data from link................


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pg.setMessage("Please wait while we checking URL validation!");
        pg.setCanceledOnTouchOutside(false);
        pg.show();
        loginUser("admin@gmail.com", "123456789");



    }

    //.........invalidiate link before 15 days...............







    // Extract String from link

    private String getInvatationId(String s) {
        return s.substring(43,47);
    }
    private String getReferralCode(String s) {
        return s.substring(31,43);

    }
    private String getCurrentTime(String s) {
        return s.substring(47,s.length()-1);

    }
    private void loginUser(String email, String password) {

        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String s=getIntent().getData().toString();
                    inviteId=getInvatationId(s);
                    final String referral=getReferralCode(s);
                    String time=getCurrentTime(s);
                    //int  diffDate= 0;

                    //diffDate = DateDifference(time);

                    //Toast.makeText(LaunchActivity.this, diffDate+"", Toast.LENGTH_SHORT).show();
                    mRootref= FirebaseDatabase.getInstance().getReference().child("Video_Users").child(referral); // Retrive data from database...
                   // final int finalDiffDate = diffDate;

                    mRootref.orderByChild("inviteId")
                            .equalTo(inviteId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                FirebaseAuth.getInstance().signOut();
                                pg.dismiss();
                                Toast.makeText(LaunchActivity.this, "This link is already used by someone", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            else{

                                //.......... launch Singup Activity......

                               pg.dismiss();
                                Toast.makeText(LaunchActivity.this, "Please Singup with valid referral code", Toast.LENGTH_SHORT).show();
                                Intent signIntent=new Intent(LaunchActivity.this,SignUpActivity.class);
                                Bundle data = new Bundle();
                                data.putString("referral",referral);
                                data.putString("invite",inviteId);
                               // data.putString("time",ConvertDayAgo(t));
                                signIntent.putExtras(data);
                                startActivity(signIntent);
                                FirebaseAuth.getInstance().signOut();
                                finish();


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            pg.hide();
                            Toast.makeText(LaunchActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();

                        }

                    });





                } else {



                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(LaunchActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();
                    pg.hide();

                }


            }

        });


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int DateDifference( String linkDate)  {
        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        java.util.Date d1 = null;

        try {
            d = dfDate.parse(linkDate);
            d1 = dfDate.parse(dfDate.format(new Date()));//Returns 15/10/2012
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        int diffInDays = (int) ((d.getTime() - d1.getTime())/ (1000 * 60 * 60 * 24));
        return diffInDays;
    }



}
