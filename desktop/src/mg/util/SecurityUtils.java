package mg.util;

public class SecurityUtils {

    /**
     * Prépare les données de l'acte de naissance et appelle StringUtil pour calculer le hash SHA-256.
     */
    public static String calculerHashNaissance(String nom, String prenoms, String dateNaiss, String lieu, String pere, String mere) {
        // 1. On sécurise les valeurs nulles et supprime les espaces pour éviter les décalages de hash
        String n = (nom != null) ? nom.trim().toUpperCase() : "";
        String p = (prenoms != null) ? prenoms.trim() : "";
        String d = (dateNaiss != null) ? dateNaiss.trim() : "";
        String l = (lieu != null) ? lieu.trim() : "";
        String pr = (pere != null) ? pere.trim() : "";
        String mr = (mere != null) ? mere.trim() : "";

        // 2. Concaténation des données selon l'ordre strict défini pour le projet
        String chaineUnique = n + ";" + p + ";" + d + ";" + l + ";" + pr + ";" + mr;

        // 3. Appel direct de ton algorithme personnalisé dans StringUtil
        // Note : Ajuste le nom de la méthode si elle s'appelle autrement (ex: appliquerSha256)
        return mg.util.StringUtil.applySha256(chaineUnique); 
    }

    public static String calculerHashDeces(String nomDefunt, String prenomsDefunt, String dateDeces, String lieuDeces) {
        String n = (nomDefunt != null) ? nomDefunt.trim().toUpperCase() : "";
        String p = (prenomsDefunt != null) ? prenomsDefunt.trim() : "";
        String d = (dateDeces != null) ? dateDeces.trim() : "";
        String l = (lieuDeces != null) ? lieuDeces.trim() : "";

        String chaineUnique = n + ";" + p + ";" + d + ";" + l;
        return mg.util.StringUtil.applySha256(chaineUnique);
    }
}