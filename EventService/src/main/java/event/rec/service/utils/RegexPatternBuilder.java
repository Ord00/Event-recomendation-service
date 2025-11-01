package event.rec.service.utils;

public class RegexPatternBuilder {

    public static String buildSearchEventPattern(String request) {
        if (request == null || request.trim().isEmpty()) {
            return ".*";
        }

        String[] words = request.trim().split("\\s+");
        StringBuilder regex = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                regex.append(".*");
            }

            regex.append(escapeForPostgresRegex(words[i]));
        }

        return regex.toString();
    }

    private static String escapeForPostgresRegex(String word) {
        return word.replaceAll("([.\\\\+*?\\[^\\]$(){}=!<>|:-])", "\\\\$1");
    }
}
