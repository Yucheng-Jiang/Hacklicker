package com.example.haclicker.DataStructure;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teacher {

    private static ClassRoom classroom;


    public static void setClassroom(ClassRoom setClassRoom) {
        classroom = setClassRoom;
    }

    public static ClassRoom getClassroom() { return classroom;}

    public static void createClassroom() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = db.getReference("ClassRooms").child(classroom.getClassID());
        ref.setValue(classroom);
    }

    public static void addQuestion(final Question question) {

        List<Question> questionList;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions");
        if (classroom.getQuestions() != null) {
            questionList = classroom.getQuestions();
            questionList.add(question);
        } else {
            questionList = new ArrayList<Question>(){{add(question);}};
        }
        classroom.setQuestions(questionList);
        ref.child(question.getQuestionId() + "").setValue(question);
    }

    public static void deleteQuestion(Question question) {

        List<Question> questionList = classroom.getQuestions();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions");
        questionList.remove(question);

        if (questionList.size() == 0 || questionList == null) {
            ref.setValue(null);
        }
        classroom.setQuestions(questionList);
        ref.child(question.getQuestionId() + "").removeValue();
    }

    public static void sendCorrectAnswer(Question question, String answer) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions")
                .child(question.getQuestionId() + "");
        List<String> list = question.getCorrectAns();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(answer);
        ref.child("answer").setValue(list);
    }

    public static void deleteCorrectAnswer(Question question, String answer) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions")
                .child(question.getQuestionId() + "");
        List<String> list = question.getCorrectAns();
        if (list == null) {
            return;
        }
        list.remove(answer);
        ref.child("answer").setValue(list);
    }

    public static void StopResponse() {
        //TODO: stop answering question
    }
}
