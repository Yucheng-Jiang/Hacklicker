package com.example.haclicker.DataStructure;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Student {
    FirebaseDatabase db;
    DatabaseReference ref;

    public void sendResponse(StudentResponse studentResponse, String classRoomID) {
        //TODO: Send student response to server
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(classRoomID).child(studentResponse.getQuestionID() + "");
        if (studentResponse != null) {
            ref.child(studentResponse.getStudentName()).setValue(studentResponse);
        }
    }

    public void updateStudentResponse(StudentResponse studentResponse) {
        //TODO: Send updated response to server
    }
}
