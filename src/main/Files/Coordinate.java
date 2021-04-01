package main.Files;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class for Coordinate
 */
public class Coordinate extends java.lang.Object {

    private double x;
    private double y;

    /***
     * For deserializing Yaml and Jackson
     */
    private Coordinate(){}

    /***
     * Constructor for Coordinates
     * @param x Represents x coordinate on the map
     * @param y Represents y coordinate on the map
     */
    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    /***
     * Compares two coordinates
     * @param o Coordinate object
     * @return True or false
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Coordinate)) {
            return false;
        }

        Coordinate m = (Coordinate) o;
        return (getX() == m.getX() &&
                getY() == m.getY());

    }

    /***
     * @return x coordinate
     */
    public double getX() {
        return this.x;
}

    /***
     * @return y coordinate
     */
    public double getY() {
        return this.y;
    }

    /***
     * Setter
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /***
     * Setter
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }
}
