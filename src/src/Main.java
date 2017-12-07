/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author jefferson
 */
public class Main extends Application {

	public static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		Main.primaryStage = primaryStage;
		loadScene("View/Login.fxml", "Login");
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void loadScene(String nameFile, String titlePage) {
            Parent root;
            Main classe = new Main();
            try {
                    root = FXMLLoader.load(classe.getClass().getResource(nameFile));
                    Scene scene = new Scene(root);			
                    primaryStage.setTitle(titlePage);
                    primaryStage.setScene(scene);
                    primaryStage.resizableProperty().setValue(Boolean.FALSE);
                    primaryStage.show();
            } catch (IOException e) {
                    e.printStackTrace();
            }
	}
}
