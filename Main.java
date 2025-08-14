import javax.swing.*;
import java.sql.SQLException;


public class Main {


    public static void main(String[] args) throws RuntimeException {
        SwingUtilities.invokeLater(() -> {

            MyJDBC m;
            try {
                m = new MyJDBC();
                m.createTable();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            MyFrame myFrame = new MyFrame();
            myFrame.setTitle("No Limit"); // Konusuruz ismi

            // Main Panel - will be cont.
            JPanel panel = new JPanel();
            myFrame.add(panel);



        });

    }


}