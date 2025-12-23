package application.model.enumUno;

public enum ScoreValue {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TWENTY(20),
    FIFTY(50);

    private final int value;

    ScoreValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScoreValue fromValue(String value) {
        for (ScoreValue scoreValue : ScoreValue.values()) {
            if (scoreValue.name().equalsIgnoreCase(value)) {
                return scoreValue;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    public static ScoreValue fromValue(int value) {
        for (ScoreValue scoreValue : ScoreValue.values()) {
            if (scoreValue.getValue() == value) {
                return scoreValue;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}