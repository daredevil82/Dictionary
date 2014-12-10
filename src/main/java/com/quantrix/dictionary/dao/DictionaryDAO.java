package com.quantrix.dictionary.dao;

import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.utils.FileIO;
import com.quantrix.dictionary.utils.IO;
import com.quantrix.dictionary.utils.WordIO;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/4/14.
 */
public class DictionaryDAO extends AbstractDAO<Word> implements IDAO<Word> {

    private Map<String, Word> dictMap;
    private static final DictionaryDAO INSTANCE = new DictionaryDAO();
    private IO fileIO;
    private WordIO wordIO;

    private DictionaryDAO(){}

    public static DictionaryDAO getInstance(){
        return INSTANCE;
    }

    public void setFileIO(IO fileIO){
        this.fileIO = fileIO;
        initDictMap();
    }

    public void setWordIO(WordIO wordIO){
        this.wordIO = wordIO;
        initDictMap();
    }

    /**
     *
     * @param query String
     * @return Word
     *
     * Executes a search operation for a given String query
     */
    @Override
    public Word get(String query) {

        return dictMap.get(query.toLowerCase());

    }

    /**
     *
     * @param query Word
     * @return Word
     *
     * Executes a query operation for a given Word object.  Convenience wrapper for get(String query)
     */
    @Override
    public Word get(Word query) {
        return get(query.getWordName().toLowerCase());
    }

    /**
     *
     * @param wordId int
     * @return Word or null
     *
     * Executes a local search query for a word based on its ID value
     */
    @Override
    public Word get(int wordId){
        for (Word word : dictMap.values()){
            if (word.getId() == wordId)
                return word;
        }

        return null;
    }

    @Override
    public Map<String, Word> getAll() {
        return dictMap;
    }

    /**
     *
     * @param entity Word
     * @return Word
     *
     * Creates a new Word object in the map and writes to file.  If a wordName already exists, it executes an update operation.
     */
    @Override
    public Word create(Word entity) {

        if (dictMap.containsKey(entity.getWordName().toLowerCase())){
            return update(entity);
        } else {
            dictMap.put(entity.getWordName().toLowerCase(), entity);
            wordIO.deleteWord(entity);
            return entity;
        }
    }

    /**
     *
     * @param entity Word
     * @return Word
     *
     * Updates a Word definition and updateTime fields, and writes to file.  If wordName does not exist, executes a create operation.
     */
    @Override
    public Word update(Word entity) {

        if (!dictMap.containsKey(entity.getWordName().toLowerCase())){
            return create(entity);
        } else {
            Word updateInstance = dictMap.get(entity.getWordName().toLowerCase());
            updateInstance.setWordDefinition(entity.getWordDefinition());
            updateInstance.setDateLastUpdated(new DateTime());
            dictMap.put(entity.getWordName().toLowerCase(), updateInstance);
            wordIO.updateWord(updateInstance);
            return updateInstance;
        }
    }

    /**
     *
     * @param entity Word
     *
     * Executes a delete operation and updates the file contents. Convenience wrapper for delete(String query)
     */
    @Override
    public void delete(Word entity) {
        delete(entity.getWordName());
    }

    /**
     *
     * @param query String
     *
     * Executes a delete operation from the map and updates the file contents
     */
    @Override
    public void delete(String query) {
        Word word = dictMap.get(query);
        dictMap.remove(query);
        wordIO.deleteWord(word);
    }

    private void writeToFile(){
        fileIO.writeToDataFile(dictMap);
    }

    private void initDictMap(){
        dictMap = wordIO.getAllWords();
    }

    public static void main(String[] args){
        DictionaryDAO dao = DictionaryDAO.getInstance();
        FileIO fileIO = FileIO.getInstance();
        dao.setFileIO(fileIO);
        //dao.initDictMap();

        //get and print first entry in data file
        Word firstWord = dao.get(1);
        System.out.println("First word in dictionary:\n" + firstWord.toString());

        //create new entry, retrieve it in a new reference and print it out
        Word newWord = new Word(dao.dictMap.size() + 1, "daoTest", "A DAO Test Execution");
        dao.create(newWord);
        Word retrieveNewWord = dao.get("daoTest");
        System.out.println("Retrive daoTest\n" + retrieveNewWord.toString());

        Map<String, Word> wordMap = dao.getAll();

        System.out.println("All values in dictionary:");
        for (Word word : wordMap.values())
            System.out.println(word.toString());

        //this should return as null
        Word failedGet = dao.get("someteststring");

        //delete the new word created above
        dao.delete(retrieveNewWord);

        //should return as null
        Word failedReturn = dao.get("daoTest");

        try {
            System.out.println(failedGet.toString());
            System.out.print(failedReturn.toString());
        } catch (NullPointerException e){
            System.out.println("Word get retrieved null values");
        }


    }
}
