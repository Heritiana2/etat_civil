package mg.model;

import mg.DAO.CitoyenDAO;

public class DemandeNaissance extends Demande {
    
    public DemandeNaissance(int idDemande, int idCitoyenQuiDemande) {
        // idCitoyenQuiDemande est l'ID de la personne qui veut son bulletin
        super(idDemande, idCitoyenQuiDemande);
    }

    @Override
    public boolean verifierValidite() {
        CitoyenDAO dao = new CitoyenDAO();
        Citoyen c = dao.findById(this.idCitoyen);
        
        // La demande est valide si le citoyen existe bien dans la base
        return (c != null);
    }
}