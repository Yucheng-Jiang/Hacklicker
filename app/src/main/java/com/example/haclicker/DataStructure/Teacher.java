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

    /**
     * Send the newly created classroom to server.
     */
    public static void createClassroom() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = db.getReference("ClassRooms").child(classroom.getClassID());
        ref.setValue(classroom);
    }

    /**
     * Add a new question to server.
     * @param question question to add
     */
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

    /**
     * Delete a question from server.
     * @param question question to delete
     */
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

    /**
     * Send correct answer for a question to server.
     * @param question target question
     * @param answer correct answer to target question
     */
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

    /**
     * Delete correct answer from server in case teacher mistakenly
     * chosen a wrong answer as right answer.
     * @param question target question
     * @param answer wrong answer to delete
     */
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
