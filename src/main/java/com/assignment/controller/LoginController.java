package com.assignment.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private Stage stage;

    // setting the stage cause its imp
    // it is full window where dashboard is visible
    public void setStage(Stage stage) {
        this.stage = stage;
        loginButton.setDisable(false);
    }


    // checking login activity here weather user is authenticated or not
    @FXML
    private void onLogin() {
        loginButton.setDisable(true);
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

       // i have hardcoded username and password
        if ("admin".equals(username) && "admin123".equals(password)) {
            errorLabel.setText("");
            loadDashboard();
        } else {
            errorLabel.setText("Invalid. Use: admin / admin123");
            usernameField.clear();
            passwordField.clear();
            loginButton.setDisable(false);
            usernameField.requestFocus();
        }
    }

    private void loadDashboard() {
        if (stage == null) {
            errorLabel.setText("error , stage not set");
            loginButton.setDisable(false);
            return;
        }

        try {
            // it loads login screen after logging in

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load(), 500, 400);

            DashboardController controller = loader.getController();
            controller.setStage(stage);
            controller.startTimer();

            stage.setScene(dashboardScene);
            stage.setTitle("Idle Detector Dashboard");
            stage.setResizable(false);
        } catch (IOException e) {
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
            loginButton.setDisable(false);
        }
    }
}
