package pl.edu.pwr.simulation.tennispropath.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.edu.pwr.simulation.tennispropath.model.CourtType;

import java.io.IOException;

/**
 * Rozbudowany kontroler ekranu konfiguracyjnego (Kreator Postaci)
 * Odpowiada za zarządzanie interfejsem graficznym (GUI) okna startowego aplikacji,
 * umożliwia wybór tożsamości, wieku i ulubionego kortu oraz realizuje interaktywną,
 * zabezpieczoną przed błędami logikę rozdysponowania startowej puli punktów umiejętności
 */
public class ConfigController {

    // =========================================================================
    // KONTROLKI FXML - TOŻSAMOŚĆ I CECHY BAZOWE
    // =========================================================================
    @FXML private TextField nameInputField;       // Pole tekstowe do wprowadzania imienia i nazwiska gracza
    @FXML private Spinner<Integer> ageSpinner;    // Komponent numeryczny (Spinner) do wyboru wieku startowego
    @FXML private ComboBox<CourtType> courtComboBox; // Rozwijana lista (ComboBox) wyboru ulubionego typu kortu

    // =========================================================================
    // KONTROLKI FXML - SYSTEM DYSTRYBUCJI PUNKTÓW (STATYSTYKI)
    // =========================================================================
    @FXML private Label poolLabel;         // Etykieta wyświetlająca aktualny stan pozostałych punktów umiejętności
    @FXML private Label forehandLabel;     // Etykieta prezentująca aktualny poziom punktów dla zagrania z forehandu
    @FXML private Label backhandLabel;     // Etykieta prezentująca aktualny poziom punktów dla zagrania z backhandu
    @FXML private Label serveLabel;        // Etykieta prezentująca aktualny poziom punktów dla serwisu

    // =========================================================================
    // WEWNĘTRZNY STAN KREATORA (LOGIKA ATRAMENTOWA)
    // =========================================================================
    private int attributePointsPool = 30; // Startowa pula punktów RPG przyznana graczowi do rozdania
    private int forehandLevel = 15;       // Początkowa, minimalna wartość bazowa statystyki forehandu
    private int backhandLevel = 15;       // Początkowa, minimalna wartość bazowa statystyki backhandu
    private int serveLevel = 15;          // Początkowa, minimalna wartość bazowa statystyki serwisu

    /**
     * Metoda automatycznie wywoływana przez kontener JavaFX po załadowaniu powiązanego pliku FXML.
     * Konfiguruje fabrykę wartości Spinnera wieku (zakres, krok, domyślna wartość), ładuje typy
     * nawierzchni z enuma CourtType bezpośrednio do kolekcji ComboBoxa oraz inicjalizuje napisy UI.
     */
    @FXML
    public void initialize() {
        // Konfiguracja SpinnerValueFactory dla wieku: zakres min 18 lat, max 40 lat, domyślnie 18, krok co 1 rok
        SpinnerValueFactory<Integer> ageFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(18, 40, 18, 1);
        ageSpinner.setValueFactory(ageFactory);

        // Konwersja tablicy wartości enuma CourtType na listę obserwowalną (ObservableList) i przekazanie jej do ComboBoxa
        courtComboBox.setItems(FXCollections.observableArrayList(CourtType.values()));
        courtComboBox.setValue(CourtType.HARD); // domyślna wartość

        updateAttributeUI();
    }

    // =========================================================================
    // OBSŁUGA SYSTEMU ROZDAWANIA PUNKTÓW (METODY ACTION EVENT PRZYCISKÓW +/-)
    // =========================================================================

    /**
     * Moduluje poziom statystyki Forehand na podstawie interakcji użytkownika z przyciskami plus/minus
     * @param event Obiekt zdarzenia akcji JavaFX niosący informację o źródłowym komponencie wywołującym
     */
    @FXML
    protected void onModifyForehand(javafx.event.ActionEvent event) {
        // Rzutowanie źródła zdarzenia na obiekt klasy Button w celu analizy jego etykiety tekstowej (+/-)
        Button btn = (Button) event.getSource();

        // Warunek dodawania: wciśnięty plus oraz dostępność wolnych punktów w puli globalnej
        if (btn.getText().equals("+") && attributePointsPool > 0) {
            forehandLevel++;
            attributePointsPool--;
            // Warunek odejmowania: wciśnięty minus oraz nienaruszenie sztywnego progu minimalnego (podłoga statystyki to 10)
        } else if (btn.getText().equals("-") && forehandLevel > 15) {
            forehandLevel--;
            attributePointsPool++;
        }
        updateAttributeUI(); // Natychmiastowe odświeżenie zmienionych wartości liczbowych na ekranie
    }

    /**
     * Moduluje poziom statystyki Backhand na podstawie interakcji użytkownika z przyciskami plus/minus
     * @param event Obiekt zdarzenia akcji JavaFX niosący informację o źródłowym komponencie wywołującym
     */
    @FXML
    protected void onModifyBackhand(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.getText().equals("+") && attributePointsPool > 0) {
            backhandLevel++;
            attributePointsPool--;
        } else if (btn.getText().equals("-") && backhandLevel > 15) {
            backhandLevel--;
            attributePointsPool++;
        }
        updateAttributeUI();
    }

    /**
     * Moduluje poziom statystyki Serwis na podstawie interakcji użytkownika z przyciskami plus/minus
     * @param event Obiekt zdarzenia akcji JavaFX niosący informację o źródłowym komponencie wywołującym
     */
    @FXML
    protected void onModifyServe(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.getText().equals("+") && attributePointsPool > 0) {
            serveLevel++;
            attributePointsPool--;
        } else if (btn.getText().equals("-") && serveLevel > 15) {
            serveLevel--;
            attributePointsPool++;
        }
        updateAttributeUI();
    }

    /**
     * Prywatna metoda pomocnicza synchronizująca aktualny stan liczników w pamięci podręcznej
     * kontrolera z widokiem tekstowym (etykietami Label) prezentowanym użytkownikowi
     */
    private void updateAttributeUI() {
        poolLabel.setText("Dostępne punkty: " + attributePointsPool);
        forehandLabel.setText(String.valueOf(forehandLevel));
        backhandLabel.setText(String.valueOf(backhandLevel));
        serveLabel.setText(String.valueOf(serveLevel));
    }

    // =========================================================================
    // URUCHOMIENIE GLÓWNEJ SYMULACJI I TRANSFER DANYCH MIĘDZY KONTROLERAMI
    // =========================================================================

    /**
     * Odpowiada za pobranie parametrów z kontrolek GUI, dokonanie walidacji wejściowej,
     * asynchroniczne załadowanie pliku głównego widoku symulacji (main-view.fxml),
     * wstrzyknięcie zebranych danych do SimulationController oraz zamianę aktywnego okna Stage.
     */
    @FXML
    protected void onStartSimulation() {
        // Pobranie i oczyszczenie z białych znaków wprowadzonego imienia i nazwiska zawodnika
        String chosenName = nameInputField.getText().trim();

        // Zabezpieczenie przed pustym polem tekstowym
        if (chosenName.isEmpty()) {
            chosenName = "Bezimienny Tenisista";
        }

        // Pobranie ostatecznie ustalonych wartości wieku oraz preferencji kortu z dedykowanych kontrolek FX
        int chosenAge = ageSpinner.getValue();
        CourtType chosenCourt = courtComboBox.getValue();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/pl/edu/pwr/simulation/tennispropath/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1800, 1000); // Konfiguracja pożądanej rozdzielczości okna głównego

            // Pobranie instancji kontrolera docelowego utworzonego automatycznie przez FXMLLoader
            SimulationController mainController = fxmlLoader.getController();

            mainController.initCustomPlayer(
                    chosenName,
                    chosenAge,
                    chosenCourt,
                    forehandLevel,
                    backhandLevel,
                    serveLevel
            );

            // Inicjalizacja i konfiguracja nowego Stage (okna) dla widoku symulacji
            Stage mainStage = new Stage();
            mainStage.setTitle("Tennis Pro Path - Symulacja");
            mainStage.setScene(scene);
            mainStage.show(); // Wyświetlenie głównego okna symulatora kariery

            // Zamknięcie dotychczasowego okna konfiguracyjnego poprzez odczytanie Stage z dowolnej aktywnej kontrolki
            Stage currentStage = (Stage) nameInputField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}