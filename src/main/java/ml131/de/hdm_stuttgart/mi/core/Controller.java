package ml131.de.hdm_stuttgart.mi.core;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import ml131.de.hdm_stuttgart.mi.datamodel.Card;
import ml131.de.hdm_stuttgart.mi.datamodel.CardFilter;
import ml131.de.hdm_stuttgart.mi.datamodel.SearchRequest;
import ml131.de.hdm_stuttgart.mi.datamodel.SearchResult;
import ml131.de.hdm_stuttgart.mi.interfaces.SearchResultListener;
import ml131.de.hdm_stuttgart.mi.util.LoggerUtil;
import ml131.de.hdm_stuttgart.mi.viewcomponents.EditingCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/***
 * Controller class that separates the behaviour of the User Interface from it's presentation. The controller connects
 * the UI with the model. It receives search requests or requests for importing a deck list and passes the requests
 * onto the model that contains the search logic. It also listens to the model by implementing the SearchResultsListener
 * Interface. Whenever the model has finished a search it informs the controller such that it can display the results.
 */
public class Controller implements SearchResultListener {

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private boolean firstTimeSearch; // Helper variable to keep track if search result is the first one ever received
    private AutoCompletionBinding oldBinding;

    private View view;
    private Model model;

    private Logger logger;

    public Controller(Model model, View view){
        this.view = view;
        this.model = model;
        this.model.addListener(this);

        this.view.getPrimaryStage().setResizable(false);
        this.logger = LogManager.getLogger(Controller.class);
        this.firstTimeSearch = true;

        addDragAndDropBehaviour();
        addEdibleCountColumn();
        addPaginationBehaviour();
        addKeyboardShortCuts();
        addButtonBehaviours();

        addDynamicAutocompleteBehaviour();

        logger.info("Application launched successfully.");
    }


    private void addKeyboardShortCuts() {
        // On press ENTER runs a new search
        view.getFilterValueTextField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                indicateSearchInUI(true);
                model.runAsyncSearch(new SearchRequest(getCardFilter(), 0, view.getPageSize(), "search"));
            }
        });
    }


    private void addEdibleCountColumn() {
        // allows to change the count value in the decklist table
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
        TableColumn countColumn = new TableColumn("count");
        countColumn.setCellFactory(cellFactory);
        countColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Card, Integer>>() {
                    @Override public void handle(TableColumn.CellEditEvent<Card, Integer> t) {
                        ((Card)t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setCount(t.getNewValue());
                    }
                });
        view.getDeckDisplayTable().getColumns().add(countColumn);
    }


    private void addButtonBehaviours() {
        // on button click removes all entries in the decklist
        view.getClearButton().setOnAction(e -> {
            view.getDeckDisplayTable().getItems().clear();
        });


        // opens popup dialog asking the user to specify a directory, on success it safes the curren decklist as a txt file
        view.getExportButton().setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Where do you want to safe your Decklist?");
            File directory = chooser.showDialog(view.getPrimaryStage());

            if(directory != null){
                if(directory.canWrite()){
                    try {
                        PrintWriter writer = new PrintWriter("Decklist.txt", "UTF-8");
                        for(Card card : view.getDeckDisplayTable().getItems()){
                            writer.println(card.getCount() + "\t" + card.getName());
                        }
                        writer.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        String fullPath = directory.getAbsolutePath()+"/Decklist.txt";
                        alert.setContentText("Your deck list has been saved to " + fullPath);
                        alert.showAndWait();
                    } catch (FileNotFoundException | UnsupportedEncodingException error) {
                        String errorMsg = LoggerUtil.errorWithAnalysis(error);
                        logger.error(errorMsg);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("Unexpected error occurred, see logfile for details.");
                        alert.showAndWait();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Path Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Missing write permission for directory " + directory.getAbsolutePath());
                }
            }
        });

        // opens popup dialog asking user to  specify a decklist file, on success it loads the file and reads the
        // card names together with their frequencies and launches a new search in order to retrieve the actual cards
        view.getImportButton().setOnAction(e -> {
            logger.debug("Import Button clicked.");
            FileChooser chooser = new FileChooser();
            File deckFile = chooser.showOpenDialog(view.getPrimaryStage());
            if(deckFile != null) {
                logger.debug(String.format("File selected: %3s", deckFile.getAbsolutePath()));
                try {
                    HashMap<String, Integer> cardsAndCounts = new HashMap<>();
                    // Open the file
                    FileInputStream fstream = new FileInputStream(deckFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    String strLine;
                    while ((strLine = br.readLine()) != null){
                        String[] parts = strLine.split("\t");
                        int count = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        cardsAndCounts.put(name, count);
                    }
                    fstream.close();
                    logger.debug(String.format("%3s cards successfully loaded from file.", cardsAndCounts.size()));

                    List<String> cardnames = new ArrayList<>(cardsAndCounts.keySet());
                    indicateSearchInUI(true);
                    model.runAsyncSearch(new SearchRequest(getCardFilter(cardnames),-1,-1, "import"));
                } catch (IOException error) {
                    logger.error(LoggerUtil.errorWithAnalysis(error));
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Path Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error happened during reading of the file, please refer to log file " +
                            "for more information");
                    error.printStackTrace();
                }
            }
        });
    }

    /***
     * Adds lazy load functionality as pages are browsed through
     */
    private void addPaginationBehaviour() {
        // Expression below without usage of lambda:
        //viewcomponents.getPagination().setPageFactory(new Callback<Integer, Node>() {
        //            @Override
        //            public Node call(Integer pageIndex) { ... }

        view.getPagination().setPageFactory(pageIndex -> {
            VBox box = new VBox(); // VBox necessary to avoid UI Bug

            SearchRequest request = new SearchRequest(getCardFilter(), pageIndex, view.getPageSize(), "search");

            if(!model.containsResultsFor(request)){
                logger.debug("Running new search for current search request.");
                indicateSearchInUI(true);
                model.runAsyncSearch(request);
            }else{
                logger.debug("Using cached search result to populate search result table.");
                List<Card> retrievedCards = model.getResults(request).getCards();
                ObservableList<Card> cards = FXCollections.observableArrayList();
                cards.addAll(retrievedCards);
                view.searchCards.removeAll();
                view.getSearchDisplayTable().setItems(cards);
                view.getSearchDisplayTable().refresh();
            }
            return box;
        });

        view.getSearchButton().setOnAction(e -> {
            logger.debug("Search button clicked, executing new search request.");
            indicateSearchInUI(true);
            model.runAsyncSearch(new SearchRequest(getCardFilter(), 0, view.getPageSize(), "search"));
        });
    }

    /***
     * Changes UI elements to show to the user that the program is busy, called before and after serach.
     * @param inProgress: enables or disables user elements
     */
    private void indicateSearchInUI(boolean inProgress) {
        if(inProgress){
            logger.debug("Disabling UI elements during search");
        }else{
            logger.debug("Re-enabling UI elements after search");
        }
        view.getProgressIndicator().setVisible(inProgress);
        view.getInProgressLabel().setVisible(inProgress);
        view.enableDisableAllControlElements(inProgress);
    }

    /***
     * We create decks by moving cards from the upper table (search results) onto the lower table (decklist).
     */
    private void addDragAndDropBehaviour() {
        view.getSearchDisplayTable().setRowFactory(tv -> {
            TableRow<Card> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                logger.debug("Drag detected event raised.");
                if (!row.isEmpty()) {
                    // get index of selected card, enable drag and drop on selected row, and copy to clipboard
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                    logger.trace("Card dragging prepared.");
                }
            });

            return row ;
        });

        view.getDeckDisplayTable().setOnDragOver(event -> {
            // opens "connection" and allows for incoming objects
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
            logger.trace("Card drag over initiated.");
        });

        view.getDeckDisplayTable().setOnDragDropped(event -> {
            // retrieves dragged card and updates deck display
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                Card draggedCard = view.getSearchDisplayTable().getItems().get(draggedIndex);
                view.getSearchDisplayTable().getItems().remove(draggedIndex);

                int dropIndex = view.getDeckDisplayTable().getItems().size();
                view.getDeckDisplayTable().getItems().add(dropIndex, draggedCard);
                event.setDropCompleted(true);
                view.getDeckDisplayTable().getSelectionModel().select(dropIndex);
                event.consume();
                logger.trace(String.format("Drag and dropped card %3s", draggedCard.getName()));
            }
        });
    }

    /***
     * Returns a configured cardfilter. The configuration depends on the selected UI Elements.
     * @return: configured Card filter ready for search
     */
    private CardFilter getCardFilter(){
        return getCardFilter(new ArrayList<>());
    }


    /***
     * Depending on the selected value of the combo box we want to add a different autocomplete list. E.g. if
     * "Card Name" is selected we want to show a list of card names instead of a list of card types.
     */
    private void addDynamicAutocompleteBehaviour(){
        ChangeListener changeListener = new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String oldValue, String newValue) {
                if(newValue.equals("Card Name")){
                    addAutocompleteBehaviour(model.getCardNames());
                }else if(newValue.equals("Card Type")){
                    addAutocompleteBehaviour(model.getCardTypes());
                }else{
                    addAutocompleteBehaviour(new ArrayList<>()); // effectively disables autocomplete
                }
            }
        };
        view.getFilterTypeComboBox().valueProperty().addListener(changeListener);
    }

    /***
     * Adds autocomplete behaviour to the text field where filter values are entered
     * @param entries: either list of names or list of card types
     */
    private void addAutocompleteBehaviour(List<String> entries){
        if(oldBinding != null) {
            oldBinding.dispose();
        }
        AutoCompletionBinding binding = TextFields.bindAutoCompletion(view.getFilterValueTextField(), t -> {
            return entries.parallelStream().filter(elem -> {
                return elem.toLowerCase().startsWith(t.getUserText().toLowerCase());
            }).collect(Collectors.toList());
        });
        oldBinding = binding;
    }

    private CardFilter getCardFilter(List<String> cardNames) {
        CardFilter currentFilter = new CardFilter();
        if(cardNames != null) {
            currentFilter.configureNameList(cardNames);
        }

        String filter = (String) view.getFilterTypeComboBox().getValue();

        switch (filter) {
            case "Card Type":
                filter = "type";
                break;
            case "Card Name":
                filter = "name";
                break;
            case "Card Effect":
                filter = "text";
                break;
        }

        String value = view.getFilterValueTextField().getText();
        if(!value.equals("")){
            currentFilter.configureFilterValue(filter, value);
        }
        currentFilter.configureColors(view.getColorSelection().getSelectedFilters());

        if(cardNames != null && !cardNames.isEmpty()) {
            currentFilter.configureNameList(cardNames);
        }

        return currentFilter;
    }

    /***
     * Retrieves the search result from the model (Model) asynchronously and depending on the passed message it
     * either updates the search result table or decklist table.
     * @param result: output of a SearchResultBroadcaster object (see interfaces)
     */
    @Override
    public void onSearchResultRetrieved(SearchResult result) {
        // needs to be run at last such that we have list of card names/types available
        if(firstTimeSearch){
            firstTimeSearch = false;
            addAutocompleteBehaviour(model.getCardNames()); // card names is selected by default
        }

        logger.info(String.format("Search results retrieved for %3s.", result.getMsg()));

        // lambda used in EventHandling. This expression without lamda would have been with an anonymous method:
        //
        // Platform.runLater(new Runnable() {{
        //            @Override
        //            public void run() {..})

        // Planform.runLater required, see
        // https://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-not-on-fx-application-thread
        Platform.runLater(() -> {        // needs to be run at last such that we have list of card names/types available
            indicateSearchInUI(false);

            if(result.getMsg().equals("search")){
                int pageCount = (int) Math.ceil((double)result.getHits()/(double)view.getPageSize());
                view.getPagination().setPageCount(pageCount);
                List<Card> retrievedCards = result.getCards();

                // Sorting cards with Streams
                ObservableList<Card> cards = FXCollections.observableArrayList();
                Arrays.stream(retrievedCards.toArray())
                        .sorted((s1, s2) -> {
                            Card card1 = (Card) s1;
                            Card card2 = (Card) s2;
                            return card1.compareTo(card2);
                        }).forEach(n->cards.add((Card)n));


                view.searchCards.removeAll();
                view.getSearchDisplayTable().setItems(cards);
                view.getSearchDisplayTable().refresh();
                logger.info("Search display table updated.");
            }else{
                ObservableList<Card> deckList = FXCollections.observableArrayList();
                deckList.addAll(result.getCards());
                for (Card currentCard : deckList) {
                    Integer count = currentCard.getCount();
                    currentCard.setCount(count);
                }
                view.getDeckDisplayTable().getItems().clear();
                view.getDeckDisplayTable().getItems().addAll(deckList);
                view.getDeckDisplayTable().refresh();
                logger.info("Deck display table updated.");
            }
        });
    }
}
