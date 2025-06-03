package pt.ipbeja.estig.po2.snowman.model;


public class Monster extends MobileElement {

    public Monster(Position position) {
        super(position);
    }

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