package Valeram;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class AutomateBuilder {
    public static Automate build(List<String> tokenValues) {
        var tokens = new ArrayList<TokenParam>();
        for (var value: tokenValues) {
            var type = TypeOfToken.getTokenValue(value);
            tokens.add(new TokenParam(type, value));
        }
        var root = new StateOfAutomate();
        build(root, tokens, 0);
        return new Automate(root);
    }

    private static void build (StateOfAutomate root, List<TokenParam> tokens, int depth) {
        var nodes = new HashMap<Character, List<TokenParam>>();
        for (TokenParam token : tokens) {
            if (token.value().length() > depth) {
                var letter = token.value().charAt(depth);

                nodes.putIfAbsent(letter, new ArrayList<>());
                nodes.get(letter).add(token);
            }
        }

        for (var key: nodes.keySet()) {
            var node = new StateOfAutomate();
            root.nextStates().putIfAbsent(key, node);
            for (var token: nodes.get(key)) {
                if (token.value().length() - 1 == depth) {
                    node.type(TypeOfToken.getTokenValue(token.value()));
                }
            }
            build(node, nodes.get(key), depth + 1);
        }
    }

}
