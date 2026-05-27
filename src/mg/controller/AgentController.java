package mg.controller;

import mg.DAO.DemandeDAO;
import mg.DAO.MariageDAO; // Import du nouveau DAO
import mg.DAO.NaissanceDAO;
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
        traiterAction(idDossier, statutActuel, "Naissance");
    }

    public void traiterAction(int idDossier, String statutActuel, String typeDemande) {
        if (statutActuel == null || typeDemande == null) return;
        
        String statutNettoye = statutActuel.toUpperCase().trim();
        String typeNettoye = typeDemande.trim();

        if (typeNettoye.equalsIgnoreCase("Décès") || typeNettoye.equalsIgnoreCase("Deces")) {
            switch (statutNettoye) {
                case "NOUVEAU":
                    JOptionPane.showMessageDialog(parentFrame, "La saisie de décès n'est pas encore implémentée dans cet écran.");
                    break;
                case "SIGNE":
                case "DELIVRE":
                case "DÉLIVRÉ":
                    marquerCommeDelivre(idDossier);
                    break;
                case "SAISI":
                    JOptionPane.showMessageDialog(parentFrame, "Ce dossier de décès est déjà chez l'officier pour signature.");
                    break;
                default:
                    JOptionPane.showMessageDialog(parentFrame, "Action non autorisée pour le statut : " + statutActuel);
                    break;
            }
            return;
        }

        switch (statutNettoye) {
            case "NOUVEAU":
                ouvrirFormulaireSaisie(idDossier);
                break;
            case "SIGNE":
                marquerCommeDelivre(idDossier);
                break;
            case "SAISI":
                JOptionPane.showMessageDialog(parentFrame, "Ce dossier est déjà chez l'Officier pour signature.");
                break;
            case "LIVRE":
            case "DÉLIVRÉ":
                JOptionPane.showMessageDialog(parentFrame, "Cet acte a déjà été imprimé et délivré au citoyen.");
                break;
            default:
                JOptionPane.showMessageDialog(parentFrame, "Action non autorisée pour le statut : " + statutActuel);
                break;
        }
    }
    private void ouvrirFormulaireSaisie(int idDossier) {
        int selectedRow = view.getTableNaissance().getSelectedRow();
        if (selectedRow == -1) return;

        String nomComplet = (String) view.getTableNaissance().getValueAt(selectedRow, 2);

        NaissanceDAO naissanceDAO = new NaissanceDAO();
        DemandeDAO demandeDAO = new DemandeDAO();

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
                boolean succes = naissanceDAO.insererNaissance(
                    idDossier,
                    form.getNom().toUpperCase(),
                    form.getPrenom(),
                    form.getDateNaissance(),
                    form.getLieu(),
                    form.getPere(),
                    form.getMere()
                );

                if (succes) {
                    demandeDAO.mettreAJourStatut(idDossier, "SAISI");
                    JOptionPane.showMessageDialog(form, "Acte de naissance saisi avec succès et transmis à l'officier !");
                    form.dispose();
                    actualiserVueTableau();
                } else {
                    JOptionPane.showMessageDialog(form, "Erreur lors de l'enregistrement de l'acte de naissance.", "Erreur SQL", JOptionPane.ERROR_MESSAGE);
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
        if (view == null) {
            return;
        }

        // 1. Rechargement des Naissances
        List<Object[]> listeNaissance = model.obtenirDemandeparType("Naissance");
        view.chargerDonnees(listeNaissance);
        
        // 2. Rechargement des Mariages (Double jointure SQL)
        List<Object[]> listeMariage = mariageDAO.findAllActesPourTableau();
        view.chargerDonneesMariage(listeMariage);

        // 3. Rechargement des Décès
        List<Object[]> listeDeces = model.obtenirDemandeparType("Décès");
        view.chargerDonneesDeces(listeDeces);
    }
}