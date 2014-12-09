package com.quantrix.dictionary.init;

import com.quantrix.dictionary.controller.DictionaryController;
import com.quantrix.dictionary.dao.DictionaryDAO;
import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.service.Service;
import com.quantrix.dictionary.service.WordService;
import com.quantrix.dictionary.utils.FileIO;
import com.quantrix.dictionary.utils.HttpUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/8/14.
 */
public class Configuration {

    private static final String CONTENT_TYPE = "text/xml;charset=utf8";
    private static final String URL_QUERY = "http://services.aonaware.com/DictService/DictService.asmx/Define?word=";
    //private static final String DICTIONARY_LOCATION = "com/quantrix/dictionary/data/dictData.dat";
    private static final String DICTIONARY_LOCATION = "dictData.dat";

    public Configuration(){}

    public String getRequestContentType(){
        return CONTENT_TYPE;
    }

    public String getRequestQueryString(){
        return URL_QUERY;
    }

    public String getDictionaryLocation(){
        return DICTIONARY_LOCATION;
    }

    private IDAO initDAO(){
        FileIO fileIO = FileIO.getInstance();
        fileIO.setDictionaryFile(DICTIONARY_LOCATION);
        IDAO idao = DictionaryDAO.getInstance();

        idao.setFileIO(fileIO);

        return idao;
    }

    private Service initService(IDAO idao){
        Service service = WordService.getInstance();
        service.setIdao(idao);
        return service;
    }

    public HttpUtils initHttpUtils(){
        return new HttpUtils(URL_QUERY, CONTENT_TYPE);
    }

    public DictionaryController initController(){
        DictionaryController dictionaryController = DictionaryController.getInstance();

        IDAO idao = initDAO();
        Service service = initService(idao);

        HttpUtils httpUtils = initHttpUtils();
        service.setHttpUtils(httpUtils);

        dictionaryController.setWordService(service);

        return dictionaryController;

    }

    public static void main(String[] args){
        Configuration configuration = new Configuration();
        DictionaryController dictionaryController = configuration.initController();

        dictionaryController.main(null);

    }

}
