package ml131.de.hdm_stuttgart.mi.datamodel.color;

import ml131.de.hdm_stuttgart.mi.interfaces.Color;

public class Red implements Color {
    @Override
    public String getColor() {
        return "Red";
    }

    // package-private constructor enforces instantiation instantiation via ColorFactory
    Red(){}

    // Two color are equal if they are instances of the same class, this is necessary to make
    // colorList.contains(colorobject) work, see also SearchEngine.checkColors
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Red;
    }

}
