import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class ExaminationPanel extends JPanel { // For the detailed day look
    // general part
    LocalDate whichDay;
    ArrayList<StudyBlock> blocks;

    // goal and total layout
    JPanel buttonPanel;
    AnimatedPressButton deleteBlock;
    AnimatedPressButton addBlock;
    JPanel labelPanel;
    JLabel labelTotalString;
    JPanel centerPanel;
    JPanel progressBarPanel;
    JLabel labelTotalInt;
    JLabel labelGoalString;
    JLabel labelGoalInt;

    // table layout
    JTable table;
    DefaultTableModel model;
    JScrollPane scrollPane;

    ExaminationPanel() throws SQLException {
        setLayout(new BorderLayout(10, 10));

        // nlayout and components are set
        buttonPanel = new JPanel();
        deleteBlock = new AnimatedPressButton("Delete Block");
        addBlock = new AnimatedPressButton("Add Block");
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addBlock);
        buttonPanel.add(deleteBlock);
        this.add(buttonPanel, BorderLayout.SOUTH);

        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout());
        labelTotalString = new JLabel("Total Time: ");
        labelPanel.add(labelTotalString);

        labelTotalInt = new JLabel();
        labelPanel.add(labelTotalInt);

        labelGoalString = new JLabel("Goal Time: ");
        labelPanel.add(labelGoalString);

        labelGoalInt = new JLabel();
        labelPanel.add(labelGoalInt);
        this.add(labelPanel, BorderLayout.NORTH);

        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));

        progressBarPanel = new JPanel();
        JProgressBar progressBar = new JProgressBar(0,100);
        
        //progressBar.setString("Current Time: " + currentTime);




        model = new DefaultTableModel();
        model.addColumn("Start Time");
        model.addColumn("Finish Time");



        // Create JTable and makes it rows uneditable by the user
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells non-editable
            }
        };

        // Add to JScrollPane for scrolling
        scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane);
        centerPanel.add(progressBarPanel);
        add(centerPanel, BorderLayout.CENTER);

        deleteBlock.addActionListener(e -> {
            int viewRow = table.getSelectedRow();

            if (viewRow >= 0) {
                // extract time from the column
                int modelRow = table.convertRowIndexToModel(viewRow);

                String startTime = (String) model.getValueAt(modelRow, 0);
                String endTime   = (String) model.getValueAt(modelRow, 1);

                // split "YYYY-MM-DD HH:MM:SS"
                String[] partsForBeginning = startTime.split(" ");
                String datePartBeginning = partsForBeginning[0];
                String timePartBeginning = partsForBeginning[1];

                String[] partsForEnd = endTime.split(" ");
                String timePartEnd = partsForEnd[1];

                // compute difference (begin → end)
                LocalTime ltBegin = LocalTime.parse(timePartBeginning);
                LocalTime ltEnd   = LocalTime.parse(timePartEnd);
                LocalTime difference = MyJDBC.difference(ltBegin, ltEnd);

                // parse for DB ops
                LocalDate date = LocalDate.parse(datePartBeginning);
                LocalTime time = ltBegin;

                // should remove from the database
                try {
                    Main.m.deleteBlock(date, time);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                // delete should update total time
                try {
                    Main.m.removeFromTheDay(date, difference);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                // should update time label in examination panel
                try {
                    set(whichDay);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                // delete should change day's color
                try {
                    Main.myFrame.trackPanel.updateCalendarDisplay();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                // Removes from table model
                model.removeRow(viewRow);
                try {
                    set(whichDay);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            } else { // not sure how to give better feedback if not selected
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });
        addBlock.addActionListener(e -> {

        });
    }

    // makes seperation easier
    void prepareEverything(LocalDate whichDay) throws SQLException {
        set(whichDay);
        populateTable(whichDay);
    }

    private void set(LocalDate whichDay) throws SQLException {
        this.whichDay = whichDay;
        LocalTime totalTime = Main.m.getDayTotalTime(whichDay);
        LocalTime goalTime = Main.m.getDayGoal(whichDay);

        // Format both to look exactly same
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTotalTime = totalTime.format(formatter);
        String formattedGoalTime = goalTime.format(formatter);

        labelTotalInt.setText(formattedTotalTime);
        labelGoalInt.setText(formattedGoalTime);

    }

    // shows all buttons for the user
    void populateTable(LocalDate whichDay) throws SQLException {
        blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");// can be changed in the future
                                                                                         // just to make visual more
                                                                                         // meaningful

        // adding blocks to my table
        for (StudyBlock block : blocks) {
            Object[] rowData = {
                    block.startTime.format(formatter),
                    block.endTime.format(formatter)
                    // Add other StudyBlock properties as needed
            };
            model.addRow(rowData);
        }
    }
}
