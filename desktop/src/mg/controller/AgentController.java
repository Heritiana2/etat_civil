package mg.controller;

import mg.DAO.AdminDAO;
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
    private int agentId;

    public AgentController(javax.swing.JFrame parent, int agentId) {
        this.model = new DemandeDAO();
        this.mariageDAO = new MariageDAO(); // Initialisation
        this.parentFrame = parent;
        this.agentId = agentId;
    }

    public int getAgentId() {
        return agentId;
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

        // Lier la demande à l'agent connecté dès que l'agent ouvre le dossier
        demandeDAO.attribuerAgentTraitant(idDossier, agentId);

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
                int idOfficierSelectionne = choisirOfficier();
                if (idOfficierSelectionne <= 0) {
                    JOptionPane.showMessageDialog(form, "Sélection de l'officier annulée. La saisie n'a pas été transmise.", "Annulation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

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
                    demandeDAO.attribuerOfficier(idDossier, idOfficierSelectionne);
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

    private int choisirOfficier() {
        AdminDAO adminDAO = new AdminDAO();
        java.util.List<Object[]> officiers = adminDAO.obtenirTousLesUtilisateurs();
        java.util.List<String> options = new java.util.ArrayList<>();
        java.util.Map<String, Integer> mapOfficiers = new java.util.HashMap<>();

        for (Object[] ligne : officiers) {
            String role = ligne[4] != null ? ligne[4].toString() : "";
            if ("Officier".equalsIgnoreCase(role)) {
                int id = (int) ligne[0];
                String nom = ligne[1] != null ? ligne[1].toString() : "";
                String prenoms = ligne[2] != null ? ligne[2].toString() : "";
                String login = ligne[3] != null ? ligne[3].toString() : "";
                String label = id + " - " + nom + " " + prenoms + " (" + login + ")";
                options.add(label);
                mapOfficiers.put(label, id);
            }
        }

        if (options.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Aucun officier disponible n'a été trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        String selection = (String) JOptionPane.showInputDialog(
            parentFrame,
            "Choisissez l'officier qui signera ce dossier :",
            "Sélection de l'officier",
            JOptionPane.PLAIN_MESSAGE,
            null,
            options.toArray(new String[0]),
            options.get(0)
        );

        if (selection == null || selection.isEmpty()) {
            return -1;
        }
        return mapOfficiers.getOrDefault(selection, -1);
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

        // 1. Rechargement des Naissances assignées à l'agent connecté
        List<Object[]> listeNaissance = model.obtenirDemandeparTypePourAgent("Naissance", agentId);
        view.chargerDonnees(listeNaissance);
        
        // 2. Rechargement des Mariages (Double jointure SQL)
        List<Object[]> listeMariage = mariageDAO.findAllActesPourTableau();
        view.chargerDonneesMariage(listeMariage);

        // 3. Rechargement des Décès assignées à l'agent connecté
        List<Object[]> listeDeces = model.obtenirDemandeparTypePourAgent("Décès", agentId);
        view.chargerDonneesDeces(listeDeces);
    }
}