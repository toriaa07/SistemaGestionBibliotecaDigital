package com.bibliotec.controller;

import com.bibliotec.model.NotificacionDto;
import com.bibliotec.service.ApiService;
import com.bibliotec.util.AlertUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class NotificacionesController {

    @FXML private TableView<NotificacionDto> notifTable;
    @FXML private TableColumn<NotificacionDto, String> colEstado;
    @FXML private TableColumn<NotificacionDto, String> colTipo;
    @FXML private TableColumn<NotificacionDto, String> colMensaje;
    @FXML private TableColumn<NotificacionDto, String> colFecha;
    @FXML private TableColumn<NotificacionDto, String> colLeida;
    @FXML private TableColumn<NotificacionDto, Void> colAccion;
    @FXML private Label subtitleLabel;

    @FXML
    public void initialize() {
        // Dot indicator (unread)
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                NotificacionDto n = getTableView().getItems().get(getIndex());
                setText(n.isLeida() ? "" : "●");
                setStyle(n.isLeida() ? "" : "-fx-text-fill: #e74c3c; -fx-font-size:10px;");
            }
        });

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item.toUpperCase()) {
                    case "VENCIMIENTO" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight:bold;");
                    case "RECORDATORIO" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight:bold;");
                    case "SISTEMA" -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight:bold;");
                    default -> setStyle("");
                }
            }
        });

        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaEnvioStr"));
        colLeida.setCellValueFactory(new PropertyValueFactory<>("estadoLeida"));

        colLeida.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Nueva".equals(item)
                        ? "-fx-text-fill: #e67e22; -fx-font-weight:bold;"
                        : "-fx-text-fill: #7f8c8d;");
            }
        });

        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Marcar leída");
            {
                btn.getStyleClass().add("btn-table-action");
                btn.setOnAction(e -> {
                    NotificacionDto n = getTableView().getItems().get(getIndex());
                    marcarLeida(n);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                NotificacionDto n = getTableView().getItems().get(getIndex());
                setGraphic(n.isLeida() ? null : btn);
            }
        });

        loadNotificaciones();
    }

    @FXML public void handleRefresh() { loadNotificaciones(); }

    @FXML
    public void handleMarcarTodas() {
        new Thread(() -> {
            try {
                ApiService.getInstance().marcarTodasLeidas();
                Platform.runLater(() -> {
                    AlertUtil.info("Listo", "Todas las notificaciones han sido marcadas como leídas.");
                    loadNotificaciones();
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        AlertUtil.error("Error", e.getMessage()));
            }
        }).start();
    }

    private void loadNotificaciones() {
        new Thread(() -> {
            try {
                List<NotificacionDto> list = ApiService.getInstance().getMisNotificaciones();
                Platform.runLater(() -> {
                    notifTable.setItems(FXCollections.observableArrayList(list));
                    long unread = list.stream().filter(n -> !n.isLeida()).count();
                    subtitleLabel.setText("Tienes " + unread + " notificación(es) sin leer");
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        AlertUtil.error("Error", "No se pudieron cargar las notificaciones: " + e.getMessage()));
            }
        }).start();
    }

    private void marcarLeida(NotificacionDto n) {
        new Thread(() -> {
            try {
                ApiService.getInstance().marcarNotificacionLeida(n.getIdNotificacion());
                Platform.runLater(this::loadNotificaciones);
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtil.error("Error", e.getMessage()));
            }
        }).start();
    }
}
