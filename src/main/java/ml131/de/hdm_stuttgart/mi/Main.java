package ml131.de.hdm_stuttgart.mi;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ml131.de.hdm_stuttgart.mi.core.Controller;
import ml131.de.hdm_stuttgart.mi.core.Model;
import ml131.de.hdm_stuttgart.mi.core.View;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyNoInternetConnectionException;
import ml131.de.hdm_stuttgart.mi.util.InternetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Logger logger = LogManager.getLogger(Main.class);

        try {
            InternetUtil.pingURL("http://google.com",1000);
        } catch (LogfriendlyNoInternetConnectionException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Cannot launch application with no open internet connection.");
            alert.showAndWait();
            logger.error(e.getMessage());
            System.exit(-1);
        }

        View view = new View(stage);
        Model model = new Model();
        new Controller(model, view);
    }
    public static void main(String[] args) {
        launch(args);
    }
}