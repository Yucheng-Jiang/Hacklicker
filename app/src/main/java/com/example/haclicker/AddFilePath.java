package com.example.haclicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class AddFilePath extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file_path);
        path_input = (EditText) findViewById(R.id.path_input);
        submit_button = (Button) findViewById(R.id.submit);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path = path_input.getText().toString();
            }
        });

        path = new ImportFile("").getFilePath();
        List<String> questions = ImportQuestonsFromLocal.readFileIntoString(path);
    }
}