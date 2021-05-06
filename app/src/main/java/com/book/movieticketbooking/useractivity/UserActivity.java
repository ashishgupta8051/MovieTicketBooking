package com.book.movieticketbooking.useractivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.book.movieticketbooking.useractivity.model.Userprofile;
import com.book.movieticketbooking.useractivity.model.Userfeedback;
import com.book.movieticketbooking.R;
import com.book.movieticketbooking.login.Login;
import com.book.movieticketbooking.useractivity.passcode.CreatePasscode;
import com.book.movieticketbooking.useractivity.passcode.PassCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button upcomingMovie,bookShow,myProfile,myBooking,feedback,download,account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        final String CurrentUserId = firebaseAuth.getUid();

        upcomingMovie = (Button)findViewById(R.id.upcoming_movie);
        bookShow = (Button)findViewById(R.id.book_show);
        myProfile = (Button)findViewById(R.id.my_profile);
        myBooking = (Button)findViewById(R.id.my_booking);
        feedback = (Button)findViewById(R.id.give_feedback);
        download = (Button)findViewById(R.id.download_movie);
        account = (Button)findViewById(R.id.user_bank);

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference PasscodeRef = FirebaseDatabase.getInstance().getReference("Passcode").child(firebaseAuth.getUid());
                PasscodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            startActivity(new Intent(UserActivity.this, PassCode.class));
                        }else {
                            startActivity(new Intent(UserActivity.this,CreatePasscode.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DownloadMovie.class));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(user_activity.this,Feedback.class));
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserActivity.this);
                bottomSheetDialog.setContentView(R.layout.user_feedback_form);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                final TextView textView = (TextView)bottomSheetDialog.findViewById(R.id.fb_user_name);
                final EditText editText = (EditText)bottomSheetDialog.findViewById(R.id.fb_feedback);
                final RatingBar ratingBar = (RatingBar)bottomSheetDialog.findViewById(R.id.fb_ratingbar);

                Button button = (Button)bottomSheetDialog.findViewById(R.id.fb_button);

                firebaseAuth = FirebaseAuth.getInstance();

                databaseReference = FirebaseDatabase.getInstance().getReference("User Info").child(firebaseAuth.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Userprofile userProfile = snapshot.getValue(Userprofile.class);
                        textView.setText(userProfile.getUserName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserActivity.this,error.getCode(),Toast.LENGTH_SHORT).show();
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       String Name = textView.getText().toString();
                       String Feedback = editText.getText().toString();
                       String RatingBar = String.valueOf(ratingBar.getRating());

                       if (TextUtils.isEmpty(Feedback)){
                            editText.setError("Enter your feedback");
                       }else if (ratingBar.getRating() == 0.0){
                            Toast.makeText(UserActivity.this,"Please give your rating",Toast.LENGTH_SHORT).show();
                       }else {
                           String id = databaseReference.push().getKey();
                           databaseReference = FirebaseDatabase.getInstance().getReference("User Feedback");
                           Userfeedback user_feedback = new Userfeedback(id,Name,RatingBar,Feedback);
                           databaseReference.child(id).setValue(user_feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                           Toast.makeText(getApplicationContext(),"Response given successfully",Toast.LENGTH_SHORT).show();
                           bottomSheetDialog.dismiss();
                               }
                           });
                       }
                    }
                });
                bottomSheetDialog.show();
            }
        });

        myBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, MyBooking.class));
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, MyProfile.class));
            }
        });

        upcomingMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, UpcomingMovie.class));
            }
        });

        bookShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, BookShow.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:{
                startActivity(new Intent(UserActivity.this, MyProfile.class));
                break;
            }
            case R.id.user_logout: {
                logout();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Activity");
        builder.setMessage("Are you sure you want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                startActivity(new Intent(UserActivity.this, Login.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}