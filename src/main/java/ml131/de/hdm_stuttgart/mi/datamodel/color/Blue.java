package ml131.de.hdm_stuttgart.mi.datamodel.color;

import ml131.de.hdm_stuttgart.mi.interfaces.Color;

class Blue implements Color {

    @Override
    public String getColor() {
        return "Blue";
    }

    // package-private constructor enforces instantiation instantiation via ColorFactory
    Blue(){}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Blue;
    }

}
