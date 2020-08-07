package com.example.haclicker;

import java.util.List;

public class ClassRoom {
    private String classID;
    private String className;
    private List<Question> questions;

    public ClassRoom(String classID, String className, List<Question> questions) {
        this.classID = classID;
        this.className = className;
        this.questions = questions;
    }

    public String getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
