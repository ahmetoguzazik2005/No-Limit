import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MyJDBC{
    private static final DateTimeFormatter SQL_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    MyJDBC()throws SQLException{
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/myDB", "root", "password");
        statement = connection.createStatement();
    }
    void createStudyBlocksTable() throws SQLException{

        String sql =
                "CREATE TABLE IF NOT EXISTS StudyBlocks (" +
                        "  start_time DATETIME NOT NULL," +
                        "  finish_time DATETIME NOT NULL" +
                        ")";

        statement.executeUpdate(sql); // will be 0 for DDL like CREATE TABLE
    }
    void createDaysTable() throws SQLException{
        String sql =
                "CREATE TABLE IF NOT EXISTS Days (" +
                        "  day_date DATE PRIMARY KEY," +
                        "  hours INT NOT NULL DEFAULT 0," +
                        "  minutes INT NOT NULL DEFAULT 0," +
                        "  seconds INT NOT NULL DEFAULT 0" +
                        ")";

        statement.executeUpdate(sql);
    }


    public void addStudyBlock(LocalDateTime start, LocalDateTime end) throws SQLException {
        Timestamp startTs = Timestamp.valueOf(start);
        Timestamp endTs   = Timestamp.valueOf(end);

        String sql = "INSERT INTO StudyBlocks (start_time, finish_time) VALUES ('"
                + startTs + "', '" + endTs + "')";


        statement.executeUpdate(sql);
    }
    void addToDay(LocalDate day, int hours, int minutes, int seconds) throws SQLException {
        // Ensure the row exists (so UPDATE always works)
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("INSERT IGNORE INTO Days (day_date) VALUES ('" + day + "')");
        }


        try (Statement st = connection.createStatement()) {
            st.executeUpdate(
                    "UPDATE Days SET hours=" + hours + ", minutes=" + minutes + ", seconds=" + seconds +
                            " WHERE day_date='" + day + "'"
            );
        }
    }

    public ArrayList<StudyBlock> makeAListOfADaysStudyBlocks(LocalDate whichDay) throws SQLException {
        // start of the day
        LocalDateTime startOfDay = whichDay.atStartOfDay();             // yyyy-MM-dd 00:00:00
        // start of the next day
        LocalDateTime endOfDay   = whichDay.plusDays(1).atStartOfDay(); // yyyy-MM-dd+1 00:00:00

        // Build SQL string
        String query =
                "SELECT start_time, finish_time " +
                        "FROM StudyBlocks " +
                        "WHERE start_time >= '" + startOfDay.format(SQL_FORMAT) + "' " +
                        "AND start_time < '" + endOfDay.format(SQL_FORMAT) + "' " +
                        "ORDER BY start_time ASC, id ASC";

        ArrayList<StudyBlock> blocks = new ArrayList<>();
        // Execute
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // read columns

                java.sql.Timestamp startTs = resultSet.getTimestamp("start_time");
                java.sql.Timestamp endTs   = resultSet.getTimestamp("finish_time");

                // Convert to LocalDateTime
                LocalDateTime start = startTs.toLocalDateTime();
                LocalDateTime end   = endTs.toLocalDateTime();

                StudyBlock temp = new StudyBlock(start);
                temp.endTime = end;
                blocks.add(temp);


            }
            return blocks;
        }
    }

}
