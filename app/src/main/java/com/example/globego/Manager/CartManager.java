package com.example.globego.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.globego.Domain.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    public static List<CartItem> cartList = new ArrayList<>();

    //
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS_KEY = "cart_items";


    // Add item to cart
    public static void addToCart(CartItem item,Context context) {
        cartList.add(item);
        saveCart(context);
    }

    // Get the cart list
    public static List<CartItem> getCartList() {
        return cartList;
    }

    // Remove item from cart by index
    public static void removeFromCart(int index,Context context) {
        if (index >= 0 && index < cartList.size()) {
            cartList.remove(index);
            saveCart(context);
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
    public static void saveCart(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartList);
        editor.putString(CART_ITEMS_KEY, json);
        editor.apply();  // Asynchronously save changes
    }

    // Load cart items from SharedPreferences
    public static void loadCart(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CART_ITEMS_KEY, null);

        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
        cartList = gson.fromJson(json, type);

        if (cartList == null) {
            cartList = new ArrayList<>();
        }
    }

    // Clear the cart
    public static void clearCart(Context context) {
        cartList.clear();  // Clear the in-memory cart list

        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CART_ITEMS_KEY);  // Remove the cart items key from SharedPreferences
        editor.apply();  // Apply changes asynchronously
    }

}
