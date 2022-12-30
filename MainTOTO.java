package task.TOTO;

import training.Methods;

import java.io.IOException;

public class MainTOTO extends Methods {
    public static void main(String[] args) {
        long start = getStartTime();
        TOTO totoTMP = new TOTO();
        try {
            totoTMP.play();
        }catch (IOException e){
            System.out.println(e);
        }

        long end = getEndTime(start);
        System.out.print("Compile Time: " + (end - start) / 6000 + "s");
    }
}
