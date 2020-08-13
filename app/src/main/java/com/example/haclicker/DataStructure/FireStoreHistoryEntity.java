package com.example.haclicker.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class FireStoreHistoryEntity {

    private String classID;
    private List<Question> questionList;
    private List<StudentResponse> studentResponseList;

    public FireStoreHistoryEntity(String classID, List<Question> questionList,
                                  List<StudentResponse> studentResponseList) {
        this.classID = classID;
        this.questionList = questionList;
        this.studentResponseList = studentResponseList;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public List<StudentResponse> getStudentResponseList() {
        return studentResponseList;
    }

    public void setStudentResponseList(List<StudentResponse> studentResponseList) {
        this.studentResponseList = studentResponseList;
    }
}
