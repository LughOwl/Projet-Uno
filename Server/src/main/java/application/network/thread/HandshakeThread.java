package application.network.thread;

import application.model.game.Game;
import application.network.Server;
import application.network.UserConnection;
import application.network.utils.DisplayCallBack;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class HandshakeThread extends Thread{
    private Server server;
    private ServerSocket serverSocket;
    private boolean running = true;

    private GameThread gameThread;

    public HandshakeThread(Server server, GameThread gameThread) {
        this.server = server;
        try {
            this.serverSocket = new ServerSocket(server.getPort());
            this.gameThread = gameThread;
        } catch (BindException e) {
            DisplayCallBack.log("[HandshakeThread] Erreur : le port " + server.getPort() + " est déjà utilisé.");
            throw new RuntimeException("[HandshakeThread] Port déjà utilisé : " + server.getPort(), e);
        } catch (IOException e) {
            throw new RuntimeException("[HandshakeThread] Erreur lors de l'ouverture du port : " + server.getPort(), e);
        }
        start();
        DisplayCallBack.log("[HandshakeThread] Le serveur est en attente de connexion sur le port " + server.getPort());
    }

    public boolean isRunning() {
        return running;
    }

    public void end() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                DisplayCallBack.log("[HandshakeThread] [end] Le serveur a été arrêté.");
            }
        } catch (IOException e) {
            DisplayCallBack.log("[HandshakeThread] [end] Erreur lors de la fermeture du serveur : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    UserConnection userConnection = new UserConnection(socket, server, gameThread);
                    server.addUser(userConnection);
                    DisplayCallBack.log("[HandshakeThread] [run] Connexion réussie avec l'utilisateur "+userConnection.getUsername()+" !");
                } catch (IOException e) {
                    if (running) {
                        DisplayCallBack.log("[HandshakeThread] [run] Erreur pendant l'attente de connexion : " + e.getMessage());
                    }
                    end();
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("[HandshakeThread] [run] Erreur dans le thread de connexion", e);
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                DisplayCallBack.log("[HandshakeThread] [run] Erreur lors de la fermeture du serveur socket : " + e.getMessage());
            }
        }
    }



}
