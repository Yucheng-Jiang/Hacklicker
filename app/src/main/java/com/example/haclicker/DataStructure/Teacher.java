package com.example.haclicker.DataStructure;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Teacher {

    private static ClassRoom classroom;
    private static List<Question> questionsToAdd = new ArrayList<>();
    //TODO:Add test path
    private static String EXPORT_CSV_FILE_PATH = "";


    public static void setClassroom(ClassRoom setClassRoom) {
        classroom = setClassRoom;
    }

    public static ClassRoom getClassroom() { return classroom;}

    /**
     * Send the newly created classroom to server.
     */
    public static void createClassroom() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref;
        ref = db.getReference("ClassRooms").child(classroom.getClassID());
        ref.setValue(classroom);
    }

    /**
     * Add a new question to server.
     */
    public static void addQuestion(int Qid) {

        List<Question> questionList = new ArrayList<>();
        for (Question question : questionsToAdd) {
            //enable answering
            question.setCanAnswer(true);
            if (question.getQuestionId() == Qid) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child("ClassRooms")
                        .child(classroom.getClassID())
                        .child("Questions");
                if (classroom.getQuestions() != null) {
                    questionList = classroom.getQuestions();
                } else {
                    questionList.add(question);
                }
                ref.child(question.getQuestionId() + "").setValue(question);
                questionsToAdd.remove(question);
                classroom.setQuestions(questionList);
                break;
            }
        }

    }

    public static void setStudentAccessibility(boolean isEnable, int Qid) {

        classroom.getQuestionById(Qid).setCanAnswer(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions")
                .child(Qid + "")
                .child("canAnswer");

        ref.setValue(isEnable);
    }

    /**
     * Delete a question from server.
     * @param question question to delete
     */
    public static void deleteQuestion(Question question) {

        List<Question> questionList = classroom.getQuestions();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions");
        questionList.remove(question);

        if (questionList.size() == 0 || questionList == null) {
            ref.setValue(null);
        }
        classroom.setQuestions(questionList);
        ref.child(question.getQuestionId() + "").removeValue();
    }

    /**
     * Send correct answer for a question to server.
     * @param question target question
     * @param answer correct answer to target question
     */
    public static void sendCorrectAnswer(Question question, String answer) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions")
                .child(question.getQuestionId() + "");
        List<String> list = question.getCorrectAns();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(answer);
        ref.child("answer").setValue(list);
    }

    /**
     * Delete correct answer from server in case teacher mistakenly
     * chosen a wrong answer as right answer.
     * @param question target question
     * @param answer wrong answer to delete
     */
    public static void deleteCorrectAnswer(Question question, String answer) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ClassRooms")
                .child(classroom.getClassID())
                .child("Questions")
                .child(question.getQuestionId() + "");
        List<String> list = question.getCorrectAns();
        if (list == null) {
            return;
        }
        list.remove(answer);
        ref.child("answer").setValue(list);
    }

    public static void addQuestionToQueue(Question question) {
        if (questionsToAdd == null) {
            questionsToAdd = new ArrayList<>();
        }
        questionsToAdd.add(question);
    }

    public static List<Question> getQuestionsToAdd() {
        return questionsToAdd;
    }

    /**
     * Export all student response as a csv file.
     * @throws IOException exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void exportResultAsCSV(List<StudentResponse> allStudentResponse) throws IOException {

        try (
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(EXPORT_CSV_FILE_PATH));
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.DEFAULT
                    .withHeader("Name", "Email", "Answer", "QuestionID", "Time Stamp", "Correct"))
        ) {
            for (StudentResponse studentResponse : allStudentResponse) {
                String name = studentResponse.getStudentName();
                String email = studentResponse.getStudentEmail();
                StringBuilder answerBuilder = new StringBuilder();
                for (String ans : studentResponse.getAnswer()) {
                    answerBuilder.append(ans);
                }
                String answer = answerBuilder.toString();
                int questionID = studentResponse.getQuestionID();
                long timeStamp = studentResponse.getTimeStamp();
                boolean correctness = false;
                StringBuilder correctAnswerBuilder = new StringBuilder();
                for (String single : classroom.getQuestions().get(questionID).getCorrectAns()) {
                    correctAnswerBuilder.append(single);
                }
                //check if student answer is correct
                if (answer.equals(correctAnswerBuilder.toString()))
                    correctness = true;
                csvPrinter.printRecord(name, email, answer, questionID, timeStamp, correctness);
            }
        }
        allStudentResponse.clear();
    }

    /**
     * Retrieve all student response of a certain class.
     * @return list of student response
     */
    private static void retrieveAllResponse() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //~ClassRooms/classID/StudentResponse
        DatabaseReference ref = db.getReference("ClassRooms")
                .child(Teacher.getClassroom().getClassID())
                .child("StudentResponse");
        ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<StudentResponse> allStudentResponse = new ArrayList<>();
                //~ClassRooms/classID/StudentResponse/questionID
                for (DataSnapshot singleQuestion : snapshot.getChildren()) {
                    //~ClassRooms/classID/StudentResponse/questionID/StuName
                    for (DataSnapshot singleResponse : singleQuestion.getChildren()) {
                        List<String> answers = new ArrayList<>();
                        //~ClassRooms/classID/StudentResponse/questionID/StuName/answer
                        for (DataSnapshot ans : singleResponse.child("answer").getChildren()) {
                            answers.add(ans.getValue().toString());
                        }
                        int questionID = Integer.parseInt(singleResponse
                                .child("questionID").getValue().toString());
                        String stuEmail = singleResponse.child("studentEmail").getValue().toString();
                        String stuName = singleResponse.child("studentName").getValue().toString();
                        long timeStamp = Long.parseLong(singleResponse
                                .child("timeStamp").getValue().toString());
                        allStudentResponse.add(new StudentResponse(stuName,
                                stuEmail, answers, questionID, timeStamp));
                    }
                }
                try {
                    exportResultAsCSV(allStudentResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setExportCSVPath(String relativePath) {
        EXPORT_CSV_FILE_PATH = relativePath;
    }
}
