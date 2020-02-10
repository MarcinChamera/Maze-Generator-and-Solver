package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Stack;

public class Maze implements Runnable, Stoppable {
    Cell[][] maze;
    final static int pathCode = 0;
    final static int emptyCode = 1;
    final static int visitedCode = 2;
    GraphicsContext gc;

    Color[] color;
    int rows;        // ilość komórek w pionie (wliczone także komórki pełniące role ścian)
    int columns;     // ilość komórek w poziomie (wliczone także komórki pełniące role ścian)
    int cellSize;    // rozmiar komórki
    private volatile boolean running = false;
    boolean toCreate, toSolve, toStop;
    public final Object monitor;
    int xWall, yWall;
    int startX, startY, endX, endY;

    public Maze(Object monitor, int rows, int columns, int cellSize) {
        this.monitor = monitor;
        this.rows = rows;
        this.columns = columns;
        this.cellSize = cellSize;
        color = new Color[] {
                Color.rgb(163,255,42),
                Color.WHITE,
                Color.GREY
        };
        maze = new Cell[rows][columns];
        toCreate = toSolve = toStop = false;
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void stop() {
        running = false;
    }

    void drawSquare(int row, int column, int colorCode) {
        gc.setFill( color[colorCode] );
        int x = cellSize * column;
        int y = cellSize * row;
        gc.fillRect(x,y, cellSize, cellSize);
    }


    public void run() {
        while(running) {
            if(toCreate) {
                makeMaze();
            } else if(toSolve) {
                for(int i = 0; i < columns; i++) {
                    for(int j  = 0; j < rows; j++) {
                        maze[i][j].visited = false;
                    }
                }
                solveMaze(startX, startY);
            } else if(toStop){
                stop();
            }
        }
    }

    void make(GraphicsContext gc) {
        this.gc = gc;
        toCreate = true;
    }

    void solve(double startRow, double startCol, double endRow, double endCol) {
        this.startX = (int) (startRow / cellSize);
        this.startY = (int) (startCol / cellSize);
        this.endX = (int) (endCol / cellSize);
        this.endY = (int) (endRow / cellSize);
        toSolve = true;
    }

    void removeWall(Cell current, Cell neighbour) {
        int col = current.col - neighbour.col;
        int row = current.row - neighbour.row;
        if(!(col != 0 && row != 0)) {
            xWall = neighbour.col + (col/2);
            yWall = neighbour.row + (row/2);
        }
    }

    void makeMaze() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                maze[i][j] = new Cell(i, j);
                // W tej implementacji rolę ścian pełnią te same obiekty, które pełnią rolę korytarzy
                // w labiryncie. Zatem na starcie należy stworzyć siatkę, składającą się na przemian
                // ze ścian i nie ścian. Domyślnie każda z komórek na starcie jest ścianą,
                // więc należy "co drugą" zmienić na "pustą".
                if(i % 2 == 1 && j % 2 == 1) {
                    drawSquare(i, j, emptyCode);
                    maze[i][j].wall = false;
                }
            }
        }
        // na stos będziemy dodawać komórki, które posiadają nieodwiedzonych jeszcze sąsiadów.
        // Analogicznie, gdy dana komórka nie będzie posiadała nieodwiedzonych sąsiadów, będzie ściągana
        // ze stosu.
        Stack<Cell> stack = new Stack<>();
        // zacznij generowanie labiryntu od lewego górnego rogu
        Cell currentCell = maze[1][1];
        // oznacz startową komórkę jako odwiedzoną
        currentCell.visited = true;
        // dodaj tę komórkę na stos
        stack.push(currentCell);
        // dopóki istnieją nieodwiedzone komórki
        while(!stack.isEmpty()) {
            // pobierz losowego sąsiada
            Cell unvisitedNeighbour = currentCell.getUnvisitedNeighbour(maze, columns, rows);
            // jeśli ten sąsiad nie został jeszcze odwiedzony
            if(unvisitedNeighbour != null) {
                // dodaj tę komórkę na stos
                stack.push(currentCell);
                // usuń ścianę pomiędzy komórką a sąsiadem
                removeWall(currentCell, unvisitedNeighbour);
                drawSquare(xWall, yWall ,emptyCode);
                maze[xWall][yWall].wall = false;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // oznacz sąsiada jako odwiedzonego
                unvisitedNeighbour.visited = true;
                // sąsiad staje się obecnie rozpatrywaną komórką
                currentCell = unvisitedNeighbour;
            // usuń obecnie rozpatrywaną komórkę ze stosu (bo każdy z jej sąsiadów został już odwiedzony)
            } else {
                currentCell = stack.pop();
            }
        }
        toCreate = false;
    }

    boolean solveMaze(int row, int col) {
        // poza granicami labiryntu
        if(row < 1 || row > rows - 1 || col < 1 || col > columns - 1) return false;
        // dodaj komórkę do ścieżki, jeśli:
        // - nie była odwiedzona
        // - nie jest ścianą
        // - nie należy do ścieżki
        if (!maze[row][col].visited && !maze[row][col].wall && !maze[row][col].path) {
            maze[row][col].path = true;
            drawSquare(row,col,pathCode);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // zakończ, jeśli znaleziono punkt końcowy
            if (row == endY && col == endX) {
                toStop = true;
                toSolve = false;
                return true; }
            // pójdź rekurencyjnie w każdym możliwym kierunku
            if ( solveMaze(row-1,col)  || solveMaze(row,col-1)  || solveMaze(row+1,col)  || solveMaze(row,col+1) ) {
                return true;
            }
            // od tego momentu, używając dotychczasowej ścieżki, nie znajdziemy wyjścia - oznaczamy więc daną komórkę
            // jako odwiedzoną.
            maze[row][col].visited = true;
            drawSquare(row,col,visitedCode);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}