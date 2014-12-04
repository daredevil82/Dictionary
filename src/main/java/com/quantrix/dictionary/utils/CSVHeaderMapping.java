package com.quantrix.dictionary.utils;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Static class to have CSV column names mapped to a distinct variable
 *
 */
public final class CSVHeaderMapping {

    public static final String[] FILE_HEADER = {"ID", "Word", "Definition", "CreateDate", "UpdateDate"};

    public static final String WORD_ID = "ID";
    public static final String WORD_NAME = "Word";
    public static final String WORD_DEFINITION = "Definition";
    public static final String WORD_CREATE_DATE = "CreateDate";
    public static final String WORD_UPDATE_DATE = "UpdateDate";

    private CSVHeaderMapping(){}

}
