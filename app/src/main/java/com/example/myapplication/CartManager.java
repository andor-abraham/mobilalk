package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<String> cartItems = new ArrayList<>();

    public static void addItem(String item) {
        cartItems.add(item);
    }

    public static void removeItem(String item) {
        cartItems.remove(item);
    }

    public static List<String> getItems() {
        return new ArrayList<>(cartItems);
    }

    public static void clear() {
        cartItems.clear();
    }

    public static boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
