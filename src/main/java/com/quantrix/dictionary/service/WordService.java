package com.quantrix.dictionary.service;

import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;

import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 *
 * Service layer for the creation, update and deletion operations of Word objects
 */
public class WordService implements Service<Word> {

    private static final WordService INSTANCE = new WordService();
    private Service searchService;
    private IDAO idao;

    private WordService(){
        searchService = SearchService.getInstance();
    }

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
        return searchService.getMap();
    }

    @Override
    public Word get(String query) {
        return (Word) searchService.get(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Word get(Word query) {
        return (Word) searchService.get(query);
    }

    @Override
    public Word get(int id) {
        return  (Word) searchService.get(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Word> getResults(String query) {
        return searchService.getResults(query);
    }
}
