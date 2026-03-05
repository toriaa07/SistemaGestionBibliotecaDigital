package com.bibliotec.controller;

import com.bibliotec.App;
import com.bibliotec.util.AlertUtil;
import com.bibliotec.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label userNameLabel;

    @FXML private Button btnCatalogo;
    @FXML private Button btnPrestamos;
    @FXML private Button btnNotificaciones;
    @FXML private Button btnPerfil;

    private List<Button> navButtons;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        userNameLabel.setText(session.getNombre());

        navButtons = List.of(btnCatalogo, btnPrestamos, btnNotificaciones, btnPerfil);
        showCatalogo();
    }

    @FXML public void showCatalogo()       { loadView("CatalogoView.fxml", btnCatalogo); }
    @FXML public void showPrestamos()      { loadView("PrestamosView.fxml", btnPrestamos); }
    @FXML public void showNotificaciones() { loadView("NotificacionesView.fxml", btnNotificaciones); }
    @FXML public void showPerfil()         { loadView("PerfilView.fxml", btnPerfil); }

    @FXML
    public void handleLogout() {
        if (AlertUtil.confirm("Cerrar Sesión", "¿Estás seguro de que deseas cerrar sesión?")) {
            SessionManager.getInstance().clearSession();
            App.showLogin();
        }
    }

    private void loadView(String fxml, Button activeBtn) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/bibliotec/fxml/" + fxml));
            Node view = loader.load();
            contentArea.getChildren().setAll(view);

            // Update active nav style
            navButtons.forEach(b -> {
                b.getStyleClass().remove("nav-btn-active");
            });
            if (activeBtn != null) {
                activeBtn.getStyleClass().add("nav-btn-active");
            }

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.error("Error", "No se pudo cargar la vista: " + fxml);
        }
    }
}
