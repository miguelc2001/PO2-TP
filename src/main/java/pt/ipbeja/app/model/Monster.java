package pt.ipbeja.app.model;

public class Monster {
    private final Position position;

    public Monster(Position position) {
        this.position = position;
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