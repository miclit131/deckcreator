package ml131.de.hdm_stuttgart.mi.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ml131.de.hdm_stuttgart.mi.datamodel.Card;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyInvalidColorException;
import ml131.de.hdm_stuttgart.mi.search.SearchEngine;
import ml131.de.hdm_stuttgart.mi.viewcomponents.ColorFilterView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/***
 * View class representing the presentation layer, this layer does not contain any business logic. The behaviour
 * of this class is delegated to the Controller.
 */
public class View {
    private Pagination pagination;
    private TableView<Card> searchDisplayTable;
    private TableView<Card> deckDisplayTable;
    private ComboBox filterTypeComboBox;
    private TextField filterValueTextField;
    private ColorFilterView colorSelection;
    private Button searchButton;
    private Button clearButton;
    private Button exportButton;
    private Button importButton;
    private ProgressIndicator progressIndicator;
    private Stage primaryStage;
    private int pageSize;
    private Label inProgressLabel;

    private List<Button> allButtons;
    public ObservableList<Card> searchCards;
    private Logger logger;

    public View(Stage primaryStage){
        logger = LogManager.getLogger(SearchEngine.class);
        pageSize = 20;

        // helper list for easy access
        allButtons = new ArrayList<>();

        pagination = new Pagination(1, 0);

        primaryStage.setTitle("Michi's Deck Creator");
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        this.primaryStage = primaryStage;

        searchCards = FXCollections.observableArrayList();
        searchDisplayTable = createCardView();
        searchDisplayTable.setItems(searchCards);
        deckDisplayTable = createCardView();
        deckDisplayTable.setEditable(true);
        deckDisplayTable.setPlaceholder(new Label("Add cards via drag & drop."));


        AnchorPane paginationAnchor = new AnchorPane();
        AnchorPane.setTopAnchor(pagination, 10.0);
        AnchorPane.setRightAnchor(pagination, 10.0);
        AnchorPane.setBottomAnchor(pagination, 10.0);
        AnchorPane.setLeftAnchor(pagination, 10.0);
        paginationAnchor.getChildren().addAll(pagination);

        searchButton = new Button("Search");
        allButtons.add(searchButton);

        ObservableList<String> options = FXCollections.observableArrayList("Card Name","Card Effect","Card Type");
        filterTypeComboBox = new ComboBox<>(options);
        filterTypeComboBox.getSelectionModel().selectFirst();

        filterValueTextField = new TextField ();

        clearButton = new Button("Clear");
        exportButton = new Button("Export");
        importButton = new Button("Import");
        allButtons.add(clearButton);
        allButtons.add(exportButton);
        allButtons.add(importButton);

        progressIndicator = new ProgressIndicator(-1);
        inProgressLabel = new Label("Please, wait");
        inProgressLabel.setFont(new Font("Arial", 20));

        inProgressLabel.setVisible(false);
        progressIndicator.setVisible(false);

        HBox middleBar = new HBox();
        middleBar.getChildren().addAll(clearButton, exportButton, importButton, progressIndicator, inProgressLabel);
        middleBar.setSpacing(10);

        HBox searchComponents = new HBox();
        searchComponents.setSpacing(10);
        searchComponents.getChildren().addAll(filterTypeComboBox, filterValueTextField, searchButton);

        try {
            colorSelection=new ColorFilterView();
        } catch (LogfriendlyInvalidColorException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }
        VBox searchAndFilterComponents = new VBox();
        searchAndFilterComponents.setSpacing(10);
        searchAndFilterComponents.getChildren().addAll(searchComponents, colorSelection);


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);

        vBox.getChildren().addAll(searchAndFilterComponents, searchDisplayTable, paginationAnchor, middleBar, deckDisplayTable);

        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /***
     * The search result viewcomponents and decklist viewcomponents are almost identical and created with this function.
     * @return generic table which can become decklist or search result table
     */
    private TableView<Card> createCardView(){
        TableView<Card> table = new TableView<>();

        //url / name column
        TableColumn<Card, Hyperlink> urlColumn = new TableColumn<>("Name");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("pictureLink"));
        urlColumn.setMinWidth(400);

        //cmc column
        TableColumn<Card, String> manaCostColumn = new TableColumn<>("Cost");
        manaCostColumn.setMinWidth(30);
        manaCostColumn.setCellValueFactory(new PropertyValueFactory<>("manaCost"));

        //type column
        TableColumn<Card, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setMinWidth(50);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        table.getColumns().addAll(urlColumn, manaCostColumn, typeColumn);
        table.setMinSize(600, 250);
        table.setStyle("-fx-selection-bar: NavajoWhite; -fx-selection-bar-non-focused: NavajoWhite;");

        return table;
    }

    /***
     * Helper turning on/off all control elements
     * @param disable: if they should be enabled (disabled=false) or disabled (disabled=true)
     */
    public void enableDisableAllControlElements(boolean disable){
        for(Button button : allButtons){
            button.setDisable(disable);
        }
        filterValueTextField.setDisable(disable);
    }

    public Pagination getPagination() {
        return pagination;
    }

    public TableView<Card> getSearchDisplayTable() {
        return searchDisplayTable;
    }

    public TableView<Card> getDeckDisplayTable() {
        return deckDisplayTable;
    }

    public ComboBox getFilterTypeComboBox() {
        return filterTypeComboBox;
    }

    public TextField getFilterValueTextField() {
        return filterValueTextField;
    }

    public ColorFilterView getColorSelection() {
        return colorSelection;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public Button getImportButton() {
        return importButton;
    }

    public int getPageSize() {
        return pageSize;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Label getInProgressLabel() {
        return inProgressLabel;
    }

}
