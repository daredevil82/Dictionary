package com.quantrix.dictionary.service;

import com.quantrix.dictionary.dao.IDAO;

import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 */
public interface Service<T> {

    public boolean save(String string1, String string2);
    public boolean update(T entity);
    public void delete(String entity);
    public void delete(T entity);
    public void setIdao(IDAO idao);

    public Map<String, T> getMap();
    public T get(String query);
    public T get(T query);
    public T get(int id);
    public List<T> getResults(String query);
}
