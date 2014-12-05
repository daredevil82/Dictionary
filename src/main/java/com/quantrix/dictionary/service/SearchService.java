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
 */
public class SearchService implements Service<Word> {

    private static final SearchService INSTANCE = new SearchService();
    private IDAO idao;
    private Service wordService;

    private SearchService(){
        wordService = WordService.getInstance();
    }

    public static SearchService getInstance(){
        return INSTANCE;
    }

    @Override
    public boolean save(String string1, String string2) {
        return wordService.save(string1, string2);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean update(Word entity) {
        return wordService.update(entity);
    }

    @Override
    public void delete(String entity) {
        wordService.delete(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void delete(Word entity) {
        wordService.delete(entity);
    }

    @Override
    public void setIdao(IDAO idao) {
        wordService.setIdao(idao);
        this.idao = idao;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Word> getMap() {
        return idao.getAll();
    }

    @Override
    public Word get(String query) {
        return (Word) idao.get(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Word get(Word query) {
        return (Word) idao.get(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Word get(int id) {
        return (Word) idao.get(id);
    }

    /**
     *
     * @param query String
     * @return List
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Word> getResults(String query){
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

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        Service searchService = SearchService.getInstance();
        IDAO idao = DictionaryDAO.getInstance();
        FileIO fileIO = FileIO.getInstance();

        idao.setFileIO(fileIO);
        searchService.setIdao(idao);

        //get and print first entry in data file
        Word firstWord = (Word) searchService.get(1);
        System.out.println("First word in dictionary:\n" + firstWord.toString());

        //create new entry, retrieve it in a new reference and print it out
        Word newWord = new Word(idao.getAll().size() + 1, "daoTest", "A DAO Test Execution");
        idao.create(newWord);
        Word retrieveNewWord = (Word) idao.get("daoTest");
        System.out.println("Retrive daoTest\n" + retrieveNewWord.toString());

        Map<String, Word> wordMap = searchService.getMap();

        System.out.println("All values in dictionary:");
        for (Word word : wordMap.values())
            System.out.println(word.toString());

        //this should return as null
        Word failedGet = (Word) searchService.get("someteststring");

        //delete the new word created above
        searchService.delete(retrieveNewWord);

        //should return as null
        Word failedReturn = (Word) searchService.get("daoTest");

        try {
            System.out.println(failedGet.toString());
            System.out.print(failedReturn.toString());
        } catch (NullPointerException e){
            System.out.println("Word get retrieved null values");
        }


    }
}
