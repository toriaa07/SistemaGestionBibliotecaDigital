package com.bibliotec.controller;

import com.bibliotec.App;
import com.bibliotec.model.LoginResponseDto;
import com.bibliotec.service.ApiService;
import com.bibliotec.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class LoginController {

    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    @FXML
    public void handleLogin() {
        String correo   = correoField.getText().trim();
        String password = passwordField.getText();

        if (correo.isEmpty() || password.isEmpty()) {
            showError("Por favor ingresa tu correo y contraseña.");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Ingresando…");
        hideError();

        new Thread(() -> {
            try {
                LoginResponseDto resp = ApiService.getInstance().login(correo, password);
                SessionManager.getInstance().setSession(
                        resp.token, resp.nombre, resp.correo, resp.rol);

                Platform.runLater(App::showMain);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Credenciales incorrectas. Verifica tu correo y contraseña.");
                    loginBtn.setDisable(false);
                    loginBtn.setText("Ingresar");
                });
            }
        }).start();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
