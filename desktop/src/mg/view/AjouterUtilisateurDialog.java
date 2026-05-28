package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AjouterUtilisateurDialog extends JDialog {

    private JTextField txtIdCitoyen;
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;
    private JButton btnEnregistrer;
    private JButton btnAnnuler;
    private boolean valide = false;

    public AjouterUtilisateurDialog(JFrame parent) {
        super(parent, "➕ Ajouter un nouvel employé", true);
        setSize(380, 360);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setBackground(new Color(245, 246, 250));
        add(panel);

        // --- CHAMPS DE SAISIE ---
        addLabel(panel, "ID Citoyen (Doit exister dans la base) :");
        txtIdCitoyen = new JTextField();
        styleComponent(txtIdCitoyen);
        panel.add(txtIdCitoyen);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        addLabel(panel, "Identifiant / Login :");
        txtLogin = new JTextField();
        styleComponent(txtLogin);
        panel.add(txtLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        addLabel(panel, "Mot de passe :");
        txtPassword = new JPasswordField();
        styleComponent(txtPassword);
        panel.add(txtPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        addLabel(panel, "Rôle au sein de la mairie :");
        String[] roles = {"Agent", "Officier", "Administrateur"};
        comboRole = new JComboBox<>(roles);
        styleComponent(comboRole);
        panel.add(comboRole);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- BOUTONS D'ACTION ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(new Color(245, 246, 250));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.setBackground(new Color(189, 195, 199));
        btnAnnuler.setForeground(Color.WHITE);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.addActionListener(e -> dispose());
        buttonPanel.add(btnAnnuler);

        btnEnregistrer = new JButton("Enregistrer");
        btnEnregistrer.setBackground(new Color(46, 204, 113));
        btnEnregistrer.setForeground(Color.WHITE);
        btnEnregistrer.setFocusPainted(false);
        buttonPanel.add(btnEnregistrer);

        panel.add(buttonPanel);
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(new Color(71, 85, 105));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    private void styleComponent(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.setFont(new Font("Arial", Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    // --- GETTERS ET SETTERS ---
    public String getIdCitoyen() { return txtIdCitoyen.getText().trim(); }
    public String getLogin() { return txtLogin.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()).trim(); }
    public String getRole() { return (String) comboRole.getSelectedItem(); }
    public JButton getBtnEnregistrer() { return btnEnregistrer; }
}