package com.example.hp.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.ecommerce.Model.Users;
import com.example.hp.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText ipPhoneno,ipPassword;
    private TextView adminLink,nonAdminLink;
    private Button LoginButton;
    ProgressDialog progressDialog;
    private String parentdbName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ipPhoneno = findViewById(R.id.login_phone_number_input);
        ipPassword = findViewById(R.id.login_password_input);
        LoginButton = findViewById(R.id.login_btn);

        adminLink = findViewById(R.id.admin_panel_link);
        nonAdminLink = findViewById(R.id.not_admin_panel_link);
        progressDialog = new ProgressDialog(this);

        Paper.init(this);
        String uname= Paper.book().read(Prevalent.UserPhoneKey);
        final String passw = Paper.book().read(Prevalent.UserPasswordKey);
        if(!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(passw))
        {
            loginUser(uname,passw);
        }

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone1 = ipPhoneno.getText().toString();
                String password1 = ipPassword.getText().toString();
                loginUser(phone1,password1);
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("LoginAdmin");
                adminLink.setVisibility(View.INVISIBLE);
                nonAdminLink.setVisibility(View.VISIBLE);
                parentdbName = "Admins";
            }
        });

        nonAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                nonAdminLink.setVisibility(View.INVISIBLE);
                parentdbName = "Users";
            }
        });


    }

    private void loginUser(final String phone,final String password)
    {

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Please Enter Phone no",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
        }
        else {

            progressDialog.setTitle("Login Account");
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            allowAccessToAccoutn(phone,password);
        }
    }

    private  void allowAccessToAccoutn(final String phone, final String password)
    {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("********: ",parentdbName );
                if(dataSnapshot.child(parentdbName).child(phone).exists())
                {

                    Users userData = dataSnapshot.child(parentdbName).child(phone).getValue(Users.class);
                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            if(parentdbName.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Welcome Admin,", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentdbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, Home.class);
                                Prevalent.currentonlineUser = userData;
                                startActivity(intent);
                            }
                        }else
                        {
                            Toast.makeText(LoginActivity.this,"Password is Incorrect",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }


                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
