package main.Files;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class representing stop
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class  myStop implements DrawShapes {

    public String stop_id;
    public Coordinate stop_coordinates;
    public Street street;

    /***
     * For deserializing Yaml and Jackson
     */
    private myStop(){}

    /***
     * Constructor for stop
     * @param id Name of the stop
     * @param c1 Coordinate of the stop
     */
    public myStop(String id, Coordinate c1, Street s) {
        this.stop_id = id;
        this.stop_coordinates = c1;
        s.addStop(this);
    }

    /***
     *  Draws a circle representing the stop
     * @return Returns circle representing the stop
     */
    @JsonIgnore
    @Override
    public List<Shape> gui() {
        Text text = new Text(stop_coordinates.getX(), stop_coordinates.getY()-5, stop_id);
        text.setStyle("-fx-font: 6.5 arial;");
        return Arrays.asList(
                text,
                new Circle(stop_coordinates.getX(), stop_coordinates.getY(), 3, Color.ORANGE));

    }

    /***
     * @return Name of the stop
     */
    public String getStop_id() {
        return stop_id;
    }

    /***
     *
     * @return Coordinates of the stop
     */
    public Coordinate getStop_coordinates() {
        return stop_coordinates;
    }

    /***
     *  Sets a street for the stop
     * @param s Street that a stop will be placed in
     */
    public void setStreet(Street s) {
        this.street = s;
    }

    public Street getStreet() {
        return street;
    }
}