package com.smartparking.ui;

import com.smartparking.service.ExitService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExitFormController {

    @FXML private TextField tfTicketId;
    @FXML private Label lblStatus;

    private Stage dialogStage;
    private DashboardController parentController;
    private final ExitService exitService = new ExitService();

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setParentController(DashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void onCancel() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void onExit() {

        String input = tfTicketId.getText().trim();

        if (input.isEmpty()) {
            lblStatus.setText("Ticket ID is required.");
            return;
        }

        int ticketId;
        try {
            ticketId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            lblStatus.setText("Invalid Ticket ID.");
            return;
        }

        try {
            ExitService.ExitResult result = exitService.exitVehicle(ticketId);

            if (result.isSuccess()) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payment Summary");
                alert.setHeaderText("Vehicle Exit Successful");
                alert.setContentText(
                        "Duration: " + result.getMinutes() + " minutes\n" +
                        "Amount Paid: ₹" + result.getAmount()
                );
                alert.showAndWait();

                if (parentController != null) {
                    parentController.loadSlots();
                }
                dialogStage.close();

            } else {
                lblStatus.setText(result.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Error processing exit.");
        }
    }
}
