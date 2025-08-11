
// not tested in the actual frame next time it will be
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// will be displayed for stopwatch
public class StopWatchPanel extends JPanel {
    int seconds;
    int minutes;
    int hours;
    Timer timer;
    JLabel timeLabel;

    JButton startButton = new JButton("Start");

    StopWatchPanel() {
        seconds = 0;
        minutes = 0;
        hours = 0;

        // will update second every 1000 ms
        timer = new Timer(1000, e -> {
            seconds++;
        });

        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // visual part of buton
        startButton.setFocusPainted(false);
        startButton.setFont(startButton.getFont().deriveFont(Font.PLAIN, 24f));
        startButton.setMargin(new Insets(10, 12, 10, 12)); // internal padding

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.isRunning()) {
                    timer.stop();
                    startButton.setText("START");
                    startButton.setBackground(Color.green);
                } else {
                    timer.start();
                    startButton.setText("STOP");
                    startButton.setBackground(Color.red);
                }
            }
        });
        this.add(startButton);
        count();
    }

    public void count() {
        while (this.isVisible()) {
            if (seconds == 60) {
                minutes++;
                seconds = 0;
            }
            if (minutes == 60) {
                hours++;
                minutes = 0;
            }
            updateTimeLabel();
        }
    }

    // formats time label eversecond
    private void updateTimeLabel() {
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeLabel.setText(formattedTime);
    }
}
