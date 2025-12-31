module com.mycompany.server_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires derbyclient;

    opens com.mycompany.server_gui.controller to javafx.fxml;
    exports com.mycompany.server_gui;
}
