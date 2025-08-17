import java.time.LocalDateTime;
public abstract class Block {
    LocalDateTime startTime;
    LocalDateTime endTime;

    Block() {
        startTime = LocalDateTime.now();


    }
    void end(){
        endTime = LocalDateTime.now();
    }

}
