package mg.DAO;

import java.sql.*;
import java.time.LocalDate;

import mg.model.Citoyen;
import mg.model.DatabaseConnexion;;

public class CitoyenDAO {
    
    private Connection connection;

    public Citoyen findById(int id) {
        Citoyen citoyen = null;
        String sql = "SELECT c.*, p.nom as nom_pere, m.nom as nom_mere " +
                    "FROM citoyen c " +
                    "LEFT JOIN citoyen p ON c.id_pere = p.id_citoyen " +
                    "LEFT JOIN citoyen m ON c.id_mere = m.id_citoyen " +
                    "WHERE c.id_citoyen = ?";

        try (Connection connection = DatabaseConnexion.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                citoyen = new Citoyen();
                
                // --- Identité du Citoyen ---
                citoyen.setIdCitoyen(rs.getInt("id_citoyen"));
                citoyen.setNom(rs.getString("nom"));
                citoyen.setPrenoms(rs.getString("prenoms"));
                citoyen.setSexe(rs.getString("sexe").charAt(0));
                
                // --- Date et Lieu (Crucial pour l'acte de naissance) ---
                citoyen.setDateNaissance(rs.getDate("date_naissance")); // À ajouter dans Citoyen.java
                citoyen.setLieuNaissance(rs.getString("lieu_naissance")); 

                // --- Filiation (Les IDs pour la logique) ---
                // On récupère directement le nom via l'alias défini dans le SQL (nom_pere et nom_mere)
                String nomPere = rs.getString("nom_pere");
                if (nomPere != null) {
                    citoyen.setNomPere(nomPere);
                } else {
                    citoyen.setNomPere("Inconnu"); // Valeur par défaut si aucun père n'est trouvé
                }

                String nomMere = rs.getString("nom_mere");
                if (nomMere != null) {
                    citoyen.setNomMere(nomMere);
                } else {
                    citoyen.setNomMere("Inconnue"); // Valeur par défaut si aucune mère n'est trouvée
                }
                // --- Filiation (Les NOMS pour l'affichage dans le formulaire) ---
                // Ces alias viennent de tes "LEFT JOIN p" et "LEFT JOIN m"
                citoyen.setNomPere(rs.getString("nom_pere")); 
                citoyen.setNomMere(rs.getString("nom_mere"));
                
                citoyen.setStatutMatrimonial(rs.getString("statut_matrimonial"));
            }
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return citoyen;
    }
}
