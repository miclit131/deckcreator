package ml131.de.hdm_stuttgart.mi.viewcomponents;

import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ml131.de.hdm_stuttgart.mi.datamodel.color.ColorFactory;
import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyInvalidColorException;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorFilterView extends HBox {

    private Map<Color, CheckBox> checkboxes;
    private CheckBox all;
    private ColorFactory colorFactory;

    public ColorFilterView() throws LogfriendlyInvalidColorException {
        colorFactory = new ColorFactory();

        checkboxes = new HashMap<>();
        CheckBox black = new CheckBox("Black");
        getChildren().add(black);
        checkboxes.put(colorFactory.getColor("black"), black);

        CheckBox blue = new CheckBox("Blue");
        getChildren().add(blue);
        checkboxes.put(colorFactory.getColor("blue"), blue);

        CheckBox white = new CheckBox("White");
        getChildren().add(white);
        checkboxes.put(colorFactory.getColor("white"), white);

        CheckBox colorless = new CheckBox("Colorless");
        getChildren().add(colorless);
        checkboxes.put(colorFactory.getColor("colorless"), colorless);

        CheckBox red = new CheckBox("Red");
        getChildren().add(red);
        checkboxes.put(colorFactory.getColor("red"), red);

        CheckBox green = new CheckBox("Green");
        getChildren().add(green);
        checkboxes.put(colorFactory.getColor("green"), green);

        all = new CheckBox("Select all");
        all.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ke) {
                if(all.isSelected()){
                    toggleAll(true);
                }else{
                    toggleAll(false);
                }

            }
        });
        getChildren().add(all);

        setSpacing(10);
        selectAllCheckboxes();
    }

    public List<Color> getSelectedFilters(){
        List<Color> selectedColors = new ArrayList<>();
        for(Map.Entry<Color, CheckBox> entry : checkboxes.entrySet()){
            if(entry.getValue().isSelected()){
                selectedColors.add(entry.getKey());
            }
        }
        return selectedColors;
    }

    private void selectAllCheckboxes(){
        all.setSelected(true);
        for(Map.Entry<Color, CheckBox> entry : checkboxes.entrySet()){
            entry.getValue().setSelected(true);
        }
    }

    public boolean isSelectedGreen() throws LogfriendlyInvalidColorException {
        return checkboxes.get(colorFactory.getColor("green")).isSelected();
    }

    public boolean isSelectedBlack() throws LogfriendlyInvalidColorException{
        return checkboxes.get(colorFactory.getColor("black")).isSelected();
    }

    public boolean isSelectedBlue() throws LogfriendlyInvalidColorException{
        return checkboxes.get(colorFactory.getColor("blue")).isSelected();
    }

    public boolean isSelectedColorless() throws LogfriendlyInvalidColorException{
        return checkboxes.get(colorFactory.getColor("colorless")).isSelected();
    }

    public boolean isSelectedWhite() throws LogfriendlyInvalidColorException{
        return checkboxes.get(colorFactory.getColor("white")).isSelected();
    }

    public boolean isSelectedRed() throws LogfriendlyInvalidColorException{
        return checkboxes.get(colorFactory.getColor("red")).isSelected();
    }

    private void toggleAll(boolean select){
        for(Map.Entry<Color, CheckBox> entry : checkboxes.entrySet()){
            entry.getValue().setSelected(select);
        }
    }

}
