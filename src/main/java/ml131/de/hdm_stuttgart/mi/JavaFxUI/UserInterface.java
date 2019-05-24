package ml131.de.hdm_stuttgart.mi.JavaFxUI;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class UserInterface extends Application{
    Button button;

    public static void fxWindow() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("test window");
        button = new Button("click me");
        button.setOnAction(e -> {
            System.out.println("you clicked me");
            System.out.println("hi there");
        });


        StackPane layout = new StackPane();
        layout.getChildren().add(button);

        Scene scene = new Scene(layout, 250, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}


