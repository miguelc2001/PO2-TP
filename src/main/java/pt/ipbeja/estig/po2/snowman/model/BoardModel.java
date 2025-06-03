package pt.ipbeja.estig.po2.snowman.model;

import pt.ipbeja.estig.po2.snowman.gui.View;
import pt.ipbeja.estig.po2.snowman.gui.ViewObserver;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class BoardModel {
    private List<List<PositionContent>> board;
    private final List<Snowball> snowballs;
    private final Monster monster;
    private ViewObserver viewObserver;

    private final Deque<SaveBoard> undoStack = new ArrayDeque<>();
    private final Deque<SaveBoard> redoStack = new ArrayDeque<>();


    public BoardModel(List<List<PositionContent>> board, Monster monster, List<Snowball> snowballs) {
        this.board = board;
        this.monster = monster;
        this.snowballs = snowballs;
    }

    public void moveMonster(Direction direction) {

        // Save current board
        saveSnapshot();

        // Get the current position of the monster
        Position monsterPosition = monster.getPosition();
        Position targetPosition = monsterPosition.newPosition(direction);

        // Check if the target position is within bounds and not a BLOCK
        if (outOfBounds(targetPosition) || isBlocked(targetPosition)) {
            return;
        }

        Snowball snowball = getSnowball(targetPosition);

        if (snowball != null) {
            Position newPosition = targetPosition.newPosition(direction);

            // Unstacks the snowballs if they are stacked
            if (snowballUnstack(direction, snowball, targetPosition)) return;

            // Moves or stacks the snowballs
            if (snowballMoveStack(newPosition, snowball)) return;

            // Checks if the new position for the snowball is not blocked or out of bounds
            if (outOfBounds(newPosition) || isBlocked(newPosition)) {
                return;
            }
        }

        // Tell the observer that the monster has moved
        if (viewObserver != null) {
            viewObserver.monsterMoved(monster.getPosition(), direction);
        }

        // Moves the monster to the targetPosition
        monster.setPosition(targetPosition);
    }

    private boolean snowballMoveStack(Position newPosition, Snowball snowball) {
        Snowball snowball1 = getSnowball(newPosition);
        if (snowball1 != null) {
            if (snowball1.canReceive(snowball)) {

                snowball1.stack(snowball);

                if (snowball1.getSize() == SnowballSize.BIG_AVERAGE_SMALL) {

                    board.get(newPosition.getRow()).set(newPosition.getCol(), PositionContent.SNOWMAN);

                    if (snowball1.getSize() == SnowballSize.BIG_AVERAGE_SMALL) {

                        board.get(newPosition.getRow()).set(newPosition.getCol(), PositionContent.SNOWMAN);

                        if (viewObserver != null) {
                            ((View) viewObserver).saveToFile(newPosition);
                            viewObserver.gameOver();
                        }
                    }
                }

                snowballs.remove(snowball);

            }

            else return true;

        } else {

            snowball.setPosition(newPosition);

            if (getPositionContent(newPosition) == PositionContent.SNOW) {
                snowball.grow();
                board.get(newPosition.getRow()).set(newPosition.getCol(), PositionContent.NO_SNOW);
            }
        }

        return false;
    }

    private boolean snowballUnstack(Direction direction, Snowball snowball, Position targetPosition) {
        SnowballSize size = snowball.getSize();

        if (size == SnowballSize.BIG_SMALL || size == SnowballSize.AVERAGE_SMALL || size == SnowballSize.BIG_AVERAGE) {
            Position newPos = targetPosition.newPosition(direction);
            if (outOfBounds(newPos) || isBlocked(newPos) || getSnowball(newPos) != null) return true;

            SnowballSize top = switch (size) {
                case BIG_SMALL, AVERAGE_SMALL -> SnowballSize.SMALL;
                case BIG_AVERAGE -> SnowballSize.AVERAGE;
                default -> throw new IllegalStateException("Unexpected size");
            };

            SnowballSize base = switch (size) {
                case BIG_SMALL, BIG_AVERAGE -> SnowballSize.BIG;
                case AVERAGE_SMALL -> SnowballSize.AVERAGE;
                default -> throw new IllegalStateException("Unexpected size");
            };

            Snowball topBall = new Snowball(newPos, top);
            if (getPositionContent(newPos) == PositionContent.SNOW) {
                topBall.grow();
                board.get(newPos.getRow()).set(newPos.getCol(), PositionContent.NO_SNOW);
            }

            snowball.setSize(base);
            snowballs.add(topBall);

            return true;
        }
        return false;
    }

    public void setViewObserver(ViewObserver viewObserver) {
        this.viewObserver = viewObserver;
    }

    public boolean outOfBounds(Position position) {
        return position.getRow() < 0 || position.getRow() >= board.size() || position.getCol() < 0 || position.getCol() >= board.get(0).size();
    }

    public boolean isBlocked(Position position) {
        PositionContent content = getPositionContent(position);
        return content == PositionContent.BLOCK;
    }

    private void saveSnapshot() {
        undoStack.push(new SaveBoard(board, snowballs, monster.getPosition()));
        redoStack.clear(); // limpa o histórico de redo ao fazer nova jogada
    }

    public void undo() {
        if (undoStack.isEmpty()) return;

        redoStack.push(new SaveBoard(board, snowballs, monster.getPosition()));

        SaveBoard snapshot = undoStack.pop();
        restoreSnapshot(snapshot);
    }

    public void redo() {
        if (redoStack.isEmpty()) return;

        undoStack.push(new SaveBoard(board, snowballs, monster.getPosition()));

        SaveBoard snapshot = redoStack.pop();
        restoreSnapshot(snapshot);
    }

    private void restoreSnapshot(SaveBoard snapshot) {
        this.board = snapshot.getBoard();
        this.snowballs.clear();
        this.snowballs.addAll(snapshot.getSnowballs());
        this.monster.setPosition(snapshot.getMonsterPosition());

        if (viewObserver != null) {
            viewObserver.modelUpdated(); // chama drawBoard() na View
        }
    }

    public Snowball getSnowball(Position position) {
        for (Snowball snowball : snowballs) {
            if (snowball.getPosition().equals(position)) return snowball;
        }
        return null;
    }

    public PositionContent getPositionContent(Position position) {
        return board.get(position.getRow()).get(position.getCol());
    }

    public Monster getMonster() {
        return monster;
    }

    public List<List<PositionContent>> getBoard() {
        return board;
    }



}