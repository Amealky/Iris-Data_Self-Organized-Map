package bm.classification.som.irisdatasom;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;


public class IrisDataSomApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        String cssPath = Paths.get("styles", "style.css").toUri().toString();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("IrisData SOM visualization");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });

    }

    public static void main(String[] args) {
        launch();
    }

}