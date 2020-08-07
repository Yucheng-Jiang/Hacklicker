package com.example.haclicker.DataStructure;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Student {
    FirebaseDatabase db;
    DatabaseReference ref;
    public Student(FirebaseDatabase db) {
        this.db = db;
    }

    /**
     * Send student response to server.
     * @param studentResponse student answer.
     * @param classRoomID classroom code.
     */
    public void sendResponse(StudentResponse studentResponse, String classRoomID) {

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
    public void updateStudentResponse(StudentResponse studentResponse, String classRoomID) {

        ref = db.getReference("ClassRooms").child(classRoomID).child("StudentResponse")
                .child(studentResponse.getQuestionID() + "");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + studentResponse.getStudentName() + "/", studentResponse);
        ref.updateChildren(childUpdates);
    }
}
