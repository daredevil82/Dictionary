package com.quantrix.dictionary.dao;

import java.io.Serializable;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Abstract class that can implement generic DAO functionality should multiple Types become involved in this project
 */
public class AbstractDAO<T extends Serializable> {

    private Class<T> clazz;

    public void setClazz(Class<T> clazz){
        this.clazz = clazz;
    }

}
