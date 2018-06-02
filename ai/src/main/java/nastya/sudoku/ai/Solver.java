package nastya.sudoku.ai;

import nastya.sudoku.commons.CellPosition;
import nastya.sudoku.commons.GameField;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;

public class Solver {

    private final BiConsumer<String, Object[]> logger;

    public Solver(BiConsumer<String, Object[]> logger) {
        this.logger = logger;
    }

    public boolean solve(GameField gameField) {
        LocalDateTime start = LocalDateTime.now();
        boolean solved = new InternalSolver(gameField).solve();
        logger.accept("Решение " + (!solved ? "не " : "") + "найдено", null);
        logger.accept("На поиск решения затрачено %d мс", new Object[]{Duration.between(start, LocalDateTime.now()).toMillis()});
        return solved;
    }

    private class InternalSolver {

        final GameField gameField;
        final Set<CellPosition> unknown;

        InternalSolver(GameField gameField) {
            this.gameField = gameField;
            unknown = gameField.allPositions()
                    .filter(pos -> !gameField.isKnown(pos))
                    .collect(Collectors.toSet());
        }

        boolean solve() {
            if (unknown.isEmpty())
                return true;

            CellPosition bestPosition = unknown.stream()
                    .min(comparingInt(this::countAvailableVariants))
                    .get();

            unknown.remove(bestPosition);
            for (int x = 0; x < gameField.getFieldSize().getRowCount(); x++)
                if (gameField.setValue(bestPosition, x)) {
                    logger.accept("Ставлю на клетку %s значение %d", new Object[]{bestPosition, x});
                    if (solve())
                        return true;
                }
            unknown.add(bestPosition);

            logger.accept("Стираю значение с клетки %s", new Object[]{bestPosition});
            gameField.setValue(bestPosition, null);
            return false;
        }

        private int countAvailableVariants(CellPosition position) {
            return (int) IntStream.range(0, gameField.getFieldSize().getRowCount())
                    .filter(x -> gameField.isAbleToSetValue(position, x))
                    .count();
        }
    }
}