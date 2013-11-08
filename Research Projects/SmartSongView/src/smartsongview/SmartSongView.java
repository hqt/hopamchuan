/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartsongview;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sampleinput.InputData;

/**
 *
 * @author Quang Trung
 */
public class SmartSongView {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PrintSmartSongView(InputData.song2, 20);
    }

//    Process:
//    1. Clone A to B
//    2. Remove all chord mark [..] from B and apply smartwrap => C
//    3. Use A to get all chord mark positions {line-offset} (include case of smartwrap)
//    4. Insert chord lines
//
//    Max Line Char calculator
//    420 - 15px - 48 chars (28 * 1.7143)
//    420 - 20px - 36 chars (21 * 1.7143 + 0.001)
//    420 - 22px - 33 chars (19.1 * 1.7143 - 0.28)
//    420 - 25px - 29 chars (16.8 * 1.7143 - 0.2)
//    420 - 30px - 24 chars (14 * 1.7143 + 0.002)
//    420 - 35px - 20 chars (12 * 1.7143 + 0.5)
//    740 - 22px - 57 chars 
//    (screenWidth - 60) / textSize * 1.7143
    public static void PrintSmartSongView(String content, int maxLineChar) {
        // Remove all chord mark [..] from B
        String contentB = content.replaceAll("\\[.*?\\]", "");

        // Smart wrap
        String contentC = StringUtils.wrap(content, maxLineChar);

        // Line - Offset - Char
        Map<Integer, Map<Integer, String>> pos = new HashMap<>();

        Pattern pattern = Pattern.compile("\\[.*?\\]");
        Matcher matcher = pattern.matcher(content);
        // Check all occurrences
        while (matcher.find()) {
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end());
            System.out.println(" Found: " + matcher.group());
        }

        System.out.println(content);
        System.out.println("");
        System.out.println("============");
        System.out.println("");
        System.out.println(contentC);
    }
}
