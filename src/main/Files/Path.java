package main.Files;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class representing path of a bus
 */
public class Path{
    public String label;
    @JsonIgnore
    public List<Coordinate> path;
    @JsonIgnore
    public Itinerary itinerary;
    @JsonIgnore
    public static List<Shape> items = new ArrayList<>();
    @JsonIgnore
    public static List<Shape> highlighted_items = new ArrayList<>();
    @JsonIgnore
    public static List<Path> paths = new ArrayList<>();
    @JsonIgnore
    public List<Bus> busses = new ArrayList<>();

    public List<Street> infrastructure = new ArrayList<>();


    /***
     * For deserializing Yaml and Jackson
     */
    private Path(){}

    /***
     * Constructor for path
     * @param label name of a path (Line)
     * @param infrastructure List of streets to travel through
     */
    public Path(String label, List<Street> infrastructure) {
        this.infrastructure = infrastructure;
        extractPath(false);
        this.label = label;
        paths.add(this);
    }

    /***
     * Extracts coordinates (path to be travaled by bus) from streets and stops. Calculates itinerary for bus
     * @param teleport Check if path is changed or just itinerary is being recalculated (If the same path was set, bus has teleported)
     */
    public void extractPath(boolean teleport){

        List<Coordinate> list = new ArrayList<>();
        List<myStop> stops = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        Coordinate prev = null;
        Coordinate next = null;
        int time = 0;

        for (int i = 0; i <= this.infrastructure.size()-1; i++){

            if(this.infrastructure.get(i).stop != null){
                list.add(this.infrastructure.get(i).stop.getStop_coordinates());
                stops.add(this.infrastructure.get(i).stop);

                if(i != 0){
                    next = this.infrastructure.get(i).stop.getStop_coordinates();
                    time += dist(prev.getX(), prev.getY(),next.getX(),next.getY())/this.infrastructure.get(i).velocity;
                    prev = next;
                    next = null;
                    times.add((int)Math.round(time/60));
                    time = 0;
                } else {
                    prev = this.infrastructure.get(i).stop.getStop_coordinates();
                }
            }

            if (i != this.infrastructure.size()-1) {
                if (i > 0) {
                    if (this.infrastructure.get(i).end.equals(this.infrastructure.get(i - 1).end) || this.infrastructure.get(i).end.equals(this.infrastructure.get(i - 1).start) ) {
                        list.add(this.infrastructure.get(i).start);
                        next = this.infrastructure.get(i).start;
                    } else {
                        list.add(this.infrastructure.get(i).end);
                        next = this.infrastructure.get(i).end;
                    }
                } else {
                    if(this.infrastructure.get(i).end.equals(this.infrastructure.get(i+1).start) ||  this.infrastructure.get(i).end.equals(this.infrastructure.get(i+1).end)) {
                        list.add(this.infrastructure.get(i).end);
                        next = this.infrastructure.get(i).end;
                    } else {
                        list.add(this.infrastructure.get(i).start);
                        next = this.infrastructure.get(i).start;
                    }
                }
                time += dist(prev.getX(), prev.getY(),next.getX(),next.getY())/this.infrastructure.get(i).velocity;
                prev = next;
                next = null;
            }
        }
        addItinerary(new Itinerary(stops, times));
        if (!teleport) {
            this.path = list;
        }
    }

    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2));
    }

    /***
     * Sorts newly added streets with the rest of the path infrastructure based of follows() function.
     * @param streets Newly added streets
     * @return LinkedHashMap with a pair of Boolean type that is true if every street connects and List of sorted streets
     */

    public LinkedHashMap<Boolean, List<Street>> follow_sort(List<Street> streets) {
        Set<Street> new_set = new LinkedHashSet<>();
        Street prev_street = null;
        List<Street> tmp = new ArrayList<>();
        Boolean bool = false;
        int start = 1;
        if (this.infrastructure.get(0).start.equals(streets.get(streets.size()-1).end)) {
            bool = true;
            tmp.add(streets.get(0));
            for (Street added_street : streets){
               if (prev_street != null){
                    if (prev_street.follows(added_street)) {
                        tmp.add(added_street);
                    }
                }
                prev_street = added_street;
            }
            new_set.addAll(tmp);
            start = tmp.size() - 1;
        } else {
            tmp = this.infrastructure;
            new_set.add(this.infrastructure.get(0));
        }
        int size = this.infrastructure.size() + streets.size();
        tmp = new ArrayList<>(tmp.subList(start , tmp.size()));
        for (Street infra_street : this.infrastructure){
            //tmp.remove(infra_street);
            for (Street infra_street2 : tmp){
                if (infra_street.follows(infra_street2)) {
                    if (bool){
                            new_set.add(infra_street);
                    }else {
                            new_set.add(infra_street2);
                    }
                }
            }
            for (Street added_street : streets){
                if (infra_street.follows(added_street)){
                    new_set.add(added_street);
                    prev_street = added_street;
                }else if (prev_street != null){
                    if (prev_street.follows(added_street)) {
                        new_set.add(added_street);
                    }
                    prev_street = added_street;
                }
            }
            for (Street infra_street2 : tmp){
                if (bool){
                    new_set.add(infra_street);
                }else {
                    new_set.add(infra_street2);
                }
            }
        }
        List<Street> new_list = new ArrayList<>(new_set);
        LinkedHashMap<Boolean, List<Street>> properties = new LinkedHashMap<>();
        if (new_list.size() != size){
            int no_stops = 0;
            for (Street street: new_list) {
                if (street.stop != null) {
                    no_stops++;
                }
            }
            if (no_stops >= 2){
                properties.put(false, new_list);
                return properties;
            }
            properties.put(false, null);
            return properties;
        }
        properties.put(true, new_list);
        return  properties;
    }

    /***
     * Adds a bus to follow this path path
     * @param bus
     */
    public void setBus(Bus bus){
        busses.add(bus);
    }

    @JsonIgnore
    Coordinate active = null;
    @JsonIgnore
    Coordinate next = null;

    /***
     * Highlights chosen path (line) and bus
     * @param color Color to be highlighted with
     * @param b List of busses to be highlighted
     * @return List of lines that were highlighted by path
     */
    public List<Shape> highlight(Color color, List<Bus> b){
        items = new ArrayList<>();
        for (int i=0; i < path.size()-1;i++){
            active = path.get(i);
            next = path.get(i+1);
                Line line = new Line(active.getX(),active.getY(), next.getX(),next.getY());
                line.setStroke(color);
                line.setStrokeWidth(1.5);
                highlighted_items.add(line);
                items.add(line);
        }
        for (Bus bus : b){
            bus.kolecko.setFill(color);
        }
        return items;
    }

    /***
     * Adds itinerary to path
     * @param itinerary
     */
    public void addItinerary(Itinerary itinerary){
        this.itinerary = itinerary;
    }

    /***
     * Highlights bus back to black
     * @param b
     */
    public void highlightBack(List<Bus> b) {
        for (Bus bus : b) {
            bus.kolecko.setFill(Color.BLACK);
        }
    }

    /***
     * Calculates how long is the path that is being traveled
     * @return length of a path
     */
    public double pathlength(){
        double result = 0;
        for (int i=0; i< path.size()-1; i++){
            active = path.get(i);
            next = path.get(i+1);
            double coord_dist = Math.sqrt(Math.pow(active.getX()- next.getX(),2) + Math.pow(active.getY() - next.getY(),2));
            result += coord_dist;
        }
        return result;
    }

    /***
     * @param path
     * @return label of a path
     */
    public String getLabel(Path path){
        return path.label;
    }

    /***
     * Calculates new coordinate using vector logic
     * @param distance Distance to be travelled
     * @return New coordinate that represents movement
     */
    public Coordinate newCoordinate(double distance){
        double len = 0;
        double coord_dist = 0;
        double x,y,d;
        for(int i=0; i < path.size()-1; i++){
            active = path.get(i);
            next = path.get(i+1);
            coord_dist = Math.sqrt(Math.pow(active.getX()- next.getX(),2) + Math.pow(active.getY() - next.getY(),2));

            if (len + coord_dist >= distance){
                break;
            }
                len += coord_dist;
        }

        if (active == null || next == null){
            return null;
        }

        double driven = (distance - len) / coord_dist;
        x = active.getX() + (next.getX()- active.getX())* driven;
        y = active.getY() + (next.getY()- active.getY())* driven;
        d = Math.atan2( y, x);
        return new Coordinate( x + (driven*Math.cos(d)), y + (driven*Math.sin(d)));

    }

    /***
     * @return Returns path
     */
    public List<Coordinate> getPath() {
        return path;
    }

    /***
     * @return Returns label
     */
    public String getLabel() {
        return label;
    }

    /***
     * @return Returns itinerary
     */
    public Itinerary getItinerary() {
        return itinerary;
    }

    /***
     * @return Returns infrastructure
     */
    public List<Street> getInfrastructure() {
        return infrastructure;
    }
}
