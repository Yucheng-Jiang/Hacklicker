package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Teacher;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * Share classroom ID with other by copying invitation text or qr code png
 */
public class ShareRoomScreen extends AppCompatActivity {
    Button copyIdBtn, saveQrBtn, roomIdDisplay;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_room_screen);
        // set UI component
        copyIdBtn = findViewById(R.id.qr_code_copy);
        saveQrBtn = findViewById(R.id.qr_code_save);
        roomIdDisplay = findViewById(R.id.roomIdDisplay);
        // retrieve classroom ID
        id = getIntent().getStringExtra("Id");
        final Bitmap QRCodeBitmap = renderQRCode();
        // save QR png onClickListener
        saveQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQRCode(QRCodeBitmap);
            }
        });
        // copy invitation text onClickListener
        copyIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "Join Haclicker room: " + id;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", str);
                clipboard.setPrimaryClip(clip);

                // Code below are cited from
                // https://stackoverflow.com/questions/22194761/hide-textview-after-some-time-in-android
                copyIdBtn.setText("ID COPIED");
                copyIdBtn.setTextColor(Color.GREEN);
                copyIdBtn.postDelayed(new Runnable() {
                    public void run() {
                        copyIdBtn.setTextColor(Color.BLACK);
                        copyIdBtn.setText("COPY ROOM ID");
                    }
                }, 1500);
                // citation ends here

            }
        });
        roomIdDisplay.setText("ROOM ID: " + id);
    }

    // Referenced from https://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application
    private Bitmap renderQRCode() {
        int QR_CODE_DIMENSION = 512;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(id, BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION);
            Bitmap bitMap = Bitmap.createBitmap(QR_CODE_DIMENSION, QR_CODE_DIMENSION, Bitmap.Config.RGB_565);
            for (int x = 0; x < QR_CODE_DIMENSION; x++) {
                for (int y = 0; y < QR_CODE_DIMENSION; y++) {
                    bitMap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : android.graphics.Color.parseColor("#DAE5ED"));
                }
            }
            ((ImageView) findViewById(R.id.qr_code_view)).setImageBitmap(bitMap);
            return bitMap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Referenced from: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    private void saveQRCode(Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                Teacher.getClassroom().getClassID() + ".jpg" ,
                "haclicker qr code");

        // Code below are cited from
        // https://stackoverflow.com/questions/22194761/hide-textview-after-some-time-in-android
        saveQrBtn.setText("IMAGE SAVED");
        saveQrBtn.setTextColor(Color.GREEN);
        saveQrBtn.postDelayed(new Runnable() {
            public void run() {
                saveQrBtn.setTextColor(Color.BLACK);
                saveQrBtn.setText("SAVE IMAGE");
            }
        }, 1500);
        // citation ends here
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}