package com.quantrix.dictionary.domain;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Plain POJO object containing Word, Definition and a couple date time fields.
 *
 */

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

public class Word implements Serializable{

    private int wordId;

    //immutable - all update oeprations should not modify this field.
    private String wordName;
    private String wordDefinition;
    private DateTime dateCreated;
    private DateTime dateLastUpdated;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy:HH:mm:ss");

    public Word(){}

    public Word(int wordId, String wordName){
        this.wordId = wordId;
        this.wordName = wordName;
        dateCreated = new DateTime();
        dateLastUpdated = this.dateCreated;
    }

    public Word(int wordId, String wordName, String wordDefinition){
        this.wordId = wordId;
        this.wordName = wordName;
        this.wordDefinition = wordDefinition;
        dateCreated = new DateTime();
        dateLastUpdated = this.dateCreated;
    }

    public Word(int wordId, String wordName, String wordDefinition, String dateCreated, String dateLastUpdated){
        this.wordId = wordId;
        this.wordName = wordName;
        this.wordDefinition = wordDefinition;
        this.dateCreated = new DateTime(dateCreated);
        this.dateLastUpdated = new DateTime(dateLastUpdated);
    }

    public int getId() {
        return wordId;
    }

    public void setId(int wordId) {
        this.wordId = wordId;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getWordDefinition() {
        return wordDefinition;
    }

    public void setWordDefinition(String wordDefinition) {
        this.wordDefinition = wordDefinition;
    }

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated){
        this.dateCreated = new DateTime(dateCreated);
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public DateTime getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(String dateLastUpdated){
        this.dateLastUpdated = new DateTime(dateLastUpdated);
    }

    public void setDateLastUpdated(DateTime dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    @Override
    public String toString(){
        return "Word:\t\t" + wordName + "\nDefinition:\t" + wordDefinition + "\nCreated:\t" + dateTimeFormatter.print(dateCreated) + "\n";
    }
}
