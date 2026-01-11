package com.mycompany.server_gui.controller;

import com.mycompany.server_gui.dao.PlayerDao;
import com.mycompany.server_gui.game.GameManager;
import com.mycompany.server_gui.network.GameServer;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {

    GameServer gs;
    boolean isServerDown = true;
    private final PlayerDao playerDao;
    private XYChart.Series<String, Number> series;

    @FXML
    private Label statusLabel;
    @FXML
    private Label usersLabel;
    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private BarChart<String, Number> chart;

    public PrimaryController() {
        playerDao = new PlayerDao();
    }

    public void onNewClientConnection(int clientNo) {
    }

    public void initialize() {
        setupChart();
        GameManager.getInstance().setUiUpdateCallback(this::updateChart);
        updateServerStatusUI();
        updateChart();
    }

    private void setupChart() {
        chart.setAnimated(false);
        chart.setLegendVisible(false);

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setLabel("Number of Players");
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(20);

        series = new XYChart.Series<>();
        series.setName("Player Statistics");

        series.getData().add(new XYChart.Data<>("Offline", 0));
        series.getData().add(new XYChart.Data<>("Online", 0));
        series.getData().add(new XYChart.Data<>("In Game", 0));

        chart.getData().add(series);
    }

    private void updateServerStatusUI() {
        if (!isServerDown) {
            statusLabel.setText("Online");
            statusLabel.setStyle("-fx-text-fill: green;");
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
        } else {
            statusLabel.setText("Offline");
            statusLabel.setStyle("-fx-text-fill: red;");
            stopBtn.setDisable(true);
            startBtn.setDisable(false);
            usersLabel.setText("0");
        }
    }

    private void updateChart() {
        Platform.runLater(() -> {
            try {
                int totalRegistered = playerDao.getAllPlayersCount();
                int totalOnlineLogged = 0;
                int inGameCount = 0;

                if (!isServerDown) {
                    totalOnlineLogged = GameManager.getInstance().getOnlineCount();
                    inGameCount = GameManager.getInstance().getInGameCount();
                }

                int onlineIdle = totalOnlineLogged - inGameCount;
                if (onlineIdle < 0) {
                    onlineIdle = 0;
                }

                int offlineCount = totalRegistered - totalOnlineLogged;
                if (offlineCount < 0) {
                    offlineCount = 0;
                }

                XYChart.Data<String, Number> offlineData = series.getData().get(0);
                XYChart.Data<String, Number> onlineData = series.getData().get(1);
                XYChart.Data<String, Number> inGameData = series.getData().get(2);

                offlineData.setYValue(offlineCount);
                onlineData.setYValue(onlineIdle);
                inGameData.setYValue(inGameCount);

                usersLabel.setText(String.valueOf(totalOnlineLogged));

                int max = Math.max(offlineCount, Math.max(onlineIdle, inGameCount));
                ((NumberAxis) chart.getYAxis()).setUpperBound(max + 5);

                applyColor(offlineData.getNode(), "grey");
                applyColor(onlineData.getNode(), "green");
                applyColor(inGameData.getNode(), "gold");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void applyColor(Node node, String color) {
        if (node != null) {
            node.setStyle("-fx-bar-fill: " + color + ";");
        }
    }

    @FXML
    void onStart() {
        if (isServerDown) {
            gs = new GameServer(5008, (count) -> {
            });
            gs.start();
            isServerDown = false;
            GameManager.getInstance().setUiUpdateCallback(this::updateChart);
            updateServerStatusUI();
            updateChart();
        }
    }

    @FXML
    void onStop() {
        if (!isServerDown) {
            gs.closeServer();
            gs = null;
            isServerDown = true;
            updateServerStatusUI();
            updateChart();
        }
    }
}
