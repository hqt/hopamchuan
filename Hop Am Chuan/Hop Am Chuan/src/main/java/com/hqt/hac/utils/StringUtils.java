package com.hqt.hac.utils;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hqt.hac.utils.LogUtils.LOGE;

/**
 * Created by Quang Trung on 11/9/13.
 */
public class StringUtils {

    private static char[] SPECIAL_CHARACTERS = {
            'á','à','ả','ã','ạ','ă','ắ','ặ','ằ','ẳ','ẵ','â','ấ','ầ','ẩ','ẫ','ậ',
            'Á','À','Ả','Ã','Ạ','Ă','Ắ','Ặ','Ằ','Ẳ','Ẵ','Â','Ấ','Ầ','Ẩ','Ẫ','Ậ',
            'đ',
            'Đ',
            'é','è','ẻ','ẽ','ẹ','ê','ế','ề','ể','ễ','ệ',
            'É','È','Ẻ','Ẽ','Ẹ','Ê','Ế','Ề','Ể','Ễ','Ệ',
            'í','ì','ỉ','ĩ','ị',
            'Í','Ì','Ỉ','Ĩ','Ị',
            'ó','ò','ỏ','õ','ọ','ô','ố','ồ','ổ','ỗ','ộ','ơ','ớ','ờ','ở','ỡ','ợ',
            'Ó','Ò','Ỏ','Õ','Ọ','Ô','Ố','Ồ','Ổ','Ỗ','Ộ','Ơ','Ớ','Ờ','Ở','Ỡ','Ợ',
            'ú','ù','ủ','ũ','ụ','ư','ứ','ừ','ử','ữ','ự',
            'Ú','Ù','Ủ','Ũ','Ụ','Ư','Ứ','Ừ','Ử','Ữ','Ự',
            'ý','ỳ','ỷ','ỹ','ỵ',
            'Ý','Ỳ','Ỷ','Ỹ','Ỵ'
    };

    private static char[] REPLACEMENTS = {
            'a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a','a',
            'A','A','A','A','A','A','A','A','A','A','A','A','A','A','A','A','A',
            'd',
            'D',
            'e','e','e','e','e','e','e','e','e','e','e',
            'E','E','E','E','E','E','E','E','E','E','E',
            'i','i','i','i','i',
            'I','I','I','I','I',
            'o','o','o','o','o','o','o','o','o','o','o','o','o','o','o','o','o',
            'O','O','O','O','O','O','O','O','O','O','O','O','O','O','O','O','O',
            'u','u','u','u','u','u','u','u','u','u','u',
            'U','U','U','U','U','U','U','U','U','U','U',
            'y','y','y','y','y',
            'Y','Y','Y','Y','Y'
    };

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
        int index = -1;

        for (int i = 0; i < SPECIAL_CHARACTERS.length; ++i) {
            if (SPECIAL_CHARACTERS[i] == ch) {
                index = i;
                break;
            }
        }

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
