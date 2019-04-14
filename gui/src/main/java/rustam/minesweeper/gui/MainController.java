package rustam.minesweeper.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import nastya.sudoku.ai.Solver;
import nastya.sudoku.commons.CellPosition;
import nastya.sudoku.commons.FieldSize;
import nastya.sudoku.commons.GameField;
import nastya.sudoku.commons.GameStatus;
import nastya.sudoku.game.GameFieldFactory;
import nastya.sudoku.game.RandomGameFieldFactoryImpl;

import java.time.LocalTime;
import java.util.List;

public class MainController extends Application {

    private final GameFieldFactory gameFieldFactory = new RandomGameFieldFactoryImpl();

    @FXML
    private GridPane gameFieldGrid;
    private Button[][] buttonsOnGrid;
    @FXML
    private ListView<String> eventLogList;
    @FXML
    private TextField blockHeightLabel;
    @FXML
    private TextField blockWidthLabel;
    @FXML
    private TextField freeCellsCountLabel;
    private GameField gameField;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private void solve() {
        if (gameField == null || gameField.getGameStatus() == GameStatus.FINISHED)
            return;
        new Solver(this::log).solve(gameField);
        redrawGrid();
    }

    private void redrawGrid() {
        gameField.allCells().forEach(cell -> {
            CellPosition position = cell.getPosition();
            String text = cell.getValue() == null ? "" : 1 + cell.getValue() + "";
            buttonsOnGrid[position.getRow()][position.getColumn()].setText(text);
        });
    }

    @FXML
    void startNewGame() {
        int blockHeight = Integer.parseInt(blockHeightLabel.getText());
        int blockWidth = Integer.parseInt(blockWidthLabel.getText());
        int sideLength = blockHeight * blockWidth;
        int freeCellPercent = Integer.parseInt(freeCellsCountLabel.getText());

        FieldSize fieldSize = new FieldSize(sideLength, sideLength);
        FieldSize blockSize = new FieldSize(blockHeight, blockWidth);
        int freeCellCount = (int) Math.round(freeCellPercent / 100.0 * fieldSize.getCellCount());
        int knownCellCount = fieldSize.getCellCount() - freeCellCount;

        gameField = gameFieldFactory.createGameField(fieldSize, blockSize, knownCellCount);

        redrawGridStructure();
        redrawGrid();
        eventLogList.getItems().clear();
    }

    private void redrawGridStructure() {
        FieldSize fieldSize = gameField.getFieldSize();
        FieldSize blockSize = gameField.getBlockSize();
        FieldSize blockCount = gameField.getBlockCount();

        resizeGridConstraints(gameFieldGrid, blockCount);
        buttonsOnGrid = new Button[fieldSize.getRowCount()][fieldSize.getColumnCount()];

        gameFieldGrid.getChildren().clear();
        gameFieldGrid.setGridLinesVisible(true);
        for (int i = 0; i < blockCount.getRowCount(); i++) {
            for (int j = 0; j < blockCount.getColumnCount(); j++) {
                GridPane block = new GridPane();
                block.setGridLinesVisible(true);
                block.setPadding(new Insets(4));
                resizeGridConstraints(block, blockSize);
                for (int x = 0; x < blockSize.getRowCount(); x++) {
                    for (int y = 0; y < blockSize.getColumnCount(); y++) {
                        int row = i * blockSize.getRowCount() + x;
                        int column = j * blockSize.getColumnCount() + y;
                        block.add(buttonsOnGrid[row][column] = createButton(), y, x);
                    }
                }
                gameFieldGrid.add(block, j, i);
            }
        }
    }

    private void resizeGridConstraints(GridPane grid, FieldSize size) {
        resizeRowConstraints(grid.getRowConstraints(), size.getRowCount());
        resizeColumnConstraints(grid.getColumnConstraints(), size.getColumnCount());
    }

    private void resizeRowConstraints(List<RowConstraints> rowConstraints, int size) {
        rowConstraints.clear();
        for (int i = 0; i < size; i++)
            rowConstraints.add(new RowConstraints() {{
                setVgrow(Priority.ALWAYS);
            }});
    }

    private void resizeColumnConstraints(List<ColumnConstraints> columnConstraints, int size) {
        columnConstraints.clear();
        for (int i = 0; i < size; i++)
            columnConstraints.add(new ColumnConstraints() {{
                setHgrow(Priority.ALWAYS);
            }});
    }

    private Button createButton() {
        Button button = new Button();
        button.setPadding(new Insets(1));
        button.setMinSize(1, 1);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setTextAlignment(TextAlignment.CENTER);
        return button;
    }

    private void log(String format, Object... args) {
        String s = LocalTime.now() + ": " + String.format(format, args);
        eventLogList.getItems().add(0, s);
        eventLogList.getFocusModel().focus(1);
    }

    @FXML
    void keyPressed(KeyEvent e) {
        if (e.isControlDown()) solve();
        if (e.isShiftDown()) startNewGame();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainWindow.fxml"));
        primaryStage.setTitle("Судоку");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}