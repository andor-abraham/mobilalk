package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListLayout = findViewById(R.id.cart_list_layout);
        Button btnPurchase = findViewById(R.id.btnPurchase);

        List<String> items = CartManager.getItems();

        if (items.isEmpty()) {
            returnToShop();
            return;
        }

        for (String item : items) {
            View itemView = createCartItemView(item);
            cartListLayout.addView(itemView);
        }

        btnPurchase.setOnClickListener(v -> {
            List<String> itemsToDelete = CartManager.getItems();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("tiles").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String name = doc.getString("name");
                    if (itemsToDelete.contains(name)) {
                        db.collection("tiles").document(doc.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Hiba a(z) " + name + " törlésekor", Toast.LENGTH_SHORT).show();
                                });
                    }
                }

                CartManager.clear();
                Toast.makeText(this, "Sikeres vásárlás! Köszönjük.", Toast.LENGTH_SHORT).show();
                returnToShop();

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Nem sikerült elérni az adatbázist.", Toast.LENGTH_SHORT).show();
            });
        });

    }

    private View createCartItemView(String itemName) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 16, 16, 16);

        TextView itemText = new TextView(this);
        itemText.setText(itemName);
        itemText.setTextSize(18f);
        itemText.setTypeface(null, Typeface.BOLD);
        itemText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemText.getLayoutParams();
        params.topMargin = 10;
        params.bottomMargin = 10;
        itemText.setLayoutParams(params);

        Button deleteButton = new Button(this);
        deleteButton.setText("Törlés a kosárból");
        deleteButton.setBackgroundResource(R.drawable.rounded_button);
        deleteButton.setTextColor(getResources().getColor(android.R.color.white));

        deleteButton.setOnClickListener(v -> {
            CartManager.removeItem(itemName);
            cartListLayout.removeView(container);

            if (CartManager.isEmpty()) {
                returnToShop();
            }
        });

        container.addView(itemText);
        container.addView(deleteButton);

        return container;
    }

    private void returnToShop() {
        Intent intent = new Intent(CartActivity.this, ShoplistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
