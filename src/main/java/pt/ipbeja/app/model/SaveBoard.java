package pt.ipbeja.app.model;

import java.util.ArrayList;
import java.util.List;

public class SaveBoard {

    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs;
    private final Position monsterPosition;

    public SaveBoard(List<List<PositionContent>> board,
                         List<Snowball> snowballs,
                         Position monsterPosition) {
        this.board = deepCopyBoard(board);
        this.snowballs = deepCopySnowballs(snowballs);
        this.monsterPosition = new Position(monsterPosition.getRow(), monsterPosition.getCol());
    }

    private List<List<PositionContent>> deepCopyBoard(List<List<PositionContent>> board) {
        List<List<PositionContent>> copy = new ArrayList<>();
        for (List<PositionContent> row : board) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }

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

    public List<List<PositionContent>> getBoard() {
        return deepCopyBoard(board);
    }

    public List<Snowball> getSnowballs() {
        return deepCopySnowballs(snowballs);
    }

    public Position getMonsterPosition() {
        return new Position(monsterPosition.getRow(), monsterPosition.getCol());
    }

}
