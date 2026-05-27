package mg.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import mg.model.DatabaseConnexion;

public class DecesDAO {

    public Map<String, String> getDonneesDefuntPourBlockchain(int idDemande) {
        Map<String, String> map = new HashMap<>();
        // --- CORRECTION : Ajout de d.id_defunt dans le SELECT ---
        String sql = "SELECT d.id_defunt, c.nom, c.prenoms, d.date_deces, d.lieu_deces " +
                     "FROM deces d " +
                     "JOIN citoyen c ON d.id_defunt = c.id_citoyen " +
                     "WHERE d.id_demande = ?";
                     
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDemande);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // --- CORRECTION : Stockage de l'ID dans la Map ---
                    map.put("id_defunt", String.valueOf(rs.getInt("id_defunt")));
                    
                    map.put("nom_defunt", rs.getString("nom"));
                    map.put("prenoms_defunt", rs.getString("prenoms"));
                    map.put("date_deces", rs.getString("date_deces"));
                    map.put("lieu_deces", rs.getString("lieu_deces"));
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur DecesDAO : " + e.getMessage());
        }
        return map;
    }

    /**
     * Met à jour le statut du citoyen pour le marquer comme Décédé.
     * @param idDefunt L'identifiant du citoyen concerné
     * @return true si la mise à jour a réussi
     */
    public boolean marquerCitoyenCommeDecede(int idDefunt) {
        String sql = "UPDATE citoyen SET statut_matrimonial = 'Décédé' WHERE id_citoyen = ?";
        
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDefunt);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Erreur lors du marquage du citoyen comme décédé : " + e.getMessage());
            return false;
        }
    }
}