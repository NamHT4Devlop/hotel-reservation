package contains;

import java.util.regex.Pattern;

public class PatternConstants {

    public static final String MY_CUSTOM_DATE_FORMAT = "MM/dd/yyyy";
    public static final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{4}";
    public static final String EMAIL_REGEX_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(PatternConstants.EMAIL_REGEX_PATTERN);
    public static final String SEPARATOR = "--------------------------------------------------";

}
