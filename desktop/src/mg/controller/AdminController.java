package mg.controller;

import mg.DAO.AdminDAO;
import mg.view.AdminDashboard;
import mg.view.AjouterUtilisateurDialog;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.util.List;

public class AdminController {

    private AdminDAO model;
    private AdminDashboard view;
    private JFrame parentFrame;

    public AdminController(JFrame parent) {
        this.model = new AdminDAO();
        this.parentFrame = parent;
    }

    public void setView(AdminDashboard view) {
        this.view = view;
        actualiserDonnees();
    }

    /**
     * Ouvre le formulaire d'inscription et traite l'enregistrement
     */
    public void ouvrirFormulaireNouvelEmploye() {
        AjouterUtilisateurDialog dialog = new AjouterUtilisateurDialog(parentFrame);
        
        dialog.getBtnEnregistrer().addActionListener(e -> {
            String idString = dialog.getIdCitoyen();
            String login = dialog.getLogin();
            String pass = dialog.getPassword();
            String role = dialog.getRole();

            // Validations basiques des champs vides
            if (idString.isEmpty() || login.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs.", "Saisie incomplète", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int idCitoyen = Integer.parseInt(idString);

                // 1. Vérification de la présence du citoyen dans le registre CUA
                if (!model.citoyenExiste(idCitoyen)) {
                    JOptionPane.showMessageDialog(dialog, "L'ID Citoyen " + idCitoyen + " n'existe pas dans le registre civil !", "Citoyen introuvable", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Création effective du compte personnel de la mairie
                boolean succes = model.inscrireUtilisateur(idCitoyen, login, pass, role);
                if (succes) {
                    JOptionPane.showMessageDialog(dialog, "Le compte de " + login + " (" + role + ") a été créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose(); // Fermer la fenêtre
                    actualiserDonnees(); // Rafraîchir instantanément le JTable de l'IHM
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'enregistrement. (Login déjà pris ?)", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "L'ID Citoyen doit être un nombre valide.", "Format incorrect", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    public void actualiserDonnees() {
        if (view == null) return;
        int[] stats = model.obtenirDonneesStatistiques();
        view.modifierCompteursStatistiques(stats[0], stats[1], stats[2], stats[3]);

        List<Object[]> blocs = model.obtenirTousLesBlocs();
        view.chargerDonneesBlockchain(blocs);

        List<Object[]> utilisateurs = model.obtenirTousLesUtilisateurs();
        view.chargerDonneesUtilisateurs(utilisateurs);
    }
}