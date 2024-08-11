package validation;

import contains.PatternConstants;

import java.util.*;

public class Validation {
    public static void isValidEmail(final String email) {
        if (!PatternConstants.EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public static boolean areDatesNotNull(Date... dates) {
        return Arrays.stream(dates).allMatch(Objects::nonNull);
    }

    public static boolean isCollectionEmpty(Collection<?> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::isEmpty)
                .orElse(true);
    }

    public static boolean isEmail(String email) {
        // Simple email validation pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public static boolean validateMenuInput(String input, String errorMessage, int expectedLength) {
        if (Optional.ofNullable(input)
                .filter(s -> s.length() == expectedLength)
                .isEmpty()) {
            System.out.println(errorMessage);
            return false;
        }
        return true;
    }

    public static boolean anyEmptyOrNull(List<?> collection, String errorMessage) {
        if (Optional.ofNullable(collection).map(List::isEmpty).orElse(true)) {
            System.out.println(errorMessage);
            return false;
        }
        return true;
    }
}
