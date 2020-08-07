package com.example.haclicker;
import com.example.haclicker.ClassRoom;
import com.example.haclicker.Question;

public class Teacher {

    private ClassRoom classroom;

    public Teacher(ClassRoom classroom) {
        this.classroom = classroom;
    }
    public void createClassroom() {
        //TODO: Create classroom.
    }

    public void addQuestion(Question question) {
        //TODO: add question to server.
    }

    public void deleteQuestion(Question question) {
        //TODO: delete question from server.
    }

    public void sendCorrectAnswer(String answer) {
        //TODO: send correct answer to server
    }

    public void StopResponse() {
        //TODO: stop answering question
    }
}
