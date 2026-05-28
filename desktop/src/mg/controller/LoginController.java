package mg.controller;

import mg.view.*;
import mg.DAO.UserDAO;

import javax.swing.*;

public class LoginController {
    private LoginFrame view;
    private UserDAO model;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.model = new UserDAO();
    }

    public void validerConnexion() {
        String pass = new String(view.getPasswordField().getPassword());
        String role = (String) view.getRoleCombo().getSelectedItem();

        if (model.verifierLogin(pass, role)) {
            int userId = model.trouverIdUtilisateur(pass, role);
            if (userId < 0) {
                JOptionPane.showMessageDialog(view, "Impossible de récupérer l'identifiant utilisateur.", "Erreur CUA", JOptionPane.ERROR_MESSAGE);
                return;
            }
            view.dispose();
            redirigerVersDashboard(role, userId);
        }
        else {
            JOptionPane.showMessageDialog(view, "Vous n'avez pas d'accès sur le rôle de : " + role, "Erreur CUA", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void redirigerVersDashboard(String role, int userId) {
        if (role.equals("Agent")) {
            JFrame mainFrame = new JFrame("CUA - Gestion État Civil (Agent)");
            mainFrame.setSize(1000, 700);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLocationRelativeTo(null);

            // 1. On crée le contrôleur avec l'id de l'agent connecté
            AgentController agentCtrl = new AgentController(mainFrame, userId);

            // 2. On passe le contrôleur au Dashboard
            AgentDashboard dash = new AgentDashboard(agentCtrl);

            // 3. On lie la vue au contrôleur
            agentCtrl.setView(dash);
            
            // 4. On appelle la méthode complète
            agentCtrl.actualiserVueTableau();

            mainFrame.add(dash);
            mainFrame.setVisible(true);
        }
        else if (role.equals("Officier")) {
            JFrame officierFrame = new JFrame("CUA - Espace Officier (Signature Numérique)");
            officierFrame.setSize(800, 400);
            officierFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            officierFrame.setLocationRelativeTo(null);

            OfficierController officierCtrl = new OfficierController(officierFrame, userId);

            OfficierDashboard dash = new OfficierDashboard(officierCtrl);

            officierCtrl.setView(dash);
            officierCtrl.actualiserTableau();

            officierFrame.add(dash);
            officierFrame.setVisible(true);
        }
        // ========================================================
        // 👑 NOUVEAU : INTEGRATION DE L'ESPACE ADMINISTRATEUR
        // ========================================================
        else if (role.equals("Admin")) {
            JFrame adminFrame = new JFrame("CUA - Panneau d'Administration (Sécurité & Blockchain)");
            adminFrame.setSize(1000, 600); // Fenêtre large pour accueillir les tables de hashs
            adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            adminFrame.setLocationRelativeTo(null);

            // 1. On instancie le contrôleur admin
            AdminController adminCtrl = new AdminController(adminFrame);

            // 2. On instancie l'IHM
            AdminDashboard dash = new AdminDashboard();

            // 3. On effectue le double couplage MVC
            dash.setController(adminCtrl);
            adminCtrl.setView(dash); // Cette méthode appelle automatiquement model.obtenirTousLesBlocs()

            adminFrame.add(dash);
            adminFrame.setVisible(true);

            view.dispose();
        }
        else {
            JOptionPane.showMessageDialog(view, "Interface " + role + " en cours de développement.");
        }
    }
}