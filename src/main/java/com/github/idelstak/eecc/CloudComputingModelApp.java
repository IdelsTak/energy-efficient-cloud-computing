package com.github.idelstak.eecc;

import com.github.idelstak.eecc.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudComputingModelApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Energy-Efficient Cloud Computing Model");
        primaryStage.setScene(new Scene(new MainView().getRoot()));
        primaryStage.show();
    }

}
