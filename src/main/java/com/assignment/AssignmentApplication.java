package com.assignment;

import com.assignment.controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AssignmentApplication extends javafx.application.Application {

	private ConfigurableApplicationContext springContext;

	// for java fx
	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(AssignmentApplication.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
		Scene scene = new Scene(loader.load(), 400, 300);

		// important
		LoginController controller = loader.getController();
		controller.setStage(primaryStage);

		primaryStage.setTitle("Assignment - Login");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	@Override
	public void stop() throws Exception {
		Platform.exit();
		if (springContext != null) {
			springContext.close();
		}
		super.stop();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
