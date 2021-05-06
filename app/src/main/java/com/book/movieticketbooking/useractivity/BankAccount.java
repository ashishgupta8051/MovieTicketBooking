package com.book.movieticketbooking.useractivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.book.movieticketbooking.useractivity.model.Account;
import com.book.movieticketbooking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BankAccount extends AppCompatActivity {
    private TextView HolderName;
    private TextView AcNumber;
    private TextView AcDate;
    private TextView AcMobile;
    private TextView AcBalance;
    private TextView AcPassword;
    private Button changePassword,addBalance,updateAccount;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank__account);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.statusbar)));
        getWindow().setStatusBarColor((ContextCompat.getColor(this,R.color.statusbar)));

        getSupportActionBar().setTitle("My Wallet");
        firebaseAuth = FirebaseAuth.getInstance();
        final String uid = firebaseAuth.getCurrentUser().getUid();

        HolderName = (TextView)findViewById(R.id.Ac_Name);
        AcNumber = (TextView)findViewById(R.id.Ac_number);
        AcDate = (TextView)findViewById(R.id.Ac_date);
        AcMobile = (TextView)findViewById(R.id.Ac_phone);
        AcBalance = (TextView)findViewById(R.id.Ac_balance);
        AcPassword = (TextView)findViewById(R.id.Ac_password);

        changePassword = (Button)findViewById(R.id.change_payment_password);
        addBalance = (Button)findViewById(R.id.add_balance);


        databaseReference = FirebaseDatabase.getInstance().getReference("Account Info").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                HolderName.setText(account.getAccountHolderName());
                AcNumber.setText(account.getAccountNumber());
                AcDate.setText(account.getAccountDate());
                AcMobile.setText(account.getAccountMobile());
                AcBalance.setText(account.getAccountMoney());
                AcPassword.setText(account.getAccountPassword());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BankAccount.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog view = new BottomSheetDialog(BankAccount.this);
                view.setContentView(R.layout.change_payment_password);
                view.setCanceledOnTouchOutside(true);

                final EditText editText1= (EditText) view.findViewById(R.id.old_password);
                final EditText editText2 = (EditText) view.findViewById(R.id.new_password);
                Button button = (Button) view.findViewById(R.id.save_password);

                view.show();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String OldPassword = editText1.getText().toString();
                        final String NewPassword = editText2.getText().toString();
                        if (TextUtils.isEmpty(OldPassword)){
                            editText1.setError("Enter current password");
                        }else if (TextUtils.isEmpty(NewPassword)){
                            editText2.setError("Enter new password");
                        }else {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Account Info").child(uid);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String Password = String.valueOf(snapshot.child("accountPassword").getValue());
                                    if(OldPassword.equals(Password)){
                                        databaseReference.child("accountPassword").setValue(NewPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(BankAccount.this, "Password successfully change", Toast.LENGTH_SHORT).show();
                                                view.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(BankAccount.this, "Something is wrong password not updated !!!", Toast.LENGTH_SHORT).show();
                                                view.dismiss();
                                            }
                                        });
                                    }else {
                                        editText1.setError("Password Incorrect");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(BankAccount.this, error.getCode(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog view = new BottomSheetDialog(BankAccount.this);
                view.setContentView(R.layout.add_amount);
                view.setCanceledOnTouchOutside(true);

                final EditText editText = (EditText) view.findViewById(R.id.add_amount);
                final TextView textView = (TextView) view.findViewById(R.id.view_balance);
                Button button = (Button) view.findViewById(R.id.add2);

                databaseReference = FirebaseDatabase.getInstance().getReference("Account Info").child(uid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Account account = snapshot.getValue(Account.class);
                        textView.setText(account.getAccountMoney());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BankAccount.this, error.getCode(), Toast.LENGTH_SHORT).show();
                    }
                });
                view.show();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String DepositAmount = editText.getText().toString();
                        int Deposit = Integer.parseInt(DepositAmount);
                        if (TextUtils.isEmpty(DepositAmount)){
                            editText.setError("Enter Amount");
                        }else if (DepositAmount.contentEquals("0")){
                            editText.setError("Enter Amount");
                        }else if (Deposit >= 2000){
                            editText.setError("You add only 2000 at a one time");
                        }else {
                            databaseReference = FirebaseDatabase.getInstance().getReference("Account Info").child(uid);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int AvailableBalance = Integer.parseInt(String.valueOf(snapshot.child("accountMoney").getValue()));
                                    int DepositBalance = Integer.parseInt(DepositAmount);
                                    int TotalAmount = DepositBalance + AvailableBalance;
                                    String Total = Integer.toString(TotalAmount);
                                    databaseReference.child("accountMoney").setValue(Total);
                                    Toast.makeText(BankAccount.this, "Amount added successfully", Toast.LENGTH_SHORT).show();
                                    view.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(BankAccount.this, ""+error.getCode(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}