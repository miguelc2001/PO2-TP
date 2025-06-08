/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;

public class Position {
    private int row;
    private int col;

    /**
     * Constructs a Position with the specified row and column.
     * @param row the row index
     * @param col the column index
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns a new Position based on the given direction.
     * @param direction the direction to move
     * @return a new Position after moving in the specified direction
     */
    public Position newPosition(Direction direction) {
        return switch (direction) {
                    case UP -> new Position(row - 1, col);
                    case DOWN -> new Position(row + 1, col);
                    case LEFT -> new Position(row, col - 1);
                    case RIGHT -> new Position(row, col + 1);
                };
    }

    /**
     * Gets the row index of this position.
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of this position.
     * @return the column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the row index of this position.
     * @param row the new row index
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the column index of this position.
     * @param col the new column index
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Checks if this position is equal to another object.
     * @param obj the object to compare
     * @return true if the positions are equal, false otherwise
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    /**
     * Returns the hash code for this position.
     * @return the hash code
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    /**
     * Returns a string representation of this position.
     * @return the position as a string in the format (row, column)
     */
    @Override
    public String toString() {
        return "(" + (row + 1) + ", " + (char)('A' + col) + ")";
    }

}