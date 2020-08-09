package com.example.haclicker.DataStructure;
import android.os.Build;
import android.util.Pair;

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

    public static void clearHistory() {
        questionList.clear();
        questionAnswer.clear();
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


    //return true if join classroom successful
    public static boolean joinClassroom(String classID) {
        List<String> allRooms = getAllRooms();;

        for (int i = 0; i < allRooms.size(); i++) {
            if (classID.equals(allRooms.get(i))) {
                return true;
            }
        }
        return false;
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
     * Send newly chosen answer to server.
     * @param studentResponse newly chosen answer
     */
    public static void updateStudentResponse(StudentResponse studentResponse, String classRoomID) {

        ref = db.getReference("ClassRooms").child(classRoomID).child("StudentResponse")
                .child(studentResponse.getQuestionID() + "");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + studentResponse.getStudentName() + "/", studentResponse);
        ref.updateChildren(childUpdates);
    }

    public static List<Question> retrieveQuestions(String roomID) {
        final List<Question> questions = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("ClassRooms")
                .child(roomID).child("Questions");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot question : snapshot.getChildren()) {
                    List<String> choices = new ArrayList<>();
                    for (long i = 0; i < question.child("choices").getChildrenCount(); i++) {
                        choices.add(question.child("choices").child(i + "").getValue().toString());
                    }
                    String id = question.child("questionId").getValue().toString();
                    String description = question.child("questionDescription").getValue().toString();
                    questions.add(new Question(description, Integer.parseInt(id), choices));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return questions;
    }

    /**
     * Check data base for all roomIDs to prevent duplicate ID.
     * @return all roomIDs
     */
    private static List<String> getAllRooms() {

        final List<String> allRoomIDS = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("ClassRooms");
        reference.addValueEventListener(new ValueEventListener() {
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

    public static Map<Integer, List<String>> getQuestionAnswer() {
        return questionAnswer;
    }

    public static void retrieveCorAns(final int Qid, String classID) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms").child(classID)
                .child("Questions").child(Qid + "").child("answer");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> answers = new ArrayList<>();
                snapshot.getValue(answers.getClass());
                for (Question question : questionList) {
                    if (question.getQuestionId() == Qid) {
                        question.setCorrectAns(answers);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static List<Question> getQuestionList() {
        return questionList;
    }

    public static void setQuestionList(List<Question> questions) {
        questionList.clear();
        questionList.addAll(questions);
    }

    public static void setCorrectAns(List<String> ans, int Qid) {
        questionList.get(Qid).setCorrectAns(ans);
    }
}
