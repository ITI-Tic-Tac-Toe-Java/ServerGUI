package com.mycompany.server_gui.utils;


import com.mycompany.server_gui.App;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author DELL
 */
public class Functions {
    public static void showErrorAlert(Exception ex) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Someting Went Wrong");
            alert.setHeaderText(null);
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        });
    }
}
