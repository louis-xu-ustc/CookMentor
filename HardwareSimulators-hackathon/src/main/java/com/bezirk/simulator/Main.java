package com.bezirk.simulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hardware Simulator");

        // Show the scene containing the root layout.
        primaryStage.setScene(new Scene(new WorkbenchPane()));
        primaryStage.show();
    }
}
