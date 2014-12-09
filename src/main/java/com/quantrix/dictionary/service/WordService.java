package com.quantrix.dictionary.service;

import com.quantrix.dictionary.dao.DictionaryDAO;
import com.quantrix.dictionary.dao.IDAO;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.utils.FileIO;
import com.quantrix.dictionary.utils.HttpUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/5/14.
 *
 * Service layer for the creation, update and deletion operations of Word objects
 */
public class WordService implements Service<Word> {

    private static final WordService INSTANCE = new WordService();
    private IDAO idao;
    private HttpUtils httpUtils;

    private WordService(){}

    public static WordService getInstance(){
        return INSTANCE;
    }

    public void setIdao(IDAO idao){
        this.idao = idao;
    }

    public void setHttpUtils(HttpUtils httpUtils){
        this.httpUtils = httpUtils;
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
        int wordId = findAvailableId();
        Word tempWord = new Word(wordId, wordName, wordDefinition);

        idao.create(tempWord);
        Word checkCreate = (Word) idao.get(wordName);

        if (checkCreate != null)
            return true;

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Word entity) {
        idao.update(entity);
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
        return idao.getAll();
    }

    @Override
    public Word get(String query) {
        return (Word) idao.get(query);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Word get(Word query) {
        return (Word) idao.get(query);
    }

    @Override
    public Word get(int id) {
        return  (Word) idao.get(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Word> getResults(String query) {
        List<Word> wordResults = new ArrayList<>();
        Map<String, Word> dictMap = idao.getAll();

        if (query.equals(""))
            return wordResults;

        for (Word word : dictMap.values()){
            if (word.getWordName().toLowerCase().contains(query.toLowerCase()))
                wordResults.add(word);
        }

        return wordResults;
    }


    /**
     *
     * @param query String word search
     * @return String defintions
     *
     * Executes a query to an online definition service.
     * Returns a String or null;
     */
    public String getOnlineDefinition(String query){
        HttpURLConnection connection = httpUtils.sendGet(query);
        return httpUtils.processDictionaryResults(connection);
    }

    /**
     *
     * @param query String
     * @return list of words sorted by edit distance to `query`
     *
     * Executes a getResults(query) operation, and sorts the results.
     */
    public List<Word> sortByEditDistance(String query){
        List<Word> wordList = getResults(query);
        return sortByEditDistance(wordList, query);
    }

    /**
     *
     * @param wordList List of words, from any source
     * @param query String
     * @return List of words sorted by edit distance to `query`
     *
     * Sorts the provided wordList into order of edit distance
     */
    public List<Word> sortByEditDistance(List<Word> wordList, String query){
        int editDistance;

        for (Word word : wordList){
            editDistance = levenshteinDistance(query, word.getWordName());
            word.setEditDistance(editDistance);
        }

        Collections.sort(wordList, Word.editComparator);

        return wordList;
    }

    /**
     *
     * @param s0 String word
     * @param s1 String word
     * @return int edit distance
     *
     * Levenshtein adaptation from http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
     */
    private int levenshteinDistance(String s0, String s1) {
        int length0 = s0.length() + 1,
            length1 = s1.length();

        //array of distances
        int[] cost = new int[length0];
        int[] newCost = new int[length0];

        //initial cost of skipping prefix in string s0
        for (int i = 0; i < length0; i++)
            cost[i] = i;

        //dynamically compute array of distances
        //transformation cost for each letter in s1
        for (int j = 1; j < length1; j++){
            newCost[0] = j;

            //transformation cost for each letter in s0
            for (int i = 1; i < length0; i++){

                //matching current letters in both strings
                int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;

                //compute cost for each transformation
                int costReplace = cost[i - 1] + match;
                int costInsert = cost[i] + 1;
                int costDelete = newCost[i - 1] + 1;

                //keep min cost
                newCost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
            }

            //swap cost/newCost arrays
            int[] swap = cost;
            cost = newCost;
            newCost = swap;
        }

        //the distance is the cost for transforming all letters in both strings
        return cost[length0 - 1];

    }

    /**
     *
     * @return int first avaliable ID from highest number found
     *
     * Convenience method to easily set wordId value on object creation
     */
    private int findAvailableId(){
        int wordId = 0;
        Map<String, Word> dictMap = idao.getAll();
        for (Word word : dictMap.values()){
            if (word.getId() > wordId)
                wordId = word.getId();
        }

        return wordId + 1;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        Service wordService = WordService.getInstance();
        IDAO idao = DictionaryDAO.getInstance();
        FileIO fileIO = FileIO.getInstance();

        idao.setFileIO(fileIO);
        wordService.setIdao(idao);

        Word firstWord = (Word) wordService.get(1);
        System.out.println("First word in dictionary:\n" + firstWord.toString());
        //create new entry, retrieve it in a new reference and print it out
        Word newWord = new Word(idao.getAll().size() + 1, "daoTest", "A DAO Test Execution");
        idao.create(newWord);
        Word retrieveNewWord = (Word) idao.get("daoTest");
        System.out.println("Retrive daoTest\n" + retrieveNewWord.toString());

        Map<String, Word> wordMap = wordService.getMap();

        System.out.println("All values in dictionary:");
        for (Word word : wordMap.values())
            System.out.println(word.toString());

        List<Word> searchResults = wordService.getResults("Test");
        System.out.println("Results from query 'Test':");
        for (Word word : searchResults)
                System.out.println(word.getId() + "\t" + word.getWordName());

        //this should return as null
        Word failedGet = (Word) wordService.get("someteststring");

        //delete the new word created above
        wordService.delete(retrieveNewWord);

        //should return as null
        Word failedReturn = (Word) wordService.get("daoTest");

        try {
            System.out.println(failedGet.toString());
            System.out.print(failedReturn.toString());
        } catch (NullPointerException e){
            System.out.println("Word get retrieved null values");
        }

    }
}
