package com.example.haclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.FireStoreHistoryEntity;
import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.StudentResponse;

import org.w3c.dom.Text;

import java.util.List;

public class HistoryDetailScreen extends AppCompatActivity {
    String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail_screen);
        // initialize data
        classID = getIntent().getStringExtra("classID");
        int questionID = getIntent().getIntExtra("questionID", -1);
        FireStoreHistoryEntity fireStoreHistoryEntity =MainScreen.getFireStoreHistoryByID(classID);
        List<StudentResponse> studentResponsesList = fireStoreHistoryEntity.getStudentResponseList();
        List<Question> questionList = fireStoreHistoryEntity.getQuestionList();
        // set
        final TextView emptyReminder = findViewById(R.id.emptyReminder);
        if (studentResponsesList != null) {
            // clear previous UI components
            LinearLayout questionListLayout = findViewById(R.id.descriptionListView);
            questionListLayout.removeAllViews();
            // combine questions on the server and questions not published yet.
            // if there's no question, set empty question reminder
            if (studentResponsesList.size() != 0) {
                emptyReminder.setVisibility(View.INVISIBLE);
            } else {
                emptyReminder.setVisibility(View.VISIBLE);
            }
            for (Question question : questionList) {
                if (question.getQuestionId() == questionID && questionID != -1) {
                    for (final StudentResponse studentResponse : studentResponsesList) {
                        if (studentResponse.getQuestionID() == questionID) {
                            // inflate from chunk_question
                            View questionChunk = getLayoutInflater().inflate(R.layout.chunk_history_detail,
                                    questionListLayout, false);
                            // set question text view with description
                            TextView descriptionTxt = questionChunk.findViewById(R.id.descriptionTxt);
                            String detail = "Student email: " + studentResponse.getStudentEmail() + "\n";
                            detail += "Student name: " + studentResponse.getStudentName() + "\n";
                            detail += "Question ID: " + studentResponse.getQuestionID() + "\n\n";

                            detail += "Question Description: " + question.getQuestionDescription() + "\n\n";
                            for (int i = 0; i < question.getChoices().size(); i++) {
                                detail += (char)((int) 'A' + i) + ") " + question.getChoices().get(i) + "\n";
                            }
                            detail += "\n Correct Answer(s): ";
                            detail += question.getCorrectAns() == null ? "Not available" : question.getCorrectAns().toString();
                            detail += "\n Chosen Answer(s): ";
                            detail +=  studentResponse.getAnswer() == null ? "Not available" : studentResponse.getAnswer().toString();
                            descriptionTxt.setText(detail);

                            // populate the chunk to linear layout
                            questionListLayout.addView(questionChunk);
                        }
                    }
                }
                break;
            }

        } else {
            // if the question list is null, set empty reminder.
            emptyReminder.setText("No response found");
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), HistoryQuestionScreen.class);
        intent.putExtra("classID", classID);
        startActivity(intent);
        finish();
    }
}