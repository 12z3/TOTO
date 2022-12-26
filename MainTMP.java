package task.TMP;

import task.TOTO.Projects.TOTO;
import training.Methods;

import java.io.IOException;

public class MainTMP extends Methods {
    public static void main(String[] args) {
        long start = getStartTime();
        TMP totoTMP = new TMP();
        try {
            totoTMP.play();
        }catch (IOException e){
            System.out.println(e);
        }

        long end = getEndTime(start);
        System.out.print("Compile Time: " + (end - start) / 6000 + "s");
    }
}
