import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ButtonWithADate extends AnimatedPressButton {
    LocalDate date;
    ArrayList<StudyBlock> blocks;
    ButtonWithADate(String text) {
        super(text);

    }
}
