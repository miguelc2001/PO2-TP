package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.Direction;
import pt.ipbeja.estig.po2.snowman.model.Monster;
import pt.ipbeja.estig.po2.snowman.model.Position;
import pt.ipbeja.estig.po2.snowman.model.Snowball;
import pt.ipbeja.estig.po2.snowman.model.SnowballSize;

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
        Snowball snowball = new Snowball(position, SnowballSize.BIG);

        Position position2 = new Position(3, 4);
        Snowball snowball2 = new Snowball(position2, SnowballSize.AVERAGE);

        snowball.stack(snowball2);

        assertEquals(SnowballSize.BIG_AVERAGE, snowball.getSize());

    }

    @Test
    public void testCompleteSnowman() {
        Position position = new Position(3, 3);
        Snowball snowball = new Snowball(position, SnowballSize.BIG_AVERAGE);

        Position position2 = new Position(3, 4);
        Snowball snowball2 = new Snowball(position2, SnowballSize.SMALL);

        snowball.stack(snowball2);

        assertEquals(SnowballSize.BIG_AVERAGE_SMALL, snowball.getSize());

    }

}