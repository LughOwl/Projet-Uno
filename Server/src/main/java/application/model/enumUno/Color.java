package application.model.enumUno;

public enum Color {
    RED, YELLOW, GREEN, BLUE, WILD;

    public static Color getColor(String color) {
        return switch (color.toUpperCase()) {
            case "RED" -> RED;
            case "YELLOW" -> YELLOW;
            case "GREEN" -> GREEN;
            case "BLUE" -> BLUE;
            case "WILD" -> WILD;
            default -> null;
        };
    }
}

