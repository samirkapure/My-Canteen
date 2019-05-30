package com.example.hp.ecommerce.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.ecommerce.Interface.ItemClickListener;
import com.example.hp.ecommerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;


    public ProductViewHolder( View itemView)
    {
        super(itemView);

        imageView= (ImageView) itemView.findViewById(R.id.product_image);
        txtProductDescription= (TextView) itemView.findViewById(R.id.produt_description);
        txtProductName= (TextView) itemView.findViewById(R.id.produt_name);
        txtProductPrice= (TextView) itemView.findViewById(R.id.produt_price);

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        listener.onClick(view , getAdapterPosition(), false);
    }
}
