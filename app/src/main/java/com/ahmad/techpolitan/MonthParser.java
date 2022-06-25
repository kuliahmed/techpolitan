package com.ahmad.techpolitan;

public class MonthParser {
    public static String getMonthByNumber(int monthNumber){
        switch(monthNumber){
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "Mei";
            case 5:
                return "Juni";
            case 6:
                return "Juli";
            case 7:
                return "Ags";
            case 8:
                return "Sep";
            case 9:
                return "Okt";
            case 10:
                return "Nov";
            case 11:
                return "Des";
            default:
                return "";
        }
    }
}
