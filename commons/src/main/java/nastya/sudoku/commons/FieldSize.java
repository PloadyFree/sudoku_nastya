package nastya.sudoku.commons;

import lombok.Value;

/**
 * Класс, описывающий размер игрового поля.
 */
@Value
public class FieldSize {
    /**
     * Количество строк поля.
     */
    int rowCount;

    /**
     * Количество столбцов поля.
     */
    int columnCount;

    /**
     * @return количество клеток на поле.
     */
    public int getCellCount() {
        return rowCount * columnCount;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", rowCount, columnCount);
    }
}