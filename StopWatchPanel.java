import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StopWatchPanel extends JPanel {
    int seconds;
    int minutes;
    int hours;
    boolean keepSecondsIncrease;

    JButton startButton = new JButton("Start");

    StopWatchPanel() {
        seconds = 0;
        minutes = 0;
        hours = 0;
        keepSecondsIncrease = false;

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keepSecondsIncrease = !keepSecondsIncrease;
            }
        });
        this.add(startButton);
        count();
    }

    public void count() {
        while (this.isVisible()) {
            while (this.isVisible() && keepSecondsIncrease) {
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                if (minutes == 60) {
                    hours++;
                    minutes = 0;
                }
            }
        }
    }
}
