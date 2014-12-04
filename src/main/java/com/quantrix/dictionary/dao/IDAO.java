package com.quantrix.dictionary.dao;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Basic DAO interface
 */

import java.util.List;

public interface IDAO<T> {

    T get(String query);
    T get(T query);

    List<T> getAll();

    T create(T entity);
    T update(T entity);

    void delete(T entity);
    void delete(String query);
}
