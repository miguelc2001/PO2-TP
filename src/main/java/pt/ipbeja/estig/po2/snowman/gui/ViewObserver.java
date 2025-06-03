package pt.ipbeja.estig.po2.snowman.gui;

import pt.ipbeja.estig.po2.snowman.model.Direction;
import pt.ipbeja.estig.po2.snowman.model.Position;

public interface ViewObserver {
    void gameOver();

    void monsterMoved(Position from, Direction direction);

    void modelUpdated();

}