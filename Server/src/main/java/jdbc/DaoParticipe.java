package jdbc;

import java.sql.*;

public class DaoParticipe {
    private static Connection connection = ConnectBDD.getInstance();

    // Requêtes SQL
    private final static String sqlEnregistrerParticipation = "INSERT INTO participe(num_joueur, num_partie, score) VALUES(?, ?, ?)";

    /**
     * Enregistre la participation d'un joueur à une partie avec son score
     * @param idJoueur l'id du joueur
     * @param idPartie l'id de la partie
     * @param score le score du joueur
     * @return true si l'opération a réussi, false sinon
     */
    public static void enregistrerParticipation(int idJoueur, int idPartie, int score) {
        try {
            PreparedStatement req = connection.prepareStatement(sqlEnregistrerParticipation);
            req.setInt(1, idJoueur);
            req.setInt(2, idPartie);
            req.setInt(3, score);
            req.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
