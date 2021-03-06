package com.quantrix.dictionary.dao;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Basic DAO interface
 */

import com.quantrix.dictionary.utils.IO;

import java.util.Map;

public interface IDAO<T> {

    void setFileIO(IO fileIO);

    T get(String query);
    T get(T query);
    T get(int id);
    Map<String, T> getAll();

    T create(T entity);
    T update(T entity);

    void delete(T entity);
    void delete(String query);
}
