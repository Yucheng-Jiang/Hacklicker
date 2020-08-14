package com.example.haclicker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haclicker.DataStructure.Chat;
import com.example.haclicker.DataStructure.ClassRoom;
import com.example.haclicker.DataStructure.FireStoreHistoryEntity;
import com.example.haclicker.DataStructure.Student;
import com.example.haclicker.DataStructure.Teacher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Main screen activity, allowing user to create and join room
 */
public class MainScreen extends AppCompatActivity {
    ImageButton settings, createRoom, joinRoom, history;
    String username, userEmail;
    final public static Map<String, FireStoreHistoryEntity> historyEntityMap = new HashMap<>();
    // set room ID length, set default to 10 digits ID number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        // find UI component
        settings = findViewById(R.id.exportBtn);
        createRoom = findViewById(R.id.create_room);
        joinRoom = findViewById(R.id.join_room);
        history = findViewById(R.id.historyBtn);
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
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchHistory();
            }
        });
    }

    /**
     * Create a new classroom instance with random ID number
     * @return the newly created classroom instance
     */
    private ClassRoom generateClassroom() {

        String id = System.currentTimeMillis() + "";
        return new ClassRoom(id, id, username, null);
    }

    private void fetchHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FireStoreHistoryEntity fireStoreHistoryEntity = document.toObject(FireStoreHistoryEntity.class);
                                historyEntityMap.put(fireStoreHistoryEntity.getClassID(), fireStoreHistoryEntity);
                            }
                            Log.d("TAG", "Fetch student history succeeded");
                            Intent intent = new Intent(getApplicationContext(), HistoryClassScreen.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }

                });
    }

    public static FireStoreHistoryEntity getFireStoreHistoryByID(String classID) {
        if (historyEntityMap.containsKey(classID)) {
            return historyEntityMap.get(classID);
        }
        return null;
    }
}