package task;

import java.util.ArrayList;
import java.util.List;

public class RegEx {
    public static void main(String[] args) {
        //String a = "slkdj 1234 *&^ H";
        ///String a = "slkdj, 12, 34 *&^ H";
        String aa = " 12";

        String b = "13, 23, 45, 33, 5, 44";
        String bb = "-13, 23, 45, 33, 5, 44";
        String bbb = "asd, 23, 45, 33, 5, 44";

        // Намира само и единствено числата от 1 до 9 + 0-та.
//        List<Integer> result = new ArrayList<>();
//        for (int i = 0; i < a.length(); i++) {
//            char c = a.charAt(i);
//            if (Character.isDigit(c)) result.add(Integer.parseInt(String.valueOf(a.charAt(i))));
//        }

        // for (int el: result) System.out.print(el + " ");

        String a = aa.trim();
        String regex2 = "\\d+";
        String regex1 = "[0-9]+";

        regex(b);


//        boolean isNumeric = a.chars().allMatch(Character::isDigit);
//        System.out.println(isNumeric);

    }

    private static boolean regex(String b) {
        String regex = "\\d{2}";
        String[] tmp = b.trim().split(", ");

        for (String s : tmp) {
            if (s.matches(regex)) {
                System.out.print(s + " ");
                return true;
            }
        }
        return false;
    }
}
