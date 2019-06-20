package ml131.de.hdm_stuttgart.mi.JavaFxUI;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ml131.de.hdm_stuttgart.mi.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import ml131.de.hdm_stuttgart.mi.searchEngine;
import java.io.IOException;



public class    UserInterface extends Application {
    Button button;
    TableView<Card> table;

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
            String name = "Aka";
            searchEngine.fillCurrentFilter("","","","","",name,"");
            try {
                searchEngine.enterSetEdition(FileManager.openConnectionToFile("Standard"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            table.setItems(Controler.getCards());
        });
        //Name column
        TableColumn<Card, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //cmc column
        TableColumn<Card, String> manaCostColumn = new TableColumn<>("Manacost");
        manaCostColumn.setMinWidth( 100);
        manaCostColumn.setCellValueFactory(new PropertyValueFactory<>("manaCost"));

        //type column
        TableColumn<Card, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setMinWidth(400);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        table = new TableView<>();
        table.getColumns().addAll(nameColumn, manaCostColumn, typeColumn);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(button);


        VBox vBox = new VBox();
        vBox.getChildren().addAll(table,button);


       // StackPane layout = new StackPane();
        //layout.getChildren().addAll(vBox ,hBox);
        //Scene scene = new Scene(layout, 600, 400);

        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}


