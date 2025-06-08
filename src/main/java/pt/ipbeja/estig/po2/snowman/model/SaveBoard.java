/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;

import java.util.ArrayList;
import java.util.List;

public class SaveBoard {

    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs;
    private final Position monsterPosition;

    /**
     * Constructs a SaveBoard object with the given board, snowballs, and monster position.
     * @param board the board layout to save
     * @param snowballs the list of snowballs to save
     * @param monsterPosition the position of the monster to save
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    public SaveBoard(List<List<PositionContent>> board,
                         List<Snowball> snowballs,
                         Position monsterPosition) {
        this.board = deepCopyBoard(board);
        this.snowballs = deepCopySnowballs(snowballs);
        this.monsterPosition = new Position(monsterPosition.getRow(), monsterPosition.getCol());
    }

    /**
     * Creates a deep copy of the board layout.
     * @param board the board layout to copy
     * @return a deep copy of the board layout
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private List<List<PositionContent>> deepCopyBoard(List<List<PositionContent>> board) {
        List<List<PositionContent>> copy = new ArrayList<>();
        for (List<PositionContent> row : board) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }

    /**
     * Creates a deep copy of the list of snowballs.
     * @param snowballs the list of snowballs to copy
     * @return a deep copy of the snowballs list
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private List<Snowball> deepCopySnowballs(List<Snowball> snowballs) {
        List<Snowball> copy = new ArrayList<>();
        for (Snowball s : snowballs) {
            copy.add(new Snowball(
                    new Position(s.getPosition().getRow(), s.getPosition().getCol()),
                    s.getSize()
            ));
        }
        return copy;
    }

    /**
     * Gets a deep copy of the board layout.
     * @return a deep copy of the board layout
     */
    public List<List<PositionContent>> getBoard() {
        return deepCopyBoard(board);
    }

    /**
     * Gets a deep copy of the list of snowballs.
     * @return a deep copy of the snowballs list
     */
    public List<Snowball> getSnowballs() {
        return deepCopySnowballs(snowballs);
    }

    /**
     * Gets a copy of the monster's position.
     * @return the position of the monster
     */
    public Position getMonsterPosition() {
        return new Position(monsterPosition.getRow(), monsterPosition.getCol());
    }
}
