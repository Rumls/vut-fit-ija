package main.Files;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.time.LocalTime;
import java.util.*;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Controller Class handling all FXML elements
 */
public class Controller {

    @FXML
    private Pane pane1;
    private int speed = 250;
    private List<UpdateTime> updates = new ArrayList<>();
    private Timer timer = new Timer();
    private Timer new_timer = new Timer();
    public String str;
    public static List<Street> streets = new ArrayList<>();
    public static Selection selection = new Selection();
    public static List<Bus> allbusses = new ArrayList<>();
    public Path path;
    private static List<Bus> busses = new ArrayList<>();
    @FXML
    private Button edit;
    private boolean pause = false;
    @FXML
    private TextField time_text;

    @FXML
    public Button blockB;

    @FXML
    private Slider slider;
    private LocalTime time = LocalTime.now();

    @FXML
    private Label timeLabel;

    @FXML
    private Label itLabel;

    @FXML
    private ChoiceBox<String> choicebox;

    /***
     * Implements zoom
     * @param event Mousewheel movement event
     */
    @FXML
    private void zoom(ScrollEvent event) {

        event.consume();
        double scroll = event.getDeltaY() > 0 ? 1.1 : 0.9;
        pane1.setScaleX(scroll * pane1.getScaleX());
        pane1.setScaleY(scroll * pane1.getScaleY());
    }

    /***
     * Highlights chosen path
     * @param string Name of a path (line) to be highlighted
     */
    @FXML
    public void pathChosen(String string){

        if (!selection.select.isEmpty() && string != "no link chosen"){
            edit.setDisable(false);
        } else {
            edit.setDisable(true);
        }

        str = string;
        path = null;
        for (int i = 0; i < Path.highlighted_items.size(); i++){
            pane1.getChildren().remove(Path.highlighted_items.get(i));
            itLabel.setText("");
        }
        if (!busses.isEmpty()) {
            for (Bus bus : busses) {
                bus.kolecko.setFill(Color.BLACK);
            }
        }

        for (Path p : Path.paths){
            if (str.equals(p.label)){
                pane1.getChildren().addAll(p.highlight(Color.ORANGE, p.busses));
                busses = p.busses;
                path = p;
                schedule(p);
                break;
            }
        }

    }

    /***
     * Prints itinerary to a label
     * @param p Path from which itinerary is going to be printed
     */
    void schedule(Path p){
        if (p.itinerary == null){
            return;
        }
        Set<String> keys = p.itinerary.dict.keySet();
        StringBuilder format = new StringBuilder();

        for(String key : keys) {
            format.append(String.format("%-15s", key));

            for (String str : p.itinerary.dict.get(key)) {
                format.append(String.format("%-15s", str));
            }

            format.append("\n");
        }

        itLabel.setText(format.toString());
    }

    /***
     * Changes speed of simulation
     * @param event Implements drag even of the slider
     */
    @FXML
    void dragged(MouseEvent event){
        Controller controller = this;
                speed = (int)slider.getValue();
                TimerMethod(controller);
    }

    /***
     * After click on a button, selected streets are blocked and removec from paths of busses that contained them
     */

    @FXML
    void blockButton(){
        for (Street street : selection.select){
            street.blocked = true;
            street.line.setStroke(Color.RED);
            for(Bus b: allbusses){
                if (b.getPath().infrastructure.contains(street)){
                    b.velocity = 0;
                    int index = b.getPath().infrastructure.indexOf(street);
                    /** If first street has been blocked, remove all next streets that do not contain a stop. **/
                    if (index == 0){
                        int i = 1;
                        while (b.getPath().infrastructure.get(i+1).stop == null){
                            i++;
                        }

                        List<Street> newStreetList = new ArrayList<>(b.getPath().infrastructure.subList(i, b.getPath().infrastructure.size()));
                        b.getPath().infrastructure = newStreetList;
                    }

                    /** If last street has been blocked, remove all previous streets that do not contain a stop. **/
                    else if (index == b.getPath().infrastructure.size()-1){
                        int i = b.getPath().infrastructure.size()-1;
                        while (b.getPath().infrastructure.get(i-1).stop == null){
                            i--;
                        }

                        List<Street> newStreetList = new ArrayList<>(b.getPath().infrastructure.subList(0, i));
                        b.getPath().infrastructure = newStreetList;

                    }
                    /** Street is in between the line **/
                    else {
                        List<Street> newStreetList = new ArrayList<>(b.getPath().infrastructure.subList(0, index));
                        b.getPath().infrastructure = newStreetList;

                        int i = b.getPath().infrastructure.size()-1;
                        while (b.getPath().infrastructure.get(i).stop == null){
                            i--;
                        }
                        if(i != 0) {
                            newStreetList = new ArrayList<>(b.getPath().infrastructure.subList(0, i+1));
                            b.getPath().infrastructure = newStreetList;
                        }

                    }
                    


                    int stops = (int) b.getPath().infrastructure.stream().filter(street_l -> street_l.stop != null).count();
                    /* Bus has only one stop, therefore has no path to travel on */
                    if(stops < 2){
                        b.getPath().path.clear();
                        b.getPath().path.add(b.getPath().infrastructure.get(0).stop.stop_coordinates);
                        b.getPath().itinerary = null;
                    } else {
                        b.getPath().extractPath(false);
                        b.velocity = 0.25;
                    }
                    b.distance = 0;
                }
            }
        }
        selection.clear();
    }

    /***
     * Slows bus on certain street to maximum
     */

    @FXML
    void redButtonPressed(){
        changeStreetVelocity(0.05);
    }

    /***
     * Slows bus on certain street slightly
     */

    @FXML
    void orangeButtonPressed(){
        changeStreetVelocity(0.15);
    }

    /***
     * Restores bus's velocity on street to normal
     */
    @FXML
    void greenButtonPressed(){
        changeStreetVelocity(0.25);
    }

    /***
     * Pauses the simulation
     */
    @FXML
    void pause(){
        if(!pause){
            pause = true;
        } else{
            pause = false;
        }
        if (pause){
            new_timer = timer;
            timer.cancel();
            slider.setDisable(true);
        } else{
            timer = new_timer;
            TimerMethod(this);
            slider.setDisable(false);
        }
    }

    /***
     * Skips simulation to desired time
     */
    @FXML
    void skip(){
        String pattern_minutes = "([0-1]?[0-9]|2[0-3]):[0-5][0-9]";
        String pattern_seconds = "([0-1]?\\d|2[0-3])(?::([0-5]?\\d))?(?::([0-5]?\\d))?";
        String str = time_text.getText();
        if(str.matches(pattern_minutes) || str.matches(pattern_seconds)) {
            String time_str = "";
            String time_str2 = "";
            while (!(time_str.equals(str) || time_str2.equals(str))) {
                time = time.plusSeconds(1);
                time_str = time.toString().substring(0, 5);
                time_str2 = time.toString().substring(0, 8);
                Platform.runLater(() -> timeLabel.setText(time.toString().substring(0, 8)));
                for (UpdateTime update : updates) {
                    update.update(time, this);
                }
            }
        }
    }

    /***
     * Checks new path, that was created and includes it to the path
     */
    @FXML
    void newPath(){
        List<Street> tmp;
        for (Street street : selection.select){
            street.blocked = false;
        }
        tmp = path.follow_sort(selection.select).get(true);
        if (tmp != null) {
            path.infrastructure = tmp;
            path.extractPath(false);
            path.busses.get(0).velocity = 0.25;
            selection.clear();
        } else {
            System.out.println("Selected path must connect");
        }
    }

    /***
     * Changes streets velocity to desired speed
     * @param velocity desired speed chosen by button
     */
    public void changeStreetVelocity(double velocity){
        for (Street street :selection.select){
            street.velocity = velocity;
        }

        for (Bus b : allbusses){
            b.getPath().extractPath(true);
        }

        selection.clear();
    }

    /***
     * Draws items on the map and adds bus circles to the update list in order to be available in the timer later
     * @param items List of shapes to be drawn on the map
     */
    public void setItems(List<DrawShapes> items) {

        for (DrawShapes drawShapes : items){
            pane1.getChildren().addAll(drawShapes.gui());

            if (drawShapes instanceof UpdateTime) {
                updates.add((UpdateTime) drawShapes);
            }
        }

        selection.blockButton = blockB;
        selection.addToPathButton = edit;
        selection.choiceBox = choicebox;
    }

    /***
     * Listener for changing the choicebox element for highlighting chosen path and it's outputting itinerary
     */
    public void fillChoiceBox(){
        choicebox.getSelectionModel().selectedItemProperty().addListener( (v,oldVal,newVal) -> pathChosen(newVal));
        choicebox.getItems().add("no link chosen");
        choicebox.setValue("no link chosen");
        for (Path p : Path.paths){
            choicebox.getItems().add(p.label);
        }
    }

    /***
     * Deselects all chosen streets if control key is not being held down while choosing streets
     * @param event
     */
    @FXML
    public void deselectStreet(MouseEvent event){
        if (!event.isControlDown()) {
            Controller.selection.clear();
        }
    }

    /***
     * Timer method that implements time and movement of busses
     */
    public void TimerMethod(Controller controller){
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(selection.select.isEmpty()){ blockB.setDisable(true); }
                    else {blockB.setDisable(false);}
                        time = time.plusSeconds(1);
                        Platform.runLater(() -> timeLabel.setText(time.toString().substring(0, 8)));
                        for (UpdateTime update : updates) {
                            update.update(time, controller);
                    }
                });
            }
        }, 0, speed);
    }

}