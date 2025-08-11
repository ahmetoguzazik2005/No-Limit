import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MyFrame extends JFrame {
    MyFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left panel (sidebar)
        JPanel left = new JPanel();
        left.setBackground(new Color(173, 216, 230)); // soft blue
        left.setPreferredSize(new Dimension(220, 0));

        left.setLayout(new GridLayout(0, 1, 0, 10)); // 0 rows means
        // -> autocalculated based of the amount of conponents

        // Padding around the buttons
        left.setBorder(new EmptyBorder(16, 12, 16, 12));
        add(left, BorderLayout.WEST);

        JButton button1 = createSidebarButton("Stopwatch");
        left.add(button1);

        JButton button2 = createSidebarButton("Today");
        left.add(button2);

        JButton button3 = createSidebarButton("Track");
        left.add(button3);

        JButton button4 = createSidebarButton("Settings");
        left.add(button4);


        // Right panel (main content)
        JPanel right = new JPanel();
        right.setBackground(new Color(245, 245, 220)); // beige
        add(right, BorderLayout.CENTER);

        // Bottom panel (status/actions)
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(152, 251, 152)); // pale green
        bottom.setPreferredSize(new Dimension(0, 100));
        add(bottom, BorderLayout.SOUTH);

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


}