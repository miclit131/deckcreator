package ml131.de.hdm_stuttgart.mi.datamodel.color;

import ml131.de.hdm_stuttgart.mi.exceptions.LogfriendlyInvalidColorException;
import ml131.de.hdm_stuttgart.mi.interfaces.Color;

import java.util.ArrayList;
import java.util.List;


/***
 * Helper class that maps color shortcuts into Color enum values and vice versa. This is necessary when parsing
 * the class, see also SearchEngine.parseColors.
 */
public class ColorFactory {

    private List<Color> colors;

    public ColorFactory(){
        colors = new ArrayList<>();
        colors.add(new Red());
        colors.add(new Green());
        colors.add(new Blue());
        colors.add(new Black());
        colors.add(new White());
        colors.add(new Colorless());
    }

    public Color getColor(char shortcut) throws LogfriendlyInvalidColorException {
        switch (shortcut){
            case 'R':
                return colors.get(0);
            case 'G':
                return colors.get(1);
            case 'U':
                return colors.get(2);
            case 'B':
                return colors.get(3);
            case 'W':
                return colors.get(4);
        }
        throw new LogfriendlyInvalidColorException(String.format("%s is not a valid color", shortcut));
    }

    public Color getColor(String name) throws LogfriendlyInvalidColorException {
        if(name.length() == 1){
           return getColor(name.charAt(0));
        }

        switch (name.toLowerCase()){
            case "red":
                return colors.get(0);
            case "green":
                return colors.get(1);
            case "blue":
                return colors.get(2);
            case "black":
                return colors.get(3);
            case "white":
                return colors.get(4);
            case "colorless":
                return colors.get(5);
        }
        throw new LogfriendlyInvalidColorException(String.format("%s is not a valid color", name));
    }

}

// Old implementation with Color Enum
//    public static final HashMap<Color, String> colorToShortcut;
//    public static final HashMap<String, Color> shortcutToColor;
//
//    static{
//        colorToShortcut = new HashMap<>();
//        colorToShortcut.put(Color.RED, "R");
//        colorToShortcut.put(Color.GREEN, "G");
//        colorToShortcut.put(Color.BLUE, "U");
//        colorToShortcut.put(Color.BLACK, "B");
//        colorToShortcut.put(Color.WHITE, "W");
//
//        shortcutToColor = new HashMap<>();
//        for(Map.Entry<Color, String> entry : colorToShortcut.entrySet()){
//            shortcutToColor.put(entry.getValue(), entry.getKey());
//        }
//    }
//public enum Color {
//    RED,
//    BLUE,
//    BLACK,
//    WHITE,
//    GREEN,
//    COLORLESS,
//}
