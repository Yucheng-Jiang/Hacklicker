package com.example.haclicker;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class ImportQuestonsFromLocal {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<String> readFileIntoString(String file) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
