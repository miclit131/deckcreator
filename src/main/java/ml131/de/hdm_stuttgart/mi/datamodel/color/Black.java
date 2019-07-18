package ml131.de.hdm_stuttgart.mi.datamodel.color;

import ml131.de.hdm_stuttgart.mi.interfaces.Color;

public class Black implements Color {
    @Override
    public String getColor() {
        return "Black";
    }

    // Two color are equal if they are instances of the same class, this is necessary to make
    // colorList.contains(colorobject) work, see also SearchEngine.checkColors
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Black;
    }

    // package-private constructor enforces instantiation instantiation via ColorFactory
    Black(){}


}
