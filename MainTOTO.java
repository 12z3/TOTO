package task.TOTO.Projects;

import training.Methods;

import java.io.IOException;
import java.time.LocalDateTime;

public class MainTOTO extends Methods {
    public static void main(String[] args) throws IOException {

        long start = getStartTime();
        TOTO toto = new TOTO();
        toto.play();
        long end = getEndTime(start);
        System.out.println("Compile Time: " + (end - start) / 6000 + " s");
    }
}
