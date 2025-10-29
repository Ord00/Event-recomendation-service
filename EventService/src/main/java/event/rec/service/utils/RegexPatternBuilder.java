package event.rec.service.utils;

import java.util.regex.Pattern;

public class RegexPatternBuilder {

    public static String buildSearchEventPattern(String request) {

        if (request == null || request.trim().isEmpty()) {
            return ".*";
        }

        String[] words = request.trim().split("\\s+");

        StringBuilder regex = new StringBuilder();

        regex.append(".*");

        for (int i = 0; i < words.length; i++) {

            if (i > 0) {
                regex.append(".*");
            }

            regex.append("\\b").append(Pattern.quote(words[i])).append("\\b");
        }

        regex.append(".*");

        return regex.toString();
    }
}
