package mg.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mg.model.DatabaseConnexion; // Ajuste selon ta classe de connexion BDD réelle

public class BlockchainDAO {

    /**
     * Parcourt toute la table bloc_blockchain et vérifie le chaînage des blocs.
     * @return true si la blockchain est intègre, false si un bloc a été falsifié.
     */
    public boolean validerIntegriteBlockchain() {
        // CORRECTION ICI : Utilisation de 'id_block' au lieu de 'index_bloc'
        String sql = "SELECT hash_precedent, hash_actuel FROM bloc_blockchain ORDER BY id_block ASC";
        
        String hashAttendu = null;
        boolean estPremierBloc = true;

        try (Connection conn = DatabaseConnexion.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String hashPrecedent = rs.getString("hash_precedent");
                String hashActuel = rs.getString("hash_actuel");

                if (estPremierBloc) {
                    // C'est le Bloc Genesis
                    hashAttendu = hashActuel;
                    estPremierBloc = false;
                } else {
                    // Vérification du maillon de la chaîne
                    if (hashPrecedent == null || !hashPrecedent.equalsIgnoreCase(hashAttendu)) {
                        System.out.println("[ALERTE BLOCKCHAIN] Rupture de liaison détectée !");
                        System.out.println("Attendu : " + hashAttendu);
                        System.out.println("Reçu dans le bloc : " + hashPrecedent);
                        return false; 
                    }
                    hashAttendu = hashActuel;
                }
            }
            return true; 

        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la Blockchain : " + e.getMessage());
            return false; 
        }
    }
}