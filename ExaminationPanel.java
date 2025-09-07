import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExaminationPanel extends JPanel { // For the detailed day look
    // Note for us: prepareEverything->set>setProgressBar

    // ADD: Professional color constants (same as TrackPanel)
    public static final Color SUCCESS_GREEN = new Color(46, 125, 50);
    public static final Color ALERT_RED = new Color(198, 40, 40);
    public static final Color ACCENT_BLUE = new Color(25, 118, 210);
    public static final Color WARM_BEIGE = new Color(245, 245, 220);
    public static final Color LIGHT_GRAY = new Color(248, 249, 250);
    public static final Color BORDER_GRAY = new Color(224, 224, 224);
    public static final Color DARK_GRAY = new Color(66, 66, 66);
    public static final Color INACTIVE_TEXT = new Color(158, 158, 158);

    // general part - UNCHANGED
    public static LocalDate whichDay;
    ArrayList<StudyBlock> blocks;

    boolean addBlockOn;
    // goal and total layout
    JPanel buttonPanel;
    AnimatedPressButton deleteBlock;
    AnimatedPressButton addBlock;
    static JPanel labelPanel;
    JLabel labelTotalString;
    JPanel centerPanel;
    JPanel progressBarPanel;
    JPanel addBlockPanel;
    CardLayout cardLayout;
    static JLabel labelTotalInt;
    JLabel labelGoalString;
    static JLabel labelGoalInt;
    JLabel dateLabelAtTop;
    static LocalTime totalTime;
    static LocalTime goalTime;

    // table layout
    JTable table;
    public static DefaultTableModel model;
    JScrollPane scrollPane;
    JProgressBar progressBar;

    // NEW: for hover highlight
    private int hoverRow = -1;

    ExaminationPanel() throws SQLException {
        addBlockOn = false;
        setLayout(new BorderLayout(10, 10));
        setBackground(WARM_BEIGE); // ADD: Professional background
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // ADD: Padding

        // IMPROVED: Professional button panel (buttons themselves untouched)
        buttonPanel = new JPanel();
        buttonPanel.setBackground(LIGHT_GRAY); // ADD: Light gray background
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        deleteBlock = new AnimatedPressButton("Delete Block"); // UNCHANGED
        addBlock = new AnimatedPressButton("Add Block"); // UNCHANGED

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0)); // IMPROVED: Better spacing
        buttonPanel.add(addBlock);
        buttonPanel.add(deleteBlock);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // IMPROVED: Professional label panel
        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        labelPanel.setBackground(Color.WHITE); // ADD: Clean white background
        labelPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // IMPROVED: Professional date label
        dateLabelAtTop = new JLabel();
        dateLabelAtTop.setFont(dateLabelAtTop.getFont().deriveFont(Font.BOLD, 16f));
        dateLabelAtTop.setForeground(ACCENT_BLUE);
        dateLabelAtTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        labelPanel.add(dateLabelAtTop);

        // IMPROVED: Professional total time labels
        labelTotalString = new JLabel("Total Time: ");
        labelTotalString.setFont(labelTotalString.getFont().deriveFont(Font.BOLD, 14f));
        labelTotalString.setForeground(DARK_GRAY);
        labelPanel.add(labelTotalString);

        labelTotalInt = new JLabel();
        labelTotalInt.setFont(labelTotalInt.getFont().deriveFont(Font.BOLD, 14f));
        labelTotalInt.setForeground(ACCENT_BLUE);
        labelTotalInt.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        labelPanel.add(labelTotalInt);

        // IMPROVED: Professional goal time labels
        labelGoalString = new JLabel("Goal Time: ");
        labelGoalString.setFont(labelGoalString.getFont().deriveFont(Font.BOLD, 14f));
        labelGoalString.setForeground(DARK_GRAY);
        labelPanel.add(labelGoalString);

        labelGoalInt = new JLabel();
        labelGoalInt.setFont(labelGoalInt.getFont().deriveFont(Font.BOLD, 14f));
        labelGoalInt.setForeground(SUCCESS_GREEN);
        labelPanel.add(labelGoalInt);
        this.add(labelPanel, BorderLayout.NORTH);

        progressBarPanel = new JPanel(); // Center it perfectly
        progressBarPanel.setBackground(Color.WHITE);
        progressBarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20) // bigger padding
        ));

        // Putting 2 panels to the upper center with card layout
        cardLayout = new CardLayout();
        progressBarPanel.setLayout(cardLayout);

        // initial visible part of progressBar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(450, 60)); // bigger
        progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD, 16f)); // larger text
        progressBar.setBackground(Color.WHITE);
        progressBar.setStringPainted(true);
        progressBar.setOpaque(false);
        progressBar.setUI(new RoundedProgressBarUI());

        addBlockPanel = new TimeSelectionPanel();
        // Adding these to card layout
        progressBarPanel.add(progressBar, "ProgressBar");
        progressBarPanel.add(addBlockPanel, "AddBlockPanel");
        cardLayout.show(progressBarPanel, "ProgressBar");

        // --- Table + ScrollPane ---
        model = new DefaultTableModel();
        model.addColumn("Start Time");
        model.addColumn("Finish Time");
        model.addColumn("Worked Time");

        // Single JTable instance: also handles zebra/hover in prepareRenderer
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                boolean selected = isRowSelected(row);
                Color even = new Color(252, 252, 252);
                Color odd = new Color(245, 247, 250);
                Color hover = new Color(232, 240, 254);

                if (!selected) {
                    c.setBackground((row % 2 == 0) ? even : odd);
                    if (row == hoverRow)
                        c.setBackground(hover);
                }
                if (c instanceof JComponent jc) {
                    // left/right padding
                    jc.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }
                c.setForeground(selected ? Color.WHITE : DARK_GRAY);
                return c;
            }
        };
        table.setFont(table.getFont().deriveFont(13f));
        table.setRowHeight(25);
        table.setGridColor(BORDER_GRAY);
        table.setSelectionBackground(ACCENT_BLUE.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        table.setForeground(DARK_GRAY);

        JTableHeader th = table.getTableHeader();
        th.setBackground(LIGHT_GRAY);
        th.setForeground(DARK_GRAY);
        th.setFont(th.getFont().deriveFont(Font.BOLD, 12f));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_GRAY));

        scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setBackground(LIGHT_GRAY);
        scrollPane.getHorizontalScrollBar().setBackground(LIGHT_GRAY);

        // --- Center panel with GridBagLayout (add each component once) ---
        centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;

        // Progress section (top ~30%)
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.30;
        centerPanel.add(progressBarPanel, gbc);

        // Table section (bottom ~70%)
        gbc.gridy = 1;
        gbc.weighty = 0.70;
        installTableStyle(); // style + behavior
        centerPanel.add(scrollPane, gbc);

        // Attach center panel to main layout
        add(centerPanel, BorderLayout.CENTER);

        deleteBlock.addActionListener(e -> {
            int viewRow = table.getSelectedRow();

            if (viewRow >= 0) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                String startTime = (String) model.getValueAt(modelRow, 0);
                String endTime = (String) model.getValueAt(modelRow, 1);
                String datePartBeginning = whichDay.toString();

                LocalTime ltBegin = LocalTime.parse(startTime);
                LocalTime ltEnd = LocalTime.parse(endTime);
                LocalTime difference = MyJDBC.difference(ltBegin, ltEnd);

                LocalDate date = LocalDate.parse(datePartBeginning);
                LocalTime time = ltBegin;

                try {
                    Main.m.deleteBlock(date, time);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                try {
                    Main.m.removeFromTheDay(date, difference);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                try {
                    MyFrame.trackPanel.updateCalendarDisplay();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                model.removeRow(viewRow);
                try {
                    set();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });
        addBlock.addActionListener(e -> {
            if (addBlockOn) {
                addBlockOn = false;
                cardLayout.show(progressBarPanel, "ProgressBar");
                addBlock.setText("Add Block");

            } else {
                addBlockOn = true;
                cardLayout.show(progressBarPanel, "AddBlockPanel");
                addBlock.setText("Progress Bar");
            }

        });
    }

    // Styles + behavior for the existing table (no reassignments)
    private void installTableStyle() {
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(new Color(238, 238, 238)); // soft divider
        table.setRowHeight(28);

        // Header polish
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_GRAY));

        // Center-align the time columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        // Monospace + emphasize the Worked Time column for readability
        final Font mono = new Font("JetBrains Mono", Font.PLAIN, 13).getFamily().equals("Dialog")
                ? new Font(Font.MONOSPACED, Font.PLAIN, 13)
                : new Font("JetBrains Mono", Font.PLAIN, 13);

        DefaultTableCellRenderer worked = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus,
                    int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                c.setFont(mono.deriveFont(Font.BOLD));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(worked);

        // Column sizing
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);

        // Hover tracking (uses panel field hoverRow)
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r != -1 && r != hoverRow) {
                    hoverRow = r;
                    table.repaint();
                }
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoverRow = -1;
                table.repaint();
            }
        });

        // Subtle scrollpane polish
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Handy tooltip
        table.setToolTipText("Tip: Click a row to select it, then use Delete Block.");
    }

    void setProgressBar() throws SQLException {
        int totalSeconds = totalTime.toSecondOfDay();
        int goalSeconds = Math.max(1, goalTime.toSecondOfDay()); // avoid /0
        if (totalSeconds >= goalSeconds) {
            progressBar.setValue(100);
        } else {
            double totalDouble = totalSeconds;
            double goalDouble = goalSeconds;
            double result = (totalDouble * 100) / goalDouble;
            progressBar.setValue((int) result);
        }
    }

    // Note for us: prepareEverything->set>setProgressBar
    // IMPROVED: Enhanced date preparation
    void prepareEverything(LocalDate whichDay) throws SQLException {
        ExaminationPanel.whichDay = whichDay;
        set();
        setProgressBar();
        populateTable();

        // Professional date formatting
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        String formattedDate = whichDay.format(dateFormatter);
        dateLabelAtTop.setText(formattedDate);

        // Color coding for today vs other days

        // getting rid of unnecessary if condiiton
        dateLabelAtTop.setText(formattedDate);
        dateLabelAtTop.setForeground(ACCENT_BLUE);

    }

    public static void set() throws SQLException {
        totalTime = Main.m.getDayTotalTime(whichDay);
        goalTime = Main.m.getDayGoal(whichDay);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTotalTime = totalTime.format(formatter);
        String formattedGoalTime = goalTime.format(formatter);

        labelTotalInt.setText(formattedTotalTime);
        labelGoalInt.setText(formattedGoalTime);

        if (totalTime != null && goalTime != null) {
            if (goalTime.isAfter(totalTime)) {
                // Goal not met - red colors
                labelTotalInt.setForeground(ALERT_RED);
                labelGoalInt.setForeground(DARK_GRAY);
                labelPanel.setBackground(new Color(255, 248, 248)); // Very light red
            } else {
                // Goal met - green colors
                labelTotalInt.setForeground(SUCCESS_GREEN);
                labelGoalInt.setForeground(DARK_GRAY);
                labelPanel.setBackground(new Color(248, 255, 248)); // Very light green
            }
        } else {
            // No data - default colors
            labelTotalInt.setForeground(DARK_GRAY);
            labelGoalInt.setForeground(DARK_GRAY);
            labelPanel.setBackground(Color.WHITE);
        }
    }

    public static String verbalWorkedTime(String workString) {
        String answer = "";
        String[] parts = workString.split(":");

        if (parts[0].equals("00")) {
        } else if (parts[0].equals("01")) {
            answer += "1 hour ";
        } else if (parts[0].startsWith("0")) {
            answer += parts[0].substring(1) + " hours ";
        } else {
            answer += parts[0] + " hours ";
        }

        if (parts[1].equals("00")) {
        } else if (parts[1].equals("01")) {
            answer += "1 minute ";
        } else if (parts[1].startsWith("0")) {
            answer += parts[1].substring(1) + " minutes ";
        } else {
            answer += parts[1] + " minutes ";
        }

        if (parts[2].equals("00")) {
        } else if (parts[2].equals("01")) {
            answer += "1 second ";
        } else if (parts[2].startsWith("0")) {
            answer += parts[2].substring(1) + " seconds ";
        } else {
            answer += parts[2] + " seconds ";
        }

        if (answer.equals("")) {
            return "None";
        }
        return answer;
    }

    // UNCHANGED: populateTable method stays exactly the same
    void populateTable() throws SQLException {
        blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (StudyBlock block : blocks) {
            String duration = String.format("%02d:%02d:%02d",
                    Duration.between(block.startTime, block.endTime).toHours(),
                    Duration.between(block.startTime, block.endTime).toMinutesPart(),
                    Duration.between(block.startTime, block.endTime).toSecondsPart());
            duration = verbalWorkedTime(duration);

            Object[] rowData = {
                    block.startTime.toLocalTime().format(formatter),
                    block.endTime.toLocalTime().format(formatter),
                    duration
            };
            model.addRow(rowData);
        }
    }
}