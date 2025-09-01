import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MyFrame extends JFrame implements ActionListener {
    // For global scope inside class
    JButton button1;
    JButton button2;
    JButton button3;
    JButton button4;
    // this panels may need to be called from the outside
    TrackPanel trackPanel;
    TodayPanel todayPanel;
    SettingsPanel settingsPanel;
    StopWatchPanel stopwatchPanel;
    static CardLayout cardLayout;
    static JPanel right;
    static ExaminationPanel examinationPanel;

    MyFrame() throws SQLException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Change to GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Left panel (sidebar) - 2 parts
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(200, 0));
        left.setBackground(new Color(173, 216, 230)); // soft blue
        left.setLayout(new GridLayout(0, 1, 0, 10));
        left.setBorder(new EmptyBorder(16, 12, 16, 12));

        // GridBag constraints for left panel
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        gbc.weightx = 1.0; // 4 parts of horizontal space
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
        cardLayout = new CardLayout();
        right.setLayout(cardLayout);

        // GridBag constraints for right panel
        gbc.gridx = 1; // Second column
        gbc.gridy = 0; // First row
        gbc.weightx = 4.0; // 1 part of horizontal space
        gbc.weighty = 1.0; // Full vertical space
        gbc.fill = GridBagConstraints.BOTH; // Fill both directions
        add(right, gbc);

        // Other sub panels initialization
        stopwatchPanel = new StopWatchPanel();
        todayPanel = new TodayPanel();
        trackPanel = new TrackPanel();
        settingsPanel = new SettingsPanel();
        examinationPanel = new ExaminationPanel();

        right.add(stopwatchPanel, "StopwatchPanel");
        right.add(todayPanel, "TodayPanel");
        right.add(trackPanel, "TrackPanel");
        right.add(examinationPanel, "ExaminationPanel"); // For the detailed look of the track panel for a day
        right.add(settingsPanel, "SettingsPanel");

        cardLayout.show(right, "StopwatchPanel");
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new AnimatedPressButton(text);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 32f));
        btn.setMargin(new Insets(10, 12, 10, 12)); // internal padding
        btn.setHorizontalAlignment(SwingConstants.CENTER); // align labels nicely

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