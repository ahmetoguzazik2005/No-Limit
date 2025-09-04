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
    JLabel dateLabelAtTop;
    LocalTime totalTime;
    LocalTime goalTime;

    // table layout
    JTable table;
    DefaultTableModel model;
    JScrollPane scrollPane;
    JProgressBar progressBar;

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

        progressBarPanel = new JPanel(new GridBagLayout()); // Center it perfectly
        progressBarPanel.setBackground(Color.WHITE);
        progressBarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // bigger padding
        ));

        ///////initial visible part of progressBar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(450, 60)); // bigger
        progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD, 16f)); // larger text
        progressBar.setBackground(Color.WHITE);
        progressBar.setStringPainted(true);
        progressBar.setOpaque(false);
        progressBar.setUI(new RoundedProgressBarUI());
        progressBarPanel.add(progressBar); // center the bar within the panel

        // --- Table + ScrollPane ---
        model = new DefaultTableModel();
        model.addColumn("Start Time");
        model.addColumn("Finish Time");
        model.addColumn("Worked Time");

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(table.getFont().deriveFont(13f));
        table.setRowHeight(25);
        table.setGridColor(BORDER_GRAY);
        table.setSelectionBackground(ACCENT_BLUE.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        table.setForeground(DARK_GRAY);

        table.getTableHeader().setBackground(LIGHT_GRAY);
        table.getTableHeader().setForeground(DARK_GRAY);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_GRAY));

        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setBackground(LIGHT_GRAY);
        scrollPane.getHorizontalScrollBar().setBackground(LIGHT_GRAY);

        // --- Center panel with GridBagLayout (add each component once) ---
        centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;

        // Progress section (top ~30%)
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.30;
        centerPanel.add(progressBarPanel, gbc);

        // Table section (bottom ~70%)
        gbc.gridy = 1;
        gbc.weighty = 0.70;
        centerPanel.add(scrollPane, gbc);

        // Attach center panel to main layout
        add(centerPanel, BorderLayout.CENTER);

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
        addBlock.addActionListener(e -> {

        });

    }

    void setProgressBar() throws SQLException {

        int totalSeconds = totalTime.toSecondOfDay();
        int goalSeconds = Math.max(1, goalTime.toSecondOfDay()); // avoid /0
        if (totalSeconds >= goalSeconds) {
            progressBar.setValue(100);
        } else {
            double totalDouble = totalSeconds;
            double goalDouble = goalSeconds;
            double result = (totalDouble * 100) / goalDouble;
            progressBar.setValue((int) result);
        }
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

    private void set(LocalDate whichDay) throws SQLException {
        this.whichDay = whichDay;
        totalTime = Main.m.getDayTotalTime(whichDay);
        goalTime = Main.m.getDayGoal(whichDay);

        setProgressBar();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTotalTime = totalTime.format(formatter);
        String formattedGoalTime = goalTime.format(formatter);

        labelTotalInt.setText(formattedTotalTime);
        labelGoalInt.setText(formattedGoalTime);

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

    private String verbalWorkedTime(String workString) {
        String answer = "";
        String[] parts = workString.split(":");

        if (parts[0].equals("00")) {
        } else if (parts[0].equals("01")) {
            answer += "1 hour ";
        } else if (parts[0].startsWith("0")) {
            answer += parts[0].substring(1) + " hours ";
        } else {
            answer += parts[0] + " hours ";
        }

        if (parts[1].equals("00")) {
        } else if (parts[1].equals("01")) {
            answer += "1 minute ";
        } else if (parts[1].startsWith("0")) {
            answer += parts[1].substring(1) + " minutes ";
        } else {
            answer += parts[1] + " minutes ";
        }

        if (parts[2].equals("00")) {
        } else if (parts[2].equals("01")) {
            answer += "1 second ";
        } else if (parts[2].startsWith("0")) {
            answer += parts[2].substring(1) + " seconds ";
        } else {
            answer += parts[2] + " seconds ";
        }

        if (answer.equals("")) {
            return "None";
        }
        return answer;
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
            duration = verbalWorkedTime(duration);

            Object[] rowData = {
                    block.startTime.toLocalTime().format(formatter),
                    block.endTime.toLocalTime().format(formatter),
                    duration
            };
            model.addRow(rowData);
        }
    }
}
