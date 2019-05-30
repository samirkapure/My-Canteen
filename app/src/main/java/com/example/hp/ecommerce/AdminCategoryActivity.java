package com.example.hp.ecommerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private Button snakcs,meals,teacoffee,beverages;

    private Button LogoutBtn, CheckOrdersButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        LogoutBtn = findViewById(R.id.admin_logout_btn);
        CheckOrdersButton = findViewById(R.id.check_orders_btn);

      snakcs = findViewById(R.id.snacks);
      meals = findViewById(R.id.meal);
      teacoffee = findViewById(R.id.teacoffee);
      beverages = findViewById(R.id.beverages);



        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        CheckOrdersButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });



        snakcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AdminCategoryActivity.this,AdminAddNewActivity.class);
                intent.putExtra("category","Snacks");
                startActivity(intent);
            }
        });

        meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AdminCategoryActivity.this,AdminAddNewActivity.class);
                intent.putExtra("category","Meals");
                startActivity(intent);
            }
        });

        teacoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AdminCategoryActivity.this,AdminAddNewActivity.class);
                intent.putExtra("category","Tea-Coffee");
                startActivity(intent);
            }
        });

        beverages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AdminCategoryActivity.this,AdminAddNewActivity.class);
                intent.putExtra("category","beverages");
                startActivity(intent);
            }
        });



    }
}
