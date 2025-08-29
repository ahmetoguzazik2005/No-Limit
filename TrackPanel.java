import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import javax.swing.*;

public class TrackPanel extends JPanel implements ActionListener {
    JPanel calendarPanel;
    // will contain center
    JPanel centerDays;
    JPanel buttonPanel;
    JLabel dateLabel;
    // updates what month will be shown
    JButton leftButton;
    JButton rightButton;
    // determines label words
    int monthId;
    int year;
    LocalDate today;

    // Calendar grid components
    JPanel calendarGridPanel;
    JPanel dayHeadersPanel;
    JPanel datesPanel;
    JButton[][] dayButtons; // 6 weeks x 7 days

    public static final String[] MONTHS = {
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };

    public static final String[] DAY_HEADERS = {
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    };

    TrackPanel() throws SQLException {
        setBackground(new Color(245, 245, 220)); // setting nicer backgrounds
        today = LocalDate.now();
        setLayout(new BorderLayout());

        calendarPanel = new JPanel();
        add(calendarPanel, BorderLayout.NORTH);

        determineDate();

        // Create and setup date label
        dateLabel = new JLabel(String.format("%s %d", MONTHS[monthId - 1], year));
        dateLabel.setMinimumSize(new Dimension(150, 35)); // Minimum size for "DECEMBER 2025"
        dateLabel.setPreferredSize(new Dimension(160, 35));

        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.add(dateLabel, BorderLayout.WEST);

        leftButton = new AnimatedPressButton("<");
        rightButton = new AnimatedPressButton(">");

        // lessens month by one
        leftButton.addActionListener(e -> {
            monthId = monthId - 1;
            if (monthId < 0) {
                monthId = 12;
                year = year - 1;
            }
            dateLabel.setText(String.format("%s %d", MONTHS[monthId - 1], year));
            try {
                updateCalendarDisplay();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // increases month by one
        rightButton.addActionListener(e -> {
            monthId = monthId + 1;
            if (monthId > 12) {
                monthId = 0;
                year = year + 1;
            }
            dateLabel.setText(String.format("%s %d", MONTHS[monthId - 1], year));
            try {
                updateCalendarDisplay();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));

        buttonPanel.add(leftButton);
        buttonPanel.add(rightButton);

        calendarPanel.add(buttonPanel, BorderLayout.EAST);

        // will work
        centerDays = new JPanel();
        add(centerDays, BorderLayout.CENTER);
        centerDays.setBackground(new Color(245, 245, 220));

        setupCalendarGrid();
        updateCalendarDisplay();
    }

    private void determineDate() {
        LocalDate currentDate = LocalDate.now();

        monthId = currentDate.getMonthValue();
        year = currentDate.getYear();
    }

    // puts the buttons into the right place
    private void setupCalendarGrid() {
        calendarGridPanel = new JPanel(new BorderLayout());
        calendarGridPanel.setBackground(Color.WHITE);
        calendarGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(calendarGridPanel, BorderLayout.CENTER);

        dayHeadersPanel = new JPanel(new GridLayout(1, 7, 1, 1));
        dayHeadersPanel.setBackground(Color.WHITE);

        // insert names on the top part
        for (String dayHeader : DAY_HEADERS) {
            JLabel headerLabel = new JLabel(dayHeader, SwingConstants.CENTER);
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 12f));
            headerLabel.setForeground(new Color(100, 100, 100));
            headerLabel.setPreferredSize(new Dimension(50, 25));
            headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            dayHeadersPanel.add(headerLabel);
        }

        calendarGridPanel.add(dayHeadersPanel, BorderLayout.NORTH);

        // Dates grid (6 weeks x 7 days = 42 buttons)
        datesPanel = new JPanel(new GridLayout(6, 7, 1, 1));
        datesPanel.setBackground(Color.WHITE);
        dayButtons = new JButton[6][7];

        for (int week = 0; week < 6; week++) {
            for (int day = 0; day < 7; day++) {
                JButton dayButton = new AnimatedPressButton("");
                dayButtons[week][day] = dayButton;
                datesPanel.add(dayButton);
            }
        }

        calendarGridPanel.add(datesPanel, BorderLayout.CENTER);
    }

    private void updateCalendarDisplay() throws SQLException {
        // Get the first day of the month and total days
        YearMonth yearMonth = YearMonth.of(year, monthId);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Get the day of week for the first day (Monday = 1, Sunday = 7)
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        // Calculate previous month details for filling leading empty cells
        YearMonth previousMonth = yearMonth.minusMonths(1);
        int daysInPreviousMonth = previousMonth.lengthOfMonth();

        // Clear all buttons first
        for (int week = 0; week < 6; week++) {
            for (int day = 0; day < 7; day++) {
                dayButtons[week][day].setText("");
                dayButtons[week][day].setBackground(Color.WHITE);
                dayButtons[week][day].setForeground(Color.BLACK);
                dayButtons[week][day].setEnabled(true);
            }
        }

        // Fill in the calendar
        int currentDay = 1;
        int nextMonthDay = 1;

        for (int week = 0; week < 6; week++) {
            for (int day = 0; day < 7; day++) {
                JButton button = dayButtons[week][day];
                int cellIndex = week * 7 + day + 1;

                if (cellIndex < firstDayOfWeek) {
                    // Previous month's days
                    int prevMonthDay = daysInPreviousMonth - (firstDayOfWeek - cellIndex - 1);
                    int prevMonth;
                    int prevYear;
                    if(monthId == 1) {
                        prevMonth = 12;
                        prevYear = year - 1;
                    }else{
                        prevMonth = monthId -1;
                        prevYear = year;
                    }
                    LocalDate prevDate = LocalDate.of(prevYear, prevMonth, prevMonthDay);
                    LocalTime totalTime = Main.m.getDayTotalTime(prevDate);
                    LocalTime goalTime = Main.m.getDayGoal(prevDate);
                    if(totalTime == null){
                        button.setBackground(Color.WHITE);
                        button.setText(String.valueOf(prevMonthDay));
                    } else if (goalTime.isAfter(totalTime)) {
                        button.setBackground(Color.red);
                        button.setText(String.valueOf(prevMonthDay));
                    }else{
                        button.setBackground(Color.green);
                        button.setText(String.valueOf(prevMonthDay ));
                    }
                    button.setForeground(Color.LIGHT_GRAY);

                } else if (currentDay <= daysInMonth) {
                    // Current month's days
                    LocalDate thisDate = LocalDate.of(year, monthId, currentDay);
                    LocalTime totalTime = Main.m.getDayTotalTime(thisDate);
                    LocalTime goalTime = Main.m.getDayGoal(thisDate);

                    if(totalTime == null){
                        button.setBackground(Color.WHITE);
                        button.setText(currentDay + "");
                    } else if (goalTime.isAfter(totalTime)) {
                        button.setBackground(Color.red);
                        button.setText(currentDay + "");
                    }else{
                        button.setBackground(Color.green);
                        button.setText(currentDay + "");
                    }

                    button.setForeground(Color.BLACK);
                    currentDay++;
                } else {


                    int nextMonth;
                    int nextYear;

                    if (monthId == 12) {
                        nextMonth = 1;
                        nextYear = year + 1;

                    }else{
                        nextMonth = monthId + 1;
                        nextYear = year;
                    }


                    // Next month's days
                    LocalDate nextDate = LocalDate.of(nextYear, nextMonth, nextMonthDay);
                    LocalTime totalTime = Main.m.getDayTotalTime(nextDate);
                    LocalTime goalTime = Main.m.getDayGoal(nextDate);

                    if(totalTime == null){
                        button.setBackground(Color.WHITE);
                        button.setText(nextMonthDay+"");
                    } else if (goalTime.isAfter(totalTime)) {
                        button.setBackground(Color.red);
                        button.setText(nextMonthDay+"");
                    }else{
                        button.setBackground(Color.green);
                        button.setText(nextMonthDay+"");
                    }

                    button.setForeground(Color.LIGHT_GRAY);
                    nextMonthDay++;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (e.getSource() == dayButtons[i][j]) {
                    String s = dayButtons[i][j].getText();
                    int day = Integer.parseInt(s);



                }
            }
        }
    }
}
