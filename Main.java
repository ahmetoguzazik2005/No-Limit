import javax.swing.*;
import java.sql.SQLException;

public class Main {
    static MyJDBC m;

    public static void main(String[] args) throws RuntimeException {
        SwingUtilities.invokeLater(() -> {

             try {
                m = new MyJDBC();
                 m.createStudyBlocksTable();
                 m.createDaysTable();

             } catch (SQLException e) {
                 throw new RuntimeException(e);
             }

            MyFrame myFrame = null;
            try {
                myFrame = new MyFrame();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            myFrame.setTitle("No Limit"); // Konusuruz ismi

            // Main Panel - will be cont.
            JPanel panel = new JPanel();
            myFrame.add(panel);

        });
    }
}