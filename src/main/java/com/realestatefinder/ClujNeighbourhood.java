package com.realestatefinder;

import java.util.Arrays;

public enum ClujNeighbourhood {
    ANDREI_MURESANU("ANDREI MURESANU"),
    AUREL_VLAICU("AUREL VLAICU"),
    BACIU("BACIU"),
    BORHANCI("BORHANCI"),
    BULGARIA("BULGARIA"),
    BUNA_ZIUA("BUNA ZIUA"),
    CALEA_TURZII("CALEA TURZII"),
    CAMPULUI("CAMPULUI"),
    DAMBUL_ROTUND("DAMBUL ROTUND"),
    EUROPA("EUROPA"),
    FAGET("FAGET"),
    GARA("GARA"),
    GHEORGHENI("GHEORGHENI"),
    GRIGORESCU("GRIGORESCU"),
    GRUIA("GRUIA"),
    HASDEU("HASDEU"),
    HOREA("HOREA"),
    INTRE_LACURI("INTRE LACURI"),
    IRIS("IRIS"),
    MANASTUR("MANASTUR"),
    MARASTI("MARASTI"),
    PLOPILOR("PLOPILOR"),
    SOMESENI("SOMESENI"),
    SUD("SUD"),
    ZORILOR("ZORILOR"),
    SEMICENTRAL("SEMICENTRAL"),
    ULTRACENTRAL("ULTRACENTRAL"),
    CENTRAL("CENTRAL"),
    UNKNOWN("");

    private final String neighbourhoodName;

    ClujNeighbourhood(String neighbourhoodName) {
        this.neighbourhoodName = neighbourhoodName;
    }

    public static ClujNeighbourhood getNeighbourhood(String neighbourhoodName) {
        String upperNeighbourhoodName = neighbourhoodName.toUpperCase();
        return Arrays.stream(values()).filter(n -> n.neighbourhoodName.equals(upperNeighbourhoodName)).findFirst().orElse(UNKNOWN);
    }

    public String getNeighbourhoodName() {
        return neighbourhoodName;
    }
}
