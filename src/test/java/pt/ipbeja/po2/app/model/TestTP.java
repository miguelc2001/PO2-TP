package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTP {



    @Test
    public void testMonsterToTheLeft() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test1.txt");
        Position monsterPosition = boardModel.getMonster().getPosition();

        boardModel.moveMonster(Direction.LEFT);

        Position newMonsterPosition = boardModel.getMonster().getPosition();

        assertEquals(monsterPosition.getRow(), newMonsterPosition.getRow());
        assertEquals(monsterPosition.getCol() - 1, newMonsterPosition.getCol());
    }

    @Test
    public void testCreateAverageSnowball() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test2.txt");

        boardModel.moveMonster(Direction.RIGHT);

        Snowball movedSnowball = boardModel.getSnowball(new Position(1, 3));
        assertEquals(SnowballSize.AVERAGE, movedSnowball.getSize());

    }

    @Test
    public void testCreateBigSnowball() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test2.txt");

        boardModel.moveMonster(Direction.RIGHT);
        boardModel.moveMonster(Direction.RIGHT);

        Snowball movedSnowball = boardModel.getSnowball(new Position(1, 4));
        assertEquals(SnowballSize.BIG, movedSnowball.getSize());
    }

    @Test
    public void testMaintainBigSnowball() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test2.txt");

        boardModel.moveMonster(Direction.RIGHT);
        boardModel.moveMonster(Direction.RIGHT);
        boardModel.moveMonster(Direction.RIGHT);

        Snowball movedSnowball = boardModel.getSnowball(new Position(1, 5));
        assertEquals(SnowballSize.BIG, movedSnowball.getSize());
    }

    @Test
    public void testAverageBigSnowball() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test3.txt");

        boardModel.moveMonster(Direction.RIGHT);

        Snowball movedSnowball = boardModel.getSnowball(new Position(1, 3));
        assertEquals(SnowballSize.BIG_AVERAGE, movedSnowball.getSize());

    }

    @Test
    public void testCompleteSnowman() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test4.txt");

        boardModel.moveMonster(Direction.RIGHT);

        Snowball movedSnowball = boardModel.getSnowball(new Position(1, 3));
        assertEquals(SnowballSize.BIG_AVERAGE_SMALL, movedSnowball.getSize());

    }

    @Test
    public void testMoveToBlockedPosition() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test1.txt");
        Position initialMonsterPosition = boardModel.getMonster().getPosition();

        boardModel.moveMonster(Direction.UP);

        Position newMonsterPosition = boardModel.getMonster().getPosition();
        assertEquals(initialMonsterPosition.getRow(), newMonsterPosition.getRow());
        assertEquals(initialMonsterPosition.getCol(), newMonsterPosition.getCol());
    }

    @Test
    public void testUnstackSnowball() {
        BoardModel boardModel = BoardModel.createBoard("/testLevels/test5.txt");

        boardModel.moveMonster(Direction.RIGHT);

        Snowball baseSnowball = boardModel.getSnowball(new Position(1, 2));
        Snowball movedSnowball = boardModel.getSnowball(new Position(1, 3));
        assertEquals(SnowballSize.BIG, baseSnowball.getSize());
        assertEquals(SnowballSize.AVERAGE, movedSnowball.getSize());
    }
}