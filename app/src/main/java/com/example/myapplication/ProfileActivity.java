package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private TextView txtEmail;
    private EditText edtOldPassword, edtNewPassword;
    private Button btnChangePassword, btnSavePassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtEmail = findViewById(R.id.emailText);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            txtEmail.setText("Email: " + email);

            String userId = currentUser.getUid();

            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                            } else {
//                                Toast.makeText(ProfileActivity.this, "Nincs adat a Firestore-ban!", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Nincs bejelentkezve felhasználó!", Toast.LENGTH_SHORT).show();
        }

        btnChangePassword.setOnClickListener(v -> {
            edtOldPassword.setVisibility(View.VISIBLE);
            edtNewPassword.setVisibility(View.VISIBLE);
            btnSavePassword.setVisibility(View.VISIBLE);
        });

        btnSavePassword.setOnClickListener(v -> {
            String oldPw = edtOldPassword.getText().toString();
            String newPw = edtNewPassword.getText().toString();

            if (oldPw.isEmpty() || newPw.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Mindkét jelszó mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPw);

                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPw).addOnCompleteListener(passwordTask -> {
                            if (passwordTask.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Jelszó sikeresen megváltoztatva!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Hiba történt a jelszó változtatásakor!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "A régi jelszó hibás!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnDeleteAccount.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Fiók törölve!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Hiba történt a törléskor! (Lehet újra hitelesítés kell)", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }
}
