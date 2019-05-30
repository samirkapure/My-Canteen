package com.example.hp.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.ecommerce.Model.Cart;
import com.example.hp.ecommerce.Model.Products;
import com.example.hp.ecommerce.Prevalent.Prevalent;
import com.example.hp.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static android.provider.Contacts.SettingsColumns.KEY;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEt, phoneEt, addressEt, cityEt;
    private  Button confirmOrderBtn;
    private String totalPrice = null;
    private TextView amt;
    private DatabaseReference databaseReference;
    private DatabaseReference orderRef;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        amt=findViewById(R.id.amt);

        totalPrice = getIntent().getStringExtra("Total Price");
        amt.setText(totalPrice+"₹");
        Toast.makeText(this, "Total Price: "+totalPrice+"₹", Toast.LENGTH_SHORT).show();

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                confirmOrder();
            }
        });





    }


    private void confirmOrder()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentonlineUser.getPhone());

        final DatabaseReference adminViewRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View").child(Prevalent.currentonlineUser.getPhone());

        final HashMap<String , Object> orderMap = new HashMap <>();

        databaseReference.child("Cart List").child("Admin view").child(Prevalent.currentonlineUser
                .getPhone()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable iterable = dataSnapshot.getChildren();
                ArrayList<Cart> cart = new ArrayList<>();
                while(iterable.iterator().hasNext()){
                    DataSnapshot dataSnapshot1 = (DataSnapshot) iterable.iterator().next();
                    cart.add(dataSnapshot1.getValue(Cart.class));
                }

                for(int i = 0; i < cart.size(); i++){
                    Log.e("Product", cart.get(i).getPid()+"-"+cart.get(i).getPname()+"-"+cart.get(i).getQuantity());
                    orderMap.put("pid",cart.get(i).getPid());
                    orderMap.put("pname",cart.get(i).getPname());
                    orderMap.put("quantity",cart.get(i).getQuantity());
                    orderMap.put("price",cart.get(i).getPrice());

                    orderRef.child(cart.get(i).getPid()).updateChildren(orderMap)
                            .addOnCompleteListener(new OnCompleteListener <Void>() {
                                @Override
                                public void onComplete(@NonNull Task <Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        //if order confirmed empty the cart
                                        FirebaseDatabase.getInstance().getReference().child("Cart List")
                                                .child("User view")
                                                .child(Prevalent.currentonlineUser.getPhone())
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task <Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(ConfirmFinalOrderActivity.this,
                                                                    "Final Order Has Been Placed", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ConfirmFinalOrderActivity.this,Home.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });

                                    }

                                }
                            });

                }
                final HashMap<String , Object> tPriceMap = new HashMap <>();
                tPriceMap.put("total_price",totalPrice);
                orderRef.updateChildren(tPriceMap);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentonlineUser.getPhone());
        DatabaseReference adminViewRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View")
                .child(Prevalent.currentonlineUser.getPhone());

        HashMap<String, Object> orderMap = new HashMap <>();
        orderMap.put("totalPrice",totalPrice);
        orderMap.put("name",nameEt.getText().toString());
        orderMap.put("phone",phoneEt.getText().toString());
        orderMap.put("address",addressEt.getText().toString());
        orderMap.put("city",cityEt.getText().toString());
        orderMap.put("date",saveCurrDate);
        orderMap.put("time",saveCurrTime);
        orderMap.put("state","not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    //if order confirmed empty the cart
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User view")
                            .child(Prevalent.currentonlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener <Void>() {
                                @Override
                                public void onComplete(@NonNull Task <Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this,
                                                "Final Order Has Been Placed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });*/
    }
}
