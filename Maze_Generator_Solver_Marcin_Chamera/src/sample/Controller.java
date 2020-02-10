package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Controller {
    public Canvas canvas;
    GraphicsContext gc;
    Maze maze;
    final Object monitor = new Object();
    double startX, startY, endX, endY;
    boolean settingStart, settingEnd;
    int cellSize, rows, columns;

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        rows = columns = 31; // ilość komórek w pionie i poziomie (wliczone także komórki pełniące role ścian)
        cellSize = 20; // rozmiar komórki
        startX = startY = cellSize + 1; // miejsce startowe, z którego zaczynamy przechodzenie labiryntu, jeśli nie wybierzemy go sami
        endX = (columns - 2) * cellSize; // współrzędna X poszukiwanego miejsca
        endY = (rows - 2) * cellSize; // współrzędna Y poszukiwanego miejsca
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER); // nałożenie warstwy "czyszczącej" na obecną
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void makeMaze() {
        this.clear(gc);
        maze = new Maze(monitor, rows, columns, cellSize);
        maze.start();
        maze.make(gc); // generuj labirynt
    }

    public void solveMaze() {
        maze.solve(startY, startX, endY, endX);
    }

    // wybierz samodzielnie miejsce startowe dla przeszukiwania labiryntu
    public void setStart() { settingStart = true;}

    // wybierz samodzielnie miejsce końcowe dla przeszukiwania labiryntu
    public void setEnd() { settingEnd = true;}

    public void mousePressed(MouseEvent mouseEvent) {
        if(settingStart) {
            this.startX = mouseEvent.getX();
            this.startY = mouseEvent.getY();
            gc.setFill(Color.BLUE);
            this.drawRect(startX, startY, cellSize);
            settingStart = false;
        } else if(settingEnd) {
            this.endX = mouseEvent.getX();
            this.endY = mouseEvent.getY();
            gc.setFill(Color.RED);
            this.drawRect(endX, endY, cellSize);
            settingEnd = false;
        }
    }

    public void drawRect(double x, double y, double blockSize) {
        gc.fillRect((int)(x/ blockSize) * blockSize,(int)(y/ blockSize) * blockSize, blockSize,blockSize);
    }
}
