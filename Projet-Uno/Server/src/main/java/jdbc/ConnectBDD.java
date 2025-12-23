package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectBDD {
    private static final String URL = "jdbc:mysql://localhost/uno_projet_jdbc";
    private static final String USER = "user_uno";
    private static final String PASSWORD = "6?TJ!68MtfRjnh7SrgE9";

    private static Connection connection;

    private ConnectBDD() {}

    public static Connection getInstance() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                //System.out.println("Connexion à la BDD réussie !");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver non trouvé! : " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Impossible d'ouvrir connexion! : " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                //System.out.println("Connexion à la BDD fermée !");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
