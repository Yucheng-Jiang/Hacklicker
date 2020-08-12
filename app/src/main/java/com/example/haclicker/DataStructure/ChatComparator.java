package com.example.haclicker.DataStructure;

import java.util.Comparator;

public class ChatComparator implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        if (o instanceof Chat && t1 instanceof Chat) {
            Chat first = (Chat) o;
            Chat second = (Chat) t1;
            int difference = first.getNumVote() - second.getNumVote();
            if (difference == 0) {
                return 0;
            } else if (difference > 0) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }
}
