package mg.controller;

import mg.view.*;
import mg.model.*;

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
            view.dispose();
            redirigerVersDashboard(role);
            
        }
        else {
            JOptionPane.showMessageDialog(view, "Vous n'avez pas une accès sur le rôle de : " + role, "Erreur CUA", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void redirigerVersDashboard(String role) {
        if (role.equals("Agent")) {
            JFrame mainFrame = new JFrame("CUA - Gestion État Civil (Agent)");
            mainFrame.setSize(1000, 700);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLocationRelativeTo(null);

            // 1. On crée le contrôleur
            AgentController agentCtrl = new AgentController(mainFrame);

            // 2.On passe le contrôleur au Dashboard
            AgentDashboard dash = new AgentDashboard(agentCtrl); 

            // 3.On lie la vue au contrôleur
            agentCtrl.setView(dash);
            
            // 4.On appelle la méthode complète
            agentCtrl.actualiserVueTableau(); 

            mainFrame.add(dash);
            mainFrame.setVisible(true);
            
            view.dispose(); 
        }
        else {
            JOptionPane.showMessageDialog(view, "Interface " + role + " en cours de developpement.");
        }
    }
}