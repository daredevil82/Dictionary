package com.quantrix.dictionary.domain;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Plain POJO object containing Word, Definition and a couple date time fields.
 *
 * Includes two internal Comparator implementations for collection sorting based on word name and edit distance to
 * a particular word.
 *
 */

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Comparator;

public class Word implements Serializable, Comparable<Word> {

    private int wordId;

    private String wordName;
    private String wordDefinition;
    private DateTime dateCreated;
    private DateTime dateLastUpdated;

    private int editDistance;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy:HH:mm:ss");

    /**
     * Custom comparer for sorting based on wordName values
     */
    public static Comparator<Word> wordComparator = new Comparator<Word>() {
        @Override
        public int compare(Word firstWord, Word secondWord) {
            return firstWord.compareTo(secondWord);
        }
    };

    /**
     * Custom comparer for sorting based on edit distance values
     */
    public static Comparator<Word> editComparator = new Comparator<Word>() {
        @Override
        public int compare(Word o1, Word o2) {
            return o1.getEditDistance() - o2.getEditDistance();
        }
    };

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

    public DateTimeFormatter getDateTimeFormatter(){
        return dateTimeFormatter;
    }

    public int getEditDistance() {
        return editDistance;
    }

    public void setEditDistance(int editDistance) {
        this.editDistance = editDistance;
    }

    @Override
    public String toString(){
        return "Word:\t\t" + wordName + "\nDefinition:\t" + wordDefinition + "\nCreated:\t" + dateTimeFormatter.print(dateCreated) + "\n";
    }

    @Override
    public int compareTo(Word o) {
        return this.wordName.compareTo(o.getWordName());
    }
}
