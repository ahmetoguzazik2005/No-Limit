import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

public class Day {
    LocalDate whichDay;
    int hours;
    int minutes;
    int seconds;
    ArrayList<StudyBlock> blocks = new ArrayList<>();


    Day(LocalDate whichDay) throws SQLException {
        this.whichDay = whichDay;
        blocks = Main.m.makeAListOfADaysStudyBlocks(whichDay);
        calculateTotalHours();
    }

    public void calculateTotalHours(){
        Duration duration;
        for(StudyBlock block : blocks){
            duration = Duration.between(block.startTime, block.endTime);
            hours += (int) duration.toHours();
            minutes += ((int) duration.toMinutes()) % 60;
            seconds += ((int) duration.toSeconds()) % 60;
        }

    }




}
