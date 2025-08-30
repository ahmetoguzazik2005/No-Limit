import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
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
    JLabel labelTotalInt;
    JLabel labelGoalString;
    JLabel labelGoalInt;

    // table layout
    JTable table;
    DefaultTableModel model;
    JScrollPane scrollPane;

    ExaminationPanel() throws SQLException {
        setLayout(new BorderLayout(10, 10));

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

        add(scrollPane, BorderLayout.CENTER);

        deleteBlock.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow >= 0) {
                String startTime = (String) model.getValueAt(selectedRow, 0);

                String[] parts = startTime.split(" ");
                String datePart = parts[0]; // "2025-08-30"
                String timePart = parts[1]; // "08:34:00"

                // Parse to LocalDate and LocalTime
                LocalDate date = LocalDate.parse(datePart);
                LocalTime time = LocalTime.parse(timePart);

                try {
                    Main.m.deleteBlock(date, time);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                // Remove from table model
                model.removeRow(selectedRow);
            } else { // not sure how to give better feedback if not selected
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
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
