package main.Files;

import java.util.List;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class for handling YAML data extraction and deserializing
 */
public class Data {
    private List<Street> streets;
    private List<myStop> stops;
    private List<Bus> bus;

    public Data(){
        //super();
    }

    public Data(List<Street> streets, List<myStop> stops, List<Bus> busses) {
        this.streets = streets;
        this.stops = stops;
        this.bus = busses;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<myStop> getStops() {
        return stops;
    }

    public List<Bus> getBus() {
        return bus;
    }
}
