/**
 * Autores:
 * Afonso Freitas - 21467
 * Miguel Correia - 21194
 */

package pt.ipbeja.estig.po2.snowman.model;

public abstract class MobileElement {
    protected Position position;

    /**
     * Constructs a MobileElement with the specified position.
     * @param position the initial position of the mobile element
     */
    public MobileElement(Position position) {
        this.position = position;
    }

    /**
     * Gets the current position of the mobile element.
     * @return the position of the mobile element
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Sets the position of the mobile element.
     * @param position the new position to set
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}