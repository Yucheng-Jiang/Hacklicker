package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HistoryDetailScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail_screen);

        final TextView descriptionTxt = findViewById(R.id.descriptionTxt);
        final Button copyBtn = findViewById(R.id.copyBtn);

        descriptionTxt.setText(getIntent().getStringExtra("detail"));
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", descriptionTxt.getText());
                clipboard.setPrimaryClip(clip);

                // Code below are cited from
                // https://stackoverflow.com/questions/22194761/hide-textview-after-some-time-in-android
                copyBtn.setText("TXT COPIED");
                copyBtn.setTextColor(Color.GREEN);
                copyBtn.postDelayed(new Runnable() {
                    public void run() {
                        copyBtn.setTextColor(Color.BLACK);
                        copyBtn.setText("COPY TEXT");
                    }
                }, 1500);
                // citation ends here
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), HistoryQuestionScreen.class);
        startActivity(intent);
        finish();
    }
}