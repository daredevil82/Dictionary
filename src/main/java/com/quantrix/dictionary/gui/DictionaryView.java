package com.quantrix.dictionary.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.quantrix.dictionary.controller.DictionaryController;
import com.quantrix.dictionary.domain.Word;
import com.quantrix.dictionary.init.Configuration;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;


/**
 * Created by jasonjohns on 12/8/14.
 */
public class DictionaryView {
    private JPanel dictionaryMainPanel;
    private JLabel DictionaryViewLabel;
    private JToolBar dictionaryToolbar;
    private JPanel dictionarySearchPanel;
    private JTextField dictionarySearch;
    private JButton dictionarySearchButton;
    private JTable wordTable;
    private JLabel wordSearchTableLabel;
    private JLabel wordDetailLabel;
    private JTextArea wordDefinitionTextArea;
    private JTextField wordNameTextInput;
    private JLabel wordNameTextInputPLabel;
    private JLabel wordDefinitionTextInputLabel;
    private JLabel wordDateCreatedTextLabel;
    private JLabel wordDateUpdatedTextLabel;
    private JTextField wordDateUpdatedTextInput;
    private JTextField wordDateCreatedTextInput;
    private JButton cancelEditButton;
    private JButton saveEditButton;
    private JLabel wordFoundFromOnlineQueryLabel;
    private JScrollPane wordTablePane;
    private JButton deleteWordButton;
    private JButton resetResultsButton;
    private JLabel deleteButtonLabel;
    private JLabel saveButtonLabel;
    private JTextField wordTextField;
    private DefaultTableModel wordTableModel, defaultTableModel;
    private TableColumnModel wordTableColumnModel;

    private DictionaryController dictionaryController;

    private Word selectedWord;
    private int selectedRow;

    public DictionaryView(DictionaryController dictionaryController) {
        this.dictionaryController = dictionaryController;
        initSearchResultsTableModel();
        initListeners();
        selectedRow = -1;

        JFrame jFrame = new JFrame("Dictionary - Fun Times");
        jFrame.setContentPane(dictionaryMainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();

        DictionaryView dictionaryView = new DictionaryView(configuration.initController());

    }

    /**
     * Initializes the event and action listeners for the UI
     */
    private void initListeners() {
        /**
         * Executes a Search operation
         */
        dictionarySearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dictionarySearch.getText().equals("")) {
                    String query = dictionarySearch.getText();
                    List<Word> searchResults = dictionaryController.searchDictionary(query);
                    Collections.sort(searchResults, Word.wordComparator);

                    populateTableModel(searchResults);

                    if (!searchResults.get(0).getWordName().equals(query)) {
                        String definitions = dictionaryController.getDefinitions(query);

                        if (definitions != null && definitions.length() > 0) {
                            wordNameTextInput.setText(query);
                            wordDefinitionTextArea.setText(definitions);
                            wordFoundFromOnlineQueryLabel.setText("Definition found online");
                            wordFoundFromOnlineQueryLabel.setVisible(true);
                        } else {
                            wordFoundFromOnlineQueryLabel.setText("Error with internet query");
                            wordFoundFromOnlineQueryLabel.setVisible(true);
                        }
                    }
                }
            }
        });

        /**
         * Reset the word list to the original values
         */
        resetResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedRow = -1;
                wordFoundFromOnlineQueryLabel.setVisible(false);
                cancelEditButton.doClick();
                List<Word> wordList = dictionaryController.getSortedList();
                populateTableModel(wordList);

            }
        });

        /**
         * Save operation that discriminates between a `Create` and `Update`.
         * If no previous selection has been made, or a Cancel button click has occurred,
         * a Save event is executed, which creates a new Word object, adds the values to the sorted JTable
         *
         * If a previous selection has been made, a check executes on whether the word input matches anything in the
         * database.  If it does, then the definition value is set and a DAO update operation executes.  On success,
         * the table definition and date updated fields are updated with the new values.
         *
         * Otherwise, a create operation takes place.
         */
        saveEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Save Executing");
                String wordName = wordNameTextInput.getText();
                String wordDefinition = wordDefinitionTextArea.getText();

                Word thisWord = dictionaryController.getWord(wordName);

                //if this is an update operation
                if (selectedRow >= 0 && thisWord != null) {

                    thisWord.setWordDefinition(wordDefinition);
                    dictionaryController.updateWord(thisWord);

                    wordDateUpdatedTextInput.setText(thisWord.getDateTimeFormatter().print(thisWord.getDateLastUpdated()));
                    wordTableModel.setValueAt(thisWord.getWordName(), selectedRow, 1);
                    wordTableModel.setValueAt(thisWord.getWordDefinition(), selectedRow, 2);
                    wordTableModel.setValueAt(thisWord.getDateTimeFormatter().print(thisWord.getDateLastUpdated()), selectedRow, 4);

                } else if (wordName.length() > 0) {
                    dictionaryController.save(wordName, wordDefinition);
                    thisWord = dictionaryController.getWord(wordName);

                    Object[] rowObject = new Object[]{
                            String.valueOf(thisWord.getId()),
                            thisWord.getWordName(),
                            thisWord.getWordDefinition(),
                            thisWord.getDateTimeFormatter().print(thisWord.getDateCreated()),
                            thisWord.getDateTimeFormatter().print(thisWord.getDateLastUpdated()),
                            thisWord};
                    wordTableModel.addRow(rowObject);

                    Vector data = wordTableModel.getDataVector();
                    Collections.sort(data, new TableColumnSorter(1));
                    String rowWord;

                    for (int i = 0; i < wordTableModel.getRowCount(); i++) {
                        rowWord = (String) wordTableModel.getValueAt(i, 1);
                        if (rowWord.equals(wordName)) {
                            wordTable.setRowSelectionInterval(i, i);
                        }
                    }
                }

                wordFoundFromOnlineQueryLabel.setVisible(false);

            }
        });

        /**
         * Clears all the detail area text content and programatically deselects the current row.
         */
        cancelEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wordNameTextInput.setText("");
                wordDefinitionTextArea.setText("");
                wordDateCreatedTextInput.setText("");
                wordDateUpdatedTextInput.setText("");

                wordTable.getSelectionModel().clearSelection();
                selectedRow = -1;
                wordFoundFromOnlineQueryLabel.setVisible(false);
                dictionarySearch.setText("");

            }
        });

        deleteWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRow >= 0) {
                    Timer labelTimer = new Timer(2000, this);
                    int wordId = Integer.parseInt((String) wordTable.getValueAt(selectedRow, 0));
                    Word deleteWord = dictionaryController.getWord(wordId);
                    dictionaryController.deleteWord(deleteWord);

                    deleteWord = dictionaryController.getWord(wordId);

                    if (deleteWord == null) {
                        //deleteButtonLabel.setText("Delete successful");
                        wordTableModel.removeRow(selectedRow);
                        cancelEditButton.doClick();
                    } else {
                        //deleteButtonLabel.setText("Delete error");
                        System.out.println("Delete error");
                    }

                    //deleteButtonLabel.setVisible(true);


                }
            }
        });

        /**
         * Gets the current row selection and populates the detail text content with appropriate values
         */
        wordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;

                selectedRow = wordTable.convertRowIndexToModel(wordTable.getSelectedRow());

                if (selectedRow >= 0) {
                    selectedWord = dictionaryController.getWord(Integer.parseInt((String) wordTable.getValueAt(selectedRow, 0)));
                    wordNameTextInput.setText(selectedWord.getWordName());
                    wordDefinitionTextArea.setText(selectedWord.getWordDefinition());
                    wordDateCreatedTextInput.setText((String) wordTable.getValueAt(selectedRow, 3));
                    wordDateUpdatedTextInput.setText((String) wordTable.getValueAt(selectedRow, 4));
                }
            }
        });
    }

    /**
     * @param wordList List of search results
     *                 <p/>
     *                 Populates the table with the contents of a List of Words
     */
    private void populateTableModel(List<Word> wordList) {
        clearTableModel(wordTableModel);

        if (wordList.size() > 0)
            populateWordTableData(wordList, wordTableModel);
    }

    /**
     * Initialize the search results table with all dictionary words sorted ascendingly.
     */
    private void initSearchResultsTableModel() {
        Object[] columnNames = new Object[]{"Word ID", "Word", "Definition", "Date Created", "Date Updated"};
        wordTableModel = new DefaultTableModel(columnNames, 0);

        List<Word> wordList = dictionaryController.getSortedList();

        populateWordTableData(wordList, wordTableModel);
        wordTable.setModel(wordTableModel);
    }

    private void clearTableModel(DefaultTableModel tableModel) {
        int rowCount,
                i;
        if (tableModel.getRowCount() > 0) {
            tableModel.getDataVector().removeAllElements();
        }
    }


    /**
     * @param wordList   List of words
     * @param tableModel DefaultTableModel table model of words
     *                   <p/>
     *                   Inserts the contents of `wordList` into the table
     */
    private void populateWordTableData(List<Word> wordList, DefaultTableModel tableModel) {
        Object[] rowObject;
        for (Word word : wordList) {
            rowObject = new Object[]{String.valueOf(word.getId()),
                    word.getWordName(),
                    word.getWordDefinition(),
                    word.getDateTimeFormatter().print(word.getDateCreated()),
                    word.getDateTimeFormatter().print(word.getDateLastUpdated()), word};
            tableModel.addRow(rowObject);
        }

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        dictionaryMainPanel = new JPanel();
        dictionaryMainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        dictionaryMainPanel.setMinimumSize(new Dimension(550, 350));
        dictionaryMainPanel.setPreferredSize(new Dimension(800, 600));
        dictionaryToolbar = new JToolBar();
        dictionaryMainPanel.add(dictionaryToolbar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        DictionaryViewLabel = new JLabel();
        DictionaryViewLabel.setText("Quantrix Interview Application - Dictionary");
        dictionaryToolbar.add(DictionaryViewLabel);
        dictionarySearchPanel = new JPanel();
        dictionarySearchPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        dictionaryMainPanel.add(dictionarySearchPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dictionarySearch = new JTextField();
        dictionarySearch.setToolTipText("Enter search term");
        dictionarySearchPanel.add(dictionarySearch, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setVisible(true);
        dictionarySearchPanel.add(panel1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(8, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, -1), null, 0, false));
        wordDateCreatedTextLabel = new JLabel();
        wordDateCreatedTextLabel.setText("Date Created");
        panel2.add(wordDateCreatedTextLabel, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDateUpdatedTextLabel = new JLabel();
        wordDateUpdatedTextLabel.setText("Date Updated");
        panel2.add(wordDateUpdatedTextLabel, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDateCreatedTextInput = new JTextField();
        wordDateCreatedTextInput.setEnabled(false);
        panel2.add(wordDateCreatedTextInput, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cancelEditButton = new JButton();
        cancelEditButton.setText("Cancel");
        panel2.add(cancelEditButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveEditButton = new JButton();
        saveEditButton.setText("Save");
        panel2.add(saveEditButton, new GridConstraints(7, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDefinitionTextInputLabel = new JLabel();
        wordDefinitionTextInputLabel.setText("Definition");
        panel2.add(wordDefinitionTextInputLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDateUpdatedTextInput = new JTextField();
        wordDateUpdatedTextInput.setEnabled(false);
        wordDateUpdatedTextInput.setText("");
        panel2.add(wordDateUpdatedTextInput, new GridConstraints(3, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        wordNameTextInputPLabel = new JLabel();
        wordNameTextInputPLabel.setText("Word Name");
        panel2.add(wordNameTextInputPLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordNameTextInput = new JTextField();
        wordNameTextInput.setEnabled(true);
        panel2.add(wordNameTextInput, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        deleteWordButton = new JButton();
        deleteWordButton.setText("Delete");
        panel2.add(deleteWordButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Label");
        label1.setVisible(false);
        panel2.add(label1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButtonLabel = new JLabel();
        deleteButtonLabel.setText("Label");
        deleteButtonLabel.setVisible(false);
        panel2.add(deleteButtonLabel, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButtonLabel = new JLabel();
        saveButtonLabel.setText("Label");
        saveButtonLabel.setVisible(false);
        panel2.add(saveButtonLabel, new GridConstraints(6, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(5, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        wordDefinitionTextArea = new JTextArea();
        wordDefinitionTextArea.setLineWrap(true);
        wordDefinitionTextArea.setWrapStyleWord(true);
        scrollPane1.setViewportView(wordDefinitionTextArea);
        wordSearchTableLabel = new JLabel();
        wordSearchTableLabel.setText("Words");
        panel1.add(wordSearchTableLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDetailLabel = new JLabel();
        wordDetailLabel.setText("Details");
        panel1.add(wordDetailLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordFoundFromOnlineQueryLabel = new JLabel();
        wordFoundFromOnlineQueryLabel.setText("New Word - Found Online");
        wordFoundFromOnlineQueryLabel.setVisible(false);
        panel1.add(wordFoundFromOnlineQueryLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordTablePane = new JScrollPane();
        panel1.add(wordTablePane, new GridConstraints(1, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, -1), null, 0, false));
        wordTable = new JTable();
        wordTable.setAutoCreateColumnsFromModel(true);
        wordTablePane.setViewportView(wordTable);
        dictionarySearchButton = new JButton();
        dictionarySearchButton.setText("Search");
        dictionarySearchPanel.add(dictionarySearchButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resetResultsButton = new JButton();
        resetResultsButton.setText("Reset");
        dictionarySearchPanel.add(resetResultsButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return dictionaryMainPanel;
    }

    private class TableColumnSorter implements Comparator {
        private int colIndex;

        TableColumnSorter(int colIndex) {
            this.colIndex = colIndex;
        }

        public int compare(Object a, Object b) {
            Vector vec1 = (Vector) a;
            Vector vec2 = (Vector) b;

            String wordOne = (String) vec1.get(colIndex);
            String wordTwo = (String) vec2.get(colIndex);

            return wordOne.compareTo(wordTwo);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }


}
