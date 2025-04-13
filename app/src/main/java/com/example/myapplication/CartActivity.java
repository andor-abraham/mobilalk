package com.example.myapplication;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListLayout = findViewById(R.id.cart_list_layout);

        List<String> items = CartManager.getItems();

        if (items.isEmpty()) {
            returnToShop();
            return;
        }

        for (String item : items) {
            View itemView = createCartItemView(item);
            cartListLayout.addView(itemView);
        }
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
