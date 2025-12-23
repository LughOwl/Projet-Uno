package application.jdbc;

import java.sql.*;

public class DaoPartie {
    private static Connection connection = ConnectBDD.getInstance();

    // Requêtes SQL
    private final static String sqlCreerPartie = "INSERT INTO partie(date_partie) VALUES(?)";

    /**
     * Crée une nouvelle partie dans la base de données
     * @return l'id de la partie créée
     */
    public static int creerPartie() {
        try {
            PreparedStatement req = connection.prepareStatement(sqlCreerPartie, Statement.RETURN_GENERATED_KEYS);
            req.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            req.executeUpdate();

            ResultSet rs = req.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
