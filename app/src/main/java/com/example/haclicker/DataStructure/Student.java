package com.example.haclicker.DataStructure;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Student {
    static FirebaseDatabase db = FirebaseDatabase.getInstance();
    static DatabaseReference ref;

    //return true if join classroom successful
    public static boolean joinClassroom(String classID) {
        List<String> allRooms = getAllRooms();
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
        ref = db.getReference("ClassRooms").child(classRoomID).child("StudentResponse")
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
}
