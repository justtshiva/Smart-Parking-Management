module com.smartparking {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    exports com.smartparking.model;
    exports com.smartparking.dao;
    exports com.smartparking.service;

    opens com.smartparking.ui to javafx.fxml, javafx.graphics;
    opens com.smartparking.dao to java.sql; // not necessary but harmless
}
