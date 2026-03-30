package mg.controller;

import mg.model.DemandeDAO;

import mg.view.AgentDashboard;

import java.util.List;

import javax.swing.JOptionPane;
import mg.view.FormulaireSaisieCopie;

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
            case "Délivré":
                JOptionPane.showMessageDialog(parentFrame, "Cet acte est déjà remis au citoyen.");
                break;
            case "Saisi":
                JOptionPane.showConfirmDialog(parentFrame, "Ce dossier est déjà chez l'Officier pour siganture.");
                break;
            default:
                JOptionPane.showMessageDialog(null, "En attente de l'Officier...");
                break;
        }
    }

    private void ouvrirFormulaireSaisie(int id) {
        FormulaireSaisieCopie form = new FormulaireSaisieCopie(parentFrame, id);

        form.getBtnEnvoyer().addActionListener(e -> {
            // On récupère les données de la vue
            String nom = form.getNom();
            String pnom = form.getPrenom();
            String date = form.getDateNaissance();
            
            // On prépare le contenu pour la table acte_original
            String contenu = "NOM: " + nom + " | PRENOMS: " + pnom + " | DATE: " + date;

            // On enregistre en base de données
            boolean succes = model.enregistrerSaisieActe(id, contenu);

            if (succes) {
                JOptionPane.showMessageDialog(form, "Données enregistrées ! Le dossier est transmis à l'officier.");
                form.dispose();
            } else {
                JOptionPane.showMessageDialog(form, "Erreur SQL lors de l'enregistrement.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.setVisible(true);
        actualiserVueTableau();
    }
    private void marquerCommeDelivre(int id) {

        int confiramation = JOptionPane.showConfirmDialog(parentFrame, "Confirmez-vous la remise de l'acte au citoyen ?", "Clôture du dossier", JOptionPane.YES_NO_OPTION);

        if (confiramation == JOptionPane.YES_OPTION) {
            boolean success = model.mettreAJourStatut(id, "Délivré");

            if (success) {
                JOptionPane.showMessageDialog(parentFrame, "Acte remis au citoyen. Dossier clôturé.");
                actualiserVueTableau();
            }
            else {
                JOptionPane.showMessageDialog(parentFrame, "Erreur SQL lors de la clôture.");
            }
        }
    }

    public void actualiserVueTableau() {
        System.out.println("Raffraichissement de la liste des demandes...");
        List <Object[]> liste = model.obtenirDemandeparType("Naissance");

       view.chargerDonnees(liste);

    }
}
