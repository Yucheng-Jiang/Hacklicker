package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.haclicker.DataStructure.StudentHistoryEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryScreen extends AppCompatActivity {

    private String role;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);

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