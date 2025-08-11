import java.time.LocalDate;

public class Day {
    LocalDate whichDay;
    StudyBlock[] blocks;

    Day() {
        blocks = new StudyBlock[40];

    }

    void increaseCapacity(StudyBlock[] blocks) {// For the case if a day have more than 40 block
        StudyBlock[] temp = new StudyBlock[blocks.length + 20];
        for (int i = 0; i < blocks.length; i++) {
            temp[i] = blocks[i];
        }
        blocks = temp; // This only changes the local 'blocks' variable
    }


}
