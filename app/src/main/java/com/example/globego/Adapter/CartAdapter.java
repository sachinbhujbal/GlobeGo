package com.example.globego.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.globego.Activity.CartActivity;
import com.example.globego.Domain.CartItem;
import com.example.globego.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnItemClickListener listener;

    //
    private List<CartItem> originalList;

    public CartAdapter(Context context, List<CartItem> cartItems, OnItemClickListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
        this.originalList = new ArrayList<>(cartItems);
    }

    // Interface for delete button callback
    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }


    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {

        CartItem item = cartItems.get(position);
        holder.titleTxt.setText(item.getTitle());
        holder.addressTxt.setText(item.getAddress());
        holder.priceTxt.setText("$"+item.getPrice());
        holder.scoreTxt.setText(""+item.getScore());

        Glide.with(context)
                .load(item.getPic())
                .into(holder.pic);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listener.onDeleteClick(holder.getAdapterPosition());
                listener.onDeleteClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, addressTxt, priceTxt, scoreTxt;
        ImageView pic,deleteImg;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
            deleteImg=itemView.findViewById(R.id.deleteImg);
            pic = itemView.findViewById(R.id.pic);

        }
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            cartItems = new ArrayList<>(originalList); // Reset to original list
        } else {
            List<CartItem> filteredList = new ArrayList<>();
            for (CartItem item : originalList) {
                // Check if title or address contains the query
                if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getAddress().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            cartItems = filteredList; // Update displayed list
        }
        notifyDataSetChanged(); // Refresh RecyclerView
    }

    public void removeItem(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());  // To refresh the view
    }
}
