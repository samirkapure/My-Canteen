package com.example.hp.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hp.ecommerce.Model.AdminOrders;
import com.example.hp.ecommerce.Model.Cart;
import com.example.hp.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.order_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders>  options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef, AdminOrders.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter <AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
                    {

                            final String uID = getRef(position).getKey();
                            userRef.child(uID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String phone = dataSnapshot.child("phone").getValue().toString();
                                    holder.userName.setText("Name: " + name);
                                    holder.userPhoneno.setText("Phone no: " + phone);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            ordersRef.child(uID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String tprice = dataSnapshot.child("total_price").getValue().toString();
                                    holder.userTotalPrice.setText("Total Price:"+tprice);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });





                            holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                    intent.putExtra("uid", uID);
                                    startActivity(intent);
                                }
                            });

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //display dialog box
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Yes",
                                                    "No"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                    builder.setTitle("Have you delivered this order  ?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) //yes
                                            {
                                                String uID = getRef(position).getKey();
                                                RemoveFromAdminView(uID);
                                            } else {
                                                finish();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout, viewGroup,false);
                        return  new AdminOrdersViewHolder(view);
                    }
                };


        ordersList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName, userPhoneno, userTotalPrice;
        public Button showOrdersBtn;

        public AdminOrdersViewHolder(View view)
        {
            super(view);

            userName = view.findViewById(R.id.order_user_name);
            userPhoneno = view.findViewById(R.id.order_phone_no);
            userTotalPrice = view.findViewById(R.id.order_total_price);
            showOrdersBtn = view.findViewById(R.id.show_all_products);

        }

    }


    private void RemoveFromAdminView(String uID)
    {
        ordersRef.child(uID).removeValue();
    }

}
