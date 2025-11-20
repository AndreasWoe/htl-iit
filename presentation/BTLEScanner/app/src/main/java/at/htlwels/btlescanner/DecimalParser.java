package at.htlwels.btlescanner;

import java.util.ArrayList;
import java.util.List;

public class DecimalParser {
    public static List<Integer> parseDecimals(StringBuffer buffer) {
        List<Integer> values = new ArrayList<>();
        String content = buffer.toString();
        int start = 0;

        while ((start = content.indexOf('*', start)) != -1) {
            int end = content.indexOf('#', start + 1);
            if (end != -1 && end - start == 5) { // 4 digits + 2 special characters
                try {
                    String s = content.substring(start + 1, end);
                    int value = Integer.parseInt(s);
                    values.add(value);
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
                start = end + 1;
            } else {
                start++;
            }
        }

        return values;
    }
}