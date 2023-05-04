package Valeram;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

public record Automate(StateOfAutomate root) {
    @Getter
    @Setter
    @Accessors(fluent = true)
    @AllArgsConstructor
    class PositionOfToken {
        private TypeOfToken type;
        private int position;
    }
    public PositionOfToken getPosition(String code, int position) {
        return getPosition(root, code, position);
    }

    private PositionOfToken getPosition(StateOfAutomate node, String code, int position) {
        if (code.length() <= position) {
            return new PositionOfToken(node.type(), position);
        }
        if (node.nextStates().containsKey(code.charAt(position))) {
            return getPosition(node.nextStates().get(code.charAt(position)), code, position + 1);
        }
        return new PositionOfToken(node.type(), position);
    }

}


