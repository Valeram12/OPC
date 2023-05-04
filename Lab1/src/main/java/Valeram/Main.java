package Valeram;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;


public class Main {
    private static final String SwiftFile = "src/main/resources/test.swift";
    private static final String Keywords = "src/main/resources/keywords.txt";
    private static final String Operathors = "src/main/resources/operators.txt";

    public static void main(String[] args) {
        List<String> keywords = null, operators = null;
        var filesAreReachable = true;
        String errorMessage = null;

        try {
            operators = read(Operathors);
            keywords = read(Keywords);
        } catch (Exception e) {
            filesAreReachable = false;
            errorMessage = e.getMessage();
        }

        if (!filesAreReachable) {
            System.out.println("An error occurred: " + errorMessage);
            return;
        }

        var l = new Lexer(operators, keywords);
        l.Parse(SwiftFile);

        print(l);
    }

    @SneakyThrows
    private static List<String> read(String path) {
        var words = new ArrayList<String>();
        var br = new BufferedReader(new FileReader(path));
        String word;
        while ((word = br.readLine()) != null) {
            words.add(word.trim());
        }
        return words;
    }

    private static void print(Lexer l) {
        System.out.println("Token: ");
        var stringBuilder1 = new StringBuilder();
        l.getTokens().forEach(t -> {
                    var val = l.getSymbolTable().get(t.getSymbolTableIndex() - 1);
                    stringBuilder1.append(String.format("%d. %s (\"%s\") with length %d", t.getSymbolTableIndex(), t.getType(), val, val.length()));
                    stringBuilder1.append(String.format(" on row %d, col %d", t.getRow(), t.getCol()));
                    stringBuilder1.append("\n");
                });
        System.out.println(stringBuilder1);
        System.out.println("Error: ");
        var stringBuilder2 = new StringBuilder();
        l.getError().forEach(i -> {
                    var val = i.getValue();
                    stringBuilder2.append(String.format("(\"%s\") on row %d, col %d", val, i.getRow(), i.getCol()));
                    stringBuilder2.append("\n");
                });
        System.out.println(stringBuilder2);

    }

}
