package nastya.sudoku.commons;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Интерфейс, описывающий поведение игры.
 */
public interface GameField extends Cloneable {

    /**
     * Возвращает размер игрового поля.
     *
     * @return размер игрового поля.
     */
    FieldSize getFieldSize();

    FieldSize getBlockSize();

    default FieldSize getBlockCount() {
        return new FieldSize(
                getFieldSize().getRowCount() / getBlockSize().getRowCount(),
                getFieldSize().getColumnCount() / getBlockSize().getColumnCount()
        );
    }

    /**
     * Возвращает статус игры.
     *
     * @return статус игры.
     */
    default GameStatus getGameStatus() {
        return allPositions().map(this::getValue).anyMatch(Objects::isNull)
                ? GameStatus.RUNNING
                : GameStatus.FINISHED;
    }

    /**
     * Возвращает значение клетки, находящейся на позиции {@code position}.
     * Если значение не установлено, возвращает {@literal null}.
     *
     * @param position позиция клетки для проверки.
     * @return значение клетки.
     */
    Integer getValue(CellPosition position);

    default Cell getCell(CellPosition position) {
        return new Cell(position, getValue(position));
    }

    default CellPosition getBlockStart(CellPosition position) {
        FieldSize blockSize = getBlockSize();
        int blockRows = blockSize.getRowCount();
        int blockColumns = blockSize.getColumnCount();

        int row = position.getRow() / blockRows * blockRows;
        int column = position.getColumn() / blockColumns * blockColumns;
        return new CellPosition(row, column);
    }

    boolean isFixed(CellPosition position);

    /**
     * Возвращает {@code true}, если значение в клетке на позиции {@code position} известно.
     *
     * @param position позиция клетки для проверки.
     * @return {@code true}, если значение в клетке на позиции {@code position} известно, иначе {@code false}.
     */
    default boolean isKnown(CellPosition position) {
        return getValue(position) != null;
    }

    default Stream<Cell> valuableCells(CellPosition position) {
        FieldSize fieldSize = getFieldSize();

        Set<CellPosition> valuablePositions = new HashSet<>();

        int fieldRows = fieldSize.getRowCount();
        for (int row = 0; row < fieldRows; row++)
            valuablePositions.add(new CellPosition(row, position.getColumn()));

        int fieldColumns = fieldSize.getColumnCount();
        for (int column = 0; column < fieldColumns; column++)
            valuablePositions.add(new CellPosition(position.getRow(), column));

        FieldSize blockSize = getBlockSize();
        int blockRows = blockSize.getRowCount();
        int blockColumns = blockSize.getColumnCount();
        CellPosition blockStart = getBlockStart(position);
        for (int i = 0; i < blockRows; i++) {
            for (int j = 0; j < blockColumns; j++) {
                int row = blockStart.getRow() + i;
                int column = blockStart.getColumn() + j;
                valuablePositions.add(new CellPosition(row, column));
            }
        }

        return valuablePositions.stream()
                .filter(this::isKnown)
                .map(this::getCell);
    }

    default boolean isAbleToSetValue(CellPosition position, Integer value) {
        if (value == null)
            return !isFixed(position);
        if (isFixed(position))
            return false;

        return valuableCells(position)
                .map(Cell::getValue)
                .noneMatch(val -> val == (int) value);
    }

    boolean setValue(CellPosition position, Integer value);

    /**
     * Возвращает стрим, состоящий из позиций всех клеток, находящихся на поле.
     *
     * @return стрим, состоящий из позиций всех клеток, находящихся на поле.
     */
    default Stream<CellPosition> allPositions() {
        FieldSize fieldSize = getFieldSize();
        int rowCount = fieldSize.getRowCount();
        int columnCount = fieldSize.getColumnCount();

        Stream.Builder<CellPosition> builder = Stream.builder();
        for (int row = 0; row < rowCount; row++)
            for (int column = 0; column < columnCount; column++)
                builder.add(new CellPosition(row, column));

        return builder.build();
    }

    default Stream<Cell> allCells() {
        return allPositions().map(this::getCell);
    }

    GameField clone();
}