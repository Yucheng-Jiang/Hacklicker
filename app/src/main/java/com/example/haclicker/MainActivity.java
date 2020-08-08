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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainScreen.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ClassRoom classroom1 = new ClassRoom("39324", "CS225", "Fat guy", null);
//        ClassRoom classroom2 = new ClassRoom("33942", "CS233", "short guy", null);
//        createClassroom(classroom1);
//        createClassroom(classroom2);
//        List<String> choices = new ArrayList<String>(){{add("A"); add("B"); add("C"); add("D"); add("D"); add("E");}};
//        Question question1 = new Question("fuck?", 0, choices);
//        Question question2 = new Question("dayaaam", 1, choices);
//        addQuestion(question1, classroom1);
//        addQuestion(question2, classroom1);
//        sendCorrectAnswer(question2, "C", classroom1);
//        addQuestion(question1, classroom2);
//        deleteQuestion(question1, classroom1);
//        deleteQuestion(question1, classroom2);
//        //TEST CODE ENDS HERE

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

    //TODO: delete the following test code
    public static void addQuestion(final Question question, ClassRoom classroom) {

        List<Question> questionList;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ClassRooms")
                .child(classroom.getClassID()).child("Questions");
        if (classroom.getQuestions() != null) {
            questionList = classroom.getQuestions();
            questionList.add(question);
        } else {
            questionList = new ArrayList<Question>(){{add(question);}};
        }
        classroom.setQuestions(questionList);
        ref.setValue(questionList);
    }

    public static void createClassroom(ClassRoom classroom) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = db.getReference("ClassRooms").child(classroom.getClassID());
        ref.setValue(classroom);
    }

    public static void deleteQuestion(Question question, ClassRoom classroom) {

        List<Question> questionList = classroom.getQuestions();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions");
        questionList.remove(question);

        if (questionList.size() == 0 || questionList == null) {
            ref.setValue(null);
        }
        classroom.setQuestions(questionList);
        ref.child(question.getQuestionId() + "").removeValue();
    }

    public static void sendCorrectAnswer(Question question, String answer, ClassRoom classroom) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions")
                .child(question.getQuestionId() + "");
        ref.child("answer").setValue(answer);
    }
}
