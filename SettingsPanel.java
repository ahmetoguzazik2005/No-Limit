import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SettingsPanel extends JPanel implements ActionListener, ChangeListener {

    private AnimatedPressButton setGoalButton;
    private AnimatedPressButton resetButton;
    private JSpinner hoursSpinner;
    private JSpinner minutesSpinner;

    private JLabel previewLabel;   // live “Goal: Xh Ym (ZZZ min)”
    private JLabel statusLabel;    // success / error line

    private int hours;
    private int minutes;

    public SettingsPanel() {
        // Background + outer padding
        setLayout(new GridBagLayout());
        setBackground(new Color(232, 244, 251)); // soft blue tint
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // “Card” container
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // subtle gradient
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255),
                        0, getHeight(), new Color(245, 249, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(210, 225, 240), 1, true),
                new EmptyBorder(18, 18, 18, 18)));
        GridBagConstraints root = gbc(0,0,1,1,1,1);
        root.fill = GridBagConstraints.BOTH;
        add(card, root);

        // Header
        JLabel title = new JLabel("Daily Focus Goal");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitle = new JLabel("Set a realistic target to stay consistent.");
        subtitle.setForeground(new Color(90, 110, 130));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);

        GridBagConstraints c = gbc(0,0,1,1,1,0);
        c.insets = new Insets(0,0,16,0);
        card.add(header, c);

        // Form (labels + spinners)
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        JLabel hLabel = new JLabel("Hours");
        hLabel.setDisplayedMnemonic('H');
        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        ((JSpinner.DefaultEditor) hoursSpinner.getEditor()).getTextField().setColumns(3);
        hoursSpinner.setToolTipText("0–23");
        hLabel.setLabelFor(hoursSpinner);

        JLabel mLabel = new JLabel("Minutes");
        mLabel.setDisplayedMnemonic('M');
        minutesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        ((JSpinner.DefaultEditor) minutesSpinner.getEditor()).getTextField().setColumns(3);
        minutesSpinner.setToolTipText("0–59");
        mLabel.setLabelFor(minutesSpinner);

        // nice row layout
        GridBagConstraints f = gbc(0,0,1,1,0,0);
        f.insets = new Insets(6, 0, 6, 12);
        f.anchor = GridBagConstraints.LINE_START;
        form.add(hLabel, f);

        f = gbc(1,0,1,1,0,0);
        form.add(hoursSpinner, f);

        f = gbc(2,0,1,1,0,0);
        f.insets = new Insets(6, 18, 6, 12);
        form.add(mLabel, f);

        f = gbc(3,0,1,1,1,0);
        form.add(minutesSpinner, f);

        // Live preview line
        previewLabel = new JLabel();
        previewLabel.setFont(previewLabel.getFont().deriveFont(Font.PLAIN, 14f));
        previewLabel.setForeground(new Color(70, 90, 110));

        f = gbc(0,1,4,1,1,0);
        f.insets = new Insets(4, 0, 0, 0);
        form.add(previewLabel, f);

        c = gbc(0,1,1,1,1,0);
        c.insets = new Insets(0,0,16,0);
        card.add(form, c);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        setGoalButton = new AnimatedPressButton("Set Goal");
        setGoalButton.setBackground(new Color(66, 133, 244)); // Google-ish blue
        setGoalButton.setForeground(Color.WHITE);
        setGoalButton.addActionListener(this);
        setGoalButton.setEnabled(false); // enabled only when > 0 min

        resetButton = new AnimatedPressButton("Reset");
        resetButton.setBackground(new Color(230, 238, 246));
        resetButton.setForeground(new Color(40, 60, 80));
        resetButton.addActionListener(this);

        buttons.add(resetButton);
        buttons.add(setGoalButton);

        c = gbc(0,2,1,1,1,0);
        card.add(buttons, c);

        // Status line
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(25, 135, 84)); // green when OK
        c = gbc(0,3,1,1,1,0);
        c.insets = new Insets(10, 0, 0, 0);
        card.add(statusLabel, c);

        // Listeners
        hoursSpinner.addChangeListener(this);
        minutesSpinner.addChangeListener(this);

        // Enter key = “Set Goal”
        InputMap im = card.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = card.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
        am.put("submit", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (setGoalButton.isEnabled()) setGoalButton.doClick();
            }
        });

        updatePreviewAndButton();
    }

    private static GridBagConstraints gbc(int x, int y, int w, int h, double wx, double wy) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y; g.gridwidth = w; g.gridheight = h;
        g.weightx = wx; g.weighty = wy;
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private void updatePreviewAndButton() {
        int h = (int) hoursSpinner.getValue();
        int m = (int) minutesSpinner.getValue();
        int total = h * 60 + m;
        previewLabel.setText(String.format("Goal: %dh %dm  (%d min total)", h, m, total));
        setGoalButton.setEnabled(total > 0);
        // Clear status when editing
        statusLabel.setText(" ");
        statusLabel.setForeground(new Color(25, 135, 84));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == setGoalButton) {
            hours = (int) hoursSpinner.getValue();
            minutes = (int) minutesSpinner.getValue();

            // Fire a property change so other panels can react if needed
            int newTotal = hours * 60 + minutes;
            LocalTime localTime = LocalTime.of(hours, minutes);
            try {
                Main.m.setDailyGoal(LocalDate.now(), localTime);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            firePropertyChange("goalMinutes", -1, newTotal);

            statusLabel.setForeground(new Color(25, 135, 84));
            statusLabel.setText(String.format("Saved! Daily goal set to %dh %dm.", hours, minutes));
        } else if (src == resetButton) {
            hoursSpinner.setValue(0);
            minutesSpinner.setValue(0);
            hours = 0; minutes = 0;
            statusLabel.setForeground(new Color(108, 117, 125));
            statusLabel.setText("Cleared. No daily goal.");
            updatePreviewAndButton();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // Keep values in bounds and update live preview
        int h = (int) hoursSpinner.getValue();
        int m = (int) minutesSpinner.getValue();
        if (h < 0) hoursSpinner.setValue(0);
        if (h > 23) hoursSpinner.setValue(23);
        if (m < 0) minutesSpinner.setValue(0);
        if (m > 59) minutesSpinner.setValue(59);
        updatePreviewAndButton();
    }

    // Optional getters if you want to read them from elsewhere
    public int getHours()   { return hours; }
    public int getMinutes() { return minutes; }
}