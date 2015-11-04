package DataModels.User;

/**
 * Created by Scott on 10/31/15.
 */
public enum Gender {
    MALE, FEMALE, NEUTRAL;

    public static Gender genderWithString(String string) {
        if ("Male".equalsIgnoreCase(string)) {
            return MALE;
        } else if ("Female".equalsIgnoreCase(string)) {
            return FEMALE;
        } else if ("Neutral".equalsIgnoreCase(string)) {
            return NEUTRAL;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (this == MALE) {
            return "Male";
        } else if (this == FEMALE) {
            return "Female";
        } else if (this == NEUTRAL) {
            return "Neutral";
        } else {
            return null;
        }
    }
}