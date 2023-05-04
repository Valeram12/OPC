package Valeram;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class TokenParam {

    private TypeOfToken type;
    private String value;

}
