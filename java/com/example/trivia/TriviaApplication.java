package com.example.trivia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TriviaApplication extends Application {
    private static String host;
    private static int delay;

    public static int getDelay() {
        return delay;
    }

    public static String getHost() {
        return host;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TriviaApplication.class.getResource("trivia_client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Trivia game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if (args.length < 2)
            throw new IllegalArgumentException(
                    "Start with CLI arguments: [hostname] [delay(sec)], for example:" +
                            "localhost 15");

        host = args[0];
        delay = Integer.parseInt(args[1]);

        launch();
    }
}