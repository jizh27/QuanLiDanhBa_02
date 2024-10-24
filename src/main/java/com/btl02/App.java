package com.btl02;

import javafx.application.Application;
import javafx.stage.Stage;
import com.btl02.controler.Controler;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        Controler controler = new Controler();
        controler.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
