package com.example.haclicker.DataStructure;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {
    static FirebaseDatabase db = FirebaseDatabase.getInstance();
    static DatabaseReference ref;
    static List<Question> questionList = new ArrayList<>();
    static Map<Integer, List<String>> questionAnswer = new HashMap<>();
    static List<Chat> chatList = new ArrayList<>();
    static List<Integer> voteHistory = new ArrayList<>(); // store voted chat id

    public static void clearData() {
        questionList.clear();
        questionAnswer.clear();
        chatList.clear();
        voteHistory.clear();
    }

    public static List<String> getMyAnswerHistory(int questionId) {
        if (questionAnswer.containsKey(questionId)) {
            return questionAnswer.get(questionId);
        }
        return null;
    }

    public static void updateQuestionList(List<Question> questions) {
        questionList = questions;
    }

    public static Question getQuestionById(int id) {
        for (Question question : questionList) {
            if (question.getQuestionId() == id) {
                return question;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateQuestionAnswer(int questionId, List<String> answers) {
        if (questionAnswer.containsKey(questionId)) {
            questionAnswer.replace(questionId, answers);
        } else {
            questionAnswer.put(questionId, answers);
        }
    }

    /**
     * Send student response to server.
     * @param studentResponse student answer.
     * @param classRoomID classroom code.
     */
    public static void sendResponse(StudentResponse studentResponse, String classRoomID) {

        //set server endpoint
        ref = FirebaseDatabase.getInstance().getReference("ClassRooms").child(classRoomID)
                .child("StudentResponse")
                .child(studentResponse.getQuestionID() + "")
                .child(studentResponse.getStudentName());

        //send to server
        ref.setValue(studentResponse);
    }

    /**
     * Check data base for all roomIDs to prevent duplicate ID.
     * @return all roomIDs
     */
    private static List<String> getAllRooms() {

        final List<String> allRoomIDS = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("ClassRooms");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ID : snapshot.getChildren()) {
                    allRoomIDS.add(ID.child("classID").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return allRoomIDS;
    }

    public static List<Question> getQuestionList() {
        return questionList;
    }

    public static void setQuestionList(List<Question> questions) {
        questionList.clear();
        questionList.addAll(questions);
    }

    public static void setCorrectAns(List<String> ans, int Qid) {
        for (Question question : questionList) {
            if (question.getQuestionId() == Qid)
                question.setCorrectAns(ans);
        }
    }

    public static List<Chat> getChatList() {
        return chatList;
    }

    public static void setChatList(List<Chat> chatList) {
        Student.chatList.clear();
        Student.chatList.addAll(chatList);
    }

    public static void unVote(int chatID) {
        voteHistory.remove(Integer.valueOf(chatID));
    }

    public static void addVote(int chatID) {
        voteHistory.add(chatID);
    }

    public static boolean isVote(int chatID) {
        return voteHistory.contains(chatID);
    }
}
