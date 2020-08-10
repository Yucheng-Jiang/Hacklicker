package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
import java.util.ArrayList;
import java.util.List;

public class FileExportScreen extends AppCompatActivity {

    Button export;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_screen);

        export = findViewById(R.id.export);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: export
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                //~ClassRooms/classID/StudentResponse
                DatabaseReference ref = db.getReference("ClassRooms")
//                .child(Teacher.getClassroom().getClassID())
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
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Data");
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
        });
    }


}