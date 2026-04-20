package mg.model;

import mg.model.DatabaseConnexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeDAO {
    
    // --- MÉTHODE 1 : Envoyer une demande (Citoyen) ---
    public boolean envoyerAOfficier(String type_demande, Date date_demande, String statut, int id_citoyen, int id_agent_traitant) {
        String sql = "INSERT INTO demande (type_demande, date_demande, statut, id_citoyen, id_agent_traitant) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, type_demande);
            pstmt.setDate(2, date_demande);
            pstmt.setString(3, statut);
            pstmt.setInt(4, id_citoyen);
            pstmt.setInt(5, id_agent_traitant);

            return pstmt.executeUpdate() > 0; // Correction ici : > 0
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MÉTHODE 2 : Récupérer la liste pour l'Agent ---
    public List<Object[]> obtenirDemandeparType(String typeActe) {
        List<Object[]> donnees = new ArrayList<>();
        String sql = "SELECT d.id_demande, c.id_citoyen, c.nom, c.prenoms, d.date_demande, d.statut " + 
                    "FROM demande d " + 
                    "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                    "WHERE d.type_demande = ? AND d.statut IN ('Nouveau', 'Saisi', 'Signé') " +
                    "ORDER BY d.date_demande ASC";
        
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, typeActe);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    donnees.add(new Object[] {
                        rs.getInt("id_demande"),
                        rs.getInt("id_citoyen"), // On garde l'ID citoyen caché pour le formulaire
                        rs.getString("nom") + " " + rs.getString("prenoms"),
                        rs.getTimestamp("date_demande"),
                        rs.getString("statut")
                    });
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return donnees;
    }

    // --- MÉTHODE 3 : LA NOUVELLE SAISIE DÉTAILLÉE (CUA) ---
    public boolean finaliserSaisieActe(Acte acte, int idDemande) {
        Connection conn = null;
        try {
            conn = DatabaseConnexion.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertion dans acte_original (Table détaillée)
            String sqlActe = "INSERT INTO acte_original (num_acte, bureau_civil, type_acte, nom_sujet, prenoms_sujet, " +
                             "date_evenement, heure_naissance, lieu_evenement, nom_pere, nom_mere, " +
                             "id_citoyen, id_bloc_creation, lieu_delivrance) " +
                             "VALUES (?, ?, 'NAISSANCE', ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)";
            
            PreparedStatement ps1 = conn.prepareStatement(sqlActe);
            ps1.setString(1, acte.getNumActe());
            ps1.setString(2, acte.getBureau());
            ps1.setString(3, acte.getNom());
            ps1.setString(4, acte.getPrenom());
            ps1.setString(5, acte.getDateNais());
            ps1.setString(6, acte.getHeureNais());
            ps1.setString(7, acte.getLieuNais());
            ps1.setString(8, acte.getPere());
            ps1.setString(9, acte.getMere());
            ps1.setInt(10, acte.getIdCitoyen());
            ps1.setString(11, acte.getLieuDelivrance());
            ps1.executeUpdate();

            // 2. Mise à jour du statut de la demande
            String sqlStatus = "UPDATE demande SET statut = 'Saisi' WHERE id_demande = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlStatus);
            ps2.setInt(1, idDemande);
            ps2.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    // --- MÉTHODE : Mettre à jour le statut d'un dossier ---
public boolean mettreAJourStatut(int idDossier, String nouveauStatut) {
    String sql = "UPDATE demande SET statut = ? WHERE id_demande = ?";

    try (Connection conn = DatabaseConnexion.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, nouveauStatut);
        pstmt.setInt(2, idDossier);

        int rowsAffected = pstmt.executeUpdate();
        
        // On affiche un petit log dans la console Linux pour le debug
        if (rowsAffected > 0) {
            System.out.println("[SQL] Dossier #" + idDossier + " passé au statut : " + nouveauStatut);
            return true;
        }
        return false;
        
    } catch (SQLException e) {
        System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
}