/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;


public class Monster extends MobileElement {

    /**
     * Constructs a Monster at the given position.
     * @param position the initial position of the monster
     */
    public Monster(Position position) {
        super(position);
    }

    /**
     * Moves the monster in the specified direction by updating its position.
     * @param direction the direction in which the monster should move
     */
    public void move(Direction direction) {
        int row = position.getRow();
        int col = position.getCol();
        switch (direction) {
            case UP -> this.position.setRow(row - 1);
            case DOWN -> this.position.setRow(row + 1);
            case LEFT -> this.position.setCol(col - 1);
            case RIGHT -> this.position.setCol(col + 1);
        }
    }
}