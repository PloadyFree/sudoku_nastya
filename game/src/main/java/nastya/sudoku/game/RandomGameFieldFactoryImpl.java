package nastya.sudoku.game;

import nastya.sudoku.commons.*;

import java.util.ArrayList;
import java.util.List;

public class RandomGameFieldFactoryImpl implements GameFieldFactory {

    private final ExtendedRandom rnd = ExtendedRandom.getInstance();

    @Override
    public GameField createGameField(FieldSize fieldSize, FieldSize blockSize, int knownCellCount) {
        List<Cell> result = new InternalGameFieldBuilder(fieldSize, blockSize).generate(1000);
        while (result.size() > knownCellCount)
            result.remove(rnd.nextInt(result.size()));
        return new GameFieldImpl(fieldSize, blockSize, result);
    }

    private class InternalGameFieldBuilder {

        int[][] a;
        FieldSize fieldSize;
        FieldSize blockSize;
        FieldSize blockCount;
        Runnable[] randomizers;

        InternalGameFieldBuilder(FieldSize fieldSize, FieldSize blockSize) {
            this.fieldSize = fieldSize;
            this.blockSize = blockSize;
            this.blockCount = new FieldSize(
                    fieldSize.getRowCount() / blockSize.getRowCount(),
                    fieldSize.getColumnCount() / blockSize.getColumnCount()
            );
            randomizers = new Runnable[]{
                    this::swapRandomRows,
                    this::swapRandomColumns,
                    this::swapRandomHorizontalBlocks,
                    this::swapRandomVerticalBlocks
            };
        }

        List<Cell> generate(int swapCount) {
            int rowCount = fieldSize.getRowCount();
            int columnCount = fieldSize.getColumnCount();
            a = new int[rowCount][columnCount];
            for (int i = 0; i < rowCount; i++) {
                int startColumn = ((i % blockSize.getRowCount()) * blockSize.getColumnCount()) + (i / blockSize.getRowCount());
                for (int j = 0; j < columnCount; j++)
                    a[i][(startColumn + j) % columnCount] = j;
            }

            for (int i = 0; i < swapCount; i++)
                randomizers[rnd.nextInt(randomizers.length)].run();

            for (int i = 0; i < rowCount; i++, System.err.println())
                for (int j = 0; j < columnCount; j++)
                    System.err.print(a[i][j] + " ");
            System.err.println();

            List<Cell> result = new ArrayList<>(rowCount * columnCount);
            for (int i = 0; i < rowCount; i++)
                for (int j = 0; j < columnCount; j++)
                    result.add(new Cell(new CellPosition(i, j), a[i][j]));
            return result;
        }

        void swapRandomRows() {
            int block = rnd.nextInt(blockCount.getRowCount());
            int blockRows = blockSize.getRowCount();
            int r1 = block * blockRows + rnd.nextInt(blockRows);
            int r2 = block * blockRows + rnd.nextInt(blockRows);
            swapRows(r1, r2);
        }

        void swapRandomColumns() {
            int block = rnd.nextInt(blockCount.getColumnCount());
            int blockColumns = blockSize.getColumnCount();
            int c1 = block * blockColumns + rnd.nextInt(blockColumns);
            int c2 = block * blockColumns + rnd.nextInt(blockColumns);
            swapColumns(c1, c2);
        }

        void swapRandomHorizontalBlocks() {
            int b1 = rnd.nextInt(blockCount.getRowCount());
            int b2 = rnd.nextInt(blockCount.getRowCount());
            swapHorizontalBlocks(b1, b2);
        }

        void swapRandomVerticalBlocks() {
            int b1 = rnd.nextInt(blockCount.getColumnCount());
            int b2 = rnd.nextInt(blockCount.getColumnCount());
            swapVerticalBlocks(b1, b2);
        }

        void swapHorizontalBlocks(int b1, int b2) {
            int start1 = b1 * blockSize.getRowCount();
            int start2 = b2 * blockSize.getRowCount();
            for (int i = 0; i < blockSize.getRowCount(); i++)
                swapRows(start1 + i, start2 + i);
        }

        void swapVerticalBlocks(int b1, int b2) {
            int start1 = b1 * blockSize.getColumnCount();
            int start2 = b2 * blockSize.getColumnCount();
            for (int i = 0; i < blockSize.getColumnCount(); i++)
                swapColumns(start1 + i, start2 + i);
        }

        void swapRows(int r1, int r2) {
            for (int i = 0; i < fieldSize.getColumnCount(); i++)
                swap(a[r1], i, a[r2], i);
        }

        void swapColumns(int c1, int c2) {
            for (int i = 0; i < fieldSize.getRowCount(); i++)
                swap(a[i], c1, a[i], c2);
        }

        void swap(int[] a1, int at1, int[] a2, int at2) {
            int x = a1[at1];
            int y = a2[at2];
            a1[at1] = y;
            a2[at2] = x;
        }
    }
}