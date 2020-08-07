package com.example.haclicker;

public class Question {
    private String questionDescription;
    private int questionId;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String choiceE;

    public Question(String questionDescription, int questionId, String choiceA, String choiceB, String choiceC, String choiceD, String choiceE) {
        this.questionDescription = questionDescription;
        this.questionId = questionId;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
        this.choiceE = choiceE;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public String getChoiceE() {
        return choiceE;
    }
}
