package pt.ipbeja.app.model;

public class Snowball {

    private SnowballSize size;
    private final Position position;

    public Snowball(Position position, SnowballSize size) {
        this.position = position;
        this.size = size;
    }

    public void grow() {
        switch (size) {
            case SMALL -> size = SnowballSize.AVERAGE;
            case AVERAGE -> size = SnowballSize.BIG;
            case BIG -> {}
        }
    }

    public void stack(Snowball other) {
        if (this.size == SnowballSize.AVERAGE && other.size == SnowballSize.BIG) {
            this.size = SnowballSize.BIG_AVERAGE;
        } else if (this.size == SnowballSize.SMALL && other.size == SnowballSize.BIG) {
            this.size = SnowballSize.BIG_SMALL;
        } else if (this.size == SnowballSize.SMALL && other.size == SnowballSize.AVERAGE) {
            this.size = SnowballSize.AVERAGE_SMALL;
        } else if (this.size == SnowballSize.SMALL && other.size == SnowballSize.BIG_AVERAGE) {
            this.size = SnowballSize.BIG_AVERAGE_SMALL; // Snowman completo
        }
    }

    public Position getPosition() {
        return position;
    }

    public SnowballSize getSize() {
        return size;
    }


}