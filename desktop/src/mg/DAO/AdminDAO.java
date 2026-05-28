package mg.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import mg.model.DatabaseConnexion;

public class AdminDAO {

    /**
     * Récupère tous les blocs minés dans la blockchain
     */
    public List<Object[]> obtenirTousLesBlocs() {
        List<Object[]> liste = new ArrayList<>();
        
        // --- CORRECTION : CAST de la colonne timestamp en CHAR pour éviter l'erreur de conversion LONG ---
        String sql = "SELECT id_block, " +
                 "       FROM_UNIXTIME(timestamp / 1000, '%d/%m/%Y %H:%i:%s') AS date_bloc, " +
                 "       hash_precedent, hash_actuel, signature_officier " +
                 "FROM bloc_blockchain ORDER BY id_block DESC";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getInt("id_block"),
                    rs.getString("date_bloc"), // --- Récupération sécurisée en String ---
                    rs.getString("hash_precedent"),
                    rs.getString("hash_actuel"),
                    rs.getInt("signature_officier")
                });
            }
        } catch (Exception e) {
            System.out.println("Erreur AdminDAO (obtenirTousLesBlocs) : " + e.getMessage());
        }
        return liste;
    }

    /**
     * Calcule les statistiques globales pour le tableau de bord de l'administrateur.
     * Retourne un tableau d'entiers : [populationVivante, totalNaissances, totalMariages, totalDeces]
     */
    public int[] obtenirDonneesStatistiques() {
        int[] stats = new int[4]; // [0, 0, 0, 0]
        
        String sqlPopulation = "SELECT COUNT(*) FROM citoyen WHERE statut_matrimonial != 'Décédé'";
        String sqlNaissances = "SELECT COUNT(*) FROM naissance";
        String sqlMariages   = "SELECT COUNT(*) FROM mariage";
        String sqlDeces      = "SELECT COUNT(*) FROM deces";

        try (Connection conn = DatabaseConnexion.getConnection()) {
            
            // 1. Comptage de la population vivante
            try (PreparedStatement ps = conn.prepareStatement(sqlPopulation);
                ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats[0] = rs.getInt(1);
            }
            
            // 2. Comptage des naissances
            try (PreparedStatement ps = conn.prepareStatement(sqlNaissances);
                ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats[1] = rs.getInt(1);
            }
            
            // 3. Comptage des mariages
            try (PreparedStatement ps = conn.prepareStatement(sqlMariages);
                ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats[2] = rs.getInt(1);
            }
            
            // 4. Comptage des décès
            try (PreparedStatement ps = conn.prepareStatement(sqlDeces);
                ResultSet rs = ps.executeQuery()) {
                if (rs.next()) stats[3] = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Erreur AdminDAO (obtenirDonneesStatistiques) : " + e.getMessage());
        }
        
        return stats;
    }

    /**
     * Récupère le personnel de la mairie en joignant la table citoyen pour avoir les noms et prénoms
     */
    public List<Object[]> obtenirTousLesUtilisateurs() {
        List<Object[]> liste = new ArrayList<>();
        String sql = "SELECT u.id_user, c.nom, c.prenoms, u.login, u.role " +
                     "FROM utilisateur_mairie u " +
                     "JOIN citoyen c ON u.id_citoyen = c.id_citoyen " +
                     "ORDER BY u.id_user ASC";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                liste.add(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenoms"),
                    rs.getString("login"),
                    rs.getString("role")
                });
            }
        } catch (Exception e) {
            System.out.println("Erreur AdminDAO (obtenirTousLesUtilisateurs) : " + e.getMessage());
        }
        return liste;
    }

    public boolean citoyenExiste(int idCitoyen) {
        String sql = "SELECT COUNT(*) FROM citoyen WHERE id_citoyen = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCitoyen);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur AdminDAO (citoyenExiste) : " + e.getMessage());
        }
        return false;
    }

    /**
     * Insère un nouvel utilisateur dans la table utilisateur_mairie.
     * Remarque : Ta table n'inclut pas de colonne mot_de_passe d'après le select,
     * nous l'ajoutons ici si elle existe, sinon retire-la de la requête SQL.
     */
    public boolean inscrireUtilisateur(int idCitoyen, String login, String motDePasse, String role) {
        // Si ta table possède un champ mot_de_passe :
        String sql = "INSERT INTO utilisateur_mairie (id_citoyen, login, role) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCitoyen);
            pstmt.setString(2, login);
            pstmt.setString(3, role);
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Erreur AdminDAO (inscrireUtilisateur) : " + e.getMessage());
            return false;
        }
    }
}