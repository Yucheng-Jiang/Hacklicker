package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JoinRoomScreen extends AppCompatActivity {

    Button confirmJoinBtn;
    ImageButton scanQrBtn, galleryQrBtn;
    EditText inputRoomId;
    TextView invalidIdTxt;

    private static final int RC_SCAN = 1;
    private static final int RC_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room_screen);

        confirmJoinBtn = findViewById(R.id.confirmJoinBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        galleryQrBtn = findViewById(R.id.galleryQrBtn);
        inputRoomId = findViewById(R.id.inputRoomId);
        invalidIdTxt = findViewById(R.id.invalidIdTxt);

        scanQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanScreen.class);
                startActivityForResult(intent, RC_SCAN);
                String result = ""; // store result here
                inputRoomId.setText(result);
            }
        });

        galleryQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: read qr from gallery
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , RC_PICK);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String classID;
        switch (requestCode) {
            case RC_SCAN:
                classID = data.getStringExtra("classID");
                // TODO: Do something with class ID
                break;
            case RC_PICK:
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                QRCodeReader reader = new QRCodeReader();
                try {
                    // BEGIN QUOTE
                    // Referenced from: https://stackoverflow.com/questions/14861553/zxing-convert-bitmap-to-binarybitmap
                    int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
                    //copy pixel data from the Bitmap into the 'intArray' array
                    bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                    LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                    BinaryBitmap binBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    // END QUOTE
                    Result result = reader.decode(binBitmap);
                    classID = result.getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO: Do something with class ID
                break;
        }
    }

}