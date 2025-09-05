import javax.swing.*;
import java.awt.*;

public class TimeSelectionPanel extends JPanel {
    private JComboBox<String> startHour, startMinute, startSecond;
    private JComboBox<String> finishHour, finishMinute, finishSecond;
    private JButton confirmButton;
    private JLabel resultLabel;

    public TimeSelectionPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ---- START TIME ----
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Start Time (HH:MM:SS):"), gbc);

        startHour   = createEditableCombo(23);
        startMinute = createEditableCombo(59);
        startSecond = createEditableCombo(59);

        gbc.gridx = 1; add(startHour, gbc);
        gbc.gridx = 2; add(startMinute, gbc);
        gbc.gridx = 3; add(startSecond, gbc);

        // ---- FINISH TIME ----
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Finish Time (HH:MM:SS):"), gbc);

        finishHour   = createEditableCombo(23);
        finishMinute = createEditableCombo(59);
        finishSecond = createEditableCombo(59);

        gbc.gridx = 1; add(finishHour, gbc);
        gbc.gridx = 2; add(finishMinute, gbc);
        gbc.gridx = 3; add(finishSecond, gbc);

        // ---- CONFIRM BUTTON ----
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        confirmButton = new JButton("Confirm");
        add(confirmButton, gbc);

        // ---- RESULT LABEL ----
        gbc.gridy = 3;
        resultLabel = new JLabel(" ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(resultLabel, gbc);

        confirmButton.addActionListener(e -> handleConfirmation());
    }

    /** Create an editable combo box with values 00..maxInclusive */
    private JComboBox<String> createEditableCombo(int maxInclusive) {
        String[] vals = new String[maxInclusive + 1];
        for (int i = 0; i <= maxInclusive; i++) {
            vals[i] = String.format("%02d", i);
        }
        JComboBox<String> combo = new JComboBox<>(vals);
        combo.setEditable(true); // <-- lets user type
        combo.setSelectedIndex(0);
        // Optional: commit typed value to 2-digit formatting on focus loss
        combo.getEditor().getEditorComponent().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                normalizeComboText(combo, maxInclusive);
            }
        });
        return combo;
    }

    /** Read, validate, and normalize text in an editable combo (00..max). Returns int value. */
    private int readAndNormalize(JComboBox<String> combo, int max) throws NumberFormatException {
        Object item = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
        String text = (item == null ? "" : item.toString().trim());
        int val = Integer.parseInt(text); // may throw NumberFormatException
        if (val < 0 || val > max) throw new NumberFormatException();
        // normalize to 2 digits and reflect back into the combo
        String normalized = String.format("%02d", val);
        combo.setSelectedItem(normalized);
        return val;
    }

    /** Ensure current content is valid 0..max and formatted as two digits; if invalid, keep as-is */
    private void normalizeComboText(JComboBox<String> combo, int max) {
        try { readAndNormalize(combo, max); } catch (Exception ignored) {}
    }

    private void handleConfirmation() {
        try {
            // Start time
            int sh = readAndNormalize(startHour,   23);
            int sm = readAndNormalize(startMinute, 59);
            int ss = readAndNormalize(startSecond, 59);
            // Finish time
            int fh = readAndNormalize(finishHour,   23);
            int fm = readAndNormalize(finishMinute, 59);
            int fs = readAndNormalize(finishSecond, 59);

            int start = sh * 3600 + sm * 60 + ss;
            int finish = fh * 3600 + fm * 60 + fs;

            if (finish <= start) {
                resultLabel.setText("❌ Finish time must be after start time!");
                resultLabel.setForeground(Color.RED);
            } else {
                resultLabel.setText(String.format(
                        "✅ Selected range: %02d:%02d:%02d → %02d:%02d:%02d",
                        sh, sm, ss, fh, fm, fs
                ));
                resultLabel.setForeground(new Color(0, 128, 0));
            }
        } catch (NumberFormatException ex) {
            resultLabel.setText("⚠ Please enter valid numbers (00–23 for hours, 00–59 for min/sec).");
            resultLabel.setForeground(new Color(200, 120, 0));
        }
    }

    // Test the panel
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Time Selection Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(420, 220);
            frame.setLocationRelativeTo(null);
            frame.add(new TimeSelectionPanel());
            frame.setVisible(true);
        });
    }
}