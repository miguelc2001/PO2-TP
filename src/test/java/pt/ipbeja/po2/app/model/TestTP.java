package pt.ipbeja.po2.app.model;

import javafx.geometry.Pos;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.model.Direction;
import pt.ipbeja.app.model.Monster;
import pt.ipbeja.app.model.Position;
import pt.ipbeja.app.model.Snowball;
import pt.ipbeja.app.model.SnowballSize;

import static org.junit.jupiter.api.Assertions.*;

public class TestTP {

    @Test
    public void testMonsterToTheLeft() {
        Position position = new Position(3, 3);
        Monster monster = new Monster(position);

        monster.move(Direction.LEFT);

        assertEquals(3, position.getRow());
        assertEquals(2, position.getCol());

    }

    @Test
    public void testCreateAverageSnowball() {
        Position position = new Position(3, 3);
        Snowball snowball = new Snowball(position, SnowballSize.SMALL);

        snowball.grow();

        assertEquals(SnowballSize.AVERAGE, snowball.getSize());
    }

    @Test
    public void testCreateBigSnowball() {
        Position position = new Position(3, 3);
        Snowball snowball = new Snowball(position, SnowballSize.AVERAGE);

        snowball.grow();

        assertEquals(SnowballSize.BIG, snowball.getSize());
    }

    @Test
    public void testMaintainBigSnowball() {
        Position position = new Position(3, 3);
        Snowball snowball = new Snowball(position, SnowballSize.BIG);

        snowball.grow();

        assertEquals(SnowballSize.BIG, snowball.getSize());
    }

    @Test
    public void testAverageBigSnowball() {
        Position position = new Position(3, 3);
        Snowball snowball = new Snowball(position, SnowballSize.AVERAGE);

        Position position2 = new Position(3, 4);
        Snowball snowball2 = new Snowball(position2, SnowballSize.BIG);

        snowball.stack(snowball2);

        assertEquals(SnowballSize.BIG_AVERAGE, snowball.getSize());

    }

    @Test
    public void testCompleteSnowman() {
        Position position = new Position(3, 3);
        Snowball snowball = new Snowball(position, SnowballSize.SMALL);

        Position position2 = new Position(3, 4);
        Snowball snowball2 = new Snowball(position2, SnowballSize.BIG_AVERAGE);

        snowball.stack(snowball2);

        assertEquals(SnowballSize.BIG_AVERAGE_SMALL, snowball.getSize());

    }

}