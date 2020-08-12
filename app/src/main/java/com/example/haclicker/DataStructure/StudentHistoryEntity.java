package com.example.haclicker.DataStructure;

import java.util.List;

public class StudentHistoryEntity {
    private String studentName;
    private String studentEmail;
    private List<String> answers;
    private int questionID;
    private String questionDescription;
    private List<String> choices;
    private List<String> correctAnswer;

    public StudentHistoryEntity(String studentName, String studentEmail, List<String> answers,
                                int questionID, String questionDescription, List<String> choices,
                                List<String> correctAnswer) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.answers = answers;
        this.questionID = questionID;
        this.questionDescription = questionDescription;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getQuestionID() {
        return questionID;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public List<String> getChoices() {
        return choices;
    }

    public List<String> getCorrectAnswer() {
        return correctAnswer;
    }
}
