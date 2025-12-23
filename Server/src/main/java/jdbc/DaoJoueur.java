package jdbc;

import java.sql.*;

public class DaoJoueur {
    private static Connection connection = ConnectBDD.getInstance();

    // Requêtes SQL
    private final static String sqlExisteJoueur = "SELECT num_joueur FROM joueur WHERE pseudo_joueur = ?";
    private final static String sqlAjouterJoueur = "INSERT INTO joueur(pseudo_joueur) VALUES(?)";

    /**
     * Vérifie si un joueur existe déjà par son pseudo
     * @param pseudo le pseudo du joueur
     * @return l'id du joueur s'il existe, -1 sinon
     */
    public static int getIdPlayer(String pseudo) {
        try {
            PreparedStatement req = connection.prepareStatement(sqlExisteJoueur);
            req.setString(1, pseudo);
            ResultSet result = req.executeQuery();
            if (result.next()) {
                return result.getInt("num_joueur");
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ajoute un nouveau joueur dans la base de données
     * @param pseudo le pseudo du joueur
     * @return l'id du joueur créé
     */
    public static int addPlayer(String pseudo) {
        try {
            PreparedStatement req = connection.prepareStatement(sqlAjouterJoueur, Statement.RETURN_GENERATED_KEYS);
            req.setString(1, pseudo);
            req.executeUpdate();

            ResultSet rs = req.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du joueur : " + e.getMessage());
            return -1;
        }
    }

    /**
     * Récupère ou crée un joueur
     * @param pseudo le pseudo du joueur
     * @return l'id du joueur
     */
    public static int getOrAddPlayerId(String pseudo) {
        int id = getIdPlayer(pseudo);
        if (id == -1) {
            id = addPlayer(pseudo);
        }
        return id;
    }
}
