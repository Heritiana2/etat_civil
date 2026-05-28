package mg.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import mg.model.Acte;
import mg.model.DatabaseConnexion;
import mg.util.StringUtil;

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

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean envoyerAOfficier(String type_demande, Date date_demande, String statut, int id_citoyen, int id_agent_traitant, int id_officier) {
        String sql = "INSERT INTO demande (type_demande, date_demande, statut, id_citoyen, id_agent_traitant, id_officier) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, type_demande);
            pstmt.setDate(2, date_demande);
            pstmt.setString(3, statut);
            pstmt.setInt(4, id_citoyen);
            pstmt.setInt(5, id_agent_traitant);
            pstmt.setInt(6, id_officier);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean attribuerAgentTraitant(int idDemande, int idAgent) {
        String sql = "UPDATE demande SET id_agent_traitant = ? WHERE id_demande = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAgent);
            pstmt.setInt(2, idDemande);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean attribuerOfficier(int idDemande, int idOfficier) {
        String sql = "UPDATE demande SET id_officier = ? WHERE id_demande = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idOfficier);
            pstmt.setInt(2, idDemande);
            return pstmt.executeUpdate() > 0;
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

    public List<Object[]> obtenirDemandeparTypePourAgent(String typeActe, int idAgentTraitant) {
        List<Object[]> donnees = new ArrayList<>();
        String sql = "SELECT d.id_demande, c.id_citoyen, c.nom, c.prenoms, d.date_demande, d.statut " +
                    "FROM demande d " +
                    "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                    "WHERE d.type_demande = ? AND d.id_agent_traitant = ? AND d.statut IN ('Nouveau', 'Saisi', 'Signé') " +
                    "ORDER BY d.date_demande ASC";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, typeActe);
                ps.setInt(2, idAgentTraitant);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    donnees.add(new Object[] {
                        rs.getInt("id_demande"),
                        rs.getInt("id_citoyen"),
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

    public List<Object[]> obtenirDemandesParTypeEtStatutPourOfficier(String typeActe, String statut, int idOfficier) {
        List<Object[]> demandes = new ArrayList<>();
        String sql = "SELECT d.id_demande, d.id_citoyen, CONCAT(c.nom, ' ', c.prenoms) AS citoyen, " +
                     "d.date_demande, d.statut " +
                     "FROM demande d " +
                     "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                     "WHERE d.type_demande = ? AND d.statut = ? AND d.id_officier = ?";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeActe);
            pstmt.setString(2, statut);
            pstmt.setInt(3, idOfficier);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                demandes.add(new Object[] {
                    rs.getInt("id_demande"),
                    rs.getInt("id_citoyen"),
                    rs.getString("citoyen"),
                    rs.getTimestamp("date_demande"),
                    rs.getString("statut")
                });
            }
            return demandes;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    public boolean supprimerToutesLesDemandes() {
        String sql = "DELETE FROM demande";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rows = pstmt.executeUpdate();
            System.out.println("[SQL] Suppression de toutes les demandes : " + rows + " lignes supprimées.");
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de toutes les demandes : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> obtenirDemandesPourOfficier() {
        List<Object[]> demandes = new ArrayList<>();
        String sql = "SELECT d.id_demande, c.nom, c.prenoms, d.date_demande " +
                     "FROM demande d " +
                     "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                     "WHERE d.statut = 'Saisi'";

        try(Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                demandes.add(new Object[] {
                    rs.getInt("id_demande"),
                    rs.getString("nom"),
                    rs.getString("prenoms"),
                    rs.getTimestamp("date_demande")
                });
            }
            return demandes;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Object[]> obtenirDemandesParTypeEtStatut(String typeActe, String statut) {
        List<Object[]> demandes = new ArrayList<>();
        String sql = "SELECT d.id_demande, d.id_citoyen, CONCAT(c.nom, ' ' , c.prenoms) AS citoyen," + 
                    "d.date_demande , d.statut " +
                     "FROM demande d " +
                     "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                     "WHERE d.type_demande = ? AND d.statut = ?";

        try(Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, typeActe);
            pstmt.setString(2, statut);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(new Object[] {
                    rs.getInt("id_demande"),
                    rs.getInt("id_citoyen"),
                    rs.getString("citoyen"),
                    rs.getTimestamp("date_demande"),
                    rs.getString("statut")
                });
            }
            return demandes;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } 
    }

    /**
     * Met à jour le statut d'une demande et enregistre son empreinte blockchain SHA-256.
     * @param idDemande L'identifiant de la demande concernée.
     * @param statut Le nouveau statut (ex: "SIGNE")
     * @param hash Le hash calculé à partir des données de l'acte
     * @return true si la mise à jour en BDD a réussi, false sinon.
     */
    public boolean mettreAJourStatutEtHash(int idDemande, String statut, String hash) {
        String sql = "UPDATE demande SET statut = ?, hash_blockchain = ? WHERE id_demande = ?";
        
        // Utilisation du try-with-resources pour s'assurer que les connexions se ferment proprement
        try (java.sql.Connection conn = mg.model.DatabaseConnexion.getConnection(); // Ajuste si ta classe de connexion s'appelle autrement
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            pstmt.setString(2, hash);
            pstmt.setInt(3, idDemande);
            
            int lignesModifiees = pstmt.executeUpdate();
            return lignesModifiees > 0; // Retourne true si la ligne a bien été mise à jour
            
        } catch (java.sql.SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut et du hash : " + e.getMessage());
            return false;
        }
    }

    public boolean signerActe(int idDemande) {
        // 1. On modifie le SELECT pour récupérer aussi l'id_mariage lié à la demande
        String sqlSelect = "SELECT c.nom, c.prenoms, d.type_demande, d.date_demande, m.id_mariage " +
                            "FROM demande d " +
                            "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                            "LEFT JOIN mariage m ON m.id_mariage = d.id_demande " + // Ajuste cette jointure si tu as une colonne id_demande dans mariage
                            "WHERE d.id_demande = ?";
                            
        String sqlUpdateDemande = "UPDATE demande SET statut = 'SIGNE', hash_blockchain = ?, date_signature = NOW() " +
                            "WHERE id_demande = ?";
                            
        String sqlUpdateMariage = "UPDATE mariage SET statut = 'SIGNE', hash_blockchain = ? WHERE id_mariage = ?";

        try(Connection conn = DatabaseConnexion.getConnection()) {
            String mixture = "";
            int idMariage = -1;

            try(PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, idDemande);
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    mixture = rs.getString("nom").trim() +
                                rs.getString("prenoms").trim() +
                                rs.getString("type_demande") +
                                rs.getTimestamp("date_demande").toString() +
                                "ID: " + idDemande;
                    
                    // On récupère le vrai ID du mariage associé
                    idMariage = rs.getInt("id_mariage");
                    if (rs.wasNull() || idMariage <= 0) {
                        // Si la jointure directe échoue, on tente de fallback sur l'idDemande
                        idMariage = idDemande; 
                    }
                } else {
                    System.out.println("⚠️ Demande introuvable pour l'ID : " + idDemande);
                    return false;
                }
            }

            String finalHash = StringUtil.applySha256(mixture);
            conn.setAutoCommit(false); // Mode transactionnel sécurisé

            try (PreparedStatement psUpdateD = conn.prepareStatement(sqlUpdateDemande);
                PreparedStatement psUpdateM = conn.prepareStatement(sqlUpdateMariage)) {
                
                // Mise à jour Table Demande
                psUpdateD.setString(1, finalHash);
                psUpdateD.setInt(2, idDemande);
                int rowsD = psUpdateD.executeUpdate();

                // Mise à jour Table Mariage avec le BON ID de mariage
                psUpdateM.setString(1, finalHash);
                psUpdateM.setInt(2, idMariage);
                int rowsM = psUpdateM.executeUpdate();

                System.out.println("[DEBUG BDD] Lignes modifiées dans Demande : " + rowsD + " | Mariage (ID " + idMariage + ") : " + rowsM);

                if (rowsM > 0) {
                    conn.commit(); // On valide si au moins la table mariage est touchée
                    System.out.println("[BLOCKCHAIN] Acte de mariage ancré avec succès ! Hash : " + finalHash);
                    return true;
                } else {
                    conn.rollback();
                    System.out.println("⚠️ L'UPDATE de la table mariage a modifié 0 ligne. Annulation.");
                }
                
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erreur SQL lors de l'exécution des UPDATEs : " + e.getMessage());
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion BDD : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}