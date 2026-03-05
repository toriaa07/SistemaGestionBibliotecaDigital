package com.bibliotec.controller;

import com.bibliotec.model.LibroDto;
import com.bibliotec.service.ApiService;
import com.bibliotec.util.AlertUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CatalogoController {

    @FXML private TextField searchField;
    @FXML private TableView<LibroDto> librosTable;
    @FXML private TableColumn<LibroDto, String> colTitulo;
    @FXML private TableColumn<LibroDto, String> colAutor;
    @FXML private TableColumn<LibroDto, String> colEditorial;
    @FXML private TableColumn<LibroDto, Integer> colAnio;
    @FXML private TableColumn<LibroDto, String> colCategorias;
    @FXML private TableColumn<LibroDto, String> colDisponibles;
    @FXML private TableColumn<LibroDto, Void> colAccion;
    @FXML private Label countLabel;

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colCategorias.setCellValueFactory(new PropertyValueFactory<>("categoriasStr"));
        colDisponibles.setCellValueFactory(new PropertyValueFactory<>("estadoDisponibilidad"));

        // Color cell for availability
        colDisponibles.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Sin disponibilidad") || item.contains("Inactivo")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Action button column
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Solicitar");
            {
                btn.getStyleClass().add("btn-table-action");
                btn.setOnAction(e -> {
                    LibroDto libro = getTableView().getItems().get(getIndex());
                    handleSolicitarPrestamo(libro);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    LibroDto libro = getTableView().getItems().get(getIndex());
                    btn.setDisable(libro.getDisponibles() <= 0 || !libro.isActivo());
                    setGraphic(btn);
                }
            }
        });

        loadLibros(null);

        searchField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) handleSearch();
        });
    }

    @FXML
    public void handleSearch() {
        loadLibros(searchField.getText().trim());
    }

    @FXML
    public void handleShowAll() {
        searchField.clear();
        loadLibros(null);
    }

    private void loadLibros(String query) {
        countLabel.setText("Cargando…");
        new Thread(() -> {
            try {
                List<LibroDto> libros = ApiService.getInstance().getLibros(query);
                Platform.runLater(() -> {
                    librosTable.setItems(FXCollections.observableArrayList(libros));
                    countLabel.setText(libros.size() + " libro(s) encontrado(s)");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    countLabel.setText("Error al cargar");
                    AlertUtil.error("Error", "No se pudo cargar el catálogo: " + e.getMessage());
                });
            }
        }).start();
    }

    private void handleSolicitarPrestamo(LibroDto libro) {
        boolean ok = AlertUtil.confirm("Solicitar Préstamo",
                "¿Deseas solicitar el préstamo de:\n\n\"" + libro.getTitulo() + "\"\nde " + libro.getAutor() + "?");
        if (!ok) return;

        new Thread(() -> {
            try {
                ApiService.getInstance().crearPrestamo(libro.getLibroId());
                Platform.runLater(() -> {
                    AlertUtil.info("Préstamo Creado",
                            "¡Préstamo solicitado exitosamente!\n\nPuedes ver el detalle en 'Mis Préstamos'.");
                    loadLibros(searchField.getText().trim());
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        AlertUtil.error("Error al Solicitar", e.getMessage()));
            }
        }).start();
    }
}
