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

    public java.util.Map<String, String> getDonneesCompletesPourPDF(int idDemande) {
        // CORRECTION : On part de 'demande d' et on fait un LEFT JOIN vers 'naissance n'
        String sql = "SELECT n.id_naissance, d.id_demande, n.nom_enfant, n.prenoms_enfant, " +
                    "n.date_naissance, n.lieu_naissance, " +
                    "n.nom_pere, n.nom_mere, " +
                    "d.statut, d.hash_blockchain " + 
                    "FROM demande d " +
                    "LEFT JOIN naissance n ON d.id_demande = n.id_demande " + 
                    "WHERE d.id_demande = ? LIMIT 1";
                    
        java.util.HashMap<String, String> map = new java.util.HashMap<>();
        try (Connection conn = mg.model.DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDemande); 
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    map.put("id_demande", String.valueOf(rs.getInt("id_demande"))); 
                    map.put("id_naissance", String.valueOf(rs.getInt("id_naissance"))); // Vaudra 0 ou null si non saisi
                    
                    // Données de l'enfant sécurisées au cas où la table naissance est vide
                    map.put("nom_enfant", rs.getString("nom_enfant") != null ? rs.getString("nom_enfant") : "Non spécifié");
                    map.put("prenoms_enfant", rs.getString("prenoms_enfant") != null ? rs.getString("prenoms_enfant") : "Non spécifié");
                    
                    java.sql.Date dateNaissance = rs.getDate("date_naissance");
                    map.put("date_naissance", dateNaissance != null ? dateNaissance.toString() : "Non spécifiée");
                    
                    map.put("lieu_naissance", rs.getString("lieu_naissance") != null ? rs.getString("lieu_naissance") : "Non spécifié");
                    map.put("nom_pere", rs.getString("nom_pere") != null ? rs.getString("nom_pere") : "Inconnu");
                    map.put("nom_mere", rs.getString("nom_mere") != null ? rs.getString("nom_mere") : "Non spécifié");
                    
                    // ICI : On récupère le VRAI statut du workflow de la table demande
                    String statut = rs.getString("statut");
                    map.put("statut", statut != null ? statut.toUpperCase().trim() : "NON_DEFINI");
                    
                    String hash = rs.getString("hash_blockchain");
                    map.put("hash_blockchain", hash != null ? hash : "0000000000000000000000000000000000000000000000000000000000000000");
                    return map;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur dans NaissanceDAO.getDonneesCompletesPourPDF : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean insererNaissance(int idDemande, String nomEnfant, String prenomsEnfant, 
                                    String dateNaissance, String lieuNaissance, 
                                    String nomPere, String nomMere) {
        String sql = "INSERT INTO naissance (id_demande, nom_enfant, prenoms_enfant, " +
                    "date_naissance, lieu_naissance, nom_pere, nom_mere) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = mg.model.DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idDemande);
            pstmt.setString(2, nomEnfant);
            pstmt.setString(3, prenomsEnfant);
            pstmt.setString(4, dateNaissance); // Format attendu par MySQL : "YYYY-MM-DD"
            pstmt.setString(5, lieuNaissance);
            pstmt.setString(6, nomPere != null ? nomPere : "Inconnu");
            pstmt.setString(7, nomMere);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion dans la table naissance : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}