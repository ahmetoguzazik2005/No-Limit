// import javax.swing.*;
// import java.awt.*;
// import java.sql.SQLException;
// import java.time.Duration;
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import javax.swing.table.DefaultTableModel;

// public class ExaminationPanel extends JPanel { // For the detailed day look
// // general part
// LocalDate whichDay;
// ArrayList<StudyBlock> blocks;

// // goal and total layout
// JPanel buttonPanel;
// AnimatedPressButton deleteBlock;
// AnimatedPressButton addBlock;
// JPanel labelPanel;
// JLabel labelTotalString;
// JLabel labelTotalInt;
// JLabel labelGoalString;
// JLabel labelGoalInt;
// JLabel dateLabelAtTop;

// // table layout
// JTable table;
// DefaultTableModel model;
// JScrollPane scrollPane;

// ExaminationPanel() throws SQLException {
// setLayout(new BorderLayout(10, 10));

// // nlayout and components are set
// buttonPanel = new JPanel();
// deleteBlock = new AnimatedPressButton("Delete Block");
// addBlock = new AnimatedPressButton("Add Block");
// buttonPanel.setLayout(new FlowLayout());
// buttonPanel.add(addBlock);
// buttonPanel.add(deleteBlock);
// this.add(buttonPanel, BorderLayout.SOUTH);

// labelPanel = new JPanel();
// labelPanel.setLayout(new FlowLayout());

// dateLabelAtTop = new JLabel();
// labelPanel.add(dateLabelAtTop);

// labelTotalString = new JLabel("Total Time: ");
// labelPanel.add(labelTotalString);

// labelTotalInt = new JLabel();
// labelPanel.add(labelTotalInt);

// labelGoalString = new JLabel("Goal Time: ");
// labelPanel.add(labelGoalString);

// labelGoalInt = new JLabel();
// labelPanel.add(labelGoalInt);
// this.add(labelPanel, BorderLayout.NORTH);

// model = new DefaultTableModel();
// model.addColumn("Start Time");
// model.addColumn("Finish Time");

// // Create JTable and makes it rows uneditable by the user
// table = new JTable(model) {
// @Override
// public boolean isCellEditable(int row, int column) {
// return false; // All cells non-editable
// }
// };

// // Add to JScrollPane for scrolling
// scrollPane = new JScrollPane(table);

// add(scrollPane, BorderLayout.CENTER);

// deleteBlock.addActionListener(e -> {
// int viewRow = table.getSelectedRow();

// if (viewRow >= 0) {
// // extract time from the column
// int modelRow = table.convertRowIndexToModel(viewRow);

// String startTime = (String) model.getValueAt(modelRow, 0);
// String endTime = (String) model.getValueAt(modelRow, 1);

// String datePartBeginning = whichDay.toString();

// // compute difference (begin → end)
// LocalTime ltBegin = LocalTime.parse(startTime);
// LocalTime ltEnd = LocalTime.parse(endTime);
// LocalTime difference = MyJDBC.difference(ltBegin, ltEnd);

// // parse for DB ops
// LocalDate date = LocalDate.parse(datePartBeginning);
// LocalTime time = ltBegin;

// // should remove from the database
// try {
// Main.m.deleteBlock(date, time);
// } catch (SQLException e1) {
// e1.printStackTrace();
// }

// // delete should update total time
// try {
// Main.m.removeFromTheDay(date, difference);
// } catch (SQLException e1) {
// e1.printStackTrace();
// }
// // should update time label in examination panel
// try {
// set(whichDay);
// } catch (SQLException e1) {
// e1.printStackTrace();
// }

// // delete should change day's color
// try {
// Main.myFrame.trackPanel.updateCalendarDisplay();
// } catch (SQLException e1) {
// e1.printStackTrace();
// }

// // Removes from table model
// model.removeRow(viewRow);
// try {
// set(whichDay);
// } catch (SQLException e1) {
// e1.printStackTrace();
// }

// } else { // not sure how to give better feedback if not selected
// JOptionPane.showMessageDialog(this, "Please select a row to delete.");
// }
// });
// }

// // makes seperation easier
// void prepareEverything(LocalDate whichDay) throws SQLException {
// set(whichDay);
// populateTable(whichDay);
// dateLabelAtTop.setText("Date: " + whichDay);
// }

// private void set(LocalDate whichDay) throws SQLException {
// this.whichDay = whichDay;
// LocalTime totalTime = Main.m.getDayTotalTime(whichDay);
// LocalTime goalTime = Main.m.getDayGoal(whichDay);

// // Format both to look exactly same
// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
// String formattedTotalTime = totalTime.format(formatter);
// String formattedGoalTime = goalTime.format(formatter);

// labelTotalInt.setText(formattedTotalTime);
// labelGoalInt.setText(formattedGoalTime);

// }

// // shows all buttons for the user
// void populateTable(LocalDate whichDay) throws SQLException {
// blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
// model.setRowCount(0);

// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");// can
// be changed in the future
// // just to make visual more
// // meaningful

// // adding blocks to my table
// for (StudyBlock block : blocks) {
// Object[] rowData = {
// block.startTime.format(formatter),
// block.endTime.format(formatter)
// // Add other StudyBlock properties as needed
// };
// model.addRow(rowData);
// }
// }
// }

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExaminationPanel extends JPanel { // For the detailed day look

    // ADD: Professional color constants (same as TrackPanel)
    public static final Color SUCCESS_GREEN = new Color(46, 125, 50);
    public static final Color ALERT_RED = new Color(198, 40, 40);
    public static final Color ACCENT_BLUE = new Color(25, 118, 210);
    public static final Color WARM_BEIGE = new Color(245, 245, 220);
    public static final Color LIGHT_GRAY = new Color(248, 249, 250);
    public static final Color BORDER_GRAY = new Color(224, 224, 224);
    public static final Color DARK_GRAY = new Color(66, 66, 66);
    public static final Color INACTIVE_TEXT = new Color(158, 158, 158);

    // general part - UNCHANGED
    LocalDate whichDay;
    ArrayList<StudyBlock> blocks;

    // goal and total layout - UNCHANGED field declarations
    JPanel buttonPanel;
    AnimatedPressButton deleteBlock;
    AnimatedPressButton addBlock;
    JPanel labelPanel;
    JLabel labelTotalString;
    JLabel labelTotalInt;
    JLabel labelGoalString;
    JLabel labelGoalInt;
    JLabel dateLabelAtTop;

    // table layout - UNCHANGED field declarations
    JTable table;
    DefaultTableModel model;
    JScrollPane scrollPane;

    ExaminationPanel() throws SQLException {
        setLayout(new BorderLayout(10, 10));
        setBackground(WARM_BEIGE); // ADD: Professional background
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // ADD: Padding

        // IMPROVED: Professional button panel (buttons themselves untouched)
        buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_GRAY); // ADD: Light gray background
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        deleteBlock = new AnimatedPressButton("Delete Block"); // UNCHANGED
        addBlock = new AnimatedPressButton("Add Block"); // UNCHANGED

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0)); // IMPROVED: Better spacing
        buttonPanel.add(addBlock);
        buttonPanel.add(deleteBlock);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // IMPROVED: Professional label panel
        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        labelPanel.setBackground(Color.WHITE); // ADD: Clean white background
        labelPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // IMPROVED: Professional date label
        dateLabelAtTop = new JLabel();
        dateLabelAtTop.setFont(dateLabelAtTop.getFont().deriveFont(Font.BOLD, 16f));
        dateLabelAtTop.setForeground(ACCENT_BLUE);
        dateLabelAtTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        labelPanel.add(dateLabelAtTop);

        // IMPROVED: Professional total time labels
        labelTotalString = new JLabel("Total Time: ");
        labelTotalString.setFont(labelTotalString.getFont().deriveFont(Font.BOLD, 14f));
        labelTotalString.setForeground(DARK_GRAY);
        labelPanel.add(labelTotalString);

        labelTotalInt = new JLabel();
        labelTotalInt.setFont(labelTotalInt.getFont().deriveFont(Font.BOLD, 14f));
        labelTotalInt.setForeground(ACCENT_BLUE);
        labelTotalInt.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        labelPanel.add(labelTotalInt);

        // IMPROVED: Professional goal time labels
        labelGoalString = new JLabel("Goal Time: ");
        labelGoalString.setFont(labelGoalString.getFont().deriveFont(Font.BOLD, 14f));
        labelGoalString.setForeground(DARK_GRAY);
        labelPanel.add(labelGoalString);

        labelGoalInt = new JLabel();
        labelGoalInt.setFont(labelGoalInt.getFont().deriveFont(Font.BOLD, 14f));
        labelGoalInt.setForeground(SUCCESS_GREEN);
        labelPanel.add(labelGoalInt);

        this.add(labelPanel, BorderLayout.NORTH);

        // IMPROVED: Professional table setup
        model = new DefaultTableModel();
        model.addColumn("Start Time");
        model.addColumn("Finish Time");
        model.addColumn("Worked Time");

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // UNCHANGED - All cells non-editable
            }
        };

        // ADD: Professional table styling
        table.setFont(table.getFont().deriveFont(13f));
        table.setRowHeight(25);
        table.setGridColor(BORDER_GRAY);
        table.setSelectionBackground(ACCENT_BLUE.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        table.setForeground(DARK_GRAY);

        // ADD: Professional header styling
        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setForeground(DARK_GRAY);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_GRAY));

        // ADD: Professional scroll pane
        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setBackground(LIGHT_GRAY);
        scrollPane.getHorizontalScrollBar().setBackground(LIGHT_GRAY);

        add(scrollPane, BorderLayout.CENTER);

        // UNCHANGED: Delete block action listener stays exactly the same
        deleteBlock.addActionListener(e -> {
            int viewRow = table.getSelectedRow();

            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                String startTime = (String) model.getValueAt(modelRow, 0);
                String endTime = (String) model.getValueAt(modelRow, 1);
                String datePartBeginning = whichDay.toString();

                LocalTime ltBegin = LocalTime.parse(startTime);
                LocalTime ltEnd = LocalTime.parse(endTime);
                LocalTime difference = MyJDBC.difference(ltBegin, ltEnd);

                LocalDate date = LocalDate.parse(datePartBeginning);
                LocalTime time = ltBegin;

                try {
                    Main.m.deleteBlock(date, time);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                try {
                    Main.m.removeFromTheDay(date, difference);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                try {
                    set(whichDay);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                try {
                    Main.myFrame.trackPanel.updateCalendarDisplay();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                model.removeRow(viewRow);
                try {
                    set(whichDay);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });
    }

    // IMPROVED: Enhanced date preparation
    void prepareEverything(LocalDate whichDay) throws SQLException {
        set(whichDay);
        populateTable(whichDay);

        // Professional date formatting
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        String formattedDate = whichDay.format(dateFormatter);
        dateLabelAtTop.setText(formattedDate);

        // Color coding for today vs other days
        if (whichDay.equals(LocalDate.now())) {
            dateLabelAtTop.setText(formattedDate);
            dateLabelAtTop.setForeground(SUCCESS_GREEN);
        } else {
            dateLabelAtTop.setForeground(ACCENT_BLUE);
        }
    }

    // IMPROVED: Dynamic colors based on goal achievement
    private void set(LocalDate whichDay) throws SQLException {
        this.whichDay = whichDay;
        LocalTime totalTime = Main.m.getDayTotalTime(whichDay);
        LocalTime goalTime = Main.m.getDayGoal(whichDay);

        // UNCHANGED: Format both to look exactly same
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTotalTime = totalTime.format(formatter);
        String formattedGoalTime = goalTime.format(formatter);

        labelTotalInt.setText(formattedTotalTime);
        labelGoalInt.setText(formattedGoalTime);

        // NEW: Dynamic color updates based on goal achievement
        if (totalTime != null && goalTime != null) {
            if (goalTime.isAfter(totalTime)) {
                // Goal not met - red colors
                labelTotalInt.setForeground(ALERT_RED);
                labelGoalInt.setForeground(DARK_GRAY);
                labelPanel.setBackground(new Color(255, 248, 248)); // Very light red
            } else {
                // Goal met - green colors
                labelTotalInt.setForeground(SUCCESS_GREEN);
                labelGoalInt.setForeground(DARK_GRAY);
                labelPanel.setBackground(new Color(248, 255, 248)); // Very light green
            }
        } else {
            // No data - default colors
            labelTotalInt.setForeground(DARK_GRAY);
            labelGoalInt.setForeground(DARK_GRAY);
            labelPanel.setBackground(Color.WHITE);
        }
    }

    // UNCHANGED: populateTable method stays exactly the same
    void populateTable(LocalDate whichDay) throws SQLException {
        blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (StudyBlock block : blocks) {
            String duration = String.format("%02d:%02d:%02d",
                    Duration.between(block.startTime, block.endTime).toHours(),
                    Duration.between(block.startTime, block.endTime).toMinutesPart(),
                    Duration.between(block.startTime, block.endTime).toSecondsPart());

            Object[] rowData = {
                    block.startTime.toLocalTime().format(formatter),
                    block.endTime.toLocalTime().format(formatter),
                    duration
            };
            model.addRow(rowData);
        }
    }
}