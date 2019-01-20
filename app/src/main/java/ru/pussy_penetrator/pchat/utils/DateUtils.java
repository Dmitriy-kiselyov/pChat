package ru.pussy_penetrator.pchat.utils;

import java.util.Date;

public class DateUtils {
    public static String formatDate(Date sendTime) {
        Date now = new Date();

        //Дата
        String formatted = "";
        if (new Date(now.getYear(), now.getMonth(), now.getDate()).getTime() ==
                new Date(sendTime.getYear(), sendTime.getMonth(), sendTime.getDate()).getTime()) {
            formatted += "";
        } else if (new Date(now.getYear(), now.getMonth(), now.getDate() - 1).getTime() ==
                new Date(sendTime.getYear(), sendTime.getMonth(), sendTime.getDate()).getTime()) {
            formatted += "вчера в ";
        } else {
            formatted += formatDay(sendTime.getDate()) + "." + formatDay(sendTime.getMonth() + 1);
            if (now.getYear() != sendTime.getYear())
                formatted += "." + sendTime.getYear();
            formatted += " в ";
        }

        formatted += formatDay(sendTime.getHours()) + ":" + formatDay(sendTime.getMinutes());

        return formatted;
    }

    private static String formatDay(int day) {
        String formatted = String.valueOf(day);
        if (day < 10) {
            formatted = "0" + formatted;
        }
        return formatted;
    }

    public static boolean sameToMinutes(Date date1, Date date2) {
        if (
                date1.getYear() == date2.getYear() &&
                date1.getMonth() == date2.getMonth() &&
                date1.getDate() == date2.getDate() &&
                date1.getHours() == date2.getHours() &&
                date1.getMinutes() == date2.getMinutes()
        ) {
            return true;
        }

        return false;
    }
}
