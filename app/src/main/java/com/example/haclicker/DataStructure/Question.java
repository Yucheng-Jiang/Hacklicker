package com.example.haclicker.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionDescription;
    private int questionId;
    private boolean canAnswer;
    private List<String> choices;
    private List<String> correctAns = new ArrayList<>();

    public Question(String questionDescription, int questionId, List<String> choices, boolean canAnswer) {
        this.questionDescription = questionDescription;
        this.questionId = questionId;
        this.choices = choices;
        this.canAnswer = canAnswer;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public int getQuestionId() {
        return questionId;
    }

    public List<String> getChoices() {
        return choices;
    }

    public List<String> getCorrectAns() { return correctAns; }

    public void setCorrectAns(List<String> correctAns) { this.correctAns = correctAns; }

    public void setCanAnswer(boolean canAnswer) {
        this.canAnswer = canAnswer;
    }

    public boolean getCanAnswer() { return canAnswer; }
}
