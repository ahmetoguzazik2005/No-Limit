import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class ExaminationPanel extends JPanel { // For the detailed day look
    LocalDate whichDay;
    ArrayList<StudyBlock> blocks;
    JTable table;
    DefaultTableModel model;
    JPanel buttonPanel;
    AnimatedPressButton deleteBlock;
    AnimatedPressButton addBlock;
    JPanel labelPanel;
    JLabel labelTotalString;
    JLabel labelTotalInt;
    JLabel labelGoalString;
    JLabel labelGoalInt;

    ExaminationPanel() throws SQLException {
        setLayout(new BorderLayout(10, 10));
        buttonPanel = new JPanel();
        deleteBlock = new AnimatedPressButton("Delete Block");
        // addBlock = new AnimatedPressButton("Add Block");

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
    }

    void set(LocalDate whichDay) throws SQLException {
        this.whichDay = whichDay;
        LocalTime totalTime = Main.m.getDayTotalTime(whichDay);
        LocalTime goalTime = Main.m.getDayGoal(whichDay);

        // Format both to look exactly same
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTotalTime = totalTime.format(formatter);
        String formattedGoalTime = goalTime.format(formatter);

        labelTotalInt.setText(formattedTotalTime);
        labelGoalInt.setText(formattedGoalTime);

        // blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
    }
}
