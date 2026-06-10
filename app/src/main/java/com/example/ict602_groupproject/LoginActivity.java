package com.example.ict602_groupproject;

import android.content.Intent;
import android.credentials.CredentialManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private CredentialManager credentialManager;
    EditText email, password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);

        mAuth = FirebaseAuth.getInstance();

        credentialManager = CredentialManager.create(this);

        findViewById(R.id.btn_google_signin).setOnClickListener(view -> signInWithGoogle());
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
                LoginActivity.this,
                request,
                null,
                ContextCompat.getMainExecutor(LoginActivity.this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        Credential credential = response.getCredential();
                        String idToken = null;

                        // 1. Try explicit class cast first
                        if (credential instanceof GoogleIdTokenCredential) {
                            idToken = ((GoogleIdTokenCredential) credential).getIdToken();
                        }
                        // 2. Comprehensive Bundle Key check (Solves version parsing issues)
                        else {
                            try {
                                Bundle bundle = credential.getData();
                                if (bundle != null) {
                                    // Look for standard library key
                                    idToken = bundle.getString("androidx.credentials.BUNDLE_KEY_ID_TOKEN");

                                    // Fallback 1: Check native OAuth token mapping key
                                    if (idToken == null || idToken.isEmpty()) {
                                        idToken = bundle.getString("idToken");
                                    }

                                    // Fallback 2: Check standard credential schema key
                                    if (idToken == null || idToken.isEmpty()) {
                                        idToken = bundle.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("AuthError", "Bundle extraction exception: " + e.getMessage());
                            }
                        }

                        // 3. Final validation check
                        if (idToken != null && !idToken.isEmpty()) {
                            Log.d("AuthSuccess", "Token successfully parsed! Forwarding to Firebase.");
                            firebaseAuthWithGoogle(idToken);
                        } else {
                            // Log the complete bundle keys to see exactly what your phone returned
                            if (credential.getData() != null) {
                                Log.e("AuthError", "Available Bundle Keys: " + credential.getData().keySet().toString());
                            }
                            Log.e("AuthError", "Failed to isolate a valid IdToken string. Credential Type: " + credential.getType());
                            Toast.makeText(LoginActivity.this, "Failed to parse Google login token.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Log.e("AuthError", "Credential Manager Failed: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "Sign-in dismissed or failed.", Toast.LENGTH_SHORT).show();
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
    private void login(View view){
        //TODO
    }
    private void switchRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}