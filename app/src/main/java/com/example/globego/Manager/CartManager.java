package com.example.globego.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.globego.Domain.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    public static List<CartItem> cartList = new ArrayList<>();

    private static final String CART_PREFS = "cart_prefs";


    // Add item to cart
    public static void addToCart(CartItem item,Context context, String userId) {
        loadCart(context, userId);
        if(!isItemInCart(item)) {
            cartList.add(item);
            saveCart(context, userId);
            saveCartToFirebase(userId);
        }
    }

    // Get the cart list
    public static List<CartItem> getCartList() {
       // return cartList;
        return new ArrayList<>(cartList);
    }

    // Remove item from cart by index
    public static void removeFromCart(int index,Context context,String userId) {
        if (index >= 0 && index < cartList.size()) {
            cartList.remove(index);
            saveCart(context,userId);
            saveCartToFirebase(userId);
        }
    }
    public static boolean isItemInCart(CartItem newItem) {
        for (CartItem item : cartList) {
            if (item.getTitle().equals(newItem.getTitle()) &&
                    item.getAddress().equals(newItem.getAddress())) {
                return true;
            }
        }
        return false;
    }
    // Save cart items to SharedPreferences
    public static void saveCart(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartList);
        editor.putString(CART_ITEMS_KEY(userId), json);
        editor.apply();  // Asynchronously save changes
    }

    // Load cart items from SharedPreferences
    public static void loadCart(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CART_ITEMS_KEY(userId), null);

        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
       // cartList = gson.fromJson(json, type);
        List<CartItem> loadedCart = gson.fromJson(json, type);

//        if (cartList == null) {
//            cartList = new ArrayList<>();
//        }
        if (loadedCart != null) {
            cartList.clear();
            cartList.addAll(loadedCart); // Preserve existing cart items
        } else {
            cartList = new ArrayList<>(); // Initialize if null
        }
    }

    // Clear the cart
    public static void clearCart(Context context, String userId) {
        cartList.clear();

        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CART_ITEMS_KEY(userId));  // Remove the cart items key from SharedPreferences
        editor.apply();  // Apply changes asynchronously

       // Clear from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("User_Carts")
                .child(userId);
        databaseReference.removeValue();
    }
    // Generate user-specific key for cart items
    private static String CART_ITEMS_KEY(String userId) {
        return "cart_items_" + userId;
    }

    // Save cart items to Firebase
    public static void saveCartToFirebase(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("User_Carts")
                .child(userId);

        databaseReference.setValue(cartList)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Cart saved to Firebase successfully.");
                    } else {
                        System.err.println("Failed to save cart to Firebase: " + task.getException());
                    }
                });
    }

    // Load cart items from Firebase
    public static void loadCartFromFirebase(String userId, FirebaseCartLoadCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("User_Carts")
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cartList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        CartItem item = dataSnapshot.getValue(CartItem.class);
                        if (item != null) {
                            cartList.add(item);
                        }
                    }catch (Exception e){
                        Log.e("FirebaseError", "Error parsing CartItem: " + e.getMessage());
                    }
                }
                callback.onCartLoaded(cartList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    // Callback interface for Firebase cart loading
    public interface FirebaseCartLoadCallback {
        void onCartLoaded(List<CartItem> cartItems);
        void onError(Exception e);
    }

}

