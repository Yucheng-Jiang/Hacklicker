package com.example.haclicker.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionDescription;
    private int questionId;
    private boolean canAnswer;
    private List<String> choices;
    private List<String> correctAns = new ArrayList<>();

    public Question() {
        questionDescription = null;
        questionId = -1;
        canAnswer = false;
        choices = null;
        correctAns = null;
    }

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

    public void setCorrectAns(List<String> setCorrectAns) {
        if (correctAns != null) {
            this.correctAns.clear();
            this.correctAns.addAll(setCorrectAns);
        } else {
            this.correctAns = new ArrayList<>();
            this.correctAns.addAll((setCorrectAns));
        }
    }

    public void setCanAnswer(boolean canAnswer) {
        this.canAnswer = canAnswer;
    }

    public boolean getCanAnswer() { return canAnswer; }
}
