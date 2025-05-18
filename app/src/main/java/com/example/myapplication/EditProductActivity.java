package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    private EditText edtName, edtDesc, edtSizeX, edtSizeY;
    private GridView imageGrid;
    private Button btnSave;

    private final String[] imageNames = {"csempe1","csempe2","csempe3",
            "csempe4","csempe5","csempe6",
            "csempe7","csempe8","csempe9"};

    private String selectedImage;
    private String originalName;
    private String documentId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        edtName  = findViewById(R.id.editTextName);
        edtDesc  = findViewById(R.id.editTextDescription);
        edtSizeX = findViewById(R.id.editTextSizex);
        edtSizeY = findViewById(R.id.editTextSizey);
        imageGrid = findViewById(R.id.imageGrid);
        btnSave  = findViewById(R.id.buttonSave);

        firestore = FirebaseFirestore.getInstance();

        Intent in = getIntent();
        originalName  = in.getStringExtra("name");
        documentId    = in.getStringExtra("productId");
        selectedImage = in.getStringExtra("picture");

        edtName.setText(originalName);
        edtDesc.setText(in.getStringExtra("description"));
        edtSizeX.setText(String.valueOf(in.getLongExtra("sizex",0)));
        edtSizeY.setText(String.valueOf(in.getLongExtra("sizey",0)));

        setupImageGrid();
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void setupImageGrid() {
        List<Integer> imageIds = new ArrayList<>();
        for (String n : imageNames) {
            int resId = getResources().getIdentifier(n,"drawable",getPackageName());
            imageIds.add(resId);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_list_item_1, imageIds) {
            @Override
            public View getView(int pos, View convert, ViewGroup parent) {
                ImageView iv = new ImageView(EditProductActivity.this);
                iv.setImageResource(imageIds.get(pos));
                iv.setLayoutParams(new GridView.LayoutParams(200,200));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (imageNames[pos].equals(selectedImage)) {
                    iv.setPadding(6,6,6,6);
                    iv.setBackgroundResource(android.R.color.holo_blue_light);
                }
                return iv;
            }
        };
        imageGrid.setAdapter(adapter);

        imageGrid.setOnItemClickListener((p,v,pos,id)->{
            selectedImage = imageNames[pos];
            Toast.makeText(this,"Kiválasztva: "+selectedImage,Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        });
    }

    private void saveChanges(){
        String newName = edtName.getText().toString().trim();
        String desc    = edtDesc.getText().toString().trim();
        String sxStr   = edtSizeX.getText().toString().trim();
        String syStr   = edtSizeY.getText().toString().trim();

        if(TextUtils.isEmpty(newName)||TextUtils.isEmpty(desc)||TextUtils.isEmpty(sxStr)||TextUtils.isEmpty(syStr)){
            Toast.makeText(this,"Minden mezőt ki kell tölteni!",Toast.LENGTH_SHORT).show();
            return;
        }
        long sx = Long.parseLong(sxStr);
        long sy = Long.parseLong(syStr);

        if(documentId!=null){
            firestore.collection("tiles").document(documentId)
                    .update("name",newName,
                            "description",desc,
                            "sizex",sx,
                            "sizey",sy,
                            "picture",selectedImage)
                    .addOnSuccessListener(a->returnToList())
                    .addOnFailureListener(e->Toast.makeText(this,"Hiba mentéskor",Toast.LENGTH_SHORT).show());
            return;
        }

        firestore.collection("tiles")
                .whereEqualTo("name",originalName)
                .get()
                .addOnSuccessListener(q->{
                    for(QueryDocumentSnapshot snap:q){
                        firestore.collection("tiles").document(snap.getId())
                                .update("name",newName,
                                        "description",desc,
                                        "sizex",sx,
                                        "sizey",sy,
                                        "picture",selectedImage)
                                .addOnSuccessListener(a->returnToList());
                    }
                })
                .addOnFailureListener(e->Toast.makeText(this,"Hiba mentéskor",Toast.LENGTH_SHORT).show());
    }

    private void returnToList(){
        Toast.makeText(this,"Sikeresen mentve!",Toast.LENGTH_SHORT).show();
        Intent back = new Intent(this, ShoplistActivity.class);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(back);
        finish();
    }
}
