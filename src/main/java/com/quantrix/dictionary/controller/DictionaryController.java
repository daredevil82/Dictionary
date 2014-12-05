package com.quantrix.dictionary.controller;

import com.quantrix.dictionary.dao.DictionaryDAO;
import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.service.SearchService;
import com.quantrix.dictionary.service.Service;
import com.quantrix.dictionary.service.WordService;
import com.quantrix.dictionary.utils.FileIO;

import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 */
public class DictionaryController {

    private static final DictionaryController INSTANCE = new DictionaryController();
    private Service wordService;
    private Service searchService;

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

    public void setSearchService(Service searchService){
        this.searchService = searchService;
    }

    /**
     *
     * @return Map dictionary words
     */
    @SuppressWarnings(value = "unchecked")
    public Map<String, Word> getMap(){
        return searchService.getMap();
    }

    /**
     *
     * @param query String
     * @return Word
     */
    public Word getWord(String query){
        return (Word) searchService.get(query);
    }

    /**
     *
     * @param word Word
     * @return Word
     */
    @SuppressWarnings("unchecked")
    public Word getWord(Word word){
        return (Word) searchService.get(word);

    }

    /**
     *
     * @param wordName String
     * @param wordDefinition String
     * @return boolean
     */
    @SuppressWarnings(value = "unchecked")
    public boolean saveWord(String wordName, String wordDefinition){
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
        return null;
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
        Service searchService = SearchService.getInstance();
        IDAO idao = DictionaryDAO.getInstance();
        FileIO fileIO = FileIO.getInstance();

        idao.setFileIO(fileIO);
        wordService.setIdao(idao);
        searchService.setIdao(idao);

        dictionaryController.setWordService(wordService);
        dictionaryController.setSearchService(searchService);

        Map<String, Word> dictMap = dictionaryController.getMap();

        System.out.println("Dictionary Values:");
        for (Word word : dictMap.values()){
            System.out.println(word.toString());
        }

        List<Word> searchResults = dictionaryController.searchDictionary("Test");
        System.out.println("Results of searching for 'Test':");
        for (Word word : searchResults)
            System.out.println(word.getId() + "\t" + word.getWordName());





    }
}
