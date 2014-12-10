package com.quantrix.dictionary.utils;

import java.util.Map;

/**
 * Created by jasonjohns on 12/10/14.
 */
public interface DatabaseIO<T> {

    void setDatabaseName(String databaseName);
    void setDatabaseConnection(String databaseConnection);

    boolean insert(T entity);
    Map<String, T> getAll();
    boolean delete(T entity);
    boolean update(T entity);

}
