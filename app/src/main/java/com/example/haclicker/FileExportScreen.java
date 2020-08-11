package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.haclicker.DataStructure.ClassRoom;
import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.StudentResponse;
import com.example.haclicker.DataStructure.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class FileExportScreen extends AppCompatActivity {
    Button exportBtn, backBtn, leaveBtn;
    Intent intent;
    String role, classID, defaultFileName;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_screen);
        // set up UI component
        exportBtn = findViewById(R.id.exportBtn);
        backBtn = findViewById(R.id.backBtn);
        leaveBtn = findViewById(R.id.leaveBtn);
        // get role extra from intent
        intent = getIntent();
        role = intent.getStringExtra("role");
        classID =  intent.getStringExtra("classID");
        // set current date and default file name
        defaultFileName = "Hacklicker data " +
                new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(Calendar.getInstance().getTime());
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role.equals("host")) {
                    hostExport();
                } else if (role.equals("student")) {
                    studentExport();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role.equals("host")) {
                    Teacher.clearData();
                } else if (role.equals("student")) {
                    Student.clearData();
                }
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void hostExport() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("ClassRooms")
                .child("8415628875")
                .child("StudentResponse");
        ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<StudentResponse> allStudentResponse = new ArrayList<>();
                //~ClassRooms/classID/StudentResponse/questionID
                for (DataSnapshot singleQuestion : snapshot.getChildren()) {
                    //~ClassRooms/classID/StudentResponse/questionID/StuName
                    for (DataSnapshot singleResponse : singleQuestion.getChildren()) {
                        List<String> answers = new ArrayList<>();
                        //~ClassRooms/classID/StudentResponse/questionID/StuName/answer
                        for (DataSnapshot ans : singleResponse.child("answer").getChildren()) {
                            answers.add(ans.getValue().toString());
                        }
                        int questionID = Integer.parseInt(singleResponse
                                .child("questionID").getValue().toString());
                        String stuEmail = singleResponse.child("studentEmail").getValue().toString();
                        String stuName = singleResponse.child("studentName").getValue().toString();
                        long timeStamp = Long.parseLong(singleResponse
                                .child("timeStamp").getValue().toString());
                        allStudentResponse.add(new StudentResponse(stuName,
                                stuEmail, answers, questionID, timeStamp));
                    }
                }
                try {
                    //save file to device
                    String data = Teacher.constructCSV(allStudentResponse);
                    FileOutputStream out = openFileOutput("result.csv", Context.MODE_PRIVATE);
                    out.write(data.getBytes());
                    out.close();

                    Context context = getApplicationContext();
                    File fileLocation = new File(getFilesDir(), "result.csv");
                    Uri path = FileProvider.getUriForFile(context,
                            "com.example.haclicker.fileprovider", fileLocation);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/csv");
                    intent.putExtra(Intent.EXTRA_SUBJECT, defaultFileName);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(Intent.createChooser(intent, "Send Email"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void studentExport() {
        // append each question
        List<Question> questions = Student.getQuestionList();
        for (int i = 0; i < questions.size(); i++) {
            final Question question = questions.get(i);
            final List<String> choices = question.getChoices();

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("ClassRooms").child(classID).child("Questions")
                    .child(question.getQuestionId() + "").child("answer");
            final int index = i;
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    StringBuilder stringBuilder = new StringBuilder();
                    // append current time
                    stringBuilder.append("Time: ")
                            .append(new Date(System.currentTimeMillis()).toString())
                            .append("\n");
                    // append class ID
                    stringBuilder.append("class ID: ").append(classID).append("\n");
                    // update correct answer
                    List<String> correctAns = new ArrayList<>();
                    for (DataSnapshot singleAnswer : snapshot.getChildren()) {
                        correctAns.add(singleAnswer.getValue().toString());
                    }
                    // get my answer
                    List<String> myAnswer = Student.getMyAnswerHistory(question.getQuestionId());
                    // sort list
                    Collections.sort(correctAns);
                    Collections.sort(myAnswer);
                    // append question description
                    stringBuilder
                            .append("Question ").append(index).append("\n")
                            .append("Question description: ")
                            .append(question.getQuestionDescription()).append("\n");
                    // append option description
                    for (int j = 0;  j < choices.size(); j++) {
                        stringBuilder.append(j).append(") ").append(choices.get(j)).append("\n");
                    }
                    // append correct choices
                    if (correctAns != null || correctAns.size() != 0) {
                        stringBuilder.append("Correct answer: ");
                        for (String option : correctAns) {
                            stringBuilder.append(option).append(", ");
                        }
                        stringBuilder.append("\n");
                    } else {
                        stringBuilder.append("Correct answer: Not Available").append("\n");
                    }
                    // append my answers
                    if (myAnswer != null || myAnswer.size() != 0) {
                        stringBuilder.append("Your answer: ");
                        for (String answer : myAnswer) {
                            stringBuilder.append(answer).append(", ");
                        }
                        stringBuilder.append("\n");
                    } else {
                        stringBuilder.append("My answer: Not Available").append("\n");
                    }
                    // end current question
                    stringBuilder.append("\n\n");

                    try {
                        //save file to device
                        String data = stringBuilder.toString();
                        FileOutputStream out = openFileOutput("myAnswer.txt", Context.MODE_PRIVATE);
                        out.write(data.getBytes());
                        out.close();

                        //share file
                        Context context = getApplicationContext();
                        File fileLocation = new File(getFilesDir(), "myAnswer.txt");
                        Uri path = FileProvider.getUriForFile(context,
                                "com.example.haclicker.fileprovider", fileLocation);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/txt");
                        intent.putExtra(Intent.EXTRA_SUBJECT, defaultFileName);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_STREAM, path);
                        startActivity(Intent.createChooser(intent, "Send Email"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        backBtn.performClick();
    }
}