package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class ShoplistActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShoplistActivity.class.getName();

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shoplist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Sikeres autentikáció!");
        }
        else {
            Log.d(LOG_TAG, "Sikertelen autentikáció!");
            finish();
        }

        Button cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShoplistActivity.this, CartActivity.class);
            startActivity(intent);
        });

        if (!user.isAnonymous()) {
            cartButton.setVisibility(View.VISIBLE);
        }

        Button add1 = findViewById(R.id.button1);
        Button add2 = findViewById(R.id.button2);
        Button add3 = findViewById(R.id.button3);
        Button add4 = findViewById(R.id.button4);
        Button add5 = findViewById(R.id.button5);

        add1.setOnClickListener(v -> {
            CartManager.addItem("Fekete márványos csempe");
            Toast.makeText(this, "Hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
        });
        add2.setOnClickListener(v -> {
            CartManager.addItem("Fehér márványos csempe");
            Toast.makeText(this, "Hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
        });
        add3.setOnClickListener(v -> {
            CartManager.addItem("Homokhatású csempe");
            Toast.makeText(this, "Hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
        });
        add4.setOnClickListener(v -> {
            CartManager.addItem("Fekete színű csempe");
            Toast.makeText(this, "Hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
        });
        add5.setOnClickListener(v -> {
            CartManager.addItem("Barna színű csempe");
            Toast.makeText(this, "Hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
        });


    }
}