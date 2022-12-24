package task.TOTO.Projects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CheResults {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Integer> a = new ArrayList<>(List.of(1,23, 34,4,16, 29)); // 1 23, 34,4,15, 29
        String[] input = scanner.nextLine().trim().split("," );

        for (int i = 0; i < input.length; i++) {
            System.out.println(input[i].trim() + " ");
        }

       // System.out.println(Arrays.toString(input));
    }
    //    private List<Integer> result = new ArrayList<>();
//    private List<Integer> yourSuppose = new ArrayList<>();
//    protected int counter = 0;
//    private final String MESSAGE1 = "Нема да се плашиш само... Продължавай.";
//    private final String MESSAGE2 = "Имаш %d съвпадения: ";


//    public static void main(String[] args) {
//
//        List<Integer> a = new ArrayList<>(List.of(12, 7, 13, 15, 34, 44));
//        List<Integer> b = new ArrayList<>(List.of(12, 7, 33, 15, 24, 49));
//        // 12, 7, 33, 15, 34, 44
//        // 19, 9, 33, 45, 34, 47
//
//        CheResults check = new CheResults();
//
//        //check.printCheckedResult(check.cherResults(a,b), check.counter);
//    }

    public CheResults() {
    }

//    protected List<Integer> cherResults(List<Integer> result, List<Integer> suppose) {
//        List<Integer> tmp = new ArrayList<>();
//        int count = 0;
//        boolean isMatch = false;
//        for (int i = 0; i < result.size(); i++) {
//            isMatch = false;
//            count = 0;
//            int el1 = result.get(i);
//            for (int j = 0; j < suppose.size(); j++) {
//                int el2 = suppose.get(j);
//                if (el1 == el2) {
//                    count++;
//                    isMatch = true;
//                }
//            }
//            if (count > 1) {
//                System.out.println("Не може да има повече от едно съвпадение....");
//                return null;
//            }
//            this.counter += count;
//            if (isMatch) tmp.add(el1);
//        }
//        return tmp;
//    }
//
//    protected void printCheckedResult(List<Integer> tmp, int counter) {
//        if (counter < 0){
//            System.out.println("ERROR < 0");
//            return;
//        }
//
//        if (counter> 0 && counter < 4){
//            System.out.println(this.MESSAGE1);
//            System.out.printf(this.MESSAGE2, counter);
//            printListResult(tmp);
//        } else if(counter == 0) {
//            System.out.println(this.MESSAGE1);
//        } else {
//            System.out.printf(this.MESSAGE2, counter);
//            printListResult(tmp);
//        }
//
//    }
//
//    private void printListResult(List<Integer> tmp) {
//        for (int i = 0; i < tmp.size(); i++) {
//            if (tmp.get(i) != 0 && i < tmp.size() - 1) {
//                System.out.print(tmp.get(i) + ", ");
//            } else if (i == tmp.size() - 1) {
//                System.out.println(tmp.get(i));
//            }
//        }
//    }

}
