package com.quantrix.dictionary.dao;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Basic DAO interface
 */

import java.util.List;
import java.util.Map;

public interface IDAO<T> {

    T get(String query);
    T get(T query);

    Map<String, T> getAll();

    T create(T entity);
    T update(T entity);

    void delete(T entity);
    void delete(String query);
}
