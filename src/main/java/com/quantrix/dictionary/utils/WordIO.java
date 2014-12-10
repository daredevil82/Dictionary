package com.quantrix.dictionary.utils;

import com.quantrix.dictionary.domain.Word;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasonjohns on 12/9/14.
 */
public class WordIO implements DatabaseIO<Word> {

    private static final WordIO INSTANCE = new WordIO();

    private PreparedStatement preparedStatement;
    private String databaseName, databaseConnection;


    private WordIO(){}

    public static WordIO getInstance(){
        return INSTANCE;
    }

    @Override
    public void setDatabaseName(String databaseName){
        this.databaseName = databaseName;
    }

    @Override
    public void setDatabaseConnection(String databaseConnection){
        this.databaseConnection = databaseConnection;
    }

    private Connection getConnection(){
        Connection connection = null;

        try {
            Class.forName(databaseName);
            connection = DriverManager.getConnection(databaseConnection);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e ){
            e.printStackTrace();
        }

        return connection;
    }

    @Override
    public boolean insert(Word word){

        Connection connection = getConnection();
        int queryResult;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO 'word'('id', 'word_name', 'word_definition','date_created', 'date_updated') VALUES(?, ?, ?, ?, ?);");
            preparedStatement.setInt(1, word.getId());
            preparedStatement.setString(2, word.getWordName());
            preparedStatement.setString(3, word.getWordDefinition());
            preparedStatement.setString(4, word.getDateTimeFormatter().print(word.getDateCreated()));
            preparedStatement.setString(5, word.getDateTimeFormatter().print(word.getDateLastUpdated()));

            connection.setAutoCommit(false);
            queryResult = preparedStatement.executeUpdate();
            connection.setAutoCommit(true);


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return queryResult == 1;
    }

    @Override
    public Map<String, Word> getAll(){
        Connection connection = getConnection();
        Map<String, Word> dictMap;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM 'word';");
            ResultSet resultSet = preparedStatement.executeQuery();
            dictMap = new HashMap<>();
            while (resultSet.next()){
                Word word = new Word(resultSet.getInt("id"), resultSet.getString("word_name"), resultSet.getString("word_definition"));
                word.setDateCreated(resultSet.getString("date_created"));
                word.setDateLastUpdated(resultSet.getString("date_updated"));

                dictMap.put(word.getWordName().toLowerCase(), word);
            }

            resultSet.close();
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return dictMap;
    }

    @Override
    public boolean delete(Word word){
        Connection connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM 'word' WHERE 'id' = ?;");
            preparedStatement.setInt(1, word.getId());

            preparedStatement.executeUpdate();
            connection.commit();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean update(Word word){
        Connection connection = getConnection();
        int queryResult;
        try {
            preparedStatement = connection.prepareStatement("UPDATE 'word' SET 'word_name' = ?, 'word_definition' = ?, 'date_updated' = ? WHERE 'id' = ?");
            preparedStatement.setString(1, word.getWordName());
            preparedStatement.setString(2, word.getWordDefinition());
            preparedStatement.setString(3, word.getDateTimeFormatter().print(word.getDateLastUpdated()));
            preparedStatement.setInt(4, word.getId());

            queryResult = preparedStatement.executeUpdate();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return queryResult == 1;
    }
}
