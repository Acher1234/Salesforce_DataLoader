package com.syntechpro.dataproject;

import com.syntechpro.dataproject.Controller.mainController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DataLoaderApplication extends Application {

    public static Image icon;
    @Override
    public void start(Stage stage) throws IOException {
        //load Stage
        icon = new Image(getClass().getResourceAsStream("/Image/salesforce.png"));
        stage.setTitle("Salesforce Data Modificator");
        stage.getIcons().add(icon);
        //load Scene
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("Controller/FirstScene.fxml"));
        Parent root = fxml.load();
        Scene scene = new Scene(root);
        ((mainController)fxml.getController()).initialiseScene(stage,scene);
        stage.setScene(scene);
        //show
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}