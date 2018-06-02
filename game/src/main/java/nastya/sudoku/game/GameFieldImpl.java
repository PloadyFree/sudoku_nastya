package nastya.sudoku.game;

import nastya.sudoku.commons.Cell;
import nastya.sudoku.commons.CellPosition;
import nastya.sudoku.commons.FieldSize;
import nastya.sudoku.commons.GameField;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameFieldImpl implements GameField {

    private final FieldSize fieldSize;
    private final FieldSize blockSize;
    private final Integer[][] field;
    private final boolean[][] isFixed;

    public GameFieldImpl(FieldSize fieldSize, FieldSize blockSize, Iterable<Cell> knownCells) {
        this.fieldSize = fieldSize;
        this.blockSize = blockSize;
        field = new Integer[fieldSize.getRowCount()][fieldSize.getColumnCount()];
        isFixed = new boolean[fieldSize.getRowCount()][fieldSize.getColumnCount()];
        for (Cell cell : knownCells) {
            CellPosition pos = cell.getPosition();
            if (!setValue(pos, cell.getValue()))
                throw new IllegalArgumentException("Не удалось поставить на поле значение " + cell);
            isFixed[pos.getRow()][pos.getColumn()] = true;
        }
    }

    @Override
    public FieldSize getFieldSize() {
        return fieldSize;
    }

    @Override
    public FieldSize getBlockSize() {
        return blockSize;
    }

    @Override
    public boolean isFixed(CellPosition position) {
        return isFixed[position.getRow()][position.getColumn()];
    }

    @Override
    public Integer getValue(CellPosition position) {
        return field[position.getRow()][position.getColumn()];
    }

    @Override
    public boolean setValue(CellPosition position, Integer value) {
        if (!isAbleToSetValue(position, value))
            return false;
        field[position.getRow()][position.getColumn()] = value;
        return true;
    }

    @Override
    public GameFieldImpl clone() {
        Stream<Cell> cells = allPositions().filter(this::isKnown).map(this::getCell);
        return new GameFieldImpl(fieldSize, blockSize, cells.collect(Collectors.toList()));
    }
}