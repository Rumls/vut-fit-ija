package main.Files;

import java.time.LocalTime;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class representing itinerary
 */
public class Itinerary {

    private List<myStop> stops;
    public List<Integer> travelTimes;
    @JsonIgnore
    private ArrayList<ArrayList<String>> stopitinerar;
    @JsonIgnore
    public LinkedHashMap<String, ArrayList<String>> dict = new LinkedHashMap<>();

    /***
     * For deserializing Yaml and Jackson
     */
    private Itinerary(){}

    /***
     *  Constructor for itinerary
     * @param stops list of stops in itinerary
     * @param travelTime travel time between those stops
     */
    public Itinerary(List<myStop> stops, List<Integer> travelTime) {
        this.stops = stops;
        this.travelTimes = travelTime;
        stopitinerar = new ArrayList<>();
        for (int i=0; i < stops.size(); i++){
            stopitinerar.add(new ArrayList<>());
        }
        itinerary_create(LocalTime.now());
    }

    /***
     * Calculates itinerary and updates times into an arraylist of arraylists of strings with attributable index for a stop
     * Time calculates according to travel times between stops that are stored in Integer list
     * Afterwards values are stored in a hashtable - Stop name (string) and its corresponding times (Array of strings)
     * @param t Local time
     */
    public void itinerary_create(LocalTime t){

        List<Integer> copytravelTimes = new ArrayList<>(travelTimes);

        int stopCoef = 1;
        int stopID = 0;
        int travelTimeIndex = 0;
        stopitinerar = new ArrayList<>();

        for (int i=0; i < stops.size(); i++){
            stopitinerar.add(new ArrayList<>());
        }

        for (int i = 0; i < 66;i++) {

            if (travelTimeIndex == travelTimes.size()) {
                travelTimeIndex = 0;
                Collections.reverse(travelTimes);
            }

            if (stopID == stops.size() - 1) {
                stopCoef = -1;
            } else if (stopID == 0) {
                stopCoef = 1;
            }

            stopitinerar.get(stopID).add(t.toString().substring(0, 5));
            t = t.plusMinutes((long)travelTimes.get(travelTimeIndex));
            stopID += stopCoef;
            travelTimeIndex += 1;
        }

        dict = new LinkedHashMap<>();
        for (int i = 0; i < stopitinerar.size(); i++){
            dict.put(stops.get(i).stop_id,stopitinerar.get(i));
        }
        setDict(dict);

        travelTimes = new ArrayList<>(copytravelTimes);

    }



    /***
     * Getter
     * @return List of stops in itinerary
     */
    public List<myStop> getStops() {
        return stops;
    }

    /***
     * Getter
     * @return List of travel times between stops
     */
    public List<Integer> getTravelTimes() {
        return travelTimes;
    }

    /***
     * Getter
     * @return Hashtable of stop names with their corresponding times
     */
    public LinkedHashMap<String, ArrayList<String>> getDict() {
        return dict;
    }

    public void setDict(LinkedHashMap<String, ArrayList<String>> dict) {
        this.dict = dict;
    }
}
