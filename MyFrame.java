import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {
    MyFrame() {
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        // Normal size at the start
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());

        JPanel leftBar = new JPanel();
        JPanel mainScreen = new JPanel();




        this.setVisible(true);

    }

}
