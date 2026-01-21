package com.smartparking.ui;

import com.smartparking.service.ParkingService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EntryFormController {

    @FXML private TextField tfOwnerName;
    @FXML private TextField tfPhone;
    @FXML private TextField tfVehicleNumber;
    @FXML private ChoiceBox<String> cbVehicleType;
    @FXML private Label lblStatus;

    private final ParkingService parkingService = new ParkingService();
    private Stage dialogStage;
    private DashboardController parentController;

    public void setParentController(DashboardController parent) {
        this.parentController = parent;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void initialize() {
        cbVehicleType.getItems().addAll("CAR", "BIKE", "VAN");
        cbVehicleType.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCancel() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void onPark() {
        String ownerName = tfOwnerName.getText().trim();
        String phone = tfPhone.getText().trim();
        String vehicleNumber = tfVehicleNumber.getText().trim();
        String vehicleType = cbVehicleType.getValue();

        if (ownerName.isEmpty() || vehicleNumber.isEmpty()) {
            lblStatus.setText("Owner name and vehicle number are required.");
            return;
        }

        try {
            ParkingService.ParkResult result =
                    parkingService.parkVehicle(ownerName, phone, vehicleNumber, vehicleType);

            if (result.isSuccess()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vehicle Parked Successfully");
                alert.setHeaderText("Parking Ticket Generated");
                alert.setContentText(
                        "Slot: " + result.getSlotCode() +
                        "\nTicket ID: " + result.getTicketId()
                );
                alert.showAndWait();

                if (parentController != null) {
                    parentController.loadSlots();
                }
                dialogStage.close();

            } else {
                lblStatus.setText("Failed: " + result.getMessage());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }
}
