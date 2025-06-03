package pt.ipbeja.estig.po2.snowman.model;

public class Score implements Comparable<Score> {
    private final String playerName;
    private final String levelName;
    private final int moves;

    public Score(String playerName, String levelName, int moves) {
        this.playerName = playerName;
        this.levelName = levelName;
        this.moves = moves;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getMoves() {
        return moves;
    }

    @Override
    public int compareTo(Score other) {
        return this.moves - other.moves;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %d", playerName, levelName, moves);
    }
}