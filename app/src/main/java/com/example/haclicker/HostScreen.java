package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;

import java.util.ArrayList;
import java.util.List;

public class HostScreen extends AppCompatActivity {
    List<Question> questionList = new ArrayList<>();
    ImageButton shareRoom, exitRoom;
    TextView emptyReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_screen);
        // add image buttons
        shareRoom = findViewById(R.id.shareRoom);
        exitRoom = findViewById(R.id.leaveRoom);
        emptyReminder = findViewById(R.id.emptyReminder);
        // exit room button set on click listener
        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: store data and delete cloud data
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(intent);
            }
        });
        // share room button set on click listener
        shareRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: share room code
            }
        });
        // update UI
        upDateUI();

    }

    private void upDateUI() {
        questionList = Teacher.getClassroom().getQuestions();
        if (questionList != null) {
            emptyReminder.setVisibility(View.INVISIBLE);
            LinearLayout playersList = findViewById(R.id.question_list);
            playersList.removeAllViews();

            for (int i = 0; i < questionList.size(); i++) {
                Question question = questionList.get(i);

                View questionChunk = getLayoutInflater().inflate(R.layout.chunk_question,
                        playersList, false);
                TextView questionTxt = questionChunk.findViewById(R.id.question_txt);
                questionTxt.setText(question.getQuestionDescription());

                playersList.addView(questionChunk);
            }
        } else {
            emptyReminder.setText("There's no question added.");
            emptyReminder.setVisibility(View.VISIBLE);
        }




    }
}