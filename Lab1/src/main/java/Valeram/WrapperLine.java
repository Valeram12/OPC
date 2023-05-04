package Valeram;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class WrapperLine {
    private String line;
    private int row;
    private int col;
    public Character nextChar() {
        return line.charAt(col);
    }
    public Character nextChar(int shift) {
        return line.charAt(col + shift);
    }
    public boolean isEnd() {
        return col >= line.length();
    }
    public boolean isEnd(int shift) {
        return col + shift >= line.length();
    }

}
