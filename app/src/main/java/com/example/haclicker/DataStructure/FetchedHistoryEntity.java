package com.example.haclicker.DataStructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchedHistoryEntity {

    private static Map<String, List<StudentHistoryEntity>> allFetchedData = new HashMap<>();

    public static Map<String, List<StudentHistoryEntity>> getAllFetchedData() {
        return allFetchedData;
    }

    public static void setAllFetchedData(Map<String, List<StudentHistoryEntity>> allFetchedData) {
        FetchedHistoryEntity.allFetchedData = allFetchedData;
    }
}
