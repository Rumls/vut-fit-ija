package main.Files;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class representing street
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Street implements DrawShapes{
    private String name;
    public Coordinate start;
    public  Coordinate end;
    @JsonIgnore
    public myStop stop;
    @JsonIgnore
    public double velocity = 0.25;
    @JsonIgnore
    public Line line;
    @JsonIgnore
    public boolean blocked = false;


    /***
     * For deserializing Yaml and Jackson
     */
    private Street(){}

    /***
     *  Constructor for the street
     * @param name Name of the street
     * @param start Starting coordinate of the street
     * @param end Ending coordinate of the street
     */
    public Street(String name, Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
        this.name = name;
        this.stop = null;
        this.velocity = velocity;
        this.blocked = blocked;
    }

    /***
     * Adding stop to the street
     * @param stop Stop to be added
     */
    public void addStop(myStop stop){
        this.stop = stop;
        System.out.println("Setting " + stop.stop_id + " to street " + this.name);
        stop.setStreet(this);
    }

    /***
     * Getter
     * @return Start of the street
     */
    public Coordinate getStart() {
        return start;
    }

    /***
     * Getter
     * @return End of the street
     */
    public Coordinate getEnd() {
        return end;
    }

    /***
     * Getter
     * @return Name of the street
     */
    public String getName() {
        return name;
    }

    /***
     * Compares ends of streets if they follow each other
     * @param s2 Street to compare
     * @return Boolean if one of coordinates equal
     */
    public boolean follows(Street s2) {
        return this.end.equals(s2.end) || this.start.equals(s2.start) || this.end.equals(s2.start) || this.start.equals(s2.end);
    }

    /***
     * Draws a line representing street
     * @return A line representing street
     */
    @Override
    public List<Shape> gui() {
        line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if(!event.isControlDown()) {
                Controller.selection.clear();
            }

            if(event.isControlDown()) {
                if( Controller.selection.contains(this)) {
                    Controller.selection.remove(this);
                } else {
                    Controller.selection.add(this);
                }
                System.out.println("Items in model: " + this.name);
            }
        });
        return Arrays.asList(line);
    }
}
