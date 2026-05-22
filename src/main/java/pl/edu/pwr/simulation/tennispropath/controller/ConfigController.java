package pl.edu.pwr.simulation.tennispropath.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class ConfigController {

    @FXML private TextField nameInputField;

    @FXML
    protected void onStartSimulation() {
        String chosenName = nameInputField.getText().trim();
        if (chosenName.isEmpty()) {
            chosenName = "Bezimienny Tenisista";
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pl/edu/pwr/simulation/tennispropath/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 900);

            SimulationController mainController = fxmlLoader.getController();
            mainController.setPlayerName(chosenName);

            Stage mainStage = new Stage();
            mainStage.setTitle("Tennis Pro Path - Symulacja");
            mainStage.setScene(scene);
            mainStage.show();

            Stage currentStage = (Stage) nameInputField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}