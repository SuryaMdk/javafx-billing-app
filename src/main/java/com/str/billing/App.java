package com.str.billing;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

/**
 * JavaFX App
 */
public class App extends Application {
	
    
	@SuppressWarnings("exports")
	@Override
    public void start(Stage primaryStage) {
    	try {
    		Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
    		Scene scene = new Scene(root, 800, 600);
    		primaryStage.setScene(scene);
    		primaryStage.setTitle("Billing Application");
    		primaryStage.setMaximized(true);
    		primaryStage.show();
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }

    public static void main(String[] args) {
    	launch(args);
    }

}