package nastya.sudoku.commons;

import java.util.Random;

public class ExtendedRandom extends Random {

    private ExtendedRandom(long seed) {
        super(seed);
    }

    private static ExtendedRandom INSTANCE = new ExtendedRandom(System.currentTimeMillis());

    public static ExtendedRandom getInstance() {
        return INSTANCE;
    }

    public int nextInt(int from, int to) {
        return from + nextInt(to - from + 1);
    }
}