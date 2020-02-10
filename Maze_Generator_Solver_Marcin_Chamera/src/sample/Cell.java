package sample;

import java.util.ArrayList;
import java.util.Random;

public class Cell {
    boolean visited, wall, path;
    int col, row;

    public Cell(int col, int row) {
        visited = false; // czy komórka została już odwiedzona
        wall = true; // zakładamy na początku przy generowaniu komórek, że każda z nich jest ścianą
        path = false; // czy komórka należy do ścieżki przy znajdywaniu przejścia przez labirynt
        this.col = col;
        this.row = row;
    }

    public Cell getUnvisitedNeighbour(Cell[][] cells, int COLS, int ROWS) {
        ArrayList<Cell> neighbours = new ArrayList<>();
        // sąsiad w odległości "2" a nie "1", w odległości "1" ściana

        // lewy sąsiad
        if(this.col > 1) {
            // jeśli nie odwiedziliśmy lewego sąsiada, dodaj go do listy
            if(!cells[this.col - 2][this.row].visited) neighbours.add(cells[this.col - 2][this.row]);
        }

        // prawy sąsiad
        if(this.col < COLS - 2) {
            // jeśli nie odwiedziliśmy prawego sąsiada, dodaj go do listy
            if(!cells[this.col + 2][this.row].visited) neighbours.add(cells[this.col + 2][this.row]);
        }

        // górny sąsiad
        if(this.row > 1) {
            // jeśli nie odwiedziliśmy górnego sąsiada, dodaj go do listy
            if(!cells[this.col][this.row - 2].visited) neighbours.add(cells[this.col][this.row - 2]);
        }

        // dolny sąsiad
        if(this.row < ROWS - 2) {
            // jeśli nie odwiedziliśmy dolnego sąsiada, dodaj go do listy
            if(!cells[this.col][this.row + 2].visited) neighbours.add(cells[this.col][this.row + 2]);
        }

        // jeśli istnieje nieodwiedzeni sąsiedzi
        if(neighbours.size() > 0) {
            Random random = new Random();
            // wylosuj i zwróc jednego z nieodwiedzonych sąsiadów
            return neighbours.get(random.nextInt(neighbours.size()));
        }
        return null;
    }
}
