package mg.model;

import mg.DAO.CitoyenDAO;

public class DemandeMariage extends Demande{
    private int idConjoint1;
    private int idConjoint2;

    public DemandeMariage(int idDemande, int idCitoyenPrincipal, int idConjoint1, int idConjoint2) {
        // 1. Appel au constructeur de la classe mère (Demande)
        // idCitoyenPrincipal est souvent l'ID de celui qui dépose le dossier
        super(idDemande, idCitoyenPrincipal);
        
        // 2. Initialisation des attributs spécifiques au mariage
        this.idConjoint1 = idConjoint1;
        this.idConjoint2 = idConjoint2;
    }

    @Override
    public boolean verifierValidite() {
        CitoyenDAO dao = new CitoyenDAO();
        Citoyen c1 = dao.findById(this.idConjoint1);
        Citoyen c2 = dao.findById(this.idConjoint2);

        if (c1 == null || c2 == null) return false;

        if (!c1.getStatutMatrimonial().equals("Célibataire") && !c1.getStatutMatrimonial().equals("Divorcé")) {
            return false;
        }
        if (!c2.getStatutMatrimonial().equals("Célibataire") && !c2.getStatutMatrimonial().equals("Divorcé")) {
            return false;
        }
        if (c1.getIdCitoyen() == c2.getIdCitoyen()) return false;

        return true;
    }

    public int getIdConjoint1() {
        return idConjoint1;
    }

    public void setIdConjoint1(int idConjoint1) {
        this.idConjoint1 = idConjoint1;
    }

    public int getIdConjoint2() {
        return idConjoint2;
    }

    public void setIdConjoint2(int idConjoint2) {
        this.idConjoint2 = idConjoint2;
    }

    
}
