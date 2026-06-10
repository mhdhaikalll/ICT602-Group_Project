package com.example.ict602_groupproject;

import android.content.Intent;
import android.credentials.CredentialManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private CredentialManager credentialManager;
    EditText et_reg_email, et_reg_password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        //configure sign-in with Google
        credentialManager = CredentialManager.create(this);

        findViewById(R.id.btn_continue_with_google).setOnClickListener(view -> signInWithGoogle());

    }
    private void signInWithGoogle() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                Runnable::run,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Credential credential = result.getCredential();

                        if (credential instanceof GoogleIdTokenCredential) {
                            GoogleIdTokenCredential googleIdTokenCredential = (GoogleIdTokenCredential) credential;
                            String idToken = googleIdTokenCredential.getIdToken();
                            firebaseAuthWithGoogle(idToken);
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Log.e("AuthError", "Credential Manager failed: " + e.getMessage());
                        runOnUiThread(() ->
                                Toast.makeText(getApplicationContext(), "Sign-in dismissed or failed.", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Welcome " + (user != null ? user.getDisplayName() : "User"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Firebase Link Failure: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown Connection Error"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void registerUser(View view) {
        String email = et_reg_email.getText().toString().trim();
        String password = et_reg_password.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        //dont know why it use regex instead of EMAIL_ADDRESS
        if(!Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+").matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
    public void switchLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}