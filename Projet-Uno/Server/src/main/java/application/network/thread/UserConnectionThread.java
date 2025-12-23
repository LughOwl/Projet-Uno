package application.network.thread;

import application.network.UserConnection;
import application.network.utils.DisplayCallBack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserConnectionThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private UserConnection userConnection;
    private boolean connected;


    public UserConnectionThread(UserConnection userConnection, Socket socket) {
        if (userConnection == null || socket == null)
            throw new NullPointerException();

        this.userConnection = userConnection;
        this.socket = socket;
        this.connected = true;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        start();
    }
    @Override
    public void run() {
        while (connected) {
            try {
                if (!checkIn()) break;

                String message = in.readLine();
                DisplayCallBack.log("[UserConnectionThread] [run] Reçu de " + userConnection.getUsername() + " : " + message);

                userConnection.handlePacket(message);
            } catch (IOException e) {
                DisplayCallBack.log("[UserConnectionThread] [run] L'utilisateur s'est déconnecté.");
                userConnection.getGameThread().endScore0();
                end();
            }
        }
        DisplayCallBack.log("[UserConnectionThread] [run] Deconnexion du serveur reussie de l'utilisateur "+userConnection.getUsername()+" !");
        userConnection.updateListUserToAll();
    }

    public void end() {
        connected = false;
    }

    public void sendMessageError(String message) {
        sendMessage("@ERROR " + message);
    }

    public void sendMessage(String message) {
        if (message == null || message.isEmpty()) {
            DisplayCallBack.log("[UserConnectionThread] [sendMessage] UserConnectionThread : Message vide ou null");
            return;
        }
        if (!checkSocket()) {
            DisplayCallBack.log("[UserConnectionThread] [sendMessage] UserConnectionThread : Socket non valide");
            return;
        }
        if (!checkOut()) {
            DisplayCallBack.log("[UserConnectionThread] [sendMessage] UserConnectionThread : PrintWriter non valide");
            return;
        }

        DisplayCallBack.log("[UserConnectionThread] [sendMessage] Envoi à " + userConnection.getUsername() + " : " + message);
        out.println(message);
    }

    private boolean checkSocket() {
        if (socket == null) {
            DisplayCallBack.log("[UserConnectionThread] [checkSocket] UserConnectionThread : Socket null");
            return false;
        }
        if (socket.isClosed()) {
            DisplayCallBack.log("[UserConnectionThread] [checkSocket] UserConnectionThread : Socket fermee");
            return false;
        }
        if (!socket.isConnected()) {
            DisplayCallBack.log("[UserConnectionThread] [checkSocket] UserConnectionThread : Socket non connectee");
            return false;
        }
        return true;
    }

    private boolean checkOut() {
        if (out == null) {
            DisplayCallBack.log("[UserConnectionThread] [checkOut] UserConnectionThread : PrintWriter null");
            return false;
        }
        if (out.checkError()) {
            DisplayCallBack.log("[UserConnectionThread] [checkOut] UserConnectionThread : PrintWriter erreur");
            return false;
        }
        return true;
    }

    private boolean checkIn() {
        if (in == null) {
            DisplayCallBack.log("[UserConnectionThread] [checkIn] UserConnectionThread : BufferedReader null");
            return false;
        }
        return true;
    }


    /**
     * Closes all network resources associated with the user connection.
     * <p>
     * This method safely closes the {@link BufferedReader} for input, the {@link PrintWriter} for output,
     * and the {@link Socket} if it is still open.
     * It also logs each step of the closure process.
     * <p>
     * Typically called when the user disconnects or when the server terminates the connection with this client.
     *
     * @throws Exception if an error occurs while closing any of the resources.
     */
    public void close() throws Exception {
        if (in != null) {
            in.close();
            DisplayCallBack.log("[UserConnectionThread] [close] Fermeture du BufferedReader de l'utilisateur " + userConnection.getUsername());
        }
        if (out != null) {
            out.close();
            DisplayCallBack.log("[UserConnectionThread] [close] Fermeture du PrintWriter de l'utilisateur " + userConnection.getUsername());
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
            DisplayCallBack.log("[UserConnectionThread] [close] Fermeture de la socket de l'utilisateur " + userConnection.getUsername());
        }
        end();
        DisplayCallBack.log("[UserConnectionThread] [close] Fermeture réussie de la connexion de l'utilisateur " + userConnection.getUsername());
    }
}
