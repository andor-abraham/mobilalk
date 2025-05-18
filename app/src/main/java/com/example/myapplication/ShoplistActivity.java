package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ShoplistActivity extends AppCompatActivity {

    private LinearLayout itemContainer;
    private FirebaseFirestore firestore;
    private Button cartButton, profileButton, uploadButton;
    private LinearLayout buttonRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoplist);

        itemContainer = findViewById(R.id.item_container);
        buttonRow     = findViewById(R.id.buttonRow);
        cartButton    = findViewById(R.id.cartButton);
        profileButton = findViewById(R.id.profileButton);
        uploadButton  = findViewById(R.id.btnUploadProduct);

        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            buttonRow.setVisibility(View.VISIBLE);
            cartButton.setVisibility(View.VISIBLE);
            profileButton.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);

            cartButton.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
            profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
            uploadButton.setOnClickListener(v -> startActivity(new Intent(this, UploadProductActivity.class)));
        } else {
            buttonRow.setVisibility(View.GONE);
        }

        getProductsCount();
        getSquareTilesCount();
        getRectangleTilesCount();
    }

    private void loadItemsFromFirestore() {
        firestore.collection("tiles")
                .orderBy("name")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        String name         = doc.getString("name");
                        String description  = doc.getString("description");
                        String pictureName  = doc.getString("picture");
                        Long sizex          = doc.getLong("sizex");
                        Long sizey          = doc.getLong("sizey");
                        String documentId   = doc.getId();

                        int imageResId = getResources().getIdentifier(pictureName, "drawable", getPackageName());
                        addItemToLayout(name, description, imageResId, sizex, sizey, documentId, pictureName);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", "Hiba a lekérdezésnél", e);
                    Toast.makeText(this, "Hiba a csempék betöltésekor", Toast.LENGTH_SHORT).show();
                });
    }

    private void addItemToLayout(String name, String description,
                                 int imageResId, Long sizex, Long sizey,
                                 String documentId, String pictureName) {

        View itemView = LayoutInflater.from(this).inflate(R.layout.item_tile, itemContainer, false);

        ImageView imageView  = itemView.findViewById(R.id.product_image);
        TextView  nameView   = itemView.findViewById(R.id.product_name);
        TextView  descView   = itemView.findViewById(R.id.product_description);
        Button addBtn    = itemView.findViewById(R.id.add_to_cart_button);
        Button delBtn    = itemView.findViewById(R.id.delete_product_button);
        Button editBtn   = itemView.findViewById(R.id.edit_product_button);

        nameView.setText(name);
        descView.setText(description + "\nMéret: " + sizex + "x" + sizey + " cm");
        imageView.setImageResource(imageResId);

        addBtn.setOnClickListener(v -> {
            CartManager.addItem(name);
            Toast.makeText(this, name + " hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
        });

        delBtn.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage("Biztosan törölni akarod a terméket?")
                .setPositiveButton("Igen", (d,w) ->
                        firestore.collection("tiles").document(documentId)
                                .delete()
                                .addOnSuccessListener(a -> {
                                    Toast.makeText(this, "Termék törölve", Toast.LENGTH_SHORT).show();
                                    itemContainer.removeView(itemView);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Hiba a törlés során", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Nem", null)
                .show());

        editBtn.setOnClickListener(v -> {
            Intent in = new Intent(this, EditProductActivity.class);
            in.putExtra("productId", documentId);
            in.putExtra("name", name);
            in.putExtra("description", description);
            in.putExtra("sizex", sizex);
            in.putExtra("sizey", sizey);
            in.putExtra("picture", pictureName);
            startActivity(in);
        });

        itemContainer.addView(itemView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemContainer.removeAllViews();
        loadItemsFromFirestore();
    }

    //    Komplex lekérdezés 1
    private void getProductsCount() {
        firestore.collection("tiles")
                .orderBy("name")
                .limit(100)
                .get()
                .addOnSuccessListener(q -> Log.d("ProductCount", "Összes termék: " + q.size()));
    }

    //    Komplex lekérdezés 2
    private void getSquareTilesCount() {
        firestore.collection("tiles")
                .whereEqualTo("sizex", 20)
                .whereEqualTo("sizey", 20)
                .orderBy("name")
                .limit(50)
                .get()
                .addOnSuccessListener(q -> Log.d("SquareTiles", "Négyzet alakú termékek: " + q.size()));
    }

    //    Komplex lekérdezés 3
    private void getRectangleTilesCount() {
        firestore.collection("tiles")
                .whereGreaterThan("sizex", 0)
                .whereLessThan("sizex", 1000)
                .orderBy("sizex")
                .limit(100)
                .get()
                .addOnSuccessListener(q -> {
                    int count = 0;
                    for (QueryDocumentSnapshot d : q) {
                        Long x = d.getLong("sizex");
                        Long y = d.getLong("sizey");
                        if (x != null && y != null && !x.equals(y)) count++;
                    }
                    Log.d("RectangleTiles", "Téglalap alakú termékek: " + count);
                });
    }
}
