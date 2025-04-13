package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileUsernameTextView, profileEmailTextView, profilePhoneTextView, profileAddressTextView;
    private EditText profileCurrentPasswordEditText, profileNewPasswordEditText;
    private Button profileChangePasswordButton, profileBackButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializálás
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI elemek
        profileUsernameTextView = findViewById(R.id.profile_username);
        profileEmailTextView = findViewById(R.id.profile_email);
        profilePhoneTextView = findViewById(R.id.profile_phone);
        profileAddressTextView = findViewById(R.id.profile_address);

        profileCurrentPasswordEditText = findViewById(R.id.profile_current_password);
        profileNewPasswordEditText = findViewById(R.id.profile_new_password);

        profileChangePasswordButton = findViewById(R.id.profile_change_password_button);
        profileBackButton = findViewById(R.id.profile_back_button);

        // Felhasználói adatok betöltése Firebase-ből
        loadUserData();

        // Jelszó változtatás logika
        profileChangePasswordButton.setOnClickListener(v -> {
            String currentPassword = profileCurrentPasswordEditText.getText().toString();
            String newPassword = profileNewPasswordEditText.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Kérjük, töltse ki mindkét mezőt.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Jelszó változtatás validálása és módosítása
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String email = user.getEmail();

                // Létrehozzuk az AuthCredential objektumot
                AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

                // Re-Authenticate
                ((com.google.firebase.auth.FirebaseUser) user).reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Ha sikeres az újra-hitelesítés, frissíthetjük a jelszót
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Jelszó sikeresen módosítva.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Hiba történt a jelszó módosítása során.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Hibás jelenlegi jelszó.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Vissza gomb
        profileBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ShoplistActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phone");
                String address = documentSnapshot.getString("address");

                profileUsernameTextView.setText(username);
                profileEmailTextView.setText(email);
                profilePhoneTextView.setText(phone);
                profileAddressTextView.setText(address);
            }
        });
    }
}
