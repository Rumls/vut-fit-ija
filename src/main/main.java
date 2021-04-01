package main;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.Files.*;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */
public class main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
    /***
     * Method that implements FXML Loading drawing all the shapes handling file loading and creating the whole simulation
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Files/layout.fxml")); /**/
        BorderPane root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = loader.getController();
        List<DrawShapes> items = new ArrayList<>();
        LocalTime time = LocalTime.now();

        String WRITE = "map1.yml";
        String READ = "map1.yml";

        //////////////////////////////---------READ/WRITE-----------///////////////////////////////////////
        boolean write = true;

        if (write) {

            List<Street> streets = Arrays.asList(
                    new Street("A", new Coordinate(50*3, 50*3), new Coordinate(100*3, 50*3)),
                    new Street("B", new Coordinate(100*3, 50*3), new Coordinate(100*3, 100*3)),
                    new Street("C", new Coordinate(50*3, 100*3), new Coordinate(100*3, 100*3)),
                    new Street("D", new Coordinate(50*3, 50*3), new Coordinate(50*3, 100*3)),
                    new Street("E", new Coordinate(100*3, 100*3), new Coordinate(100*3, 150*3)),
                    new Street("F", new Coordinate(50*3, 150*3), new Coordinate(100*3, 150*3)),
                    new Street("G", new Coordinate(50*3, 100*3), new Coordinate(50*3, 150*3)),
                    new Street("H", new Coordinate(100*3, 150*3), new Coordinate(100*3, 200*3)),
                    new Street("I", new Coordinate(50*3, 200*3), new Coordinate(100*3, 200*3)),
                    new Street("J", new Coordinate(50*3, 150*3), new Coordinate(50*3, 200*3)),
                    new Street("K", new Coordinate(100*3, 150*3), new Coordinate(150*3, 150*3)),
                    new Street("L", new Coordinate(150*3, 150*3), new Coordinate(200*3, 150*3)),
                    new Street("M", new Coordinate(150*3, 150*3), new Coordinate(150*3, 200*3)),
                    new Street("N", new Coordinate(200*3, 150*3), new Coordinate(200*3, 200*3)),
                    new Street("O", new Coordinate(150*3, 200*3), new Coordinate(200*3, 200*3)),
                    new Street("P", new Coordinate(100*3, 200*3), new Coordinate(150*3, 200*3)),
                    new Street("Q", new Coordinate(150*3, 200*3), new Coordinate(200*3, 150*3)),
                    new Street("R", new Coordinate(100*3, 200*3), new Coordinate(150*3, 150*3)),
                    new Street("S", new Coordinate(50*3, 200*3), new Coordinate(100*3, 150*3)),
                    new Street("T", new Coordinate(50*3, 150*3), new Coordinate(100*3, 100*3))
            );

            List<myStop> stops1 = Arrays.asList(
                    new myStop("Jedna", new Coordinate(75*3, 175*3),streets.get(18)),
                    new myStop("Dva", new Coordinate(125*3, 150*3),streets.get(10)),
                    new myStop("Tri", new Coordinate(175*3, 150*3),streets.get(11))
            );

            List<myStop> stops2 = Arrays.asList(
                    new myStop("Styri", new Coordinate(75*3, 50*3),streets.get(0)),
                    new myStop("Pat", new Coordinate(100*3, 75*3),streets.get(1)),
                    new myStop("Sest", new Coordinate(100*3, 125*3),streets.get(4))
            );

            List<myStop> stops3 = Arrays.asList(
                    new myStop("Sedem", new Coordinate(50*3, 75*3),streets.get(3)),
                    new myStop("Osem", new Coordinate(50*3, 125*3),streets.get(6)),
                    new myStop("Devat", new Coordinate(75*3, 200*3),streets.get(8))
            );


            List<Bus> busses = Arrays.asList(
                    new Bus(0.25, streets.get(18).stop.stop_coordinates, new Path("Linka 21",Arrays.asList(
                            streets.get(18),streets.get(10),streets.get(11)
                    ))),
                    new Bus(0.25, streets.get(0).stop.stop_coordinates, new Path("Linka 11", Arrays.asList(
                            streets.get(0),streets.get(1),streets.get(4)
                    ))),
                    new Bus(0.25, streets.get(3).stop.stop_coordinates, new Path("Linka 31",Arrays.asList(
                            streets.get(3),streets.get(6),streets.get(9),streets.get(8)
                    )))
                    );

            List<myStop> stops = Stream.of(
                        stops1,
                        stops2,
                        stops3
            ).flatMap(Collection::stream).collect(Collectors.toList());

            //Data data = new Data(streets, stops, busses);

            items.addAll(streets);
            items.addAll(stops);
            items.addAll(busses);
            Controller.allbusses.addAll(busses);
            Controller.streets.addAll(streets);

           /*YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            ObjectMapper map = new ObjectMapper(factory);
            map.writeValue(new File(WRITE), data);*/

        } else {

            YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            ObjectMapper map = new ObjectMapper(factory);
            Data data = map.readValue(new File(READ), Data.class);

            items.addAll(data.getStreets());
            items.addAll(data.getStops());
            for (myStop stop : data.getStops()) {
                items.add(new myStop(stop.stop_id, stop.stop_coordinates, stop.street));
            }

            for (Bus bus : data.getBus()) {
                Bus bus_edit = new Bus(bus.getVelocity(),bus.getLocation(), new Path(bus.getPath().getLabel(),bus.getPath().infrastructure));
                items.add(bus_edit);
                Controller.allbusses.add(bus_edit);
            }

            Controller.streets.addAll(data.getStreets());
        }

        controller.fillChoiceBox();
        controller.setItems(items);
        controller.TimerMethod(controller);
    }
}