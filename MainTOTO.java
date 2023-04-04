package task.TOTO;

import training.Methods;

import java.io.IOException;

public class MainTOTO extends Methods {
    public static void main(String[] args) {
        long start = getStartTime();
        TOTO totoTMP = new TOTO();
        try {
            totoTMP.play();
        } catch (IOException e) {
            System.out.println(e);
        }
        long end = getEndTime(start);
        long thisTime = (end - start) / 1_000_000_000;

        if (thisTime < 60) {
            System.out.print("Compile Time: " + thisTime + " s");
        } else if (thisTime > 60 && thisTime < 3600) {
            thisTime /= 60;
            System.out.print("Compile Time: " + thisTime + " min");
        }
    }
}
