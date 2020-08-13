package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.StudentHistoryEntity;
import com.example.haclicker.DataStructure.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryClassScreen extends AppCompatActivity {
    TextView emptyReminder;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_class_screen);
        emptyReminder = findViewById(R.id.emptyReminder);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        updateUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        Map<String, List<StudentHistoryEntity>> fireBaseData = new HashMap<>();
        // get all existing questions on the firebase server
        if (fireBaseData != null) {
            // clear previous UI components
            LinearLayout questionListLayout = findViewById(R.id.question_list);
            questionListLayout.removeAllViews();
            // combine questions on the server and questions not published yet.
            // if there's no question, set empty question reminder
            if (fireBaseData.size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
            } else {
                emptyReminder.setVisibility(View.VISIBLE);
            }

            for (final Map.Entry<String, List<StudentHistoryEntity>> entry : fireBaseData.entrySet()) {
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());
                // inflate from chunk_question
                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        questionListLayout, false);
                // set question text view with description
                Button classIdBtn = questionChunk.findViewById(R.id.question_txt);
                classIdBtn.setText(entry.getKey());

                // set each question chunk onClickListener
                classIdBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), HistoryQuestionScreen.class);
                        intent.putExtra("classID", entry.getKey());
                        startActivity(intent);
                        finish();
                    }
                });
                // populate the chunk to linear layout
                questionListLayout.addView(questionChunk);
            }
        } else {
            // if the question list is null, set empty reminder.
            emptyReminder.setText("No class found");
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: clear stored data
        Intent intent = new Intent(getApplicationContext(), HostScreen.class);;

        startActivity(intent);
        finish();
    }

    private void fetchStudentHistory() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Student").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(email)) {

                                    List<String> classIDs = new ArrayList<>();
                                    Map<String, Object> data = document.getData();
                                    if (data != null) {
                                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                                            classIDs.add(entry.getValue().toString());
                                        }
                                    }

                                    for (final String id : classIDs) {
                                        document.getReference().collection(id)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                Map<String, List<StudentHistoryEntity>> studentHistory = new HashMap<>();
                                                if (task.isSuccessful()) {
                                                    List<StudentHistoryEntity> ans = new ArrayList<>();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        StudentHistoryEntity entity = document.toObject(StudentHistoryEntity.class);
                                                        ans.add(entity);
                                                    }
                                                    studentHistory.put(id, ans);
                                                }
                                            }
                                        });
                                    }
                                }
                                //Log.d("TAG", document.getId() + " => " + document.getData());

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void fetchTeacherHistory() {

        Map<String, List<StudentHistoryEntity>> studentHistory = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Host").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> hostEmails = new ArrayList<>();
                                Map<String, Object> data = document.getData();
                                if (data != null) {
                                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                                        hostEmails.add(entry.getValue().toString());
                                    }

                                    for (String stu : hostEmails) {
                                        document.getReference().collection(stu)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                List<String> stuEmails = new ArrayList<>();
                                                                Map<String, Object> stuData = document.getData();
                                                                for (Map.Entry<String, Object> entry : stuData.entrySet()) {
                                                                    stuEmails.add(entry.getValue().toString());
                                                                }
                                                                for (final String singleStu : stuEmails) {
                                                                    //get all questions a specific student has answered
                                                                    document.getReference().collection(singleStu)
                                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Map<String, List<StudentHistoryEntity>> hostHistory = new HashMap<>();
                                                                                List<StudentHistoryEntity> entities = new ArrayList<>();
                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                    StudentHistoryEntity entity = document.toObject(StudentHistoryEntity.class);
                                                                                    entities.add(entity);
                                                                                }
                                                                                hostHistory.put(singleStu, entities);
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }
                });
    }
}