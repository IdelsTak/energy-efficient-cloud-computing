package com.github.idelstak.eecc.ui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public abstract class View {

    private final Parent root;

    public View(String resource) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
            fxmlLoader.setController(this);
            root = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Parent getRoot() {
        return root;
    }

}
