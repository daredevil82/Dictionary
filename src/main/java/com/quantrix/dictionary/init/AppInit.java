package com.quantrix.dictionary.init;

import com.quantrix.dictionary.controller.DictionaryController;
import com.quantrix.dictionary.gui.DictionaryView;

/**
 * Created by jasonjohns on 12/8/14.
 */
public class AppInit {

    public static void main(String[] args){
        Configuration configuration = new Configuration();
        DictionaryController dictionaryController = configuration.initController();
        DictionaryView dictionaryView = new DictionaryView(dictionaryController);
    }
}
