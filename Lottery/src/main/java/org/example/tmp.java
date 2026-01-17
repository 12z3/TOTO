package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class tmp {
    Set<List<Integer>> suppose;
    List<Set<List<Integer>>> supposes;


    static List<Set<List<Integer>>> listOfGeneratedSupposes(Set<List<Integer>> suppose) {
        List<Set<List<Integer>>> supposes = new ArrayList<>();
        supposes.add(suppose);
        return supposes;
    }


    private static void printThisSuppose(String bgTotoFilePath,
                                         Set<List<Integer>> suppose, int matches, double std, String errorMsg) {
        System.out.print("Да ги запиша ли?: ");
        String answer = Manipulate.answer();

        if (answer.equals("Y")) {
            Integer webIdx = WebData.getWebIdxFromFileData(bgTotoFilePath).getLast();
            try {
                Write.toFile(suppose, matches, std, ++webIdx);
            } catch (IOException e) {
                System.out.println(errorMsg +
                        "Не мога да запиша файла");
            }
        }
    }
}

