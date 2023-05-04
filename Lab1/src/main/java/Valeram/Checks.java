package Valeram;

public interface Checks {
    static boolean isDigit(char c) {
        return Character.isDigit(c);
    }
    static boolean isText(char c) {
        return Character.isAlphabetic(c) || c == '_';
    }
    static boolean isPunctuation(Character c) {
        return ",;[]{}()".contains(c.toString());
    }
    static boolean isOperator(Character c) {
        return "+-*/<>=?!.:&|^~%".contains(c.toString());
    }
    static boolean isWhitespace(char c) {
        return c == ' ';
    }
    static boolean isTab(char c) {
        return c == '\t';
    }
    static boolean isComment(char c1, char c2) {
        return c1 == '/' && (c2 == '/' || c2 == '*');
    }
    static boolean isDoubleQuote(char c) {
        return c == '"';
    }
    static boolean isEndOfToken(char c) {
        return isWhitespace(c) || isTab(c) || isPunctuation(c) || isOperator(c);
    }

}
