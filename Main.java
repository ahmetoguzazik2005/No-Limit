import javax.swing.*;

public class Main {

    static boolean fullScreen = false;

    public static void main(String[] args) {
        MyFrame myFrame = new MyFrame();
        myFrame.setTitle("No Limit"); // Konusuruz ismi okk

        // Main Panel - will be cont.
        JPanel panel = new JPanel();
        myFrame.add(panel);
    }

}