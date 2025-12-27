module com.mycompany.server_gui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.server_gui to javafx.fxml;
    exports com.mycompany.server_gui;
}
