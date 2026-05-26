package mg.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mg.model.DatabaseConnexion;
import mg.model.DemandeMariage;
import mg.model.Mariage;

public class MariageDAO {

    // Méthode d'insertion incluant les deux témoins
    public boolean save(Mariage m) {
        String sql = "INSERT INTO mariage (id_epoux, id_epouse, id_temoin1, id_temoin2, date_mariage, lieu_mariage, regime_matrimonial) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, m.getIdEpoux());
            pstmt.setInt(2, m.getIdEpouse());
            pstmt.setInt(3, m.getIdTemoin1());
            pstmt.setInt(4, m.getIdTemoin2());
            pstmt.setDate(5, m.getDateMariage());
            pstmt.setString(6, m.getLieuMariage());
            pstmt.setString(7, m.getRegimeMatrimonial());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Jointure quadruple pour extraire les conjoints ET les témoins par ID
    public Mariage findById(int idMariage) {
        Mariage m = null;
        String sql = "SELECT m.*, " +
                     "e.nom as nom_e, e.prenoms as pre_e, " +
                     "w.nom as nom_w, w.prenoms as pre_w, " +
                     "t1.nom as nom_t1, t1.prenoms as pre_t1, " +
                     "t2.nom as nom_t2, t2.prenoms as pre_t2 " +
                     "FROM mariage m " +
                     "INNER JOIN citoyen e ON m.id_epoux = e.id_citoyen " +
                     "INNER JOIN citoyen w ON m.id_epouse = w.id_citoyen " +
                     "LEFT JOIN citoyen t1 ON m.id_temoin1 = t1.id_citoyen " +
                     "LEFT JOIN citoyen t2 ON m.id_temoin2 = t2.id_citoyen " +
                     "WHERE m.id_mariage = ?";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMariage);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    m = new Mariage();
                    m.setIdMariage(rs.getInt("id_mariage"));
                    m.setIdEpoux(rs.getInt("id_epoux"));
                    m.setIdEpouse(rs.getInt("id_epouse"));
                    m.setIdTemoin1(rs.getInt("id_temoin1"));
                    m.setIdTemoin2(rs.getInt("id_temoin2"));
                    m.setDateMariage(rs.getDate("date_mariage"));
                    m.setLieuMariage(rs.getString("lieu_mariage"));
                    m.setRegimeMatrimonial(rs.getString("regime_matrimonial"));
                    
                    m.setNomCompletEpoux(rs.getString("nom_e") + " " + rs.getString("pre_e"));
                    m.setNomCompletEpouse(rs.getString("nom_w") + " " + rs.getString("pre_w"));
                    
                    if (rs.getString("nom_t1") != null) {
                        m.setNomCompletTemoin1(rs.getString("nom_t1") + " " + rs.getString("pre_t1"));
                    }
                    if (rs.getString("nom_t2") != null) {
                        m.setNomCompletTemoin2(rs.getString("nom_t2") + " " + rs.getString("pre_t2"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public List<DemandeMariage> findAllDemandes() {
        List<DemandeMariage> liste = new ArrayList<>();
        String sql = "SELECT d.id_demande, d.id_citoyen_principal, d.id_conjoint1, d.id_conjoint2, " +
                    "c1.nom as nom1, c1.prenoms as prenom1, " +
                    "c2.nom as nom2, c2.prenoms as prenom2 " +
                    "FROM demande_mariage d " + 
                    "JOIN citoyen c1 ON d.id_conjoint1 = c1.id_citoyen " +
                    "JOIN citoyen c2 ON d.id_conjoint2 = c2.id_citoyen";

        try (Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DemandeMariage dm = new DemandeMariage(
                    rs.getInt("id_demande"),
                    rs.getInt("id_citoyen_principal"),
                    rs.getInt("id_conjoint1"),
                    rs.getInt("id_conjoint2")
                );
                liste.add(dm);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return liste;
    }

    // MODIFIÉ : Récupère maintenant aussi les noms des Témoins pour ton tableau principal
    public List<Object[]> findAllActesPourTableau() {
        List<Object[]> liste = new ArrayList<>();
        
        // Utilisation de LEFT JOIN pour les témoins : si id_temoin est NULL (comme pour tes anciens actes 1 et 2), 
        // la ligne s'affichera quand même sans faire planter l'application.
        String sql = "SELECT m.id_mariage, " +
                    "CONCAT(e.nom, ' ', e.prenoms) AS nom_epoux, " +
                    "CONCAT(w.nom, ' ', w.prenoms) AS nom_epouse, " +
                    "IFNULL(CONCAT(t1.nom, ' ', t1.prenoms), 'Non renseigné') AS nom_temoin1, " +
                    "IFNULL(CONCAT(t2.nom, ' ', t2.prenoms), 'Non renseigné') AS nom_temoin2, " +
                    "m.date_mariage, m.lieu_mariage, m.regime_matrimonial, m.statut " +
                    "FROM mariage m " +
                    "JOIN citoyen e ON m.id_epoux = e.id_citoyen " +
                    "JOIN citoyen w ON m.id_epouse = w.id_citoyen " +
                    "LEFT JOIN citoyen t1 ON m.id_temoin1 = t1.id_citoyen " +
                    "LEFT JOIN citoyen t2 ON m.id_temoin2 = t2.id_citoyen";

        try (Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Le tableau d'objets contient désormais 9 colonnes, dont le statut
                Object[] ligne = new Object[] {
                    rs.getInt("id_mariage"),
                    rs.getString("nom_epoux"),
                    rs.getString("nom_epouse"),
                    rs.getString("nom_temoin1"), // Nouvelle colonne
                    rs.getString("nom_temoin2"), // Nouvelle colonne
                    rs.getDate("date_mariage"),
                    rs.getString("lieu_mariage"),
                    rs.getString("regime_matrimonial"),
                    rs.getString("statut")
                };
                liste.add(ligne);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return liste;
    }

    public boolean updateStatut(int idMariage, String statut) {
        String sql = "UPDATE mariage SET statut = ? WHERE id_mariage = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            pstmt.setInt(2, idMariage);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateStatutEtHash(int idMariage, String statut, String hashBlockchain) {
        String sql = "UPDATE mariage SET statut = ?, hash_blockchain = ? WHERE id_mariage = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            pstmt.setString(2, hashBlockchain);
            pstmt.setInt(3, idMariage);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean ancrerNouveauBloc(String hashActuel, int idOfficier) { // Changement de String à int
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
                psInsert.setInt(4, idOfficier); // Utilisation de setInt au lieu de setString
                
                int rows = psInsert.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> getDonneesCompletesPourPDF(int idMariage) {
        String sql = "SELECT m.id_mariage, " +
                    "CONCAT(c1.nom, ' ', c1.prenoms) as nom_epoux, " +
                    "CONCAT(c2.nom, ' ', c2.prenoms) as nom_epouse, " +
                    "m.date_mariage, m.lieu_mariage, m.regime_matrimonial, " +
                    "CONCAT(t1.nom, ' ', t1.prenoms) as temoin1, " +
                    "CONCAT(t2.nom, ' ', t2.prenoms) as temoin2, " +
                    "m.hash_blockchain " +
                    "FROM mariage m " +
                    "JOIN citoyen c1 ON m.id_epoux = c1.id_citoyen " +
                    "JOIN citoyen c2 ON m.id_epouse = c2.id_citoyen " +
                    "LEFT JOIN citoyen t1 ON m.id_temoin1 = t1.id_citoyen " +
                    "LEFT JOIN citoyen t2 ON m.id_temoin2 = t2.id_citoyen " +
                    "WHERE m.id_mariage = ?";
                    
        java.util.HashMap<String, String> map = new java.util.HashMap<>();
        try (Connection conn = mg.model.DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idMariage);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    map.put("id_mariage", String.valueOf(rs.getInt("id_mariage")));
                    map.put("nom_epoux", rs.getString("nom_epoux"));
                    map.put("nom_epouse", rs.getString("nom_epouse"));
                    map.put("date_mariage", rs.getDate("date_mariage").toString());
                    map.put("lieu_mariage", rs.getString("lieu_mariage"));
                    map.put("regime_matrimonial", rs.getString("regime_matrimonial"));
                    map.put("temoin1", rs.getString("temoin1") != null ? rs.getString("temoin1") : "N/A");
                    map.put("temoin2", rs.getString("temoin2") != null ? rs.getString("temoin2") : "N/A");
                    
                    // Si le hash n'est pas encore dans la table mariage, on peut aller chercher 
                    // le dernier hash_actuel lié à cet officier ou générer une valeur par défaut.
                    String hash = rs.getString("hash_blockchain");
                    map.put("hash_blockchain", hash != null ? hash : "NON_ANCRE_ERREUR");
                    return map;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}