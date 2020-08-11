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

/**
 * Join room screen. Allow users to join rooms with valid ID.
 * Users can add room ID manually or through QR code scan.
 */
public class JoinRoomScreen extends AppCompatActivity {
    Button confirmJoinBtn;
    ImageButton scanQrBtn, galleryQrBtn;
    EditText inputRoomId;
    TextView invalidIdTxt;
    Intent intent;

    private final Boolean[] run = new Boolean[]{Boolean.TRUE};
    private static final int RC_SCAN = 1;
    private static final int RC_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room_screen);
        // set UI component
        confirmJoinBtn = findViewById(R.id.confirmJoinBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        galleryQrBtn = findViewById(R.id.galleryQrBtn);
        inputRoomId = findViewById(R.id.inputRoomId);
        invalidIdTxt = findViewById(R.id.invalidIdTxt);
        intent = getIntent();

        // scan QR code button
        scanQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanScreen.class);
                startActivityForResult(intent, RC_SCAN);
            }
        });

        // recognize QR code from gallery photo
        galleryQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , RC_PICK);
            }
        });

        // confirm join button
        confirmJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get ID text in the editText box
                final String id = inputRoomId.getText().toString();
                // get all existing room IDs on the firebase
                final List<String> allRoomIDS = new ArrayList<>();
                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("ClassRooms");
                reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // if allow firebase to fetch update
                            if (run[0]) {
                                // update all existing room IDs
                                for (DataSnapshot ID : snapshot.getChildren()) {
                                    allRoomIDS.add(ID.child("classID").getValue().toString());
                                }
                                // if there's a match, login to the room
                                if (allRoomIDS.contains(id)) {
                                    Intent intent = new Intent(getApplicationContext(), StudentScreen.class);
                                    intent.putExtra("ClassID", id);
                                    startActivity(intent);
                                    // stop firebase from fetching
                                    run[0] = false;
                                    finish();
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
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // if the intent has classId extra, join the room automatically
        if (intent.hasExtra("classId")) {
            inputRoomId.setText(intent.getStringExtra("classId"));
            confirmJoinBtn.performClick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String classID = null;
        switch (requestCode) {
            // if user choose to scan, get classID from intent directly
            case RC_SCAN:
                if (data.hasExtra("classID")) {
                    classID = data.getStringExtra("classID");
                } else {
                    classID = "";
                }
                break;
            // if user choose to select from gallery, recognize the png first
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
                    classID = reader.decode(binBitmap).getText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        Intent intent = new Intent(getApplicationContext(), JoinRoomScreen.class);
        intent.putExtra("classId", classID);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
        startActivity(intent);
        finish();
    }

}