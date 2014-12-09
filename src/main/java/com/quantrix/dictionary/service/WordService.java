package com.quantrix.dictionary.service;

import com.quantrix.dictionary.dao.DictionaryDAO;
import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.utils.FileIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 *
 * Service layer for the creation, update and deletion operations of Word objects
 */
public class WordService implements Service<Word> {

    private static final WordService INSTANCE = new WordService();
    private IDAO idao;

    private WordService(){}

    public static WordService getInstance(){
        return INSTANCE;
    }

    public void setIdao(IDAO idao){
        this.idao = idao;
    }

    /**
     *
     * @param wordName String
     * @param wordDefinition String
     * @return boolean
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean save(String wordName, String wordDefinition){
        Word tempWord = new Word(idao.getAll().size() + 1, wordName, wordDefinition);

        idao.create(tempWord);
        Word checkCreate = (Word) idao.get(wordName);

        if (checkCreate != null)
            return true;

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean update(Word entity) {
        return (boolean)idao.update(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void delete(String entity) {
        idao.delete(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void delete(Word entity) {
        idao.delete(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Word> getMap() {
        return idao.getAll();
    }

    @Override
    public Word get(String query) {
        /*
        Word queryResult = (Word) idao.get(query);
        if (queryResult == null)
            queryResult = getOnlineDefinition(query);
        */

        return (Word) idao.get(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Word get(Word query) {
        return (Word) idao.get(query);
    }

    @Override
    public Word get(int id) {
        return  (Word) idao.get(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Word> getResults(String query) {
        List<Word> wordResults = new ArrayList<>();
        Map<String, Word> dictMap = idao.getAll();

        if (query.equals(""))
            return wordResults;

        for (Word word : dictMap.values()){
            if (word.getWordName().toLowerCase().contains(query.toLowerCase()))
                wordResults.add(word);
        }

        return wordResults;
    }

    public Word getOnlineDefinition(Word word){
        return getOnlineDefinition(word.getWordName());
    }

    public Word getOnlineDefinition(String query){
        return null;
    }

    public int levenshteinDistance(String s0, String s1) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        Service wordService = WordService.getInstance();
        IDAO idao = DictionaryDAO.getInstance();
        FileIO fileIO = FileIO.getInstance();

        idao.setFileIO(fileIO);
        wordService.setIdao(idao);

        Word firstWord = (Word) wordService.get(1);
        System.out.println("First word in dictionary:\n" + firstWord.toString());
        //create new entry, retrieve it in a new reference and print it out
        Word newWord = new Word(idao.getAll().size() + 1, "daoTest", "A DAO Test Execution");
        idao.create(newWord);
        Word retrieveNewWord = (Word) idao.get("daoTest");
        System.out.println("Retrive daoTest\n" + retrieveNewWord.toString());

        Map<String, Word> wordMap = wordService.getMap();

        System.out.println("All values in dictionary:");
        for (Word word : wordMap.values())
            System.out.println(word.toString());

        List<Word> searchResults = wordService.getResults("Test");
        System.out.println("Results from query 'Test':");
        for (Word word : searchResults)
                System.out.println(word.getId() + "\t" + word.getWordName());

        //this should return as null
        Word failedGet = (Word) wordService.get("someteststring");

        //delete the new word created above
        wordService.delete(retrieveNewWord);

        //should return as null
        Word failedReturn = (Word) wordService.get("daoTest");

        try {
            System.out.println(failedGet.toString());
            System.out.print(failedReturn.toString());
        } catch (NullPointerException e){
            System.out.println("Word get retrieved null values");
        }

    }
}
