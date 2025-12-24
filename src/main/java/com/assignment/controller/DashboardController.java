package com.assignment.controller;

import com.assignment.nativebridge.IdleDetectorLibrary;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DashboardController {

    @FXML private Label timerLabel;
    @FXML private TextField secondsField;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Label statusLabel;
    @FXML private Label userLabel;

    @Setter
    private Stage stage;
    private AnimationTimer timer;
    private long startTime;

    private Thread idleThread;
    private volatile boolean cancelIdleCheck = false;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final String HTTP_ENDPOINT  = "http://httpbin.org/post";
    private static final String HTTPS_ENDPOINT = "https://httpbin.org/post";


    // it runs automatically, and it initializes all the fields and texts
    @FXML
    public void initialize() {
        timerLabel.setText("Timer: 00:00");
        statusLabel.setText("");
        startButton.setText("Start Idle Check");
        stopButton.setText("Stop");
        stopButton.setDisable(false);
        if (userLabel != null) {
            userLabel.setText("admin logged in");
        }
    }

    public void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                int seconds = (int) elapsed;
                int mins = seconds / 60;
                int secs = seconds % 60;
                timerLabel.setText(String.format("Timer: %02d:%02d", mins, secs));
            }
        };
        timer.start();
    }

    @FXML
    private void onStart() {
        try {
            int seconds = Integer.parseInt(secondsField.getText().trim());
            if (seconds <= 0 || seconds > 300) {
                statusLabel.setText("Enter 1-300 seconds");
                return;
            }

            cancelCurrentIdleThread();

            cancelIdleCheck = false;
            startButton.setDisable(true);
            startButton.setText("Checking...");
            startButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            statusLabel.setText("Checking idle for " + seconds + " seconds...");

            callIdleDllInBackground(seconds);

        } catch (NumberFormatException e) {
            statusLabel.setText("Enter valid number");
        }
    }

    @FXML
    private void onStop() {
        if (timer != null) {
            timer.stop();
        }
        cancelCurrentIdleThread();
        statusLabel.setText("ideal check stopped");
        startButton.setDisable(false);
        startButton.setText("Start idle Check");
        startButton.setStyle("");
    }

    private void cancelCurrentIdleThread() {
        cancelIdleCheck = true;
        if (idleThread != null && idleThread.isAlive()) {
            idleThread.interrupt();
        }
    }

    private void callIdleDllInBackground(int seconds) {
        idleThread = new Thread(() -> {
            int result;
            try {
                result = IdleDetectorLibrary.INSTANCE.waitForIdle(seconds);
            } catch (Throwable t) {
                t.printStackTrace();

                Platform.runLater(() -> {
                    statusLabel.setText("DLL error: " + t.getMessage());
                    startButton.setDisable(false);
                    startButton.setText("Try Again");
                    startButton.setStyle("");
                });
                return;
            }

            if (cancelIdleCheck) {
                return;
            }

            boolean isIdle = (result == 1);

            Platform.runLater(() -> {
                if (result == -1) {
                    statusLabel.setText("DLL failed (GetLastInputInfo).");
                    startButton.setText("Try Again");
                    startButton.setStyle("");
                } else if (isIdle) {
                    statusLabel.setText("User idle for " + seconds + " seconds");
                    startButton.setText("Idle Found");
                    startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    if (timer != null) {
                        timer.stop();
                    }

                    showIdleNotification();
                    fireIdleApisAsync();

                } else {
                    statusLabel.setText("User active - Not idle");
                    startButton.setText("Try Again");
                    startButton.setStyle("");
                }
                startButton.setDisable(false);
            });
        });
        idleThread.setDaemon(true);
        idleThread.start();
    }

    private void showIdleNotification() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Idle Notification");
        alert.setHeaderText(null);
        alert.setContentText("Desktop is in idle state");
        alert.show();
    }

    private void fireIdleApisAsync() {
        new Thread(() -> {
            try {
                String jsonBody = "{"
                        + "\"userEmailId\":\"test@example.com\","
                        + "\"idleState\":true"
                        + "}";

                HttpRequest httpReq = HttpRequest.newBuilder()
                        .uri(URI.create(HTTP_ENDPOINT))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpRequest httpsReq = HttpRequest.newBuilder()
                        .uri(URI.create(HTTPS_ENDPOINT))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> httpResp =
                        httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
                HttpResponse<String> httpsResp =
                        httpClient.send(httpsReq, HttpResponse.BodyHandlers.ofString());

                System.out.println("HTTP idle API status = " + httpResp.statusCode());
                System.out.println("HTTPS idle API status = " + httpsResp.statusCode());

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
