package mg.DAO;

import java.sql.*;

import mg.model.DatabaseConnexion;

public class UserDAO {
    public boolean verifierLogin(String mdp, String role) {
        String query = "SELECT * FROM utilisateur_mairie where login = ? AND role = ?";
        try(Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            
                pstmt.setString(1, mdp);
                pstmt.setString(2, role);

                ResultSet rs = pstmt.executeQuery();
                return rs.next();
        } catch (SQLException e) {
            // TODO: handle exception
            System.err.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }
}
