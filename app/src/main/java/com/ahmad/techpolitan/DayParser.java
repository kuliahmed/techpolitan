package com.ahmad.techpolitan;

public class DayParser {
    public static String getDayByNumber(int dayNumber){
        switch(dayNumber){
            case 1:
                return "Minggu";
            case 2:
                return "Senin";
            case 3:
                return "Selasa";
            case 4:
                return "Rabu";
            case 5:
                return "Kamis";
            case 6:
                return "Jumat";
            case 7:
                return "Sabtu";
            default:
                return "";
        }
    }
}
