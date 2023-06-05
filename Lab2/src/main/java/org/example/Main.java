package org.example;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;

import java.io.IOException;
import java.io.InputStream;

class Main {
    public static void main(String[] args) {
        try {

            InputStream inputStream = Main.class.getResourceAsStream("Contents.swift");
            Lexer lexer = new Swift5Lexer(CharStreams.fromStream(inputStream));
            TokenStream tokenStream = new CommonTokenStream(lexer);
            Swift5Parser parser = new Swift5Parser(tokenStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

