package nastya.sudoku.commons;

import lombok.Value;

@Value
public class Cell {
    CellPosition position;
    Integer value;
}