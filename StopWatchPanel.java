import javax.swing.*;
import java.awt.*;

public class StopWatchPanel extends JPanel {
    private int seconds = 0, minutes = 0, hours = 0;
    private final Timer timer;
    private final JLabel timeLabel;
    private final JButton startButton;
    private final JButton endButton;// makes the clock time zero and also should create or at least make the
                                    // timeobject finish and save to the db
    private final JPanel bottomPanel;// for putting buttons together

    public StopWatchPanel() {
        setBackground(new Color(245, 245, 220)); // setting nicer backgrounds

        setLayout(new BorderLayout());
        bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 220)); // Beige
        add(bottomPanel, BorderLayout.SOUTH);

        // Create the label for the time
        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        add(timeLabel, BorderLayout.CENTER);

        // Create the start/stop button
        startButton = new JButton("START");
        startButton.setFont(startButton.getFont().deriveFont(Font.PLAIN, 24f));
        startButton.setFocusPainted(false);
        startButton.setMargin(new Insets(10, 12, 10, 12));
        // first part should also be green
        startButton.setBackground(Color.GREEN);
        startButton.setOpaque(true);
        startButton.setContentAreaFilled(true);
        startButton.setBorderPainted(false);
        bottomPanel.add(startButton);

        endButton = new JButton("FINISH");
        endButton.setFont(startButton.getFont().deriveFont(Font.PLAIN, 24f));
        endButton.setFocusPainted(false);
        endButton.setMargin(new Insets(10, 12, 10, 12));
        endButton.setBackground(Color.RED);
        endButton.setOpaque(true);
        endButton.setContentAreaFilled(true);
        endButton.setBorderPainted(false);
        bottomPanel.add(endButton);

        // Timer — fires every 1000 ms (1 second)
        timer = new Timer(1000, e -> {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            if (minutes == 60) {
                minutes = 0;
                hours++;
            }
            updateTimeLabel();
        });

        // Button click toggles the timer
        startButton.addActionListener(e -> {
            if (timer.isRunning()) {
                // Stop the timer
                timer.stop();
                startButton.setText("START");

                // better visuals
                startButton.setBackground(Color.GREEN);
                startButton.setOpaque(true);
                startButton.setContentAreaFilled(true);
                startButton.setBorderPainted(false);

            } else {
                // Start the timer
                timer.start();
                startButton.setText(" STOP ");// for making button size same for all

                // better visuals
                startButton.setBackground(Color.ORANGE);
                startButton.setOpaque(true);
                startButton.setContentAreaFilled(true);
                startButton.setBorderPainted(false);
            }
        });

        // Button click toggles the timer
        endButton.addActionListener(e -> {
            // should also handle db records
            timer.stop();
            seconds = 0;
            minutes = 0;
            hours = 0;
            updateTimeLabel();

            // if time is zero it should not create new records
        });

    }

    // Format the time as HH:MM:SS
    private void updateTimeLabel() {
        // for better visual
        if (hours == 0) {
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        } else {
            timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }

    }

    // Quick demo frame -> instead for junk main-> deleted junk main !!!
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stopwatch");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new StopWatchPanel());
            frame.setSize(400, 250);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}