package com.hqt.hac.utils;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Quang Trung on 11/9/13.
 */
public class StringUtils {

    private static char[] SPECIAL_CHARACTERS = { ' ', '!', '"', '#', '$', '%',
            '*', '+', ',', ':', '<', '=', '>', '?', '@', '[', '\\', ']', '^',
            '`', '|', '~', 'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò',
            'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â', 'ã', 'è', 'é', 'ê',
            'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý', 'Ă', 'ă', 'Đ', 'đ',
            'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ',
            'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ', 'Ắ', 'ắ', 'Ằ', 'ằ',
            'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế',
            'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị',
            'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ', 'Ộ',
            'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ',
            'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ', 'ữ', 'Ự', 'ự', };

    private static char[] REPLACEMENTS = { ' ', '\0', '\0', '\0', '\0', '\0',
            '\0', '_', '\0', '_', '\0', '\0', '\0', '\0', '\0', '\0', '_',
            '\0', '\0', '\0', '\0', '\0', 'A', 'A', 'A', 'A', 'E', 'E', 'E',
            'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a', 'a', 'a',
            'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A',
            'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I',
            'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', };

    /**
     * Convert inline chord sign to two lines chord sign style.
     * @param content
     * @return String
     */
    public static String formatLyricTwoLines(String content) {
        String lines[] = content.split("\n");
        String result = "";
        for (String line : lines) {
            StringBuilder chordLine = new StringBuilder();
            String lyricLine = line.replaceAll("\\[.*?\\]", "");
            Pattern pattern = Pattern.compile("\\[.*?\\]");
            Matcher matcher = pattern.matcher(line);

            int lastStart = 0;
            int stackSpace = 0;
            while (matcher.find()) {
                for (int i = 0; i < matcher.start() - lastStart - stackSpace * 2; ++i) {
                    chordLine.append(" ");
                }
                chordLine.append(matcher.group());
                lastStart = matcher.start();
                stackSpace = matcher.group().length();
            }
            result += chordLine + "\n";
            result += lyricLine + "\n";
        }
        return result;
    }

    /**
     * Remove accent by character
     * @param ch
     * @return
     */
    public static char removeAcients(char ch) {
        int index = Arrays.binarySearch(SPECIAL_CHARACTERS, ch);
        if (index >= 0) {
            ch = REPLACEMENTS[index];
        }
        return ch;
    }

    /**
     * Remove accent by String
     * @param s
     * @return
     */
    public static String removeAcients(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAcients(sb.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
