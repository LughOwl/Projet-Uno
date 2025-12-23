package application.network.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class DisplayCallBack {
    private static Consumer<String> logConsumer;
    private static boolean typeAffichage = false; // true = console, false = JavaFX

    public static void setLogConsumer(Consumer<String> consumer) {
        logConsumer = consumer;
    }

    public static boolean isTypeAffichage() {
        return typeAffichage;
    }

    public static void setTypeAffichage(boolean typeAffichage) {
        DisplayCallBack.typeAffichage = typeAffichage;
    }

    public static void log(String message) {
        if (logConsumer != null) {
            // +this.getClass().getName()+
            if (typeAffichage) {
                System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "] " + message);
            } else
                logConsumer.accept("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "] " + message);
        }
    }
}


