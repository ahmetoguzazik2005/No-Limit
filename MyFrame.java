import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MyFrame extends JFrame implements ActionListener {
    // For global scope inside class
    JButton button1;
    JButton button2;
    JButton button3;
    JButton button4;
    CardLayout cardLayout;
    JPanel right;

    MyFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Change to GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Left panel (sidebar) - 2 parts
        JPanel left = new JPanel();
        left.setBackground(new Color(173, 216, 230)); // soft blue
        left.setLayout(new GridLayout(0, 1, 0, 10));
        left.setBorder(new EmptyBorder(16, 12, 16, 12));

        // GridBag constraints for left panel
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        gbc.weightx = 2.0; // 2 parts of horizontal space
        gbc.weighty = 1.0; // Full vertical space
        gbc.fill = GridBagConstraints.BOTH; // Fill both directions
        add(left, gbc);

        button1 = createSidebarButton("Stopwatch");
        left.add(button1);
        button2 = createSidebarButton("Today");
        left.add(button2);
        button3 = createSidebarButton("Track");
        left.add(button3);
        button4 = createSidebarButton("Settings");
        left.add(button4);

        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);

        // Right panel (main content) - 3 parts
        right = new JPanel();
        right.setBackground(new Color(245, 245, 220)); // beige
        cardLayout = new CardLayout();
        right.setLayout(cardLayout);

        // GridBag constraints for right panel
        gbc.gridx = 1; // Second column
        gbc.gridy = 0; // First row
        gbc.weightx = 3.0; // 3 parts of horizontal space
        gbc.weighty = 1.0; // Full vertical space
        gbc.fill = GridBagConstraints.BOTH; // Fill both directions
        add(right, gbc);

        // Other sub panels initialization
        StopWatchPanel stopwatchPanel = new StopWatchPanel();
        TodayPanel todayPanel = new TodayPanel();
        TrackPanel trackPanel = new TrackPanel();
        SettingsPanel settingsPanel = new SettingsPanel();

        right.add(stopwatchPanel, "StopwatchPanel");
        right.add(todayPanel, "TodayPanel");
        right.add(trackPanel, "TrackPanel");
        right.add(settingsPanel, "SettingsPanel");

        cardLayout.show(right, "StopwatchPanel");
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 24f));
        btn.setMargin(new Insets(10, 12, 10, 12)); // internal padding
        btn.setHorizontalAlignment(SwingConstants.LEFT); // align labels nicely
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            cardLayout.show(right, "StopwatchPanel");

        } else if (e.getSource() == button2) {
            cardLayout.show(right, "TodayPanel");

        } else if (e.getSource() == button3) {
            cardLayout.show(right, "TrackPanel");

        } else if (e.getSource() == button4) {
            cardLayout.show(right, "SettingsPanel");

        }

    }
}