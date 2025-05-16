package pt.ipbeja.app.model;

public class Snowball extends MobileElement {

    private SnowballSize size;

    public Snowball(Position position, SnowballSize size) {
        super(position);
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
        if (!canReceive(other)) return;

        switch (this.size) {
            case BIG -> {
                if (other.size == SnowballSize.AVERAGE) {
                    size = SnowballSize.BIG_AVERAGE;
                } else if (other.size == SnowballSize.SMALL) {
                    size = SnowballSize.BIG_SMALL;
                }
            }
            case AVERAGE -> {
                if (other.size == SnowballSize.SMALL) {
                    size = SnowballSize.AVERAGE_SMALL;
                }
            }
            case BIG_AVERAGE -> {
                if (other.size == SnowballSize.SMALL) {
                    size = SnowballSize.BIG_AVERAGE_SMALL; // Snowman completo
                }
            }
        }
    }

    public boolean canReceive(Snowball other) {
        switch (this.size) {
            case BIG -> {
                return other.size == SnowballSize.AVERAGE || other.size == SnowballSize.SMALL;
            }
            case AVERAGE, BIG_AVERAGE -> {
                return other.size == SnowballSize.SMALL;
            }
            default -> {
                return false;
            }
        }
    }

    public SnowballSize getSize() {
        return size;
    }

}