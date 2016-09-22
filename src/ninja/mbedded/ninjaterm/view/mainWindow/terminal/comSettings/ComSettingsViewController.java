package ninja.mbedded.ninjaterm.view.mainWindow.terminal.comSettings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import ninja.mbedded.ninjaterm.model.Model;
import ninja.mbedded.ninjaterm.managers.ComPortManager;
import ninja.mbedded.ninjaterm.util.comport.*;

import java.io.IOException;

/**
 * Controller for the "COM Settings tab" which is part of the main window.
 *
 * @author          Geoffrey Hunter <gbmhunter@gmail.com> (www.mbedded.ninja)
 * @since           2016-07-10
 * @last-modified   2016-09-22
 */
public class ComSettingsViewController {

    //================================================================================================//
    //========================================== FXML BINDINGS =======================================//
    //================================================================================================//

    @FXML
    public Button reScanButton;

    @FXML
    public ComboBox<String> foundComPortsComboBox;

    @FXML
    public ComboBox<BaudRates> baudRateComboBox;

    @FXML
    public ComboBox<NumDataBits> numDataBitsComboBox;

    @FXML
    public ComboBox<Parities> parityComboBox;

    @FXML
    public ComboBox<NumStopBits> numStopBitsComboBox;

    @FXML
    public Button openCloseComPortButton;

    //================================================================================================//
    //=========================================== CLASS FIELDS =======================================//
    //================================================================================================//

    private Model model;

    private ComPortManager comPortManager;

    public ComSettingsViewController() {

    }

    public void init() {

        // Attach handler for "Scan" button press
        reScanButton.setOnAction((actionEvent) -> {
            scanButtonPressed();
        });

        //==============================================//
        //=============== POPULATE COMBOBOXES ==========//
        //==============================================//

        baudRateComboBox.getItems().setAll(BaudRates.values());
        baudRateComboBox.getSelectionModel().select(BaudRates.BAUD_9600);

        numDataBitsComboBox.getItems().setAll(NumDataBits.values());
        numDataBitsComboBox.getSelectionModel().select(NumDataBits.EIGHT);

        parityComboBox.getItems().setAll(Parities.values());
        parityComboBox.getSelectionModel().select(Parities.NONE);

        numStopBitsComboBox.getItems().setAll(NumStopBits.values());
        numStopBitsComboBox.getSelectionModel().select(NumStopBits.ONE);

        // Attach handler for when selected COM port changes. This is responsible for
        // enabling/disabling the "Open" button as appropriate
        foundComPortsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("ComboBox selected item changed.");

            // newValue will be null if a scan was done and no COM ports
            // were found
            if (newValue == null) {
                openCloseComPortButton.setDisable(true);
            } else {
                openCloseComPortButton.setDisable(false);
            }
        });
    }

    public void setStatusBarController(Model model) {
        this.model = model;
    }

    private void scanButtonPressed() {
        System.out.println("Scan button pressed.");

        scanComPorts();
    }

    public void scanComPorts() {

        System.out.println(this.getClass().getName() + ".scanComPorts() called.");

        // Clear any existing COM ports that are in the combobox from a previous scan
        foundComPortsComboBox.getItems().clear();

        String[] portNames = comPortManager.scan();

        if(portNames.length == 0) {
            model.status.addMsg("No COM ports found on this computer.");
            return;
        }

        model.status.addMsg(portNames.length + " COM port(s) found.");
        foundComPortsComboBox.getItems().addAll(portNames);

        // Select first one in list for convenience
        foundComPortsComboBox.getSelectionModel().select(0);
    }

    public void setComPortManager(ComPortManager comPortManager) {
        this.comPortManager = comPortManager;
    }




}