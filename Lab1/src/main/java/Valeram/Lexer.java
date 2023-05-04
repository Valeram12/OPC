package Valeram;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import static Valeram.Checks.*;

@Getter
@Setter
public class Lexer {

    private final Automate keywordsOfAutomate;
    private final Automate operatorsOfAutomate;
    private List<Token> tokens;
    private LexerConditions state;
    private WrapperLine wrapperLine;
    private List<ErrorToken> error;
    private List<String> symbolTable;
    private StringBuilder cache;

    public Lexer(List<String> operators, List<String> keywords) {
        keywordsOfAutomate = AutomateBuilder.build(keywords);
        operatorsOfAutomate = AutomateBuilder.build(operators);
        tokens = new ArrayList<>();
        state = LexerConditions.DEFAULT;
        error = new ArrayList<>();
        symbolTable = new ArrayList<>();
        cache = new StringBuilder();
    }
    @SneakyThrows
    private void InnerParse(String path) {
        var reader = new BufferedReader(new FileReader(path));
        String line;
        wrapperLine = new WrapperLine();
        while ((line = reader.readLine()) != null) {
            wrapperLine.line(line).col(0);
            while (!wrapperLine.isEnd()) {
                TokenHandling();
            }
            wrapperLine.row(wrapperLine.row() + 1);
        }
    }
    public boolean Parse(String path) {
        try {
            InnerParse(path);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    private void TokenHandling() {
        var symbol = wrapperLine.nextChar();

        if (isWhitespace(symbol)) {
            WhitespaceHandling();
            return;
        } else if (isTab(symbol)) {
            TabHandling();
            return;
        }
        error.add(new ErrorToken(symbol, "Invalid symbol", wrapperLine.row(), wrapperLine.col()));
        wrapperLine.col(wrapperLine.col() + 1);
    }
    private void WhitespaceHandling() {
        var shift = 0;
        var space = wrapperLine.nextChar(shift);

        while (space == ' ' && !wrapperLine.isEnd(shift + 1)) {
            shift++;
            space = wrapperLine.nextChar(shift);
        }

        addToken(TypeOfToken.WHITE_SPACE, shift);
    }
    private void TabHandling() {
        var shift = 0;
        var tab = wrapperLine.nextChar(shift);

        while (tab == '\t' && !wrapperLine.isEnd(shift + 1)) {
            shift++;
            tab = wrapperLine.nextChar(shift);
        }

        addToken(TypeOfToken.TAB, shift);
    }
    private void addToken(TypeOfToken type, int length) {
        add(type, length, "");
    }
    private void addTokenCache(TypeOfToken type, int length) {
        add(type, length, cache.toString());
        cache = new StringBuilder();
    }
    private void add(TypeOfToken type, int length, String prefix) {
        var start = wrapperLine.col();
        var end = wrapperLine.col() + length;
        symbolTable.add(prefix + wrapperLine.line().substring(start, end));

        int index = symbolTable.size() - 1;
        tokens.add(new Token(type, wrapperLine.row(), wrapperLine.col(), index));
        wrapperLine.col(wrapperLine.col() + length);
        state = LexerConditions.DEFAULT;
    }


}
