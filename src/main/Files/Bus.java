package main.Files;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class of a bus
 */
public class Bus implements DrawShapes, UpdateTime {
    public double velocity = 2;
    private Coordinate location;
    private Path path;
    @JsonIgnore
    public double distance = 0;
    @JsonIgnore
    private List<Shape> items;
    @JsonIgnore
    public Shape kolecko;

    /***
     * For deserializing Yaml and Jackson
     */
    public Bus(){
        super();
    }

    /***
     * Constructor of bus. Creates a circle representing bus on the map with given velocity, starting location and path.
     * Adds Circle to items - for drawing
     * @param velocity Speed of a bus
     * @param location Starting location
     * @param path Path by which the bus travels
     */
    public Bus(double velocity, Coordinate location, Path path) {
        this.velocity = velocity;
        this.location = location;
        this.path = path;

        items = new ArrayList<>();
        Circle circle = new Circle(location.getX(),location.getY(),4,Color.BLACK);
        items.add(circle);
        this.kolecko = circle;
        path.setBus(this);
    }


    /***
     * Moves bus according to calculated coordinates (Movement)
     * @param coordinate Coordinate to which the bus translates
     */
    @JsonIgnore
    private void moveGui(Coordinate coordinate) {
        for (Shape shape: items){
            try {
                shape.setTranslateX((coordinate.getX()) - location.getX());
                shape.setTranslateY((coordinate.getY()) - location.getY());
            } catch (Exception e){
                System.out.println("Bus failed to move!");
            }
        }
    }


    /***
     * Returns circle for drawing
     * @return Circle - representation of bus
     */
    @JsonIgnore
    @Override
    public List<Shape> gui() {

        return items;
    }

    /***
     * Calculates distance between two points
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of second point
     * @return distance between 2 points
     */
    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2));
    }

    /***
     * Checks on which street the bus is located and changes its speed accordingly
     * @param x x coordinate of bus's location
     * @param y y coordinate of bus's location
     */
    public void isOnStreet(double x, double y){
        for (Street s : Controller.streets){
            if((int)dist(s.start.getX(),s.start.getY(),(int)x,(int)y) + (int)dist((int)x,(int)y, s.end.getX(),s.end.getY()) == (int)dist(s.start.getX(),s.start.getY(),s.end.getX(),s.end.getY())){
                this.velocity = s.velocity;
            }
        }
    }

    /***
     *  Updates movement of vehicle on the map
     * @param time Localtime
     */
    @JsonIgnore
    @Override
    public void update(LocalTime time, Controller controller) {
        try {
            distance += this.velocity;
            if (distance > path.pathlength()) {
                distance = 0;
                Collections.reverse(path.path);
            }

            Coordinate location = path.newCoordinate(distance);

            if (path.infrastructure.get(0).stop.stop_coordinates.getX() == (int) location.getX() && path.infrastructure.get(0).stop.stop_coordinates.getY() == (int) location.getY()) {
                path.itinerary.itinerary_create(time);
                controller.pathChosen(controller.str);
            }

            if(this.velocity != 0) {
                isOnStreet(location.getX(), location.getY());
                moveGui(location);
            }

        } catch(Exception e){
            System.out.println("Bus position failed to update!");
        }
    }

    /***
     * Getter
     * @return velocity of bus
     */
    public double getVelocity() {
        return velocity;
    }

    /***
     * Getter
     * @return location of bus
     */
    public Coordinate getLocation() {
        return location;
    }

    /***
     * Getter
     * @return path of bus
     */
    public Path getPath() {
        return path;
    }

    /***
     * Getter
     * @return distance of bus
     */
    public double getDistance() {
        return distance;
    }

    /***
     * Getter
     * @return items to draw
     */
    public List<Shape> getItems() {
        return items;
    }

    /***
     * Getter
     * @return instance representing bus
     */
    public Shape getKolecko() {
        return kolecko;
    }
}

