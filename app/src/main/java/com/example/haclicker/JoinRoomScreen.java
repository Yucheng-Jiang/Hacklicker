package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Student;

public class JoinRoomScreen extends AppCompatActivity {

    Button confirmJoinBtn;
    ImageButton scanQrBtn, galleryQrBtn;
    EditText inputRoomId;
    TextView invalidIdTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room_screen);

        confirmJoinBtn = findViewById(R.id.confirmJoinBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        galleryQrBtn = findViewById(R.id.galleryQrBtn);
        inputRoomId = findViewById(R.id.inputRoomId);
        invalidIdTxt = findViewById(R.id.invalidIdTxt);


        confirmJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = inputRoomId.getText().toString();
                if (Student.joinClassroom(id)) {
                    // TODO: start new activity here
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
        });
    }

}