package com.github.josercl.logic;

import com.github.josercl.util.TimeUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SrtFixer {

    private static final String TIME_REG_EX = "(\\-)?(\\d{0,2}:\\d{0,2}:\\d{0,2}(,\\d{0,3})?)";
    private static final String REG_EX = TIME_REG_EX + "\\s*\\-\\->\\s*" + TIME_REG_EX;
    private static final String REG_EX_LINE_NUMBER = "^\\d+$";

    private static final String TIMES_SEPARATOR = " --> ";

    private Pattern timePattern = Pattern.compile(REG_EX);

    public void fixFile(String file, String timeDiff, int lineDiff) {
        int millis = TimeUtil.convertToMillis(timeDiff);
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;
            StringBuilder outputBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (Pattern.matches(REG_EX_LINE_NUMBER, line)) {
                    outputBuilder.append(Integer.parseInt(line) + lineDiff);
                } else if (Pattern.matches(REG_EX, line)) {
                    outputBuilder.append(fixTime(line, millis));
                } else {
                    outputBuilder.append(line);
                }
                outputBuilder.append("\n");
            }
            br.close();
            reader.close();

            FileWriter fw = new FileWriter(file);
            fw.write(outputBuilder.toString());
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String fixTime(String time, int timeDiff) {
        Matcher match = timePattern.matcher(time);
        if (match.matches()) {
            int start = TimeUtil.convertToMillis(match.group(2)) + timeDiff;
            int end = TimeUtil.convertToMillis(match.group(5)) + timeDiff;

            return TimeUtil.convertFromMillis(start) +
                    TIMES_SEPARATOR +
                    TimeUtil.convertFromMillis(end);
        }
        return null;
    }

    public void showHelp() {
        System.out.println("Usage: java SrtFixer file time_adjusment [line_number_adjustment]");
        System.out.println("file\t\t\tThe path of the file to fix");
        System.out.println("time_adjustment\t\tTime difference to fix srt times, must be specified in format [-]hh:mm:ss,SSS");
        System.out.println("line_number_adjustment\t(Optional) Adjustment to use for subtitles number");
    }
}
