package com.bsuir.neural_network.search_for_similar_photos.enumeration;

public enum SearchScore {
    NINETY_FIVE("NINETY_FIVE", 10),
    NINETY("NINETY", 9),
    EIGHTY("EIGHTY", 8),
    SEVENTY("SEVENTY", 7),
    SIXTY("SIXTY", 6),
    FIFTY("FIFTY", 5),
    FORTY("FORTY", 4),
    THIRTY("THIRTY", 3),
    TWENTY("TWENTY", 2),
    TEN("TEN", 1);

    private final String displayValue;
    private final int value;

    SearchScore(String displayValue, int value){
        this.displayValue = displayValue;
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    @Override
    public String toString(){
        return displayValue;
    }
}
