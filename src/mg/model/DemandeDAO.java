package mg.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeDAO {
    
    public boolean envoyerAOfficier(String type_demande, Date date_demande, String Statut, int id_citoyen, int id_agent_traitant) {
        String sql = "INSERT INTO demande (type_demande, date_demande, statut, id_citoyen, id_agent_traitant) VALUES (?, 'En attente', ?, ?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, type_demande);
            pstmt.setDate(2, date_demande);
            pstmt.setString(3, Statut);
            pstmt.setInt(4, id_citoyen);
            pstmt.setInt(5, id_agent_traitant);

            return pstmt.executeUpdate() > 1;
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }

    public boolean mettreAJourStatut(int idDossier, String nouveauStatut) {
        String sql = "UPDATE demande SET statut = ? WHERE id_demande = ?";

        try(Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nouveauStatut);
                pstmt.setInt(2, idDossier);

                return pstmt.executeUpdate() > 0;
            }
            catch( SQLException e) {
                e.printStackTrace();
                return false;
            }
    }

    public List <Object[]> obtenirDemandeparType(String typeActe) {
        List<Object[]> donnees = new ArrayList<>();

        String sql = "SELECT d.id_demande, c.nom, c.prenoms, d.date_demande, d.statut " + 
                    "FROM demande d " + 
                    "JOIN citoyen c ON d.id_citoyen = c.id_citoyen " +
                    "WHERE d.type_demande = ?" +
                    "ORDER BY d.date_demande ASC";
        
        try (Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, typeActe);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    donnees.add(new Object[] {
                        rs.getInt("id_demande"),
                        rs.getString("nom") + " " + rs.getString("prenoms"),
                        rs.getTimestamp("date_demande"),
                        rs.getString("statut")
                    });
                }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    return donnees;
    }

    public boolean enregistrerSaisieActe(int idDemande, String contenu) {
        Connection conn = null;
        try {
            conn = DatabaseConnexion.getConnection();
            conn.setAutoCommit(false);

            String sqlActe = "INSERT INTO acte_original (num_acte, contenu_xml) VALUES(?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sqlActe);
            ps1.setInt(1, idDemande);
            ps1.setString(2, contenu);
            ps1.executeUpdate();

            String sqlStatus = "UPDATE demande SET statut = 'Saisi' WHERE id_demande = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlStatus);
            ps2.setInt(1, idDemande);
            ps2.executeUpdate();

            conn.commit();

            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // TODO: handle exception
                }
            }
            e.printStackTrace();
            return false;
            // TODO: handle exception
        }
    }
}
