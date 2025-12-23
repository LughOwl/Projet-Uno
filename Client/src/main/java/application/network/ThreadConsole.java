package application.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadConsole extends Thread{
    private ThreadClient client;
    private BufferedReader in;
    private boolean isRunning = true;

    public ThreadConsole(ThreadClient client) {
        try {
            this.client = client;
            this.in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                client.afficherMessage(message);
                System.out.println(message);
            } catch (IOException e) {
                client.afficherDeconnexionServeur();
                isRunning = false;
            }
        }
    }
}
