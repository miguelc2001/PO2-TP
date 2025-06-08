/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.gui;

import pt.ipbeja.estig.po2.snowman.model.Direction;
import pt.ipbeja.estig.po2.snowman.model.Position;

public interface ViewObserver {

    /**
     * Notifies that the game is over.
     */
    void gameOver();

    /**
     * Notifies that the monster has moved from a position in a given direction.
     * @param from the original position of the monster
     * @param direction the direction in which the monster moved
     */
    void monsterMoved(Position from, Direction direction);

    /**
     * Notifies that the model has been updated and the view should refresh.
     */
    void modelUpdated();
}