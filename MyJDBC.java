import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



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
    void createTable() throws SQLException{
        if (isThereTable()){
            return;
        }
        String sql =
                "CREATE TABLE IF NOT EXISTS StudyBlocks (" +
                        "  start_time DATETIME NOT NULL," +
                        "  finish_time DATETIME NOT NULL" +
                        ")";

        statement.executeUpdate(sql); // will be 0 for DDL like CREATE TABLE
    }

    boolean isThereTable() throws SQLException{
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "studyBlocks", null)) {
            if (rs.next()) { // There is already a table
                return true;
            } else {
                return false; // no table yet
            }
        }
    }

    public void addStudyBlock(LocalDateTime start, LocalDateTime end) throws SQLException {
        Timestamp startTs = Timestamp.valueOf(start);
        Timestamp endTs   = Timestamp.valueOf(end);

        String sql = "INSERT INTO StudyBlocks (start_time, finish_time) VALUES ('"
                + startTs + "', '" + endTs + "')";


        statement.executeUpdate(sql);
    }

    public void makeAListOfADaysStudyBlocks(LocalDate whichDay) throws SQLException {
        // start of the day
        LocalDateTime startOfDay = whichDay.atStartOfDay();             // yyyy-MM-dd 00:00:00
        // start of the next day
        LocalDateTime endOfDay   = whichDay.plusDays(1).atStartOfDay(); // yyyy-MM-dd+1 00:00:00

        // Build SQL string
        String query = "SELECT * FROM StudyBlocks " +
                "WHERE start_time >= '" + startOfDay.format(SQL_FORMAT) + "' " +
                "AND start_time < '" + endOfDay.format(SQL_FORMAT) + "'";


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

            }
        }
    }

}
