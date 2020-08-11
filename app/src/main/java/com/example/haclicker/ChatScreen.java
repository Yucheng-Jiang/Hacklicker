package com.example.haclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.haclicker.DataStructure.Chat;
import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends AppCompatActivity {
    String classID;
    String role;
    TextView chatInputTxt;
    Button chatSendBtn;
    int totalChatNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        // set UI component
        chatInputTxt = findViewById(R.id.chatInputTxt);
        chatSendBtn = findViewById(R.id.chatSendBtn);
        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = chatInputTxt.getText().toString();
                if (input.length() != 0) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    sendChat(new Chat(totalChatNum, input, user.getDisplayName()));
                    chatInputTxt.setText("");
                }
            }
        });
        // get intent
        Intent intent = getIntent();
        classID = intent.getStringExtra("classID");
        role = intent.getStringExtra("role");
        // update UI
        // keep updating updateChatList
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateChatList();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
    }

    public void updateChatList() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ClassRooms").child(classID).child("Chat");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Chat> chatList = new ArrayList<>();
                for (DataSnapshot singleChat : snapshot.getChildren()) {
                    int chatID = Integer.parseInt(singleChat.child("chatId").getValue().toString());
                    String content = singleChat.child("chatContent").getValue().toString();
                    int vote = Integer.parseInt(singleChat.child("numVote").getValue().toString());
                    String userName = singleChat.child("userName").getValue().toString();
                    boolean isAnswered = (boolean) singleChat.child("answered").getValue();
                    chatList.add(new Chat(chatID, content, vote, userName, isAnswered));
                }
                // if questions on the server is different from questions in local
                // update UI
                totalChatNum = chatList.size();
                if (role.equals("student")) {
                    if (!chatList.equals(Student.getChatList())) {
                        Student.setChatList(chatList);
                        updateUI();
                    }
                } else if (role.equals("host")) {
                    if (!chatList.equals(Teacher.getClassroom().getChatList())) {
                        Teacher.getClassroom().setChatList(chatList);
                        updateUI();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateUI() {
        List<Chat> chats = null;
        if (role.equals("student")) {
            chats = Student.getChatList();
        } else if (role.equals("host")) {
            chats = Teacher.getClassroom().getChatList();
        }
        if (chats != null) {
            // remove all current views
            LinearLayout chatListLayout = findViewById(R.id.chatListLayout);
            chatListLayout.removeAllViews();
            // iterate through every question
            for (final Chat chat : chats) {
                View chatChunk = getLayoutInflater().inflate(R.layout.chunk_chat,
                        chatListLayout, false);
                final int currentID = chat.getChatId();
                // set user avatar
                final Button userAvatar = chatChunk.findViewById(R.id.userAvatar);
                userAvatar.setText(chat.getUserName());
                // set chat content
                final Button userChatContent = chatChunk.findViewById(R.id.userChatContent);
                userChatContent .setText(chat.getChatContent());
                if (chat.isAnswered()) {
                    userChatContent.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                }
                // enable teacher to mark answered question
                userChatContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (role.equals("host")) {
                            if (chat.isAnswered()) {
                                chat.markNotAnswered();
                                userChatContent.setBackgroundColor(android.R.drawable.btn_default);
                            } else {
                                chat.markAnswered();;
                                userChatContent.setBackgroundColor(android.graphics.Color.parseColor("#99ff99"));
                            }
                        }
                        sendChat(chat);
                    }
                });
                // set vote button
                final Button chatVoteBtn = chatChunk.findViewById(R.id.chatVoteBtn);
                chatVoteBtn.setId(chat.getChatId());
                chatVoteBtn.setText("+ " + chat.getNumVote());
                chatVoteBtn.setBackgroundColor(android.R.drawable.btn_default);
                // change color based on vote history
                if (role.equals("student")) {
                    if (Student.isVote(currentID)) {
                        chatVoteBtn.setTextColor(android.graphics.Color.parseColor("#07C160"));
                    }
                } else if (role.equals("host")) {
                    if (Teacher.isVote(currentID)) {
                        chatVoteBtn.setTextColor(android.graphics.Color.parseColor("#07C160"));
                    }
                }
                chatVoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int num = chat.getNumVote();
                        if (role.equals("student")) {
                            if (Student.isVote(currentID)) {
                                chat.voteDecrement();
                                Student.unVote(currentID);
                                chatVoteBtn.setBackgroundColor(android.R.drawable.btn_default);
                                num--;
                            } else {
                                chat.voteIncrement();
                                Student.addVote(currentID);
                                chatVoteBtn.setTextColor(android.graphics.Color.parseColor("#07C160"));
                                num++;
                            }
                        } else if (role.equals("host")) {
                            if (Teacher.isVote(currentID)) {
                                Teacher.unVote(currentID);
                                chat.voteDecrement();
                                chatVoteBtn.setBackgroundColor(android.R.drawable.btn_default);
                                num--;
                            } else {
                                chat.voteIncrement();
                                Teacher.addVote(currentID);
                                chatVoteBtn.setTextColor(android.graphics.Color.parseColor("#07C160"));
                                num++;
                            }
                        }
                        chatVoteBtn.setText("+ " + num);
                        sendChat(chat);
                    }
                });
                chatListLayout.addView(chatChunk);
            }
        }
    }

    public void sendChat(Chat chat) {
        Chat.sendNewChat(chat, classID);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}