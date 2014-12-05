package com.quantrix.dictionary.utils;

import com.quantrix.dictionary.domain.Word;

import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 */
public interface IO {

    void writeToDataFile(Map<String, Word> dictMap);
    Map<String, Word> loadDataFile();
}
