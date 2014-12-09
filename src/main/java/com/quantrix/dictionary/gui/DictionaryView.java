package com.quantrix.dictionary.gui;

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
import java.util.List;


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

                    populateTableModel(searchResults);
                }
            }
        });

        /**
         * Reset the word list to the original values
         */
        resetResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Word> wordList = dictionaryController.getSortedList();
                populateTableModel(wordList);
            }
        });

        saveEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Save Executing");

                if (selectedRow >= 0) {
                    String wordName = wordNameTextInput.getText();
                    String wordDefinition = wordDefinitionTextArea.getText();

                    dictionaryController.save(wordName, wordDefinition);

                    int wordId = Integer.parseInt((String) wordTable.getValueAt(selectedRow, 0));

                    Word thisWord = dictionaryController.getWord(wordId);

                    wordDateUpdatedTextInput.setText(thisWord.getDateTimeFormatter().print(thisWord.getDateLastUpdated()));
                    wordTableModel.setValueAt(thisWord.getWordName(), selectedRow, 1);
                    wordTableModel.setValueAt(thisWord.getWordDefinition(), selectedRow, 2);
                    wordTableModel.setValueAt(thisWord.getDateTimeFormatter().print(thisWord.getDateLastUpdated()), selectedRow, 4);

                }
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
        dictionaryMainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        dictionaryMainPanel.setMinimumSize(new Dimension(550, 350));
        dictionaryMainPanel.setPreferredSize(new Dimension(800, 600));
        dictionaryToolbar = new JToolBar();
        dictionaryMainPanel.add(dictionaryToolbar, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        DictionaryViewLabel = new JLabel();
        DictionaryViewLabel.setText("Quantrix Interview Application - Dictionary");
        dictionaryToolbar.add(DictionaryViewLabel);
        dictionarySearchPanel = new JPanel();
        dictionarySearchPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        dictionaryMainPanel.add(dictionarySearchPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dictionarySearch = new JTextField();
        dictionarySearch.setToolTipText("Enter search term");
        dictionarySearchPanel.add(dictionarySearch, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setVisible(true);
        dictionarySearchPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        wordDateCreatedTextLabel = new JLabel();
        wordDateCreatedTextLabel.setText("Date Created");
        panel2.add(wordDateCreatedTextLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDateUpdatedTextLabel = new JLabel();
        wordDateUpdatedTextLabel.setText("Date Updated");
        panel2.add(wordDateUpdatedTextLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDateCreatedTextInput = new JTextField();
        wordDateCreatedTextInput.setEnabled(false);
        panel2.add(wordDateCreatedTextInput, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        wordDefinitionTextArea = new JTextArea();
        wordDefinitionTextArea.setLineWrap(true);
        wordDefinitionTextArea.setWrapStyleWord(true);
        panel2.add(wordDefinitionTextArea, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, 300), new Dimension(150, 50), new Dimension(300, 300), 0, false));
        cancelEditButton = new JButton();
        cancelEditButton.setText("Cancel");
        panel2.add(cancelEditButton, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveEditButton = new JButton();
        saveEditButton.setText("Save");
        panel2.add(saveEditButton, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDefinitionTextInputLabel = new JLabel();
        wordDefinitionTextInputLabel.setText("Definition");
        panel2.add(wordDefinitionTextInputLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDateUpdatedTextInput = new JTextField();
        wordDateUpdatedTextInput.setEnabled(false);
        wordDateUpdatedTextInput.setText("");
        panel2.add(wordDateUpdatedTextInput, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        wordNameTextInputPLabel = new JLabel();
        wordNameTextInputPLabel.setText("Word Name");
        panel2.add(wordNameTextInputPLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordNameTextInput = new JTextField();
        wordNameTextInput.setEnabled(true);
        panel2.add(wordNameTextInput, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        deleteWordButton = new JButton();
        deleteWordButton.setText("Delete");
        panel2.add(deleteWordButton, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Label");
        label1.setVisible(false);
        panel2.add(label1, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButtonLabel = new JLabel();
        deleteButtonLabel.setText("Label");
        deleteButtonLabel.setVisible(false);
        panel2.add(deleteButtonLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButtonLabel = new JLabel();
        saveButtonLabel.setText("Label");
        saveButtonLabel.setVisible(false);
        panel2.add(saveButtonLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordSearchTableLabel = new JLabel();
        wordSearchTableLabel.setText("Words");
        panel1.add(wordSearchTableLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordDetailLabel = new JLabel();
        wordDetailLabel.setText("Details");
        panel1.add(wordDetailLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordFoundFromOnlineQueryLabel = new JLabel();
        wordFoundFromOnlineQueryLabel.setText("New Word - Found Online");
        wordFoundFromOnlineQueryLabel.setVisible(false);
        panel1.add(wordFoundFromOnlineQueryLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wordTablePane = new JScrollPane();
        panel1.add(wordTablePane, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        wordTable = new JTable();
        wordTable.setAutoCreateColumnsFromModel(true);
        wordTablePane.setViewportView(wordTable);
        dictionarySearchButton = new JButton();
        dictionarySearchButton.setText("Search");
        dictionarySearchPanel.add(dictionarySearchButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resetResultsButton = new JButton();
        resetResultsButton.setText("Reset");
        dictionarySearchPanel.add(resetResultsButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return dictionaryMainPanel;
    }
}
