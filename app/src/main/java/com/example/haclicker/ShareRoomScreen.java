package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Teacher;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareRoomScreen extends AppCompatActivity {

    TextView roomIdDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_room_screen);

        final Bitmap QRCodeBitmap = renderQRCode();
        findViewById(R.id.qr_code_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQRCode(QRCodeBitmap);
            }
        });

        roomIdDisplay = findViewById(R.id.roomIdDisplay);
        roomIdDisplay.setText("ROOM ID: " + Teacher.getClassroom().getClassID());



    }

    // Referenced from https://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application
    private Bitmap renderQRCode() {
        String classID = Teacher.getClassroom().getClassID();

        int QR_CODE_DIMENSION = 512;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(classID, BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION);
            Bitmap bitMap = Bitmap.createBitmap(QR_CODE_DIMENSION, QR_CODE_DIMENSION, Bitmap.Config.RGB_565);
            for (int x = 0; x < QR_CODE_DIMENSION; x++) {
                for (int y = 0; y < QR_CODE_DIMENSION; y++) {
                    bitMap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
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
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        File directory = cw.getDir("saved_qr_codes", Context.MODE_PRIVATE);
//        File mypath=new File(directory, Teacher.getClassroom().getClassID() + ".jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

}