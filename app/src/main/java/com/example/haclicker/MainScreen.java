package com.example.haclicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haclicker.DataStructure.ClassRoom;
import com.example.haclicker.DataStructure.Question;
import com.example.haclicker.DataStructure.Teacher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainScreen extends AppCompatActivity {

    ImageButton settings, createRoom, joinRoom;
    String username, userEmail, className;

    public static final int ID_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        settings = findViewById(R.id.settings);
        createRoom = findViewById(R.id.create_room);
        joinRoom = findViewById(R.id.join_room);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            username = signInAccount.getDisplayName();
            userEmail = signInAccount.getEmail();
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsoleScreen.class);
                startActivity(intent);
            }
        });

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassRoom classroom = generateClassroom();
                Teacher.setClassroom(classroom);
                Teacher.createClassroom();
                List<String> list = new ArrayList<String>() {{
                    add("Choice A");
                    add("Choice B");
                    add("Choice C");
                }};
                Teacher.addQuestion(new Question("test message. You see you one day day, look what look.", 11, list));
                Intent intent = new Intent(getApplicationContext(), HostScreen.class);
                startActivity(intent);
            }
        });
    }
    private ClassRoom generateClassroom() {

        List<String> allRoomIDs = getAllRooms();
        Random random = new Random();
        while (true) {
            StringBuilder id = new StringBuilder();
            for (int i = 0; i < ID_LENGTH; i++) {
                id.append(random.nextInt(10));
            }
            if (!allRoomIDs.contains(id.toString())) {
                return new ClassRoom(id.toString(), id.toString(), username, null);
            }
        }
    }

    /**
     * Check data base for all roomIDs to prevent duplicate ID.
     * @return all roomIDs
     */
    private List<String> getAllRooms() {

        final List<String> allRoomIDS = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("ClassRooms");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ID : snapshot.getChildren()) {
                    if (ID.child("classID").getValue() == null) {
                        System.out.println("fucker");
                    }
                    allRoomIDS.add(ID.child("classID").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return allRoomIDS;
    }
}