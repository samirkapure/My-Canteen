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
import android.widget.Toast;

import com.example.hp.ecommerce.Model.Cart;
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

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessButton;
    private TextView txtTotalPrice, txtmsg1;

    private int overallTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        txtmsg1 = findViewById(R.id.msg1);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessButton = findViewById(R.id.next_process_button);
        txtTotalPrice = findViewById(R.id.total_price);

        NextProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                txtTotalPrice.setText("Total Price: "+ String.valueOf(overallTotalPrice));
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);


                intent.putExtra("Total Price", String.valueOf(overallTotalPrice));

                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User view")
                 .child(Prevalent.currentonlineUser.getPhone()).child("Products"),Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter <Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
                    {
                        holder.txtProductQuantity.setText("Quantity: "+model.getQuantity());
                        holder.txtProductPrice.setText("Price:"+model.getPrice());
                        holder.txtProductName.setText("Product Name:"+model.getPname());


                        int oneTypeProductPrice =  ((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                        overallTotalPrice += oneTypeProductPrice ;

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options[] = new  CharSequence[]
                                    {
                                        "Edit",     //0
                                        "Remove"    //1
                                    };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options:");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                             if(which ==0)
                                             {
                                                 Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                                 intent.putExtra("pid", model.getPid());
                                                 startActivity(intent);
                                             }
                                             if(which == 1)
                                             {
                                                 cartListRef.child("User view")
                                                         .child(Prevalent.currentonlineUser.getPhone())
                                                         .child("Products")
                                                         .child(model.getPid())
                                                         .removeValue()
                                                         .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task)
                                                             {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        Toast.makeText(CartActivity.this,
                                                                                "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(CartActivity.this, Home.class);
                                                                        startActivity(intent);
                                                                    }
                                                             }
                                                         });
                                                 cartListRef.child("Admin view")
                                                         .child(Prevalent.currentonlineUser.getPhone())
                                                         .child("Products")
                                                         .child(model.getPid())
                                                         .removeValue()
                                                         .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task)
                                                             {
                                                                 if(task.isSuccessful())
                                                                 {
                                                                     Toast.makeText(CartActivity.this,
                                                                             "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                                                                     Intent intent = new Intent(CartActivity.this, Home.class);
                                                                     startActivity(intent);
                                                                 }
                                                             }
                                                         });
                                             }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
                recyclerView.setAdapter(adapter);
                adapter.startListening();
    }



}
