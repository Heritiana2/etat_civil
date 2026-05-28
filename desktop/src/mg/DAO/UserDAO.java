package mg.DAO;

import java.sql.*;

import mg.model.DatabaseConnexion;

public class UserDAO {
    public boolean verifierLogin(String login, String role) {
        String query = "SELECT * FROM utilisateur_mairie WHERE login = ? AND role = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, login);
            pstmt.setString(2, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }

    public int trouverIdUtilisateur(String login, String role) {
        String query = "SELECT id_user FROM utilisateur_mairie WHERE login = ? AND role = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, login);
            pstmt.setString(2, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_user");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        return -1;
    }
}
