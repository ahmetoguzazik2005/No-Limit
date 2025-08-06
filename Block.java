import java.time.LocalDateTime;
public abstract class Block {
    LocalDateTime startTime;
    LocalDateTime endTime;

    Block() {
        startTime = LocalDateTime.now();
        System.out.println(startTime);
        // merge 31

    }
    void end(){
        endTime = LocalDateTime.now();
    }

}
