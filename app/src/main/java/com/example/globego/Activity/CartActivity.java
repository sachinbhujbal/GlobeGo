package com.example.globego.Activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.globego.Adapter.CartAdapter;
import com.example.globego.Domain.CartItem;
import com.example.globego.Domain.ItemDomain;
import com.example.globego.Manager.CartManager;
import com.example.globego.R;
import com.example.globego.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.TestOnly;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity implements CartAdapter.OnItemClickListener, PaymentResultListener {

    private static final String TAG = CartActivity.class.getSimpleName();;
    ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private double totalPrice = 0.0;
    private ArrayList<CartItem> cartList = new ArrayList<>();

    private ItemDomain object;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CartActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding=ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        CartManager.loadCart(this,userId);
        CartManager.loadCartFromFirebase(userId, new CartManager.FirebaseCartLoadCallback() {

            @Override
            public void onCartLoaded(List<CartItem> cartItems) {
            //    Toast.makeText(CartActivity.this, "Cart loaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        // Initialize cartList with items from CartManager if not null
        ArrayList<CartItem> cartItemsFromManager = (ArrayList<CartItem>) CartManager.getCartList();
        if (cartItemsFromManager != null && !cartItemsFromManager.isEmpty()) {
            cartList.addAll(cartItemsFromManager);
        }

        cartAdapter = new CartAdapter(this, cartList, this);
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cartRecyclerView.setAdapter(cartAdapter);

        if (cartList != null && !cartList.isEmpty()) {
            calculateTotalPrice();
            updatePriceText();
        }
        else {
            binding.priceTxt.setText("$0");
        }

        Checkout.preload(getApplicationContext());

        binding.makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartList.isEmpty()){
                    Toast.makeText(CartActivity.this,"Amount will be Converted to INR",Toast.LENGTH_SHORT).show();
                    startPayment();
                }else{
                    Toast.makeText(CartActivity.this, "Your Cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startPayment() {

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", ""); // Default value is an empty string
        String email = sharedPreferences.getString("email", "");
        String mbNo = sharedPreferences.getString("mbNo", "");

        Checkout checkout = new Checkout();
       // checkout.setKeyID("rzp_test_RbeNMtZadToDC0");
        checkout.setKeyID("rzp_test_4cqx4vq0frQxaH");

        checkout.setImage(R.drawable.bed);

        final Activity activity = this;


        try {
            JSONObject options = new JSONObject();

            options.put("name", name);
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
       //     options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            String amountInPaise = String.valueOf((int) (totalPrice*100));
            options.put("amount", amountInPaise);
            options.put("prefill.email",email);
            options.put("prefill.contact", mbNo);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    private void calculateTotalPrice() {
        totalPrice = 0.0;  // Reset total price
        if (cartList != null && !cartList.isEmpty()) {
            for (CartItem item : cartList) {
                totalPrice += item.getPrice();  // Add each item's price to total
            }
        }
    }

    // Method to update the total price on screen
    private void updatePriceText() {
        binding.priceTxt.setText("$" + totalPrice);
    }

    @Override
    public void onDeleteClick(int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (cartList != null && !cartList.isEmpty() && position < cartList.size()) {
            // Remove the item from the cart list
            CartItem item = cartList.get(position);
            cartList.remove(position);
            CartManager.removeFromCart(position,CartActivity.this,userId);
            cartAdapter.notifyItemRemoved(position);
            totalPrice -= item.getPrice();
            updatePriceText();

            cartAdapter.notifyItemRangeChanged(position, cartList.size());
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        double finalTotalPrice = totalPrice;
        getTicket();
        clearCart();
        Toast.makeText(this,"Payment Successful",Toast.LENGTH_SHORT).show();
        sendPaymentNotification(finalTotalPrice);

    }
    // Method to clear the cart after payment
    private void clearCart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartList.clear();  // Clear all items from the cart list
        CartManager.clearCart(this,userId);  // Save the cleared cart to SharedPreferences
        cartAdapter.notifyDataSetChanged();  // Notify adapter that data has changed
        totalPrice = 0.0;  // Reset total price
        updatePriceText();  // Update the price display to show $0
    }
    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this,"Payment Failed",Toast.LENGTH_SHORT).show();
    }

// take all the info from ItemDomain and then pass it to TicketActivity
    private void getTicket() {

        if (cartList != null && !cartList.isEmpty()) {

            CartItem firstCartItem = cartList.get(0);
            // imp
            object = new ItemDomain();
            object.setTitle(firstCartItem.getTitle());
            object.setPic(firstCartItem.getPic());
            object.setDuration(firstCartItem.getDuration());
            object.setTimeTour(firstCartItem.getTimeTour());
            object.setDateTour(firstCartItem.getDateTour());
            object.setTourGuideName(firstCartItem.getTourGuideName());
            object.setTourGuidePic(firstCartItem.getTourGuidePic());
            object.setBed(firstCartItem.getBed());
            firstCartItem.setItemDomain(object);

            Intent intent = new Intent(CartActivity.this, TicketActivity.class);
            intent.putExtra("object",object);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(this, "No items in the cart to generate a ticket.", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendPaymentNotification(double finalTotalPrice) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "payment_success_channel";
        // For Android 8.0+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Payment Success",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.bed); // Replace with your app icon
        Bitmap bigPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bell_icon); // Replace with payment_success_image

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channelId)
                    .setSmallIcon(R.drawable.bed) // Replace with your icon
                    .setLargeIcon(largeIcon)  // Add large icon
                    .setContentTitle("Payment Successful")
                    .setContentText("Your payment of ₹" + finalTotalPrice + " is complete.")
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigPictureStyle()
                            .bigPicture(bigPicture) // Show a big image
                            .bigLargeIcon((Bitmap) null)) // Hide the large icon when the big picture is shown
                    .build();
        }

        notificationManager.notify(1, notification);
    }
}