package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.haclicker.DataStructure.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), ConsoleScreen.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Delete send temp classroom to server.
        sendTempData();
        sendTempStuResponse();
        mAuth = FirebaseAuth.getInstance();
        createRequest();
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendTempData() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = db.getReference("ClassRooms");
        List<String> choices = new ArrayList<String>() {
            {add("A");
            add("B");
            add("C");
            add("D");
            add("E");
            }
        };
        List<Question> questionList = new ArrayList<>();
        questionList.add(new Question("fuck?", 0,
                choices));
        questionList.add(new Question("damn?", 01,
                choices));
        List<String> corans = new ArrayList<String>() {{add("A"); add("E");}};

        questionList.get(0).setCorrectAns(corans);
        ref.child("CS126").setValue(new ClassRoom("0", "CS126", "Michael Woodley", questionList));
        System.out.println("fuck");
    }

    public void sendTempStuResponse() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = db.getReference("ClassRooms").child("CS126").child("StudentResponse");
        List<String> ans = new ArrayList<>();
        ans.add("A");
        ans.add("B");
        StudentResponse studentResponse1 = new StudentResponse("Yucheng Jiang",
                "ycj2@illinois.edu", ans, 0, System.currentTimeMillis());
        StudentResponse studentResponse2 = new StudentResponse("Yueqi Jiang",
                "yueqij2@illinois.edu", ans, 0, System.currentTimeMillis());
        ref.child(studentResponse1.getQuestionID() + "").child(studentResponse1.getStudentName()).setValue(studentResponse1);
        ref.child(studentResponse2.getQuestionID() + "").child(studentResponse2.getStudentName()).setValue(studentResponse2);
    }
}