package pl.edu.pwr.simulation.tennispropath;

import javafx.application.Application;

/**
 * Uruchamia aplikację JavaFX poprzez przekazanie klasy startowej SimulationApplication.
 */
public class Launcher {
    /**
     * Punkt wejścia aplikacji.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        Application.launch(SimulationApplication.class, args);
    }
}
