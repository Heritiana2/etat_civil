package mg.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NaissanceDAO {
    
    public boolean ancrerNouveauBloc(String hashActuel, int idOfficier) {
        String sqlSelectLast = "SELECT hash_actuel FROM bloc_blockchain ORDER BY id_block DESC LIMIT 1";
        String sqlInsert = "INSERT INTO bloc_blockchain (timestamp, hash_precedent, hash_actuel, signature_officier) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = mg.model.DatabaseConnexion.getConnection();
            
            String hashPrecedent = "0"; 
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelectLast);
                 ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    hashPrecedent = rs.getString("hash_actuel");
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                long currentTimestamp = System.currentTimeMillis() / 1000L;
                
                psInsert.setLong(1, currentTimestamp);
                psInsert.setString(2, hashPrecedent);
                psInsert.setString(3, hashActuel);
                psInsert.setInt(4, idOfficier);
                
                int rows = psInsert.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public java.util.Map<String, String> getDonneesCompletesPourPDF(int idNaissance) {
        String sql = "SELECT n.id_naissance, n.nom_enfant, n.prenoms_enfant, " +
                    "n.date_naissance, n.lieu_naissance, " +
                    "n.nom_pere, n.nom_mere, " +
                    "b.hash_actuel as hash_blockchain " +
                    "FROM naissance n " +
                    "LEFT JOIN bloc_blockchain b ON b.signature_officier = ? " + 
                    // Note : Ajuste la jointure selon la structure exacte de ta table. 
                    // Si tu as directement ajouté une colonne hash_blockchain dans ta table naissance, fais :
                    // "SELECT ..., n.hash_blockchain FROM naissance n WHERE n.id_naissance = ?"
                    "WHERE n.id_naissance = ? LIMIT 1";
                    
        java.util.HashMap<String, String> map = new java.util.HashMap<>();
        try (Connection conn = mg.model.DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idNaissance);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    map.put("id_naissance", String.valueOf(rs.getInt("id_naissance")));
                    map.put("nom_enfant", rs.getString("nom_enfant"));
                    map.put("prenoms_enfant", rs.getString("prenoms_enfant"));
                    map.put("date_naissance", rs.getDate("date_naissance").toString());
                    map.put("lieu_naissance", rs.getString("lieu_naissance"));
                    map.put("nom_pere", rs.getString("nom_pere") != null ? rs.getString("nom_pere") : "Inconnu");
                    map.put("nom_mere", rs.getString("nom_mere"));
                    
                    // Récupération sécurisée du Hash
                    String hash = rs.getString("hash_blockchain");
                    map.put("hash_blockchain", hash != null ? hash : "NON_ANCRE_OU_GENERE");
                    return map;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}