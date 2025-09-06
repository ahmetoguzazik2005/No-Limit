import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class MyJDBC {
    private static final DateTimeFormatter SQL_FORMAT = // For local date time
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SQL_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");// For local date
    private static final DateTimeFormatter SQL_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    Connection connection;
    Statement statement;
    ResultSet resultSet;

    MyJDBC() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/myDB", "root", "password");
        statement = connection.createStatement();
    }

    void createStudyBlocksTable() throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS StudyBlocks (" +
                "  start_time DATETIME NOT NULL," +
                "  finish_time DATETIME NOT NULL" +
                ")";

        statement.executeUpdate(sql); // will be 0 for DDL like CREATE TABLE
    }

    void createDaysTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Days (" +
                "  day_date DATE PRIMARY KEY," +
                "  total_time TIME NOT NULL DEFAULT '00:00:00'," +
                "  goal_time  TIME NOT NULL DEFAULT '00:01:00'" +
                ")";

        statement.executeUpdate(sql);
    }

    public void addStudyBlock(LocalDateTime start, LocalDateTime end) throws SQLException {

        String sql = "INSERT INTO StudyBlocks (start_time, finish_time) VALUES ('"
                + start.format(SQL_FORMAT) + "', '" + end.format(SQL_FORMAT) + "')";

        statement.executeUpdate(sql);
    }


    public void addToDay(LocalDate day, LocalTime time) throws SQLException {
        // Ensure the row exists (so UPDATE always works)
        statement.executeUpdate("INSERT IGNORE INTO Days (day_date) VALUES ('" + day.format(SQL_DATE) + "')"); // create

        String sql = "UPDATE Days " +
                "SET total_time = ADDTIME(total_time, '" + time.format(SQL_TIME) + "') " +
                "WHERE day_date = '" + day.format(SQL_DATE) + "'";

        statement.executeUpdate(sql);

    }
    public void addToDay(LocalDate day) throws SQLException {

        String sql = "Insert into Days (day_date) VALUES ('" + day.format(SQL_DATE) + "')";

        statement.executeUpdate(sql);

    }

    public void addToDayImprovedCaller(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime)
            throws SQLException {
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) {
            addToDay(startDate, difference(startTime, endTime));
            return;
        } else {
            LocalTime endOfTheDay = LocalTime.of(23, 59, 59);
            LocalTime startOfTheDay = LocalTime.of(0, 0, 0);
            for (int i = 0; i < days; i++) {
                if (i == days - 1) {
                    addToDay(startDate.plusDays(i), difference(startOfTheDay, endTime));
                } else {
                    addToDay(startDate.plusDays(i), difference(startTime, endOfTheDay));
                }

            }
        }

    }

    /*
     * public static LocalTime difference(LocalTime start, LocalTime end) {
     * if(start.isBefore(end)) {
     * System.out.println("true");
     * }else{
     * System.out.println("false");
     * }
     * long seconds = ChronoUnit.SECONDS.between(start, end);
     * return LocalTime.ofSecondOfDay(seconds);
     * }
     */

    public static LocalTime difference(LocalTime start, LocalTime end) {
        int sec1 = start.getSecond();
        int sec2 = end.getSecond();
        int min1 = start.getMinute();
        int min2 = end.getMinute();
        int hour1 = start.getHour();
        int hour2 = end.getHour();

        int newMin;
        int newSec;
        int newHour;
        int minDec = 0;
        int hourDec = 0;

        if (sec2 >= sec1) {
            newSec = sec2 - sec1;
        } else {
            newSec = sec2 + 60 - sec1;
            minDec++;
        }

        if (min2 >= min1) {
            newMin = min2 - min1;
            if (minDec > 0) {
                newMin = newMin - minDec;
                if (newMin < 0) {
                    newMin = 59;
                    hourDec++;
                }

            }
        } else {
            newMin = min2 + 60 - min1;
            hourDec++;
            if (minDec > 0) {
                newMin = newMin - minDec;
            }

        }
        newHour = hour2 - hour1 - hourDec;

        LocalTime newTime = LocalTime.of(newHour, newMin, newSec);
        return newTime;

    }

    public ArrayList<StudyBlock> makeAListOfADaysStudyBlocks(LocalDate whichDay) throws SQLException {
        // start of the day
        LocalDateTime startOfDay = whichDay.atStartOfDay(); // yyyy-MM-dd 00:00:00
        // start of the next day
        LocalDateTime endOfDay = whichDay.plusDays(1).atStartOfDay(); // yyyy-MM-dd+1 00:00:00

        // Build SQL string
        String query = "SELECT start_time, finish_time " +
                "FROM StudyBlocks " +
                "WHERE start_time >= '" + startOfDay.format(SQL_FORMAT) + "' " +
                "AND start_time < '" + endOfDay.format(SQL_FORMAT) + "' " +
                "ORDER BY start_time ASC, finish_time ASC";// id word was causing problem

        ArrayList<StudyBlock> blocks = new ArrayList<>();
        // Execute
        resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            // read columns

            java.sql.Timestamp startTs = resultSet.getTimestamp("start_time");
            java.sql.Timestamp endTs = resultSet.getTimestamp("finish_time");

            // Convert to LocalDateTime
            LocalDateTime start = startTs.toLocalDateTime();
            LocalDateTime end = endTs.toLocalDateTime();

            StudyBlock temp = new StudyBlock(start);
            temp.endTime = end;
            blocks.add(temp);

        }
        return blocks;

    }

    public LocalTime getDayGoal(LocalDate day) throws SQLException {
        String sql = "SELECT goal_time " +
                "FROM Days " +
                "WHERE day_date = '" + day.format(SQL_DATE) + "'";
        resultSet = statement.executeQuery(sql);

        if (resultSet.next()) {
            java.sql.Time t = resultSet.getTime("goal_time");
            if (t != null) {
                return t.toLocalTime();
            } else {
                return null;
            }
        }
        return null;
    }

    public void setDailyGoal(LocalDate day, LocalTime time) throws SQLException {
        Time sqlTime = Time.valueOf(time);
        String sql = "UPDATE Days " +
                "SET goal_time = '" + sqlTime + "'"
                + " WHERE day_date = '" + day.format(SQL_DATE) + "'";
        statement.executeUpdate(sql);
    }

    public LocalTime getDayTotalTime(LocalDate day) throws SQLException {
        String sql = "SELECT total_time " +
                "FROM Days " +
                "WHERE day_date = '" + day.format(SQL_DATE) + "'";
        resultSet = statement.executeQuery(sql);

        if (resultSet.next()) {
            java.sql.Time t = resultSet.getTime("total_time");
            if (t != null) {
                return t.toLocalTime();
            } else {
                return null;
            }
        }
        return null;
    }

    public void deleteBlock(LocalDate day, LocalTime time) throws SQLException {
        LocalDateTime combined = LocalDateTime.of(day, time);
        System.out.println(combined);
        String sql = "DELETE FROM StudyBlocks WHERE start_time = '"
                + combined.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";
        statement.executeUpdate(sql);
    }

    void removeFromTheDay(LocalDate day, LocalTime difference) throws SQLException {
        System.out.println("Removed: " + difference.toString());
        String sql = "UPDATE Days " +
                "SET total_time = SUBTIME(total_time, '" + difference.format(SQL_TIME) + "') " +
                "WHERE day_date = '" + day.format(SQL_DATE) + "'";

        statement.executeUpdate(sql);

    }
    public boolean doesDateExist(LocalDate date) throws SQLException {
        // Format the LocalDate to SQL DATE format (yyyy-MM-dd)
        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Build the SQL query (no prepared statement)
        String sql = "SELECT EXISTS (SELECT 1 FROM Days WHERE day_date = '" + dateString + "')";

        // Execute the query
        resultSet = statement.executeQuery(sql);

        // If there's a result, check if it returns 1 (true) or 0 (false)
        if (resultSet.next()) {
            return resultSet.getInt(1) == 1;
        }

        return false; // Default if something unexpected happens
    }

}
