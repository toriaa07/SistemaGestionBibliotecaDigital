package com.bibliotec.controller;

import com.bibliotec.model.PrestamoDto;
import com.bibliotec.service.ApiService;
import com.bibliotec.util.AlertUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class PrestamosController {

    @FXML private TableView<PrestamoDto> prestamosTable;
    @FXML private TableColumn<PrestamoDto, String> colTitulo;
    @FXML private TableColumn<PrestamoDto, String> colAutor;
    @FXML private TableColumn<PrestamoDto, String> colFechaPrestamo;
    @FXML private TableColumn<PrestamoDto, String> colFechaVence;
    @FXML private TableColumn<PrestamoDto, String> colFechaDevolucion;
    @FXML private TableColumn<PrestamoDto, String> colEstado;
    @FXML private TableColumn<PrestamoDto, Void> colAccion;

    @FXML private ToggleButton btnTodos;
    @FXML private ToggleButton btnActivos;
    @FXML private ToggleButton btnVencidos;
    @FXML private ToggleButton btnDevueltos;

    private ObservableList<PrestamoDto> allPrestamos = FXCollections.observableArrayList();
    private ToggleGroup filterGroup;

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autorLibro"));
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamoStr"));
        colFechaVence.setCellValueFactory(new PropertyValueFactory<>("fechaVencimientoStr"));
        colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucionStr"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Estado cell colors
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item.toUpperCase()) {
                    case "ACTIVO"   -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight:bold;");
                    case "VENCIDO"  -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight:bold;");
                    case "DEVUELTO" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight:bold;");
                    default         -> setStyle("");
                }
            }
        });

        // Action column
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Devolver");
            {
                btn.getStyleClass().add("btn-table-action");
                btn.setOnAction(e -> {
                    PrestamoDto p = getTableView().getItems().get(getIndex());
                    handleDevolver(p);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                PrestamoDto p = getTableView().getItems().get(getIndex());
                boolean canReturn = "ACTIVO".equalsIgnoreCase(p.getEstado())
                        || "VENCIDO".equalsIgnoreCase(p.getEstado());
                btn.setDisable(!canReturn);
                btn.setVisible(canReturn);
                setGraphic(canReturn ? btn : null);
            }
        });

        // Toggle group for filters
        filterGroup = new ToggleGroup();
        btnTodos.setToggleGroup(filterGroup);
        btnActivos.setToggleGroup(filterGroup);
        btnVencidos.setToggleGroup(filterGroup);
        btnDevueltos.setToggleGroup(filterGroup);
        filterGroup.selectToggle(btnTodos);

        loadPrestamos();
    }

    @FXML
    public void handleRefresh() { loadPrestamos(); }

    @FXML
    public void handleFilter() { applyFilter(); }

    private void loadPrestamos() {
        new Thread(() -> {
            try {
                List<PrestamoDto> list = ApiService.getInstance().getMisPrestamos();
                Platform.runLater(() -> {
                    allPrestamos.setAll(list);
                    applyFilter();
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        AlertUtil.error("Error", "No se pudieron cargar los préstamos: " + e.getMessage()));
            }
        }).start();
    }

    private void applyFilter() {
        Toggle sel = filterGroup.getSelectedToggle();
        List<PrestamoDto> filtered;

        if (sel == btnActivos) {
            filtered = allPrestamos.stream()
                    .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado())).collect(Collectors.toList());
        } else if (sel == btnVencidos) {
            filtered = allPrestamos.stream()
                    .filter(p -> "VENCIDO".equalsIgnoreCase(p.getEstado())).collect(Collectors.toList());
        } else if (sel == btnDevueltos) {
            filtered = allPrestamos.stream()
                    .filter(p -> "DEVUELTO".equalsIgnoreCase(p.getEstado())).collect(Collectors.toList());
        } else {
            filtered = allPrestamos;
        }
        prestamosTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void handleDevolver(PrestamoDto prestamo) {
        boolean ok = AlertUtil.confirm("Confirmar Devolución",
                "¿Confirmas la devolución del libro?\n\n\"" + prestamo.getTituloLibro() + "\"");
        if (!ok) return;

        new Thread(() -> {
            try {
                ApiService.getInstance().devolverPrestamo(prestamo.getIdPrestamo());
                Platform.runLater(() -> {
                    AlertUtil.info("Devolución Exitosa", "El libro ha sido devuelto correctamente.");
                    loadPrestamos();
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        AlertUtil.error("Error al Devolver", e.getMessage()));
            }
        }).start();
    }
}
