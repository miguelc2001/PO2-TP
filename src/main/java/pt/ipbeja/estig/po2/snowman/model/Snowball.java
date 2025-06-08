/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;

public class Snowball extends MobileElement {

    private SnowballSize size;

    /**
     * Constructs a Snowball with a given position and size.
     * @param position the position of the snowball
     * @param size the initial size of the snowball
     */
    public Snowball(Position position, SnowballSize size) {
        super(position);
        this.size = size;
    }

    /**
     * Increases the size of the snowball to the next level, if possible.
     */
    public void grow() {
        switch (size) {
            case SMALL -> size = SnowballSize.AVERAGE;
            case AVERAGE -> size = SnowballSize.BIG;
            case BIG -> {}
        }
    }

    /**
     * Stacks another snowball onto this one, changing its size accordingly.
     * @param other the snowball to stack onto this one
     */
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

//    public boolean isStacked() {
//        return switch (this.size) {
//            case BIG_AVERAGE, BIG_SMALL, AVERAGE_SMALL, BIG_AVERAGE_SMALL -> true;
//            default -> false;
//        };
//    }

    /**
     * Checks if this snowball can receive another snowball on top.
     * @param other the snowball to check if it can be received
     * @return true if this snowball can receive the other, false otherwise
     */
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

    /**
     * Gets the current size of the snowball.
     * @return the size of the snowball
     */
    public SnowballSize getSize() {
        return size;
    }

    /**
     * Sets the size of the snowball.
     * @param newSize the new size to set
     */
    public void setSize(SnowballSize newSize) {
        this.size = newSize;
    }
}