package nastya.sudoku.game;

import nastya.sudoku.commons.FieldSize;
import nastya.sudoku.commons.GameField;

/**
 * Фабрика, позволяющая создавать игровые поля со случайной расстановкой мин.
 */
public interface GameFieldFactory {

    /**
     * Создаёт игровое поле размера {@code fieldSize} с {@code knownCellCount} открытыми клетками.<br/>
     *
     * @param fieldSize размер поля.
     * @param knownCellCount количество заранее известных клеток.
     * @return игровое поле размера {@code fieldSize} с {@code knownCellCount} открытыми клетками.
     */
    GameField createGameField(FieldSize fieldSize, FieldSize blockSize, int knownCellCount);
}