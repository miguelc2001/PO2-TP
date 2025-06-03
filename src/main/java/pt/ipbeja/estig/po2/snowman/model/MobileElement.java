package pt.ipbeja.estig.po2.snowman.model;

public abstract class MobileElement {
    protected Position position;

    public MobileElement(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}