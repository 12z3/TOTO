package task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegEx {
    /**
     * <a href="https://regexr.com/">...</a>
     * <a href="https://www.vogella.com/tutorials/JavaRegularExpressions/article.html">...</a>
     */
    public static void main(String[] args) {
        //String a = "slkdj 1234 *&^ H";
        ///String a = "slkdj, 12, 34 *&^ H";
        String aa = " 12";

        String b = " 8, 16, 27, 29, 38, 12";
        String b4 = "3, 3, 5, 3, 5, 4";
        String b2 = "-13, 23, 45, 33, 5, 44";
        String b3 = "asd, 23, 45, 33, 5, 44";

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

        System.out.println(isValidInputList(b));
        System.out.println(isValidInputList1(b));

//        boolean isNumeric = a.chars().allMatch(Character::isDigit);
//        System.out.println(isNumeric);

    }

    private static boolean isValidInputList(String input) {
        System.out.println(input);
        int countDigit = 0;
        //String regex = "\\d{1,2}";                                // ... гарантира, че е число 23, 5
        //String regex = "\\d{2} ?|\\d";
        //String regex = "^(0?[1-9]|[1-4][0-9])(,(0?[1-9]|[1-4][0-9]))*$";
        String regex = "^(0?[1-9]|[1-4][0-9])(,(0?[1-9]|[1-4][0-9]))*$";
        //String regex = "^\\d{1,2}(,\\d{1,2})*$";
        String[] tmp = input.trim().split(", ");

        for (String s : tmp) {
            if (s.matches(regex)) countDigit++;
        }
        return countDigit == tmp.length;
    }

    private static boolean isValidInputList1(String input){
        System.out.println(input);
        Pattern pattern = Pattern.compile("^(0?[1-9]|[1-4][0-9])(,(0?[1-9]|[1-4][0-9]))*$");
        //Pattern pattern = Pattern.compile("^(0?[1-9]|[1-4][0-9])(,(0?[1-9]|[1-4][0-9]))*$");
        //Pattern pattern = Pattern.compile("\\d{2} ?|\\d");
        //Pattern pattern = Pattern.compile("\\d{1,2}");
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }


}
