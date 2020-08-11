package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConsoleScreen extends AppCompatActivity {

    Button logout;
    TextView name, mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_screen);

        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            name.setText("Name: " + signInAccount.getDisplayName());
            mail.setText("Email: " + signInAccount.getEmail());
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        finish();
    }
}