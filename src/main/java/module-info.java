module com.mycompany.server_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;


    opens com.mycompany.server_gui.controller to javafx.fxml;
    exports com.mycompany.server_gui;
}
