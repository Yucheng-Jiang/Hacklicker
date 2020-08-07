package com.example.haclicker.DataStructure;

import java.util.List;

public class StudentResponse {
    private String studentName;
    private String studentEmail;
    private List<String> answer;
    private int questionID;
    private long timeStamp;

    public StudentResponse(String studentName, String studentEmail, List<String> answer, int questionID, long timeStamp) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.answer = answer;
        this.questionID = questionID;
        this.timeStamp = timeStamp;
    }

    public int getQuestionID() {
        return questionID;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
