package com.example.hp.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closetxtBtn, saveTextBtn;

    private Uri imageUri;
    private String myurl = "";

    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userPhoneEditText = findViewById( R.id.settings_phone_number);
        addressEditText = findViewById( R.id.settings_address);

        profileChangeTextBtn = findViewById( R.id.profile_image_change_btn);
        closetxtBtn = findViewById( R.id.close_settings_btn);
        saveTextBtn = findViewById(R.id.update_account_settings);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        closetxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";

                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> hashMap = new HashMap <>();
        hashMap.put("name",fullNameEditText.getText().toString());
        hashMap.put("address",addressEditText.getText().toString());
        hashMap.put("phone",userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(hashMap);

        startActivity(new Intent(SettingsActivity.this,Home.class));
        Toast.makeText(SettingsActivity.this, "Profile Info Updated Successfully", Toast.LENGTH_SHORT).show();

        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri= result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error..Try Again", Toast.LENGTH_SHORT).show();
            startActivity( new Intent(SettingsActivity.this, SettingsActivity.class));//refreshing
            finish();
        }
    }

    private void userInfoSaved()
    {

        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if(checker.equals("clicked"))
        {
            uploadImage();
        }

    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload Picture");
        progressDialog.setMessage("Please wait while uploading user Info");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d("****** ",imageUri.toString());
        if(imageUri != null)
        {

            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentonlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw  task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myurl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> hashMap = new HashMap <>();
                        hashMap.put("name",fullNameEditText.getText().toString());
                        hashMap.put("address",addressEditText.getText().toString());
                        hashMap.put("phone",userPhoneEditText.getText().toString());
                        hashMap.put("image",myurl);
                        ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(hashMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this,Home.class));
                        Toast.makeText(SettingsActivity.this, "Profile Info Updated Successfully", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                    }
               }
            });

        }
        else {
            Toast.makeText(this, "Image is not Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if( dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
