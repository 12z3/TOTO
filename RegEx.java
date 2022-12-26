package task;

public class RegEx {
    /**
     * <a href="https://regexr.com/">...</a>
     * <a href="https://www.vogella.com/tutorials/JavaRegularExpressions/article.html">...</a>
     */
    public static void main(String[] args) {
        //String a = "slkdj 1234 *&^ H";
        ///String a = "slkdj, 12, 34 *&^ H";
        String aa = " 12";

        String b = " 3, 23, 45, 33, 5, 44";
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

//        boolean isNumeric = a.chars().allMatch(Character::isDigit);
//        System.out.println(isNumeric);

    }

    private static boolean isValidInputList(String input) {
        int countDigit = 0;
        String regex = "\\d{2}?|\\d";                                // ... гарантира, че е число 23, 5
        String[] tmp = input.trim().split(", ");

        for (String s : tmp) {
            if (s.matches(regex)) countDigit++;
        }
        return countDigit == tmp.length;
    }


}
