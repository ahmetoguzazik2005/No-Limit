import javax.swing.Timer;

// only for testing components easily
public class JunkMain {
    static int seconds = 59;
    static int minutes = 0;
    static int hours = 0;

    public static void main(String[] args) {
        Timer timer = new Timer(1000, e -> {
            seconds++;
        });
        timer.start();

        while (true) {
            if (seconds == 60) {
                minutes++;
                seconds = 0;
            }
            if (minutes == 60) {
                hours++;
                minutes = 0;
            }
            System.out.println(hours + ":" + minutes + ":" + seconds);
        }
    }
}
