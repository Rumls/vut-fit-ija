package main.Files;

import java.time.LocalTime;

/**
 * @author Jakub Mlkvy, Daniel Paul
 */

/***
 * Interface that implements update method used by timer to move a bus
 */
public interface UpdateTime {
    void update(LocalTime time, Controller controller);
}
