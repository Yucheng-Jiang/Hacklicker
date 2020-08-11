package com.example.haclicker.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class ClassRoom {
    private String classID;
    private String className;
    private String instructor;
    private List<Question> questions;
    private List<Chat> chatList;

    public ClassRoom(String classID, String className, String instructor, List<Question> setQuestions) {
        this.classID = classID;
        this.className = className;
        this.instructor = instructor;
        if (setQuestions == null) {
            questions = new ArrayList<>();
        } else {
            questions = setQuestions;
        }
        chatList = new ArrayList<>();
    }

    public Question getQuestionById(int id) {
        for (Question question : questions) {
            if (question.getQuestionId() == id) {
                return question;
            }
        }
        return null;
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

    public void setChatList(List<Chat> chatList) {
        this.chatList.clear();
        this.chatList.addAll(chatList);
    }

    public List<Chat> getChatList() { return this.chatList; }
}
