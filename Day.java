import java.time.LocalDate;

public class Day {
    LocalDate whichDay;
    StudyBlock[] blocks;

    Day() {
        StudyBlock[] blocks = new StudyBlock[40];

    }

    void increaseCapacity(){ // For the case if a day have more than 40 block -> we know that
        StudyBlock[] temp = new StudyBlock[blocks.length+20];


    }
}
