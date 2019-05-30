package com.example.hp.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createacbtn;
    private EditText ipName,ipPassword,ipPhoneno;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        createacbtn = findViewById(R.id.register_btn);
        ipName = findViewById(R.id.register_uname_input);
        ipPassword = findViewById(R.id.register_password_input);
        ipPhoneno = findViewById(R.id.register_phone_number_input);
        progressDialog = new ProgressDialog(this);

        createacbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }
    private void createAccount()
    {
        String name = ipName.getText().toString();
        String phone = ipPhoneno.getText().toString();
        String password = ipPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Please Enter Name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Please Enter Phone no",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            validatePhoneNo(name,phone,password);

        }

    }

    private  void validatePhoneNo(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
         RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot)
             {
                 if(!(dataSnapshot.child("Users").child(phone)).exists())
                 {
                     HashMap<String,Object>userdataMap = new HashMap <>();
                     userdataMap.put("phone",phone);
                     userdataMap.put("password",password);
                     userdataMap.put("name",name);

                     RootRef.child("Users").child(phone).updateChildren(userdataMap)
                             .addOnCompleteListener(new OnCompleteListener <Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful())
                                     {
                                         Toast.makeText(RegisterActivity.this,"Account Successfully created",Toast.LENGTH_SHORT).show();
                                         progressDialog.dismiss();
                                         Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                         startActivity(intent);

                                     }else {
                                         progressDialog.dismiss();
                                         Toast.makeText(RegisterActivity.this,"Error while creating account ..Please Try again ",Toast.LENGTH_SHORT).show();
                                     }

                                 }
                             });

                 }
                 else{
                     Toast.makeText(RegisterActivity.this,"This "+phone+" already exists",Toast.LENGTH_SHORT).show();
                     progressDialog.dismiss();
                     Toast.makeText(RegisterActivity.this,"Please try again with another number",Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                     startActivity(intent);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }
}
