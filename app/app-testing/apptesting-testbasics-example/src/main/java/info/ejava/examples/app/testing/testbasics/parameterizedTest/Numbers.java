package info.ejava.examples.app.testing.testbasics.parameterizedTest;

public class Numbers {

    public static boolean isOdd(int number) {
        return number %2 != 0;
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }
    
}
