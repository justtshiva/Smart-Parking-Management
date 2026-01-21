package com.smartparking.ui;

import com.smartparking.dao.ParkingSlotDao;
import com.smartparking.model.DashboardRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.smartparking.model.DashboardRow;


import java.util.List;

public class DashboardController {

    @FXML
    private TableView<DashboardRow> slotTable;

    @FXML
    private TableColumn<DashboardRow, String> colSlotCode;

    @FXML
    private TableColumn<DashboardRow, Integer> colFloor;

    @FXML
    private TableColumn<DashboardRow, String> colStatus;

    @FXML
    private TableColumn<DashboardRow, Integer> colTicketId;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblEmpty;

    @FXML
    private Label lblOccupied;

    private final ParkingSlotDao dao = new ParkingSlotDao();
    private final ObservableList<DashboardRow> tableData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colSlotCode.setCellValueFactory(new PropertyValueFactory<>("slotCode"));
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTicketId.setCellValueFactory(new PropertyValueFactory<>("ticketId"));

        slotTable.setItems(tableData);
        loadSlots();
    }

    @FXML
    private void onRefresh() {
        loadSlots();
    }

    public void loadSlots() {
        try {
            tableData.clear();
            List<DashboardRow> rows = dao.fetchDashboardData();
            tableData.addAll(rows);

            long total = rows.size();
            long empty = rows.stream()
                    .filter(r -> "EMPTY".equalsIgnoreCase(r.getStatus()))
                    .count();
            long occupied = rows.stream()
                    .filter(r -> "OCCUPIED".equalsIgnoreCase(r.getStatus()))
                    .count();

            lblTotal.setText("Total: " + total);
            lblEmpty.setText("Empty: " + empty);
            lblOccupied.setText("Occupied: " + occupied);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onNewEntry() {
        openDialog(
                "/com/smartparking/ui/views/entry_form.fxml",
                "New Vehicle Entry"
        );
    }

    @FXML
    public void onVehicleExit() {
        openDialog(
                "/com/smartparking/ui/views/exit_form.fxml",
                "Vehicle Exit"
        );
    }

    private void openDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initOwner(slotTable.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));

            // ✅ THIS IS THE FIX
            if (controller instanceof EntryFormController efc) {
                efc.setParentController(this);
                efc.setDialogStage(dialog);
            } else if (controller instanceof ExitFormController xfc) {
                xfc.setParentController(this);
                xfc.setDialogStage(dialog);
            }

            dialog.showAndWait();
            loadSlots();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
