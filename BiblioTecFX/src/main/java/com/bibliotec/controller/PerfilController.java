package com.bibliotec.controller;

import com.bibliotec.service.ApiService;
import com.bibliotec.util.AlertUtil;
import com.bibliotec.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class PerfilController {

    @FXML private Label nombreLabel;
    @FXML private Label correoLabel;
    @FXML private Label rolLabel;
    @FXML private Label rolBadge;
    @FXML private PasswordField passActualField;
    @FXML private PasswordField passNuevaField;
    @FXML private PasswordField passConfirmField;
    @FXML private Label passErrorLabel;

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        nombreLabel.setText(session.getNombre());
        correoLabel.setText(session.getCorreo());
        rolLabel.setText(session.getRol());
        rolBadge.setText(session.getRol());

        if (session.isAdmin()) {
            rolBadge.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; " +
                    "-fx-background-radius: 12; -fx-padding: 2 10; -fx-font-size: 11;");
        }
    }

    @FXML
    public void handleChangePassword() {
        String actual  = passActualField.getText();
        String nueva   = passNuevaField.getText();
        String confirm = passConfirmField.getText();

        if (actual.isEmpty() || nueva.isEmpty() || confirm.isEmpty()) {
            showPassError("Por favor completa todos los campos.");
            return;
        }
        if (nueva.length() < 8) {
            showPassError("La nueva contraseña debe tener al menos 8 caracteres.");
            return;
        }
        if (!nueva.equals(confirm)) {
            showPassError("Las contraseñas nuevas no coinciden.");
            return;
        }

        hidePassError();

        new Thread(() -> {
            try {
                ApiService.getInstance().changePassword(actual, nueva);
                Platform.runLater(() -> {
                    AlertUtil.info("Contraseña Actualizada",
                            "Tu contraseña ha sido cambiada exitosamente.");
                    passActualField.clear();
                    passNuevaField.clear();
                    passConfirmField.clear();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showPassError(e.getMessage()));
            }
        }).start();
    }

    private void showPassError(String msg) {
        passErrorLabel.setText(msg);
        passErrorLabel.setVisible(true);
        passErrorLabel.setManaged(true);
    }

    private void hidePassError() {
        passErrorLabel.setVisible(false);
        passErrorLabel.setManaged(false);
    }
}
