import java.sql.*;

public class MyJDBC {
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    MyJDBC() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/myDB", "root", "password");// make the
                                                                                                         // credentials
                                                                                                         // same for us
        statement = connection.createStatement();
    }

    void createTable() throws SQLException {
        if (isThereTable()) {
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS StudyBlocks (" +
                "  start_time DATETIME NOT NULL," +
                "  finish_time DATETIME NOT NULL" +
                ")";

        statement.executeUpdate(sql); // will be 0 for DDL like CREATE TABLE
    }

    boolean isThereTable() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "studyBlocks", null)) {
            if (rs.next()) { // There is already a table
                return true;
            } else {
                return false; // no table yet
            }
        }
    }

    void addStudyBlock(String start_time, String finish_time) throws SQLException {
        statement.execute("INSERT INTO studyBlocks");

    }
}
