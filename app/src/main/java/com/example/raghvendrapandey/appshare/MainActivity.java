package com.example.raghvendrapandey.appshare;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private Button urlShare;
    private FirebaseAuth mAuth;
    String myReferral;
    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Invite Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.user_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);


        urlShare=(Button)findViewById(R.id.urlShare);
        urlShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   createUrl();
                    // Toast.makeText(MainActivity.this, myreffral, Toast.LENGTH_SHORT).show();


            }
        });

    }


    public void createUrl() {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        FirebaseDatabase.getInstance().getReference().child("Referral_code").child(uid).child("code").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myReferral=dataSnapshot.getValue().toString();
                String ran = generateString(6);
               // Toast.makeText(this, myReferral, Toast.LENGTH_SHORT).show();

              //  String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                long millis = System.currentTimeMillis() % 1000;
                String as=ran+millis;
                // Toast.makeText(this, time+"", Toast.LENGTH_SHORT).show();
                String link = "https://videoBB.com/?invitedby="+myReferral+as;
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLink(Uri.parse(link))
                        .setDynamicLinkDomain("tg636.app.goo.gl")
                        .buildShortDynamicLink()
                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {


                            @Override

                            public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                Uri  mInvitationUrl = shortDynamicLink.getShortLink();
                                String subject =" VideoBB App";
                                String invitationLink = mInvitationUrl.toString();
                                String msg = "Let's open my App! Use my referrer link: "
                                        + invitationLink;

                                // ........custom Intent chooser........it will open only facebook, whatsapp, Gmail.
                                List<Intent> targetShareIntents=new ArrayList<Intent>();
                                Intent shareIntent=new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                List<ResolveInfo> resInfos=getPackageManager().queryIntentActivities(shareIntent, 0);
                                if(!resInfos.isEmpty()){
                                    System.out.println("Have package");
                                    for(ResolveInfo resInfo : resInfos){
                                        String packageName=resInfo.activityInfo.packageName;
                                        // Log.i("Package Name", packageName);
                                        if(packageName.contains("com.whatsapp") || packageName.contains("com.facebook.katana") || packageName.contains("com.google.android.gm")){
                                            Intent intent=new Intent();
                                            intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, msg);// set message
                                            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                            intent.setPackage(packageName);
                                            targetShareIntents.add(intent);
                                        }
                                    }
                                    if(!targetShareIntents.isEmpty()){
                                        System.out.println("Have Intent");
                                        Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
                                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                                        startActivity(chooserIntent);
                                    }else{

                                    }
                                }
                            }

                        });
                //Toast.makeText(MainActivity.this, myReferral, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }
  else {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("Referral_code").child(uid).child("code").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        myReferral = dataSnapshot.getValue().toString();
                        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Video_Users").child(myReferral);
                        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                                Users.class,
                                R.layout.invite_user_layout,
                                UsersViewHolder.class,
                                mUsersDatabase

                        ) {
                            @Override
                            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {

                                usersViewHolder.setDisplayName(users.getName());
                                usersViewHolder.setEmail(users.getEmail());
                                usersViewHolder.setInviteID(users.getInviteId());


                            }
                        };


                        mUsersList.setAdapter(firebaseRecyclerAdapter);
                    }
                    else
                        Toast.makeText(MainActivity.this, "you do not have any invite user", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.toString() , Toast.LENGTH_SHORT).show();

                }
            });

        }
} public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDisplayName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_name);
            userNameView.setText(name);

        }

        public void setEmail(String email){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_mail);
            userStatusView.setText(email);


        }

        public void setInviteID(String id){

            TextView userStatusView = (TextView) mView.findViewById(R.id.id);
            userStatusView.setText(id);


        }


    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

}
