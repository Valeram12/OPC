package Valeram;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private TypeOfToken type;
    private int row;
    private int col;
    private int symbolTableIndex;

}
