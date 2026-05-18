package mg.controller;

import mg.DAO.DemandeDAO;
import mg.DAO.MariageDAO; // Import du nouveau DAO
import mg.model.Acte; 
import mg.view.AgentDashboard;
import mg.view.FormulaireSaisieCopie;
import java.util.List;
import javax.swing.JOptionPane;

public class AgentController {
    
    private DemandeDAO model;
    private MariageDAO mariageDAO; // Instance ajoutée
    private AgentDashboard view;
    private javax.swing.JFrame parentFrame;

    public AgentController(javax.swing.JFrame parent) {
        this.model = new DemandeDAO();
        this.mariageDAO = new MariageDAO(); // Initialisation
        this.parentFrame = parent;
    }

    public void setView(AgentDashboard view) {
        this.view = view;
        // 💡 Dès que la vue est liée, on charge immédiatement toutes les données
        actualiserVueTableau(); 
    }

    public void traiterAction(int idDossier, String statutActuel) {
        switch (statutActuel) {
            case "Nouveau":
                ouvrirFormulaireSaisie(idDossier);
                break;
            case "Signé":
                marquerCommeDelivre(idDossier);
                break;
            case "Saisi":
                JOptionPane.showMessageDialog(parentFrame, "Ce dossier est déjà chez l'Officier pour signature.");
                break;
            default:
                JOptionPane.showMessageDialog(null, "Action non autorisée pour ce statut.");
                break;
        }
    }

    private void ouvrirFormulaireSaisie(int idDossier) {
        int selectedRow = view.getTableNaissance().getSelectedRow();
        if (selectedRow == -1) return;

        int idCitoyen = (int) view.getTableNaissance().getValueAt(selectedRow, 1);
        String nomComplet = (String) view.getTableNaissance().getValueAt(selectedRow, 2);

        FormulaireSaisieCopie form = new FormulaireSaisieCopie(parentFrame, idDossier);
        form.setNom(nomComplet); 

        form.getBtnEnvoyer().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(form, 
                "VÉRIFICATION : \nNom : " + form.getNom().toUpperCase() + 
                "\nPrénoms : " + form.getPrenom().toUpperCase() + 
                "\nPère : " + form.getPere() + 
                "\nMère : " + form.getMere() + 
                "\n\nConfirmez-vous que ces informations sont conformes au registre papier ?", 
                "Validation avant Blockchain", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Acte acte = new Acte();
                acte.setIdCitoyen(idCitoyen);
                acte.setNumActe(form.getNumActe());
                acte.setBureau(form.getBureau());
                acte.setNom(form.getNom().toUpperCase());
                acte.setPrenom(form.getPrenom());
                acte.setDateNais(form.getDateNaissance());
                acte.setLieuNais(form.getLieu());
                acte.setPere(form.getPere());
                acte.setMere(form.getMere());
                acte.setLieuDelivrance(form.getLieuDelivrance());

                boolean succes = model.finaliserSaisieActe(acte, idDossier);

                if (succes) {
                    JOptionPane.showMessageDialog(form, "Acte enregistré avec succès et transmis à l'officier !");
                    form.dispose();
                    actualiserVueTableau();
                } else {
                    JOptionPane.showMessageDialog(form, "Erreur lors de l'enregistrement (Vérifiez si le N° d'acte existe déjà).", "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        form.setVisible(true);
    }

    private void marquerCommeDelivre(int id) {
        int confirmation = JOptionPane.showConfirmDialog(parentFrame, "Confirmez-vous la remise de l'acte physique au citoyen ?", "Clôture du dossier", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            if (model.mettreAJourStatut(id, "Délivré")) {
                JOptionPane.showMessageDialog(parentFrame, "Acte remis. Dossier clôturé.");
                actualiserVueTableau();
            }
        }
    }

    // --- CORRECTION ET CENTRALISATION DU CHARGEMENT ---
    public void actualiserVueTableau() {
        // 1. Rechargement des Naissances
        List<Object[]> listeNaissance = model.obtenirDemandeparType("Naissance");
        if (view != null) {
            view.chargerDonnees(listeNaissance);
        }
        
        // 2. Rechargement des Mariages (Double jointure SQL)
        List<Object[]> listeMariage = mariageDAO.findAllActesPourTableau();
        if (view != null) {
            view.chargerDonneesMariage(listeMariage);
        }
    }
}