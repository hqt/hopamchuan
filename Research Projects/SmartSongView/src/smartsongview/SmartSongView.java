/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartsongview;

import java.util.List;
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
        PrintSmartSongView(InputData.song2,30);
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
        // 2. Remove all chord mark [..] from B
        String contentB = content.replaceAll("\\[.*?\\]", "");
        // Use this in android instead;
        // String contentC = android.text.TextUtils.join("\n", contentB.split(".*", maxLineChar));
//        String contentC = "";
//        String[] contentCs = StringUtils.splitIntoLine(contentB, maxLineChar);
//        for (String line : contentCs) {
//            contentC += line + "\n";
//        }
        String contentC = StringUtils.wrap(contentB, maxLineChar);
        
        System.out.println(contentB);
        System.out.println("");
        System.out.println("============");
        System.out.println("");
        System.out.println(contentC);
    }
}
