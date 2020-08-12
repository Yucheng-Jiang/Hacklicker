package com.example.haclicker.DataStructure;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Chat {
    private int chatId;
    private String chatContent;
    private int numVote = 0;
    private String userName;
    private String userEmail;
    private boolean isAnswered = false;

    public Chat(int setChatId, String setChatContent, int setNumVote, String setUserName, String setUserEmail, boolean setIsAnswered) {
        this(setChatId, setChatContent, setUserName, setUserEmail);
        numVote = setNumVote;
        isAnswered = setIsAnswered;
    }

    public Chat(int setChatId, String setChatContent, int setNumVote, String setUserName, String setUserEmail) {
        this(setChatId, setChatContent, setUserName, setUserEmail);
        numVote = setNumVote;
    }

    public Chat(int setChatId, String setChatContent, String setUserName, String setUserEmail) {
        this.chatId = setChatId;
        this.chatContent = setChatContent;
        this.userName = setUserName;
        this.userEmail = setUserEmail;
    }

    public void voteIncrement() {
        numVote++;
    }

    public void voteDecrement() {
        numVote--;
    }

    public int getChatId() {
        return chatId;
    }

    public String getChatContent() {
        return chatContent;
    }

    public int getNumVote() {
        return numVote;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setNumVote(int numVote) {
        this.numVote = numVote;
    }

    public static void sendNewChat(Chat chat, String classID) {
        //set server endpoint
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ClassRooms").child(classID)
                .child("Chat")
                .child(chat.getChatId() + "");

        //send to server
        ref.setValue(chat);
    }

    public void markAnswered() {
        isAnswered = true;
    }

    public void markNotAnswered() {
        isAnswered = false;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
