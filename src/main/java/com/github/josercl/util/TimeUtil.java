package com.github.josercl.util;

public class TimeUtil {

    private static final String HOUR_SEPARATOR = ":";
    private static final String MILLIS_SEPARATOR = ",";

    private static final String HOUR_FORMAT = "%02d";
    private static final String MILLIS_FORMAT = "%03d";

    private TimeUtil(){}

    public static int convertToMillis(String timeDiff) {
        String[] pieces = timeDiff.split(HOUR_SEPARATOR);
        int h = Integer.parseInt(pieces[0]);
        int m = Integer.parseInt(pieces[1]);
        String[] pieces2 = pieces[2].split(MILLIS_SEPARATOR);
        int s = Integer.parseInt(pieces2[0]);
        int millis = 0;
        if (pieces2.length > 1) {
            millis = Integer.parseInt(pieces2[1]);
        }

        if (timeDiff.charAt(0) == '-' || h < 0) {
            m *= -1;
            s *= -1;
            millis *= -1;
        }

        return millis + s * 1000 + m * 60000 + h * 3600000;
    }

    public static String convertFromMillis(int inputMillis) {
        int millis = inputMillis;
        int hours = millis / 3600000;
        millis %= 3600000;
        int minutes = millis / 60000;
        millis %= 60000;
        int seconds = millis / 1000;
        millis %= 1000;

        StringBuilder builder = new StringBuilder();
        builder.append(String.format(HOUR_FORMAT, hours))
                .append(HOUR_SEPARATOR)
                .append(String.format(HOUR_FORMAT, minutes))
                .append(HOUR_SEPARATOR)
                .append(String.format(HOUR_FORMAT, seconds))
                .append(MILLIS_SEPARATOR)
                .append(String.format(MILLIS_FORMAT, millis))
                .trimToSize();

        return builder.toString();
    }

    public static String formatAsTime(int hours, int minutes, int seconds, int millis, boolean negative){
        int totalMillis = millis + seconds * 1000 + minutes * 60000 + hours * 3600000;
        String formatted = convertFromMillis(totalMillis);
        if(negative){
            formatted = "-" + formatted;
        }
        return formatted;
    }
}
