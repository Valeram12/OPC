package org.example;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

import java.util.BitSet;

public class SwiftSupport {
    public static final BitSet operatorHead = new BitSet(0x10000);
    public static final BitSet operatorCharacter;

    public static final BitSet leftWS = new BitSet(255);
    public static final BitSet rightWS = new BitSet(255);

    static {
        operatorHead.set('/');
        operatorHead.set('=');
        operatorHead.set('-');
        operatorHead.set('+');
        operatorHead.set('!');
        operatorHead.set('*');
        operatorHead.set('%');
        operatorHead.set('&');
        operatorHead.set('|');
        operatorHead.set('<');
        operatorHead.set('>');
        operatorHead.set('^');
        operatorHead.set('~');
        operatorHead.set('?');


        operatorCharacter = (BitSet) operatorHead.clone();


        leftWS.set(Swift5Parser.WS);
        leftWS.set(Swift5Parser.LPAREN);
        leftWS.set(Swift5Parser.Interpolataion_multi_line);
        leftWS.set(Swift5Parser.Interpolataion_single_line);
        leftWS.set(Swift5Parser.LBRACK);
        leftWS.set(Swift5Parser.LCURLY);
        leftWS.set(Swift5Parser.COMMA);
        leftWS.set(Swift5Parser.COLON);
        leftWS.set(Swift5Parser.SEMI);

        rightWS.set(Swift5Parser.WS);
        rightWS.set(Swift5Parser.RPAREN);
        rightWS.set(Swift5Parser.RBRACK);
        rightWS.set(Swift5Parser.RCURLY);
        rightWS.set(Swift5Parser.COMMA);
        rightWS.set(Swift5Parser.COLON);
        rightWS.set(Swift5Parser.SEMI);
        rightWS.set(Swift5Parser.Line_comment);
        rightWS.set(Swift5Parser.Block_comment);
    }

    private static boolean isCharacterFromSet(Token token, BitSet bitSet) {
        if (token.getType() == Token.EOF) {
            return false;
        } else {
            String text = token.getText();
            int codepoint = text.codePointAt(0);
            if (Character.charCount(codepoint) != text.length()) {
                return false;
            } else {
                return bitSet.get(codepoint);
            }
        }
    }

    public static boolean isOperatorHead(Token token) {
        return isCharacterFromSet(token, operatorHead);
    }

    public static boolean isOperatorCharacter(Token token) {
        return isCharacterFromSet(token, operatorCharacter);
    }

    public static boolean isOpNext(TokenStream tokens) {
        int start = tokens.index();
        Token lt = tokens.get(start);
        int stop = getLastOpTokenIndex(tokens);
        return stop != -1;
    }

    /**
     * Find stop token index of next operator; return -1 if not operator.
     */
    public static int getLastOpTokenIndex(TokenStream tokens) {
	SwiftSupport.fillUp(tokens);
        int currentTokenIndex = tokens.index();
        Token currentToken = tokens.get(currentTokenIndex);


        if (currentToken.getType() == Swift5Parser.DOT && tokens.get(currentTokenIndex + 1).getType() == Swift5Parser.DOT) {

            currentTokenIndex += 2;
            currentToken = tokens.get(currentTokenIndex);


            while (currentToken.getType() == Swift5Parser.DOT || isOperatorCharacter(currentToken)) {
                currentTokenIndex++;
                currentToken = tokens.get(currentTokenIndex);
            }

            return currentTokenIndex - 1;
        }

        if (isOperatorHead(currentToken)) {

            currentToken = tokens.get(currentTokenIndex);
            while (isOperatorCharacter(currentToken)) {
                currentTokenIndex++;
                currentToken = tokens.get(currentTokenIndex);
            }
            return currentTokenIndex - 1;
        } else {
            return -1;
        }
    }

    /**
     * "If an operator has whitespace around both sides or around neither side,
     * it is treated as a binary operator. As an example, the + operator in a+b
     * and a + b is treated as a binary operator."
     */
    public static boolean isBinaryOp(TokenStream tokens) {
	SwiftSupport.fillUp(tokens);
        int stop = getLastOpTokenIndex(tokens);
        if (stop == -1) return false;

        int start = tokens.index();
        Token currentToken = tokens.get(start);
        Token prevToken = tokens.get(start - 1);
        Token nextToken = tokens.get(stop + 1);
        boolean prevIsWS = isLeftOperatorWS(prevToken);
        boolean nextIsWS = isRightOperatorWS(nextToken);

        if (prevIsWS) {
            return nextIsWS;
        } else {
            if (currentToken.getType() == Swift5Lexer.BANG || currentToken.getType() == Swift5Lexer.QUESTION) {
                return false;
            } else {
                if (!nextIsWS) return nextToken.getType() != Swift5Lexer.DOT;
            }
        }
        return false;
    }

    /**
     * "If an operator has whitespace on the left side only, it is treated as a
     * prefix unary operator. As an example, the ++ operator in a ++b is treated
     * as a prefix unary operator."
     */
    public static boolean isPrefixOp(TokenStream tokens) {
	SwiftSupport.fillUp(tokens);
        int stop = getLastOpTokenIndex(tokens);
        if (stop == -1) return false;

        int start = tokens.index();
        Token prevToken = tokens.get(start - 1);
        Token nextToken = tokens.get(stop + 1);
        boolean prevIsWS = isLeftOperatorWS(prevToken);
        boolean nextIsWS = isRightOperatorWS(nextToken);

        return prevIsWS && !nextIsWS;
    }

    /**
     * "If an operator has whitespace on the right side only, it is treated as a
     * postfix unary operator. As an example, the ++ operator in a++ b is treated
     * as a postfix unary operator."
     * <p>
     * "If an operator has no whitespace on the left but is followed immediately
     * by a dot (.), it is treated as a postfix unary operator. As an example,
     * the ++ operator in a++.b is treated as a postfix unary operator (a++ .b
     * rather than a ++ .b)."
     */
    public static boolean isPostfixOp(TokenStream tokens) {
	SwiftSupport.fillUp(tokens);
        int stop = getLastOpTokenIndex(tokens);
        if (stop == -1) return false;

        int start = tokens.index();
        Token prevToken = tokens.get(start - 1); // includes hidden-channel tokens
        Token nextToken = tokens.get(stop + 1);
        boolean prevIsWS = isLeftOperatorWS(prevToken);
        boolean nextIsWS = isRightOperatorWS(nextToken);
        return !prevIsWS && nextIsWS ||
                !prevIsWS && nextToken.getType() == Swift5Parser.DOT;
    }

    public static boolean isOperator(TokenStream tokens, String op) {
	SwiftSupport.fillUp(tokens);
        int stop = getLastOpTokenIndex(tokens);
        if (stop == -1) return false;

        int start = tokens.index();
        String text = tokens.getText(Interval.of(start, stop));

        return text.equals(op);
    }

    public static boolean isLeftOperatorWS(Token t) {
        return leftWS.get(t.getType());
    }

    public static boolean isRightOperatorWS(Token t) {
        return rightWS.get(t.getType()) || t.getType() == Token.EOF;
    }

    public static boolean isSeparatedStatement(TokenStream tokens, int indexOfPreviousStatement) {
	SwiftSupport.fillUp(tokens);

        int indexFrom = indexOfPreviousStatement - 1;
        int indexTo = tokens.index() - 1;

        if (indexFrom >= 0) {
            // Stupid check for new line and semicolon, can be optimized
            while (indexFrom >= 0 && tokens.get(indexFrom).getChannel() == Token.HIDDEN_CHANNEL) {
                indexFrom--;
            }

            for(int i =indexTo;i>= indexFrom;i--){
                String t = tokens.get(i).getText();
                if(t.contains("\n") || t.contains(";")){
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
    public static void fillUp(TokenStream tokens)
    {
	for (int jj = 1;;++jj)
	{
	    int t = tokens.LA(jj);
	    if (t == -1) break;
	}
    }
}
