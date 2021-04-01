package main.Files;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Class representing active selection
 */
public class Selection {
    public List<Street> select = new ArrayList<>();

    public Button blockButton;

    public Button addToPathButton;

    public ChoiceBox choiceBox;

    /***
     * Add street to selection
     * @param item Street to be added
     */
    public void add(Street item){
        blockButton.setDisable(false);
        if(choiceBox.getValue().toString() != "no link chosen") {
            addToPathButton.setDisable(false);
        }
        item.line.setStroke(Color.BLUE);
        select.add(item);
    }

    /***
     * Removes street from selection
     * @param item Street to be removed
     */
    public void remove(Street item) {

        if(!item.blocked) {
            item.line.setStroke(Color.BLACK);
        }else {
            item.line.setStroke(Color.RED);
        }
        select.remove(item);
    }

    /***
     * Clears whole active selection
     */
    public void clear() {
        while( !select.isEmpty()) {
            remove( select.iterator().next());
        }
        blockButton.setDisable(true);
        addToPathButton.setDisable(true);
    }

    /***
     * Checks if street is in the active selection
     * @param item Street to be checked
     * @return Boolean if street is in the active selection
     */
    public boolean contains(Street item) {
        return select.contains(item);
    }
}
