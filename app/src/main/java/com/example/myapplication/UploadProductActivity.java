package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadProductActivity extends AppCompatActivity {

    private EditText edtName, edtDescription, edtSizeX, edtSizeY;

    private Button btnSave;
    private FirebaseFirestore firestore;

    private String selectedImageName = null;

    private void setupImageGrid() {
        GridView imageGrid = findViewById(R.id.imageGrid);

        String[] imageNames = {"csempe1", "csempe2", "csempe3", "csempe4", "csempe5", "csempe6", "csempe7", "csempe8", "csempe9"};

        List<Integer> imageIds = new ArrayList<>();
        for (String name : imageNames) {
            int resId = getResources().getIdentifier(name, "drawable", getPackageName());
            imageIds.add(resId);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, imageIds) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(UploadProductActivity.this);
                imageView.setImageResource(imageIds.get(position));
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return imageView;
            }
        };

        imageGrid.setAdapter(adapter);

        imageGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedImageName = imageNames[position];
            Toast.makeText(this, "Kiválasztva: " + selectedImageName, Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadproduct);

        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtSizeX = findViewById(R.id.edtSizeX);
        edtSizeY = findViewById(R.id.edtSizeY);
        btnSave = findViewById(R.id.btnSave);

        firestore = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveProduct());

        setupImageGrid();

    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String sizexStr = edtSizeX.getText().toString().trim();
        String sizeyStr = edtSizeY.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(sizexStr) || TextUtils.isEmpty(sizeyStr)) {
            Toast.makeText(this, "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        int sizex = Integer.parseInt(sizexStr);
        int sizey = Integer.parseInt(sizeyStr);

        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("description", description);
        product.put("sizex", sizex);
        product.put("sizey", sizey);
        product.put("picture", selectedImageName);

        firestore.collection("tiles")
                .add(product)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Sikeres feltöltés!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show());


        Intent intent = new Intent(UploadProductActivity.this, ShoplistActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}
