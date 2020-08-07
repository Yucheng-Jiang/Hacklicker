package com.example.haclicker.DataStructure;

import java.util.List;

public class ClassRoom {
    private String classID;
    private String className;
    private String instructor;
    private List<Question> questions;

    public ClassRoom(String classID, String className, String instructor, List<Question> questions) {
        this.classID = classID;
        this.className = className;
        this.instructor = instructor;
        this.questions = questions;
    }

    public String getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public String getInstructor() { return instructor; }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
