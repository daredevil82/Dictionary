package com.quantrix.dictionary.controller;

import com.quantrix.dictionary.dao.DictionaryDAO;
import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.service.Service;
import com.quantrix.dictionary.service.WordService;
import com.quantrix.dictionary.utils.FileIO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 */
public class DictionaryController {

    private static final DictionaryController INSTANCE = new DictionaryController();
    private Service wordService;

    /**
     *
     * @return DictionaryController instance
     */
    public static DictionaryController getInstance(){
        return INSTANCE;
    }

    public void setWordService(Service wordService){
        this.wordService = wordService;
    }

    /**
     *
     * @return Map dictionary words
     */
    @SuppressWarnings(value = "unchecked")
    public Map<String, Word> getMap(){
        return wordService.getMap();
    }


    /**
     *
     * @return sorted list of words
     */
    @SuppressWarnings("unchecked")
    public List<Word> getSortedList(){
        Map<String, Word> wordMap = wordService.getMap();

        List<Word> sortedWords = new ArrayList<>(wordMap.values());
        Collections.sort(sortedWords, Word.wordComparator);

        return sortedWords;
    }

    public List<Word> searchWords(String query){
        List<Word> wordList = wordService.getResults(query);
        Collections.sort(wordList, Word.wordComparator);

        return wordList;
    }

    /**
     *
     * @param query String
     * @return Word
     */
    public Word getWord(String query){
        return (Word) wordService.get(query);
    }

    /**
     *
     * @param word Word
     * @return Word
     */
    @SuppressWarnings("unchecked")
    public Word getWord(Word word){
        return (Word) wordService.get(word);

    }

    /**
     *
     * @param id int Word Id
     * @return Word
     */
    @SuppressWarnings("unchecked")
    public Word getWord(int id){
        return (Word) wordService.get(id);
    }

    /**
     *
     * @param wordName String
     * @param wordDefinition String
     * @return boolean
     */
    @SuppressWarnings(value = "unchecked")
    public boolean save(String wordName, String wordDefinition){
        return wordService.save(wordName, wordDefinition);
    }

    /**
     *
     * @param word Word
     * @return boolean
     *
     * Updates a Word entity
     */
    @SuppressWarnings("unchecked")
    public boolean updateWord(Word word){
        return wordService.update(word);
    }

    /**
     *
     * @param query String
     * @return List of matches
     */
    @SuppressWarnings(value = "unchecked")
    public List<Word> searchDictionary(String query){
        return wordService.getResults(query);
    }

    /**
     *
     * @param word String
     *
     * Delete a word based on its string value
     */
    public void deleteWord(String word){
        wordService.delete(word);
    }

    /**
     *
     * @param word Word
     *
     * Delete a word based on a entity instance
     */
    @SuppressWarnings("unchecked")
    public void deleteWord(Word word){
        wordService.delete(word);
    }

    public static void main(String[] args){
        DictionaryController dictionaryController = DictionaryController.getInstance();
        Service wordService = WordService.getInstance();
        IDAO idao = DictionaryDAO.getInstance();
        FileIO fileIO = FileIO.getInstance();

        idao.setFileIO(fileIO);
        wordService.setIdao(idao);

        dictionaryController.setWordService(wordService);

        Word firstWord = (Word) wordService.get(1);
        System.out.println("First word in dictionary:\n" + firstWord.toString());

        dictionaryController.save("daoTest", "A DAO Test Execution");

        Word retrieveNewWord = dictionaryController.getWord("daoTest");
        System.out.println("Retrive daoTest\n" + retrieveNewWord.toString());

        Map<String, Word> dictMap = dictionaryController.getMap();

        System.out.println("Dictionary Values:");
        for (Word word : dictMap.values()){
            System.out.println(word.toString());
        }

        List<Word> searchResults = dictionaryController.searchDictionary("Test");
        System.out.println("Results of searching for 'Test':");
        for (Word word : searchResults)
            System.out.println(word.getId() + "\t" + word.getWordName());

        //this should return as null
        Word failedGet = dictionaryController.getWord("someteststring");

        //delete the new word created above
        dictionaryController.deleteWord(retrieveNewWord);

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
