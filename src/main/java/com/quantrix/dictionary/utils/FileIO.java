package com.quantrix.dictionary.utils;

import com.quantrix.dictionary.domain.Word;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonjohns on 12/4/14.
 *
 * Handles the actual read/write to the file
 *
 * Enforces singleton pattern
 *
 */
public class FileIO {

    private static final String DICTIONARYFILE = "dictData.dat";
    private static final FileIO INSTANCE = new FileIO();

    private File dataFile;
    private Writer dataWriter;
    private Reader dataReader;

    private CSVFormat csvFormat;
    private CSVParser csvParser;
    private CSVPrinter csvPrinter;

    private FileIO(){}

    public static FileIO getInstance(){
        return INSTANCE;
    }

    /**
     *
     * @param dictMap HashMap
     *
     * Writes the contents of the HashMap to file.  Rather than updating individual line(s), the file is overwritten with
     * the entire contents of Word objects in the HashMap.
     */
    public void writeToDataFile(Map<String, Word> dictMap){
        ClassLoader classLoader = getClass().getClassLoader();

        try {
            dataFile = new File(classLoader.getResource(DICTIONARYFILE).getFile());
            dataWriter = new FileWriter(dataFile);

            csvFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
            csvPrinter = new CSVPrinter(dataWriter, csvFormat);

            csvPrinter.printRecord(CSVHeaderMapping.FILE_HEADER);
            List<String> wordData;

            //CSVPrinter.printRecord is best utilized with an Iterable object, thus the initialization of a new ArrayList
            //for each dictionary value
            for (Word word : dictMap.values()){
                wordData = new ArrayList<>();
                wordData.add(String.valueOf(word.getId()));
                wordData.add(word.getWordName());
                wordData.add(word.getWordDefinition());
                wordData.add(word.getDateCreated().toString());
                wordData.add(word.getDateLastUpdated().toString());

                csvPrinter.printRecord(wordData);
            }

        } catch (NullPointerException | IOException e){
            e.printStackTrace();
        } finally {
            try {
                dataWriter.flush();
                dataWriter.close();
                csvPrinter.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    /**
     *
     * @return Map
     *
     * Parses the CSV dictionary file and returns a HashMap instance with String - Word pairs
     */
    public Map<String, Word> loadDataFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        Map<String, Word> dictMap = new HashMap<>();

        try {
            //Open file connection and read stream
            dataFile = new File(classLoader.getResource(DICTIONARYFILE).getFile());
            dataReader = new FileReader(dataFile);

            //Initialize Apache Commons CSV instances and parse the dictionary file
            csvFormat = CSVFormat.DEFAULT.withHeader(CSVHeaderMapping.FILE_HEADER);
            csvParser = new CSVParser(dataReader, csvFormat);
            List<CSVRecord> csvRecordList = csvParser.getRecords();

            int wordId;
            String wordName,
                   wordDefinition,
                   createDate,
                   updateDate;

            //Populate HashMap instance with String - Word pairs
            //skip first line which contains the header
            for (int i = 1; i < csvRecordList.size(); i++){
                CSVRecord record = csvRecordList.get(i);
                wordId = Integer.parseInt(record.get(CSVHeaderMapping.WORD_ID));
                wordName = record.get(CSVHeaderMapping.WORD_NAME);
                wordDefinition = record.get(CSVHeaderMapping.WORD_DEFINITION);
                createDate = record.get(CSVHeaderMapping.WORD_CREATE_DATE);
                updateDate = record.get(CSVHeaderMapping.WORD_UPDATE_DATE);

                dictMap.put(wordName, new Word(wordId, wordName, wordDefinition, createDate, updateDate));
            }

        } catch (NullPointerException | IOException | NumberFormatException e){
            e.printStackTrace();
        } finally {
            //Connection cleanup
            try {
                dataReader.close();
                csvParser.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return dictMap;
    }

    public static void main(String[] args){

        FileIO fileIO = FileIO.getInstance();
        //Map<String, Word> dictMap = fileIO.loadDataFile();
        Map<String, Word> dictMap = new HashMap<>();

        dictMap.put("testWord", new Word(dictMap.size() + 1, "testWord", "testDefinition"));
        dictMap.put("halo", new Word(dictMap.size() + 1, "halo", "gold band hovering above head"));


        System.out.println("Dictionary Map Values");
        for (Word word : dictMap.values()){
            System.out.println(word.toString());
        }

        fileIO.writeToDataFile(dictMap);

        Map<String, Word> testMap = fileIO.loadDataFile();
        for (Word word : testMap.values()){
            System.out.println(word.toString());
        }

    }



}
