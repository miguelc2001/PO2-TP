/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;

public class Score implements Comparable<Score> {
    private final String playerName;
    private final String levelName;
    private final int moves;

    /**
     * Constructs a Score object with player name, level name, and number of moves.
     * @param playerName the name of the player
     * @param levelName the name of the level
     * @param moves the number of moves taken
     */
    public Score(String playerName, String levelName, int moves) {
        this.playerName = playerName;
        this.levelName = levelName;
        this.moves = moves;
    }

    /**
     * Gets the name of the player.
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the name of the level.
     * @return the level's name
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * Gets the number of moves taken.
     * @return the number of moves
     */
    public int getMoves() {
        return moves;
    }

    /**
     * Compares this score to another score based on the number of moves.
     * @param other the other Score to compare to
     * @return a negative integer, zero, or a positive integer as this score is less than, equal to, or greater than the specified score
     */
    @Override
    public int compareTo(Score other) {
        return this.moves - other.moves;
    }

    /**
     * Returns a string representation of the score.
     * @return a string in the format "playerName - levelName - moves"
     */
    @Override
    public String toString() {
        return String.format("%s - %s - %d", playerName, levelName, moves);
    }
}