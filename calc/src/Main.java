import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

public class  Main{
    private enum TypeCalculator {
        ARABIC, ROMAN
    }

    private static final NavigableMap<String, Integer> MAP_ROMAN_ARABIC = new TreeMap<>() {{
        put("I", 1);
        put("IV", 4);
        put("V", 5);
        put("IX", 9);
        put("X", 10);
    }};

    private static final NavigableMap<Integer, String> MAP_ARABIC_ROMAN = new TreeMap<>() {{
        put(1, "I");
        put(4, "IV");
        put(5, "V");
        put(9, "IX");
        put(10, "X");
        put(40, "XL");
        put(50, "L");
        put(90, "XC");
        put(100, "C");
    }};

    public static int calculateArabic(String numberFirst, String numberSecond, String operation) {
        int num1;
        int num2;

        try {
            num1 = Integer.parseInt(numberFirst);
            num2 = Integer.parseInt(numberSecond);
        }catch (NumberFormatException e){
            throw new RuntimeException("Ошибка. Введены не целые числа");
        }

        if(!(num1 >= 0 && num1 <= 10 && num2 >=0 && num2 <= 10)){
            throw new RuntimeException("Ошибка. Введены недопустимые значения чисел.(меньше 1 или больше 10)");
        }

        int result = switch (operation) {
            case "+" -> num1 + num2;
            case "-" -> num1 - num2;
            case "/" -> num1 / num2;
            default -> num1 * num2;
        };

        return result;
    }

    public static String calculateRoman(String numberFirst, String numberSecond, String operation) {
        String num1 = convertToArabic(numberFirst);
        String num2 = convertToArabic(numberSecond);

        int result = calculateArabic(num1, num2, operation);

        if (result < 1) {
            throw new RuntimeException("Ошибка. " +
                    "Отрицательное число не может быть представлено в римской системе счисления");
        }

        return convertToRoman(result);

    }

    // метод конвертации из римского в арабское
    private static String convertToArabic(String number) {
        if (number.contains("-")) {
            throw new RuntimeException("Ошибка. Римское число не может быть отрицательным");
        }

        int result = 0;
        for (int i = 0; i < number.length(); i++) {

            if (i == 0 || MAP_ROMAN_ARABIC.get(String.valueOf(number.charAt(i))) <= MAP_ROMAN_ARABIC.get(String.valueOf(number.charAt(i - 1)))) {
                result += MAP_ROMAN_ARABIC.get(String.valueOf(number.charAt(i)));

            } else {
                result = 2 * MAP_ROMAN_ARABIC.get((String.valueOf(number.charAt(i)))) -
                        MAP_ROMAN_ARABIC.get(String.valueOf(number.charAt(i))) - result;
            }
        }

        // проверка, что пользователь корректно ввел римские числа, например IV, а не IIII
        if (!convertToRoman(result).equals(number)) {
            throw new RuntimeException("Ошибка. Римское число %s введено некорректно".formatted(number));
        }
        return Integer.toString(result);
    }

    // метод конвертации числа из арабского в римское
    private static String convertToRoman(int number) {

        StringBuilder result = new StringBuilder();

        while (number != 0) {
            int value = MAP_ARABIC_ROMAN.floorKey(number);
            result.append(MAP_ARABIC_ROMAN.get(value));
            number -= value;
        }

        return result.toString();
    }

    private static TypeCalculator getTypeCalculator(String numberFirst, String numberSecond) {
        boolean isArabicFirstNumber = true;
        boolean isArabicSecondNumber = true;

        String srtRomanNumbers = "CLXIV";

        for (int i = 0; i < srtRomanNumbers.length(); i++) {
            if (numberFirst.contains(Character.toString(srtRomanNumbers.charAt(i)))) {
                isArabicFirstNumber = false;
            }
            if (numberSecond.contains(Character.toString(srtRomanNumbers.charAt(i)))) {
                isArabicSecondNumber = false;
            }
        }

        if (isArabicFirstNumber && isArabicSecondNumber) {
            return TypeCalculator.ARABIC;
        } else if (!isArabicFirstNumber && !isArabicSecondNumber) {
            return TypeCalculator.ROMAN;
        } else
            throw new RuntimeException("Ошибка ввода. Числа введены в разных системах счисления.");
    }

    private static void checkOperation(String operation) {
        if (!"+-*/".contains(operation)) {
            throw new RuntimeException("Ошибка ввода арифметического оператора");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\033[92mКалькулятор\033[0m\n" +
                "Особенности при работе:\n" +
                "Калькулятор работает с арабскими и римскими числами размерностью от 1 до 10 включительно\n" +
                "Вводимые числа должны быть только целыми и либо все арабские, либо все римские\n" +
                "\033[93mДля выхода введите \033[91mesc\033[0m");

        while (true) {
            System.out.print("Выражение, которое необходимо вычислить: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("esc")) {
                System.out.println("Приложение закрыто");
                return;
            }

            String[] partsString = input.split(" ");

            if (partsString.length != 3) {
                throw new RuntimeException("Ошибка ввода. Выражение необходимо написать одной строкой");
            }

            String numberFirst = partsString[0];
            String numberSecond = partsString[2];
            String operation = partsString[1];

            TypeCalculator typeCalculator = getTypeCalculator(numberFirst, numberSecond);
            checkOperation(operation);

            switch (typeCalculator) {
                case ROMAN -> System.out.println("\033[92m" + input + " = " +
                        calculateRoman(numberFirst, numberSecond, operation) + "\033[0m");
                case ARABIC -> System.out.println("\033[92m" + input + " = " +
                        calculateArabic(numberFirst, numberSecond, operation) + "\033[0m");
            }
        }
    }
}