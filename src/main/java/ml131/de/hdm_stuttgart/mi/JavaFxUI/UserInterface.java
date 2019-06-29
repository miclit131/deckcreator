package ml131.de.hdm_stuttgart.mi.JavaFxUI;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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

import java.io.IOException;
import java.util.ArrayList;


public class  UserInterface extends Application {

    private Button searchButton;
    private TableView<Card> searchDisplayTable;
    private TableView<Card> deckDisplayTable;
    private TableColumn<Card, String> nameColumn;
    private TableColumn<Card, Hyperlink> urlColumn;
    private TableColumn<Card, Hyperlink> urlColumn2;
    private static String name="";
    private static String effect="";
    private static String type="";
    private Pagination pagination;
    public static void fxWindow() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        int numInstances = 1094;
        int pageSize = 50;
        int pages = numInstances / pageSize;
        pagination = new Pagination(pages, 0);

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

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            if ((nameFilter.getText() != null && !nameFilter.getText().isEmpty())) {
                UserInterface.name=nameFilter.getText();
            }
            if ((effectFilter.getText() != null && !effectFilter.getText().isEmpty())) {
                UserInterface.effect=effectFilter.getText();
            }
            if ((typeFilter.getText() != null && !typeFilter.getText().isEmpty())) {
                UserInterface.type=typeFilter.getText();
            }
            ObservableList<Card> cards = runSearch(pageSize, 1);
            searchDisplayTable.setItems(cards);
        });

        pagination.setStyle("-fx-border-color:#75ff54;");
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if ((nameFilter.getText() != null && !nameFilter.getText().isEmpty())) {
                    UserInterface.name=nameFilter.getText();
                }
                if ((effectFilter.getText() != null && !effectFilter.getText().isEmpty())) {
                    UserInterface.effect=effectFilter.getText();
                }
                if ((typeFilter.getText() != null && !typeFilter.getText().isEmpty())) {
                    UserInterface.type=typeFilter.getText();
                }
                ObservableList<Card> cards = runSearch(pageSize, pageIndex+1);
                searchDisplayTable.setItems(cards);
                return searchDisplayTable;
            }
        });
        AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(pagination, 10.0);
        AnchorPane.setRightAnchor(pagination, 10.0);
        AnchorPane.setBottomAnchor(pagination, 10.0);
        AnchorPane.setLeftAnchor(pagination, 10.0);
        anchor.getChildren().addAll(pagination);

        //url / name column
        urlColumn = new TableColumn<>("Name");
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


        // url / name column 2
        urlColumn2 = new TableColumn<>("Name");
        urlColumn2.setCellValueFactory(new PropertyValueFactory<>("pictureLink"));
        urlColumn2.setCellFactory(new HyperlinkCell());

        //cmc column2
        TableColumn<Card, String> manaCostColumn2 = new TableColumn<>("Manacost");
        manaCostColumn2.setMinWidth(50);
        manaCostColumn2.setCellValueFactory(new PropertyValueFactory<>("manaCost"));

        //type column2
        TableColumn<Card, String> typeColumn2 = new TableColumn<>("Type");
        typeColumn2.setMinWidth(50);
        typeColumn2.setCellValueFactory(new PropertyValueFactory<>("type"));

        searchDisplayTable = new TableView<>();
        searchDisplayTable.getColumns().addAll(urlColumn, manaCostColumn, typeColumn);

        deckDisplayTable = new TableView<>();
        deckDisplayTable.getColumns().addAll(urlColumn2, manaCostColumn2, typeColumn2);




//        searchDisplayTable.setRowFactory(tableView -> {
//            final TableRow<Card> row = new TableRow<>();
//            row.hoverProperty().addListener((observable) -> {
//                final Card card = row.getItem();
//                if (row.isHover() && card != null) {
//                    String urlString = card.getRawURL();
//                    ImageView view = new ImageView(new Image(urlString));
//                    view.setFitHeight(400);
//                    view.setFitWidth(400);
//                    view.setPreserveRatio(true);
//
//                    button.setGraphic(view);
//                }
//            });
//
//            return row;
//        });


        addButtonToTable(searchDisplayTable);
        addButtonToTable(deckDisplayTable);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(anchor, deckDisplayTable,filters);


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);
        vBox.getChildren().addAll(searchButton,hBox);

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
//                            deckDisplayTable.setItems(cards);
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

    private static ObservableList<Card> runSearch(int pageSize, int currentPage){
        searchEngine.currentFilter=new CardFilter();
        searchEngine.currentResults=new ArrayList<>();
        searchEngine.includedPages=0;
        searchEngine.fillCurrentFilter("",UserInterface.type,UserInterface.effect,"","",UserInterface.name,"");
        //searchEngine.fillCurrentFilter("","","","","",name,"");
        try {
            searchEngine.enterSetEdition(FileManager.openConnectionToFile("Standard"), pageSize, currentPage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        searchDisplayTable.setItems(Controler.getCards());
        System.out.println(searchEngine.cardcount);
        searchEngine.cardcount=0;
        return Controler.getCards();
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


