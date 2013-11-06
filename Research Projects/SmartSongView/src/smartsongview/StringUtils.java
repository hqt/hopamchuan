/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartsongview;

/**
 *
 * @author Quang Trung
 */
public class StringUtils {

    /**
     * Author: TrungDQ Description: wrap word by maxLength per line
     *
     * @param inputString
     * @param maxLength
     * @return
     */
    public static String wrap(String inputString, int maxLength) {
        // Because of newline reduction
        maxLength = maxLength + 1;

        // The last space position where the cursor passed
        int lastSpacePos = -1;

        // This will follows lastSpacePos to see if lastSpacePos found a new pos or not.
        int lastLastSpacePost = -1;

        // Counting variable to know when to cut a new line
        int count = 1;

        // Because we are going to insert new \n character from left to right
        // so everytime we insert a new character, the later characters position
        // must subtract for this number
        int stackPos = 0;

        // Result string will be append to this builder
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputString.length(); ++i, ++count) {
            // Get current character
            String curChar = inputString.substring(i, i + 1);

            // Append to the result
            sb.append(curChar);


            // Capture the last space character position
            if (curChar.equals(" ")) {
                lastSpacePos = i;
            }

            // If the cursor is in new line then reset the counter.
            if (curChar.equals("\n")) {
                count = 0;
            } else if (count >= maxLength && !inputString.substring(i + 1, i + 2).equals("\n")) {
                // If the counter have excessed the max line char number and the next char is not a newline char
                // TODO: here [ Trong ------------------------------------------------- cơn mưa đêm nhẹ như gió ]
                // Insert a newline char to the last space character position, plus the stack
                if (lastLastSpacePost != lastSpacePos) {
                    sb.insert(lastSpacePos + 1 + stackPos, "\n");
                    lastLastSpacePost = lastSpacePos;

                    // Increase stack by 1
                    stackPos++;
                    count = 0;
                }

            }
        }
        return sb.toString();
    }
}
