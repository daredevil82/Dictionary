package com.quantrix.dictionary.utils;

import com.quantrix.dictionary.domain.Word;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasonjohns on 12/9/14.
 */
public class WordIO {

    private static final WordIO INSTANCE = new WordIO();
    private Connection connection;
    private PreparedStatement preparedStatement;


    private WordIO(){}

    public static WordIO getInstance(){
        return INSTANCE;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean insertData(Word word){
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO 'word' VALUES(?, ?, ?, ?), ?;");
            preparedStatement.setInt(1, word.getId());
            preparedStatement.setString(2, word.getWordName());
            preparedStatement.setString(3, word.getWordDefinition());
            preparedStatement.setDate(4, Date.valueOf(word.getDateCreated().toString()));
            preparedStatement.setDate(5, Date.valueOf(word.getDateLastUpdated().toString()));


            preparedStatement.execute();
            connection.commit();


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
        return true;
    }

    public Map<String, Word> getAllWords(){
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM 'word';");
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, Word> dictMap = new HashMap<>();
            while (resultSet.next()){
                Word word = new Word(resultSet.getInt("id"), resultSet.getString("word_name"), resultSet.getString("word_definition"));
                word.setDateCreated(resultSet.getString("date_created"));
                word.setDateLastUpdated(resultSet.getString("date_updated"));

                dictMap.put(word.getWordName(), word);
            }

            return dictMap;
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
    }

    public boolean deleteWord(Word word){
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

    public boolean updateWord(Word word){
        try {
            preparedStatement = connection.prepareStatement("UPDATE 'word' SET 'word_name' = ?, 'word_definition' = ?, 'date_updated' = ? WHERE 'id' = ?;");
            preparedStatement.setString(1, word.getWordName());
            preparedStatement.setString(2, word.getWordDefinition());
            preparedStatement.setDate(3, Date.valueOf(word.getDateCreated().toString()));
            preparedStatement.setInt(4, word.getId());

            preparedStatement.execute();
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
}
