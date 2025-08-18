import java.time.LocalDateTime;
public abstract class Block {
    LocalDateTime startTime;
    LocalDateTime endTime;
    Block() {
        startTime = LocalDateTime.now();
    }
    Block(LocalDateTime startTime){
        this.startTime = startTime;
    }


    void end(){
        endTime = LocalDateTime.now();
    }

}
