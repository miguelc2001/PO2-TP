/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;

import pt.ipbeja.estig.po2.snowman.gui.View;
import pt.ipbeja.estig.po2.snowman.gui.ViewObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
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

    /**
     * Creates a new BoardModel from a level file.
     * @param level the resource path of the level file
     * @return a new BoardModel instance
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    public static BoardModel createBoard(String level) {
        List<List<PositionContent>> board = new ArrayList<>();

        // Definir o layout do tabuleiro com base em símbolos
        String[] layout = loadLayoutFromFile(level);

        List<Snowball> snowballs = new ArrayList<>();
        Monster monster = null;

        for (int row = 0; row < layout.length; row++) {
            List<PositionContent> rowContent = new ArrayList<>();
            for (int col = 0; col < layout[row].length(); col++) {
                char tile = layout[row].charAt(col);
                Position pos = new Position(row, col);

                switch (tile) {
                    case '_' -> rowContent.add(PositionContent.SNOW);
                    case '+' -> rowContent.add(PositionContent.BLOCK);
                    case 's' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(pos, SnowballSize.SMALL));
                    }
                    case 'a' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(pos, SnowballSize.AVERAGE));
                    }
                    case 'b' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(pos, SnowballSize.BIG));
                    }
                    case 'X' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(pos, SnowballSize.BIG_AVERAGE));
                    }
                    case 'Y' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(pos, SnowballSize.AVERAGE_SMALL));
                    }
                    case 'Z' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        snowballs.add(new Snowball(pos, SnowballSize.BIG_SMALL));
                    }
                    case 'M' -> {
                        rowContent.add(PositionContent.NO_SNOW);
                        monster = new Monster(pos);
                    }
                    default -> rowContent.add(PositionContent.NO_SNOW);
                }
            }
            board.add(rowContent);
        }

        if (monster == null) {
            monster = new Monster(new Position(board.size() / 2, board.get(0).size() / 2));
        }

        return new BoardModel(board, monster, snowballs);
    }

    /**
     * Loads the layout from a file.
     * @param resourcePath the resource path to the layout file
     * @return an array of strings representing the board layout
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private static String[] loadLayoutFromFile(String resourcePath) {
        List<String> lines = new ArrayList<>();

        try (InputStream is = BoardModel.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            // Skip da linha que contém o nome do nível
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException | NullPointerException e) {
            System.err.println("Erro ao ler o ficheiro de layout: " + resourcePath);
            e.printStackTrace();
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Gets the name of the level from the file.
     * @param level the resource path of the level file
     * @return the name of the level
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    public static String getLevelName(String level) {
            try (InputStream is = BoardModel.class.getResourceAsStream(level);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.readLine();
            } catch (IOException | NullPointerException e) {
                System.err.println("Error reading level name from: " + level);
                e.printStackTrace();
                return null;
            }
        }

    /**
     * Moves the monster in the given direction, handling snowball movement and stacking.
     * @param direction the direction to move the monster
     */
    public void moveMonster(Direction direction) {

        Position monsterPosition = monster.getPosition();
        Position targetPosition = monsterPosition.newPosition(direction);

        // Check if the target position is within bounds and not a BLOCK
        if (outOfBounds(targetPosition) || isBlocked(targetPosition)) {
            return;
        }

        // Save current board
        saveSnapshot();

        Snowball snowball = getSnowball(targetPosition);

        if (snowball != null) {
            Position newPosition = targetPosition.newPosition(direction);

            // Check if the new position for the snowball is not blocked or out of bounds
            if (outOfBounds(newPosition) || isBlocked(newPosition)) {
                return; // Block the move if the snowball can't be moved
            }

            // Unstacks the snowballs if they are stacked
            if (snowballUnstack(direction, snowball, targetPosition)) return;

            // Moves or stacks the snowballs
            if (snowballMoveStack(newPosition, snowball)) return;
        }

        // Moves the monster to the targetPosition
        monster.setPosition(targetPosition);

        // Tell the observer that the monster has moved
        if (viewObserver != null) {
            viewObserver.monsterMoved(monster.getPosition(), direction);
        }
    }


    /**
     * Handles the movement or stacking of a snowball to a new position.
     * If there is already a snowball at the new position, tries to stack them.
     * If the resulting stack forms a complete snowman, updates the board and notifies the observer.
     * If the position is empty, moves the snowball and grows it if on snow.
     *
     * @param newPosition the position to move or stack the snowball to
     * @param snowball the snowball to move or stack
     * @return true if the move was blocked or stacking was not possible, false otherwise
     */
    private boolean snowballMoveStack(Position newPosition, Snowball snowball) {
        Snowball snowball1 = getSnowball(newPosition);
        if (snowball1 != null) {
            if (snowball1.canReceive(snowball)) {

                snowball1.stack(snowball);

                if (snowball1.getSize() == SnowballSize.BIG_AVERAGE_SMALL) {

                    board.get(newPosition.getRow()).set(newPosition.getCol(), PositionContent.SNOWMAN);

                    if (viewObserver != null) {
                        ((View) viewObserver).saveToFile(newPosition);
                        viewObserver.gameOver();
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

    /**
     * Handles unstacking a snowball if it is a composite.
     * @param direction the direction to unstack
     * @param snowball the snowball to unstack
     * @param targetPosition the position of the snowball
     * @return true if unstacking occurred, false otherwise
     */
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

    /**
     * Sets the view observer for this model.
     * @param viewObserver the observer to notify
     */
    public void setViewObserver(ViewObserver viewObserver) {
        this.viewObserver = viewObserver;
    }

    /**
     * Checks if a position is out of the board bounds.
     * @param position the position to check
     * @return true if out of bounds, false otherwise
     */
    public boolean outOfBounds(Position position) {
        return position.getRow() < 0 || position.getRow() >= board.size() || position.getCol() < 0 || position.getCol() >= board.get(0).size();
    }

    /**
     * Checks if a position is blocked by a block tile.
     * @param position the position to check
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked(Position position) {
        PositionContent content = getPositionContent(position);
        return content == PositionContent.BLOCK;
    }

    /**
     * Saves the current board state to the undo stack.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private void saveSnapshot() {
        undoStack.push(new SaveBoard(board, snowballs, monster.getPosition()));
        redoStack.clear(); // limpa o histórico de redo ao fazer nova jogada
    }

    /**
     * Undoes the last move, restoring the previous board state.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    public void undo() {
        if (undoStack.isEmpty()) return;

        redoStack.push(new SaveBoard(board, snowballs, monster.getPosition()));

        SaveBoard snapshot = undoStack.pop();
        restoreSnapshot(snapshot);
    }

    /**
     * Redoes the last undone move, restoring the next board state.
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    public void redo() {
        if (redoStack.isEmpty()) return;

        undoStack.push(new SaveBoard(board, snowballs, monster.getPosition()));

        SaveBoard snapshot = redoStack.pop();
        restoreSnapshot(snapshot);
    }

    /**
     * Restores a saved board state.
     * @param snapshot the saved board state to restore
     * This code was generated or modified with the assistance of an AI tool (GitHub Copilot).
     */
    private void restoreSnapshot(SaveBoard snapshot) {
        this.board = snapshot.getBoard();
        this.snowballs.clear();
        this.snowballs.addAll(snapshot.getSnowballs());
        this.monster.setPosition(snapshot.getMonsterPosition());

        if (viewObserver != null) {
            viewObserver.modelUpdated(); // chama drawBoard() na View
        }
    }

    /**
     * Gets the snowball at a given position.
     * @param position the position to check
     * @return the snowball at the position, or null if none
     */
    public Snowball getSnowball(Position position) {
        for (Snowball snowball : snowballs) {
            if (snowball.getPosition().equals(position)) return snowball;
        }
        return null;
    }

    /**
     * Gets the content of a position on the board.
     * @param position the position to check
     * @return the content at the position
     */
    public PositionContent getPositionContent(Position position) {
        return board.get(position.getRow()).get(position.getCol());
    }

    /**
     * Gets the monster object.
     * @return the monster
     */
    public Monster getMonster() {
        return monster;
    }


    /**
     * Gets the board layout.
     * @return the board as a list of lists
     */
    public List<List<PositionContent>> getBoard() {
        return board;
    }

    /**
     * Gets the list of snowballs on the board.
     * @return the list of snowballs
     */
    public List<Snowball> getSnowballs() {
        return snowballs;
    }
}