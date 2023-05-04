package Valeram;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class StateOfAutomate {

    private TypeOfToken type = TypeOfToken.INVALID;
    private Map<Character, StateOfAutomate> nextStates = new HashMap<>();

}
