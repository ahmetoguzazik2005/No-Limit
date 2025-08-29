import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ExaminationPanel extends JPanel{ // For the detailed day look
    LocalDate whichDay;
    ArrayList<StudyBlock> blocks;

    ExaminationPanel() throws SQLException {

    }

    void set(LocalDate whichDay) throws SQLException {
        this.whichDay = whichDay;
        LocalTime totalTime = Main.m.getDayTotalTime(whichDay);
        LocalTime goalTime = Main.m.getDayGoal(whichDay);
        //blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
        this.setLayout(new BorderLayout(10,10));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout());
        JLabel label1 = new JLabel("Total Time: ");
        labelPanel.add(label1);
        JLabel label2 = new JLabel(totalTime.toString());
        labelPanel.add(label2);
        JLabel label3 = new JLabel("Goal Time: ");
        labelPanel.add(label3);
        JLabel label4 = new JLabel(goalTime.toString());
        labelPanel.add(label4);
        this.add(labelPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        AnimatedPressButton deleteBlock = new AnimatedPressButton("Delete Block");
        AnimatedPressButton addBlock = new AnimatedPressButton("Add Block");






    }

}
