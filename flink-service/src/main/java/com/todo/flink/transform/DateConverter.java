package com.todo.flink.transform;

import java.time.LocalDateTime;

public class DateConverter {

    public static LocalDateTime fromArray(int[] arr) {
        if (arr == null) return null;

        int year = arr[0];
        int month = arr[1];
        int day = arr[2];
        int hour = arr.length > 3 ? arr[3] : 0;
        int minute = arr.length > 4 ? arr[4] : 0;
        int second = arr.length > 5 ? arr[5] : 0;
        int nano = arr.length > 6 ? arr[6] : 0;

        return LocalDateTime.of(year, month, day, hour, minute, second, nano);
    }
}