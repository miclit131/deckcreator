package ml131.de.hdm_stuttgart.mi.JavaFxUI;
import javafx.collections.FXCollections;
import ml131.de.hdm_stuttgart.mi.exceptions.ExceptionCluster;
import ml131.de.hdm_stuttgart.mi.searchEngine;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import ml131.de.hdm_stuttgart.mi.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;


public class    UserInterface extends Application {
    Button button;
    Button testConnection;
    private TableView<Card> searchDisplayTable;
    private TableView<Card> deckDisplayTable;
    private TableColumn<Card, String> nameColumn;
    private TableColumn<Card, Hyperlink> urlColumn;
    private static String name="";
    private static String effect="";
    private static String type="";
    private Logger UiLogger = LogManager.getLogger(searchEngine.class);
    public static void fxWindow() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("test window");

        TextField nameFilter = new TextField ();
        nameFilter.setPromptText("search for a card name");
        TextField effectFilter = new TextField ();
        effectFilter.setPromptText("search for effect text");
        TextField typeFilter = new TextField ();
        typeFilter.setPromptText("search creature type");
        VBox textFilter = new VBox();
        textFilter.getChildren().addAll(nameFilter,effectFilter,typeFilter);


        //color selection Checkboxes
        CheckBox green = new CheckBox("Green");
        CheckBox black = new CheckBox("Black");
        CheckBox red = new CheckBox("Red");
        CheckBox blue = new CheckBox("Blue");
        CheckBox white = new CheckBox("White");
        CheckBox colorless = new CheckBox("Colorless");

        HBox colorSelection1=new HBox();
        HBox colorSelection2=new HBox();
        colorSelection1.getChildren().addAll(green,blue,red);
        colorSelection2.getChildren().addAll(black,white,colorless);
        VBox colorSelection=new VBox();
        colorSelection.setPadding(new Insets(10,10,10,10));
        colorSelection.setSpacing(10);
        colorSelection.getChildren().addAll(colorSelection1,colorSelection2);
        VBox filters =new VBox();
        filters.getChildren().addAll(colorSelection,textFilter);
        testConnection = new Button("test connection");
        testConnection.setOnAction(e->{
            try{
            if(!FileManager.pingURL("google.com",500)){
                AlertBox.display("Connection","connection successful");
                UiLogger.debug("Connection has been tested, SUCCESSFUL");
            }else{
                AlertBox.display("Connection","failed");
                UiLogger.debug("Connection test FAILED");
            }}catch(ExceptionCluster ex){
                UiLogger.debug("Connection test FAILED");
                AlertBox.display(ex.getErrorType(),ex.getMessage());
            }

        });
        button = new Button("Search");
        button.setOnAction(e -> {
            System.out.println("you clicked me");

            if ((nameFilter.getText() != null && !nameFilter.getText().isEmpty())) {
                UserInterface.name=nameFilter.getText();
            }
            if ((effectFilter.getText() != null && !effectFilter.getText().isEmpty())) {
                UserInterface.effect=effectFilter.getText();
            }
            if ((typeFilter.getText() != null && !typeFilter.getText().isEmpty())) {
                UserInterface.type=typeFilter.getText();
            }
            searchEngine.currentFilter=new CardFilter();
            searchEngine.currentResults=new ArrayList<>();
            searchEngine.fillCurrentFilter("",UserInterface.type,UserInterface.effect,"","",UserInterface.name,"");
            //searchEngine.fillCurrentFilter("","","","","",name,"");
            try {
                searchEngine.enterSetEdition(FileManager.openConnectionToFile("Standard"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            searchDisplayTable.setItems(Controler.getCards());
            System.out.println(searchEngine.cardcount);
            System.out.println();
            searchEngine.cardcount=0;
        });
        //Name column
        TableColumn<Card, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(50);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //url column
        urlColumn = new TableColumn<>("Address");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("pictureLink"));
        urlColumn.setCellFactory(new HyperlinkCell());

        //cmc column
        TableColumn<Card, String> manaCostColumn = new TableColumn<>("Manacost");
        manaCostColumn.setMinWidth( 50);
        manaCostColumn.setCellValueFactory(new PropertyValueFactory<>("manaCost"));

        //type column
        TableColumn<Card, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setMinWidth(50);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn addToDeckColumn = new TableColumn("Action");
        addToDeckColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        addToDeckColumn.setMinWidth(50);


        //Name column2
        TableColumn<Card, String> nameColumn2 = new TableColumn<>("Name");
        nameColumn.setMinWidth(50);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //cmc column2
        TableColumn<Card, String> manaCostColumn2 = new TableColumn<>("Manacost");
        manaCostColumn.setMinWidth(50);
        manaCostColumn.setCellValueFactory(new PropertyValueFactory<>("manaCost"));

        //type column2
        TableColumn<Card, String> typeColumn2 = new TableColumn<>("Type");
        typeColumn.setMinWidth(50);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        searchDisplayTable = new TableView<>();
        searchDisplayTable.getColumns().addAll(nameColumn, manaCostColumn, typeColumn,urlColumn);
        deckDisplayTable = new TableView<>();
        deckDisplayTable.getColumns().addAll(nameColumn2, manaCostColumn2, typeColumn2);

        addButtonToTable(searchDisplayTable);
        addButtonToTable(deckDisplayTable);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(searchDisplayTable,deckDisplayTable,filters);


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);
        vBox.getChildren().addAll(button,hBox,testConnection);

        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void addButtonToTable(TableView tableView) {
        TableColumn<Card, Void> colBtn = new TableColumn("");
        colBtn.setMinWidth(150);
        Callback<TableColumn<Card, Void>, TableCell<Card, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Card, Void> call(final TableColumn<Card, Void> param) {
                final TableCell<Card, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Add -> Deck");

                    {
                        btn.setOnAction(e -> {
                            ObservableList<Card> cards= FXCollections.observableArrayList();
                            Card currentIndexCard = getTableRow().getItem();
                            cards.add(currentIndexCard);
                            deckDisplayTable.getItems().addAll(cards);
                            System.out.println(currentIndexCard.getName());
                            tableView.getItems().remove(getIndex());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        tableView.getColumns().add(colBtn);
    }

    public class HyperlinkCell implements Callback<TableColumn<Card, Hyperlink>, TableCell<Card, Hyperlink>> {

        @Override
        public TableCell<Card, Hyperlink> call(TableColumn<Card, Hyperlink> arg) {
            TableCell<Card, Hyperlink> cell = new TableCell<Card, Hyperlink>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    setGraphic(item);
                }
            };
            return cell;
        }
    }
}


