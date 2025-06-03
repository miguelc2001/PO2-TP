package pt.ipbeja.estig.po2.snowman.model;

public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position newPosition(Direction direction) {
        return switch (direction) {
                    case UP -> new Position(row - 1, col);
                    case DOWN -> new Position(row + 1, col);
                    case LEFT -> new Position(row, col - 1);
                    case RIGHT -> new Position(row, col + 1);
                };
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "(" + (row + 1) + ", " + (char)('A' + col) + ")";
    }

}