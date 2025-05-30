package pt.ipbeja.app.ui;

import pt.ipbeja.app.model.Direction;
import pt.ipbeja.app.model.Position;

public interface ViewObserver {
    void gameOver();

    void monsterMoved(Position from, Direction direction);

    void modelUpdated();

}