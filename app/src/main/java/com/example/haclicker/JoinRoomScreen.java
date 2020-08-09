package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.StudentResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JoinRoomScreen extends AppCompatActivity {

    Button confirmJoinBtn;
    ImageButton scanQrBtn, galleryQrBtn;
    EditText inputRoomId;
    TextView invalidIdTxt;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room_screen);

        confirmJoinBtn = findViewById(R.id.confirmJoinBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        galleryQrBtn = findViewById(R.id.galleryQrBtn);
        inputRoomId = findViewById(R.id.inputRoomId);
        invalidIdTxt = findViewById(R.id.invalidIdTxt);
        intent = getIntent();


        scanQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanScreen.class);
                startActivity(intent);
                String result = ""; // store result here
                inputRoomId.setText(result);
            }
        });

        galleryQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: read qr from gallery
                String result = ""; // store result here
                inputRoomId.setText(result);
            }
        });

        confirmJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = inputRoomId.getText().toString();

                final List<String> allRoomIDS = new ArrayList<>();
                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("ClassRooms");
                reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ID : snapshot.getChildren()) {
                                allRoomIDS.add(ID.child("classID").getValue().toString());
                            }

                            if (allRoomIDS.contains(id)) {
                                // TODO: start new activity here
                                Intent intent = new Intent(getApplicationContext(), StudentScreen.class);
                                intent.putExtra("Id", id);
                                startActivity(intent);
                            } else {
                                // Code below are cited from
                                // https://stackoverflow.com/questions/22194761/hide-textview-after-some-time-in-android
                                confirmJoinBtn.setText("INVALID ID");
                                confirmJoinBtn.setTextColor(Color.RED);
                                invalidIdTxt.postDelayed(new Runnable() {
                                    public void run() {
                                        confirmJoinBtn.setTextColor(Color.BLACK);
                                        confirmJoinBtn.setText("JOIN ROOM");
                                    }
                                }, 1500);
                                // citation ends here
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        if (intent.hasExtra("classId")) {
            inputRoomId.setText(intent.getStringExtra("classId"));
            confirmJoinBtn.performClick();
        }
    }

}