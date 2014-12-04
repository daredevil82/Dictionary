package com.quantrix.dictionary.dao;

import com.quantrix.dictionary.domain.Word;

import java.util.List;

/**
 * Created by jasonjohns on 12/4/14.
 */
public class DictionaryDAO extends AbstractDAO<Word> implements IDAO<Word> {

    @Override
    public Word get(String query) {
        return null;
    }

    @Override
    public Word get(Word query) {
        return null;
    }

    @Override
    public List<Word> getAll() {
        return null;
    }

    @Override
    public Word create(Word entity) {
        return null;
    }

    @Override
    public Word update(Word entity) {
        return null;
    }

    @Override
    public void delete(Word entity) {

    }

    @Override
    public void delete(String query) {

    }
}
