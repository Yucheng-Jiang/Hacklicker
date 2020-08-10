package com.example.haclicker.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class ClassRoom {
    private String classID;
    private String className;
    private String instructor;
    private List<Question> questions;

    public ClassRoom(String classID, String className, String instructor, List<Question> setQuestions) {
        this.classID = classID;
        this.className = className;
        this.instructor = instructor;
        if (setQuestions == null) {
            questions = new ArrayList<>();
        } else {
            questions = setQuestions;
        }
    }

    public String getClassID() {
        return classID;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
