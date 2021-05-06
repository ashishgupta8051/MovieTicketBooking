package com.book.movieticketbooking.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.book.movieticketbooking.useractivity.UserActivity;
import com.book.movieticketbooking.useractivity.model.Account;
import com.book.movieticketbooking.useractivity.model.Userprofile;
import com.book.movieticketbooking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    private TextView sign_in;
    private EditText name,email,password,dob,mobile,address;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String Name,Dob,Email,Password,Mobile,Address;
    private String Balance = String.valueOf(0);
    private String type = String.valueOf(1);
    private String UserProfilePic = "None";
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setupUI();


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day+"/"+month+"/"+year;
                        dob.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    String Email = email.getText().toString().trim();
                    String Password = password.getText().toString().trim();

                    progressDialog.setTitle("Creating new account");
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                SendUserData();
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this, UserActivity.class));
                                finish();
                            }else {
                                progressDialog.dismiss();
                                String message = task.getException().getMessage();
                                Toast.makeText(SignUp.this,"Registration Failed" + message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setupUI(){
        sign_in = (TextView)findViewById(R.id.user_sign_in);

        name = (EditText)findViewById(R.id.user_name);
        dob = (EditText)findViewById(R.id.user_dob);
        email = (EditText)findViewById(R.id.user_email);
        password = (EditText)findViewById(R.id.user_password);
        mobile = (EditText)findViewById(R.id.user_mobile);
        address = (EditText)findViewById(R.id.user_address);

        register = (Button)findViewById(R.id.user_registration_bt);

        //profileImage = (ImageView) findViewById(R.id.r_imageView);
    }

    public boolean validate(){
        boolean result = false;
        Name = name.getText().toString();
        Dob = dob.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
        Mobile = mobile.getText().toString();
        Address = address.getText().toString();

        if (TextUtils.isEmpty(Name)){
            name.setError("Enter name");
        }else if (TextUtils.isEmpty(Email)){
            email.setError("Enter email");
        }else if (TextUtils.isEmpty(Password)){
            password.setError("Enter password");
        }else if (TextUtils.isEmpty(Mobile) || mobile.getText().toString().length() < 10 || mobile.getText().toString().length() > 10){
            mobile.setError("Enter a valid phone number");
        }else if (TextUtils.isEmpty(Address)){
            address.setError("Enter address");
        }else if (TextUtils.isEmpty(Dob)){
             dob.setError("Select date");
        }else {
            result = true;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUp.this,Login.class));
    }

    private void SendUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Info").child(firebaseAuth.getUid());
        Userprofile userProfile = new Userprofile(Name,Dob,Email,Mobile,Address,type,UserProfilePic);
        databaseReference.setValue(userProfile);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Account Info").child(firebaseAuth.getUid());
        Account account = new Account(Name,Mobile,Dob,Mobile,Balance,Password);
        databaseReference1.setValue(account);
    }
}