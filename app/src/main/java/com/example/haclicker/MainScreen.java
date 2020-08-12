package com.example.haclicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haclicker.DataStructure.Chat;
import com.example.haclicker.DataStructure.ClassRoom;
import com.example.haclicker.DataStructure.Teacher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Main screen activity, allowing user to create and join room
 */
public class MainScreen extends AppCompatActivity {
    ImageButton settings, createRoom, joinRoom;
    String username, userEmail;
    // set room ID length, set default to 10 digits ID number
    public static final int ID_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        // find UI component
        settings = findViewById(R.id.exportBtn);
        createRoom = findViewById(R.id.create_room);
        joinRoom = findViewById(R.id.join_room);
        // get user name name and email
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            username = signInAccount.getDisplayName();
            userEmail = signInAccount.getEmail();
        }
        // setting button onclickListener
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConsoleScreen.class);
                startActivity(intent);
            }
        });
        // create button onclickListener
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // generate a new room with random ID (without collision)
                ClassRoom classroom = generateClassroom();
                // set the classroom to Teacher class (static field)
                Teacher.setClassroom(classroom);
                // update the class room to firebase server
                Teacher.createClassroom();
                Chat.sendNewChat(new Chat(0, "Welcome", username, userEmail), classroom.getClassID());
                // jump to host screen activity
                Intent intent = new Intent(getApplicationContext(), HostScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        // join button onclickListener
        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinRoomScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Create a new classroom instance with random ID number
     * @return the newly created classroom instance
     */
    private ClassRoom generateClassroom() {

        String id = System.currentTimeMillis() + "";
        return new ClassRoom(id.toString(), id.toString(), username, null);
//        // get all existing room IDs
//        List<String> allRoomIDs = getAllRooms();
//        // generate a random 10 digits number while avoiding collision
//        Random random = new Random();
//        while (true) {
//            StringBuilder id = new StringBuilder();
//            for (int i = 0; i < ID_LENGTH; i++) {
//                id.append(random.nextInt(10));
//            }
//            if (!allRoomIDs.contains(id.toString())) {
//                return new ClassRoom(id.toString(), id.toString(), username, null);
//            }
//        }
    }

    /**
     * Check data base for all roomIDs to prevent duplicate ID.
     * @return all roomIDs
     */
    private List<String> getAllRooms() {
        final List<String> allRoomIDS = new ArrayList<>();
        // create a new database reference
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("ClassRooms");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ID : snapshot.getChildren()) {
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