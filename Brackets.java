package task;

import java.util.*;

public class Brackets {
    public static void main(String[] args) {
        int idx1 = 0, idx2 = 0;
        char bracket1 = '(';
        char bracket2 = ')';
        String input = "1 *2+ (3 - 5 *2 + 4/2 + 2*6 - 6/3)+ 3* (4+5*6) +..... ((3-2)*5)";
        ArrayDeque<String> a1 = new ArrayDeque<>();

        StringBuilder equation = (createdEquation(input));
        System.out.println(equation);

        idx1 = getBracketsIndexes(equation).get(0);
        idx2 = getBracketsIndexes(equation).get(1);

        StringBuilder equ1 = (getEquationIntoBrackets(equation));
        System.out.println(equ1 + " = " + calculateEquation(new StringBuilder(equ1)));
        System.out.println(equ1);

        int result = 0;
        for (int i = idx1; i < idx2; i++) {

        }

    }

//    private static int getMultiplicationIndex(String input) {
//        int multiPidx = 0;
//        multiPidx = findIndexOfOperation(input, "*");
//        return multiPidx;
//    }

    private static int findIndexOfOperation(StringBuilder input, String operation, int index) {
        int multiPidx = 0;
        multiPidx = input.indexOf(operation, index);
        if (multiPidx != -1) {
            return multiPidx;
        }
        return multiPidx;
    }

    private static List<Integer> getBracketsIndexes(StringBuilder equation) {
        List<Integer> bracketsPositions = new ArrayList<>();
        int idx1 = 0, idx2 = 0;
        for (int i = 0; i < equation.length(); i++) {
            if (equation.indexOf("(", i) != -1) idx1 = equation.indexOf("(");
            if (equation.indexOf(")", i) != -1) idx2 = equation.indexOf(")");
        }
        bracketsPositions.add(idx1);
        bracketsPositions.add(idx2);
        return bracketsPositions;
    }

    private static StringBuilder getEquationIntoBrackets(StringBuilder equation) {
        StringBuilder equationIntoBrackets = new StringBuilder();
        int index1 = getBracketsIndexes(equation).get(0) + 1; // Това е вярно само за случая когато има само
        int index2 = getBracketsIndexes(equation).get(1);     // * и / -> 3 - 5*2 + 4/2

        for (int i = index1, j = 0; i < index2; i++, j++) {
            equationIntoBrackets.append(equation.charAt(i));

        }
        return createdEquation(String.valueOf(equationIntoBrackets));
    }

    private static int calculateEquation(StringBuilder equation) {
        Map<Integer, Integer> multiMap = new TreeMap<>();
        Map<Integer, Integer> dervMap = new TreeMap<>();

        ArrayDeque<Integer> ard = new ArrayDeque();
        ArrayDeque<Integer> ardResult = new ArrayDeque();              // 0 1 234 5 6 7
        List<Integer> indexes = new ArrayList<>();                     // 3 - 5*2 + 3 - 1 + 4/2 + 1 + 2 + 2*6 + 6 - 6/3
        int mediumResultM = 0, mediumResultD = 0, result = 0;          // 3 - 5*2 + 4/2
        char charX;

        for (int i = 0, indexMM = 0, indexDD = 0, counterM = 0, counterD = 0; i < equation.length(); i++) {
            int indexM = findIndexOfOperation(equation, "*", indexMM);
            if (indexM != -1) {
                counterM++;
                int x1 = Integer.parseInt(String.valueOf(equation.charAt(indexM - 1)));
                int x2 = Integer.parseInt(String.valueOf(equation.charAt(indexM + 1)));
                mediumResultM = x1 * x2;
                multiMap.put(indexM -1, mediumResultM);
                indexMM = ++indexM;
            }
            int indexD = findIndexOfOperation(equation, "/", indexDD);
            if (indexD != -1) {
                counterD++;
                int x1 = Integer.parseInt(String.valueOf(equation.charAt(indexD - 1)));
                int x2 = Integer.parseInt(String.valueOf(equation.charAt(indexD + 1)));
                mediumResultD = x1 / x2;
                dervMap.put(indexD -1, mediumResultD);
                indexDD = ++ indexD;
            }
        }

//        idxM = findIndexOfOperation(equation, "*");
//        idxD = findIndexOfOperation(equation, "/");
//
//        if (idxM != -1) {
//            int x1 = Integer.parseInt(String.valueOf(equation.charAt(idxM - 1)));
//            int x2 = Integer.parseInt(String.valueOf(equation.charAt(idxM + 1)));
//            mediumResultM = x1 * x2;
//        }
//        if (idxD != -1) {
//            int x1 = Integer.parseInt(String.valueOf(equation.charAt(idxM - 1)));
//            int x2 = Integer.parseInt(String.valueOf(equation.charAt(idxM + 1)));
//            mediumResultD = x1 / x2;
//        }
//        if (idxM < idxD) {
//            ardResult.push(mediumResultD);
//            ardResult.push(mediumResultM);
//        } else {
//            ardResult.push(mediumResultM);
//            ardResult.push(mediumResultD);
//        }
        for (int i = 0; i < equation.length(); i++) {                  // 3 - 5*2 + 3 - 1 + 4/2 + 1 + 2 + 2*6 + 6 - 6/3
            charX = equation.charAt(i);
            if (Character.isDigit(charX)) {
                ard.push(Integer.parseInt(String.valueOf(charX)));
            } else {
                switch (charX) {
                    case '+' -> {
                        //result = ard.pop() +
                        i += 3;
                        ard.push(result);
                    }
                    case '-' -> {
                        //result = ard.pop() -
                        i += 3;
                        ard.push(result);
                    }
                }
            }
        }


        return ard.pop();
    }

    private static StringBuilder createdEquation(String input) {
        StringBuilder equation = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != ' ' && input.charAt(i) != '.') {
                equation.append(input.charAt(i));

            }
        }
        return equation;
    }
}
