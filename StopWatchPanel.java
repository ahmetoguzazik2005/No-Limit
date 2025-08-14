import javax.swing.*;
import java.awt.*;

public class StopWatchPanel extends JPanel {
    private int seconds = 0, minutes = 0, hours = 0;
    private final Timer timer;
    private final JLabel timeLabel;
    private final JButton startButton;

    public StopWatchPanel() {
        setLayout(new BorderLayout());

        // Create the label for the time
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        add(timeLabel, BorderLayout.CENTER);


        // Create the start/stop button
        startButton = new JButton("START");
        startButton.setFont(startButton.getFont().deriveFont(Font.PLAIN, 24f));
        startButton.setFocusPainted(false);
        startButton.setMargin(new Insets(10, 12, 10, 12));
        add(startButton, BorderLayout.SOUTH);

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
                timer.stop();
                startButton.setText("START");
                startButton.setBackground(Color.GREEN);
            } else {
                timer.start();
                startButton.setText("STOP");
                startButton.setBackground(Color.RED);
            }
            startButton.setOpaque(true);
            startButton.setContentAreaFilled(true);
        });
    }

    // Format the time as HH:MM:SS
    private void updateTimeLabel() {
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
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