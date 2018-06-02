package nastya.sudoku.commons;

import lombok.Value;

/**
 * Класс, описывающий позицию клетки на поле.
 */
@Value
public class CellPosition {

    /**
     * Номер строки клетки.
     */
    int row;

    /**
     * Номер столбца клетки.
     */
    int column;

    public String toString() {
        return String.format("(%d, %d)", row, column);
    }
}