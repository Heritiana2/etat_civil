package mg.controller;

import mg.model.DemandeDAO;
import mg.model.Acte; // N'oublie pas l'import de ton modèle Acte
import mg.view.AgentDashboard;
import mg.view.FormulaireSaisieCopie;
import java.util.List;
import javax.swing.JOptionPane;

public class AgentController {
    
    private DemandeDAO model;
    private AgentDashboard view;
    private javax.swing.JFrame parentFrame;

    public AgentController(javax.swing.JFrame parent) {
        this.model = new DemandeDAO();
        this.parentFrame = parent;
    }

    public void setView(AgentDashboard view) {
        this.view = view;
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
        // 1. Récupérer les infos de la ligne sélectionnée pour le pré-remplissage
        int selectedRow = view.getTableNaissance().getSelectedRow();
        if (selectedRow == -1) return;

        // On récupère l'ID Citoyen (qui est en colonne 1 selon ton DAO) et le Nom complet
        int idCitoyen = (int) view.getTableNaissance().getValueAt(selectedRow, 1);
        String nomComplet = (String) view.getTableNaissance().getValueAt(selectedRow, 2);

        FormulaireSaisieCopie form = new FormulaireSaisieCopie(parentFrame, idDossier);
        
        // Pré-remplissage automatique du nom pour aider l'agent
        form.setNom(nomComplet); 

        form.getBtnEnvoyer().addActionListener(e -> {
            
            // 2. Vérification visuelle (Sécurité anti-faute d'orthographe)
            int confirm = JOptionPane.showConfirmDialog(form, 
                "VÉRIFICATION : \nNom : " + form.getNom().toUpperCase() + 
                "\nPrénoms : " + form.getPrenom().toUpperCase() + 
                "\nPère : " + form.getPere() + 
                "\nMère : " + form.getMere() + 
                "\n\nConfirmez-vous que ces informations sont conformes au registre papier ?", 
                "Validation avant Blockchain", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // 3. On crée l'objet Acte avec TOUS les champs du formulaire CUA
                Acte acte = new Acte();
                acte.setIdCitoyen(idCitoyen);
                acte.setNumActe(form.getNumActe());
                acte.setBureau(form.getBureau());
                acte.setNom(form.getNom().toUpperCase());
                acte.setPrenom(form.getPrenom());
                acte.setDateNais(form.getDateNaissance());
                acte.setHeureNais(form.getHeureNaissance());
                acte.setLieuNais(form.getLieu());
                acte.setPere(form.getPere());
                acte.setMere(form.getMere());
                acte.setLieuDelivrance(form.getLieuDelivrance());

                // 4. On enregistre via le DAO (transaction SQL)
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

    public void actualiserVueTableau() {
        List<Object[]> liste = model.obtenirDemandeparType("Naissance");
        view.chargerDonnees(liste);
    }
}