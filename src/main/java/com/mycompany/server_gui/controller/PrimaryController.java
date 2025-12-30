package com.mycompany.server_gui.controller;

import com.mycompany.server_gui.network.GameServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {

    GameServer gs;
    boolean isServerDown = true;

    @FXML
    private Label statusLabel;
    @FXML
    private Label usersLabel;
    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;

    public void initialize() {
        if (!isServerDown) {
            statusLabel.setText("Online");
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
        } else {
            statusLabel.setText("Offline");
            stopBtn.setDisable(true);
            startBtn.setDisable(false);
            usersLabel.setText("0");
        }
    }

    @FXML
    void onStart() {
        if (isServerDown) {
            gs = new GameServer(5008, (e) -> {
                Platform.runLater(() -> {
                    showAlert("Internal Server Error", e.getLocalizedMessage());
                });
            });

            gs.start();

            isServerDown = false;

            initialize();
        }
    }

    @FXML
    void onStop() {
        if (!isServerDown) {
            gs.closeServer();
            gs = null;
            isServerDown = true;
            initialize();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
