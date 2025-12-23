package application.model.enumUno;

public enum NumberEnum {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9);

    private final int value;

    NumberEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int getValue(int value) {
        for (NumberEnum number : NumberEnum.values()) {
            if (number.getValue() == value) {
                return number.getValue();
            }
        }
        return -1; // or throw an exception
    }
    public static NumberEnum getValue(String value) {
        try {
            for (NumberEnum number : NumberEnum.values()) {
                if (number.name().equalsIgnoreCase(value)) {
                    return number;
                }
            }
        } catch (NumberFormatException e) {
            // Handle the exception if the string cannot be parsed to an integer
        }
        return null; // or throw an exception
    }
}

