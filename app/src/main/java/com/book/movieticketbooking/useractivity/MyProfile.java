package com.book.movieticketbooking.useractivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.book.movieticketbooking.useractivity.model.Userprofile;
import com.book.movieticketbooking.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private TextView Name,Dob,Email,Mobile,Address;
    private CircleImageView imageView;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeRefreshLayout;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.statusbar)));
        getWindow().setStatusBarColor((ContextCompat.getColor(this,R.color.statusbar)));

        setupUI();
        getSupportActionBar().setTitle("Profile");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyProfile.this, UpdateProfileImage.class));
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Info").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userprofile userProfile = snapshot.getValue(Userprofile.class);
                String ProfilePicUrl = userProfile.getUserProfilePic();
                if (ProfilePicUrl.contentEquals("None")){
                    imageView.setImageResource(R.drawable.login);
                }else {
                    Picasso.get().load(ProfilePicUrl).fit().into(imageView);
                }

                Name.setText(userProfile.getUserName());
                Dob.setText(userProfile.getUserDob());
                Email.setText(userProfile.getUserEmail());
                Mobile.setText(userProfile.getUserMobile());
                Address.setText(userProfile.getUserAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfile.this,error.getCode(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupUI(){
        Name = (TextView)findViewById(R.id.userName);
        Dob = (TextView)findViewById(R.id.userDob);
        Email = (TextView)findViewById(R.id.userEmail);
        Mobile = (TextView)findViewById(R.id.userMobile);
        Address = (TextView)findViewById(R.id.userAddress);

        imageView = (CircleImageView) findViewById(R.id.profile_Image);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_update:{
                startActivity(new Intent(MyProfile.this, UpdateUserProfile.class));
                break;
            }
            case R.id.action_change_p_m: {
                startActivity(new Intent(MyProfile.this, ChangeMailPass.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}