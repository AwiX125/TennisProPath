package pl.edu.pwr.simulation.tennispropath;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimulationApplication.class.getResource("config-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 800);

        stage.setTitle("Konfiguracja Projektu - Tennis Pro Path");
        stage.setScene(scene);
        stage.show();
    }
}
