package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.FireStoreHistoryEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class HistoryClassScreen extends AppCompatActivity {
    TextView emptyReminder;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_class_screen);
        emptyReminder = findViewById(R.id.emptyReminder);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        updateUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        // get all existing questions on the firebase server
        // clear previous UI components
        LinearLayout questionListLayout = findViewById(R.id.question_list);
        questionListLayout.removeAllViews();
        // combine questions on the server and questions not published yet.
        // if there's no question, set empty question reminder
        if (MainScreen.historyEntityMap.size() != 0) {
            emptyReminder.setVisibility(View.INVISIBLE);
        } else {
            emptyReminder.setVisibility(View.VISIBLE);
        }

        for (final Map.Entry<String, FireStoreHistoryEntity> entry : MainScreen.historyEntityMap.entrySet()) {
            FireStoreHistoryEntity fireStoreHistoryEntity = entry.getValue();
            // inflate from chunk_question
            View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                    questionListLayout, false);
            // set question text view with description
            Button classIdBtn = questionChunk.findViewById(R.id.question_txt);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(fireStoreHistoryEntity.getClassID()));
            classIdBtn.setText("Class: " + fireStoreHistoryEntity.getClassID() + "\n\n "
                    + calendar.getTime());


            // set each question chunk onClickListener
            classIdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), HistoryQuestionScreen.class);
                    intent.putExtra("classID", entry.getKey());
                    startActivity(intent);
                    finish();
                }
            });
            // populate the chunk to linear layout
            questionListLayout.addView(questionChunk);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainScreen.class);;
        startActivity(intent);
        finish();
    }

}