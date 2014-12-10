package com.quantrix.dictionary.init;

import com.quantrix.dictionary.controller.DictionaryController;
import com.quantrix.dictionary.dao.DictionaryDAO;
import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.service.Service;
import com.quantrix.dictionary.service.WordService;
import com.quantrix.dictionary.utils.FileIO;
import com.quantrix.dictionary.utils.HttpUtils;
import com.quantrix.dictionary.utils.WordIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/8/14.
 */
public class Configuration {

    private static final String CONTENT_TYPE = "text/xml;charset=utf8";
    private static final String URL_QUERY = "http://services.aonaware.com/DictService/DictService.asmx/Define?word=";
    private static final String DATABASE_NAME = "org.sqlite.JDBC";
    private static final String DATABASE_CONNECTION = "jdbc:sqlite:dictionary.db";

    public Configuration(){}

    public String getRequestContentType(){
        return CONTENT_TYPE;
    }

    public String getRequestQueryString(){
        return URL_QUERY;
    }

    private IDAO initFileDAO(){
        FileIO fileIO = FileIO.getInstance();
        IDAO idao = DictionaryDAO.getInstance();

        idao.setFileIO(fileIO);

        return idao;
    }

    private IDAO initDatabaseDAO(){
        Connection connection = initDataSource();
        WordIO wordIO = WordIO.getInstance();
        wordIO.setConnection(connection);

        IDAO idao = DictionaryDAO.getInstance();
        idao.setWordIO(wordIO);

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

        IDAO idao = initDatabaseDAO();
        Service service = initService(idao);

        HttpUtils httpUtils = initHttpUtils();
        service.setHttpUtils(httpUtils);

        dictionaryController.setWordService(service);

        return dictionaryController;

    }

    private Connection initDataSource(){
        Connection connection = null;

        try {
            Class.forName(DATABASE_NAME);
            connection = DriverManager.getConnection(DATABASE_CONNECTION);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e ){
            e.printStackTrace();
        }

        return connection;
    }


    public static void main(String[] args){
        Configuration configuration = new Configuration();
        DictionaryController dictionaryController = configuration.initController();

        dictionaryController.main(null);

    }

}
