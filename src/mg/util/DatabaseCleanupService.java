package mg.util;

import mg.DAO.DemandeDAO;

public class DatabaseCleanupService {

    public static void main(String[] args) {
        DemandeDAO demandeDAO = new DemandeDAO();

        boolean ok = demandeDAO.supprimerToutesLesDemandes();
        if (ok) {
            System.out.println("✅ Toutes les demandes ont été supprimées de la base de données.");
        } else {
            System.err.println("❌ Échec de la suppression des demandes. Consultez les logs pour plus de détails.");
        }
    }
}
