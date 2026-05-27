package mg.view;

import javax.swing.*;
import java.awt.*;
import mg.controller.*;
import mg.DAO.BlockchainDAO; // Importation du DAO de ta blockchain

public class LoginFrame extends JFrame {
    private static final Color COLOR_BG = new Color(245, 246, 250);
    private static final Color COLOR_CUA_BLUE = new Color(0, 102, 204);
    private static final Color COLOR_CUA_YELLOW = new Color(255, 204, 0);
    private static final Color COLOR_TEXT_LABEL = new Color(50, 50, 50);

    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    private float opacity = 0.0f;

    public LoginFrame() {
        setTitle("CUA - Connexion sécurisée");
        setSize(400, 650); // Légèrement agrandi pour accueillir le bouton de vérification publique
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setOpacity(0.0f);

        Timer fadeInTimer = new Timer(30, e -> {
            opacity += 0.05f;
            if (opacity >= 1.0f) {
                opacity = 1.0f;
                ((Timer)e.getSource()).stop();
            }
            setOpacity(opacity);
        });
        fadeInTimer.start();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(panel);

        ImageIcon logoIcon = null;
        try {
            ImageIcon tempIcon = new ImageIcon("src/mg/view/images.png");
            if (tempIcon.getIconWidth() > 0) {
                Image img = tempIcon.getImage();
                logoIcon = new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        JLabel imageLabel = new JLabel(logoIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(imageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel logoLabel = new JLabel("COMMUNE URBAINE D'ANTANANARIVO");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 15));
        logoLabel.setForeground(COLOR_CUA_BLUE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        JLabel subTitle = new JLabel("État civil Numérique");
        subTitle.setForeground(COLOR_CUA_YELLOW);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 35)));
      
        addLabel(panel, "Rôle : ");
        String[] roles = {"Agent", "Officier", "Admin"};
        roleCombo = new JComboBox<>(roles);
        styleComponent(roleCombo);
        panel.add(roleCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        addLabel(panel, "Mot de passe :");
        passwordField = new JPasswordField();
        styleComponent(passwordField);
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- BOUTON DE CONNEXION ---
        JButton btnLogin = new JButton("SE CONNECTER");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(COLOR_CUA_BLUE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogin.addActionListener(e -> {
            LoginController controller = new LoginController(this);
            controller.validerConnexion();
        });
        panel.add(btnLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- SÉPARATEUR VISUEL ---
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- INTEGRATION LOGIQUE : BOUTON ESPACE VÉRIFICATEUR PUBLIC ---
        JButton btnVerificateur = new JButton("🔍 VÉRIFIER UN ACTE (PUBLIC)");
        btnVerificateur.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerificateur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnVerificateur.setBackground(new Color(44, 62, 80)); // Couleur sombre axée sécurité
        btnVerificateur.setForeground(Color.WHITE);
        btnVerificateur.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerificateur.setFocusPainted(false);
        btnVerificateur.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVerificateur.addActionListener(e -> {
            // Ouvre l'interface publique sans requérir de connexion préalable
            new mg.view.VerificateurActeFrame().setVisible(true);
        });
        panel.add(btnVerificateur);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- BOUTON QUITTER ---
        JButton btnClose = new JButton("Quitter");
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setForeground(Color.GRAY);
        btnClose.addActionListener(e -> System.exit(0));
        panel.add(btnClose);
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JComboBox<String> getRoleCombo() {
        return roleCombo;
    }

    private void addLabel(JPanel p, String text) {
        JLabel l = new JLabel(text);
        l.removeNotify();
        l.setForeground(COLOR_TEXT_LABEL);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void styleComponent(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.setBackground(Color.WHITE);
        c.setForeground(Color.BLACK);
        c.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    public static void main(String[] args) {
        // -----------------------------------------------------------------
        // ETAPE 3 : CONTRÔLE DE ROBUSTESSE AUTOMATIQUE AU DÉMARRAGE
        // -----------------------------------------------------------------
        System.out.println("[SÉCURITÉ CUA] Audit d'intégrité de la blockchain locale...");
        
        BlockchainDAO blockchainDAO = new BlockchainDAO();
        boolean estValide = blockchainDAO.validerIntegriteBlockchain();

        if (!estValide) {
            // Blocage immédiat si la chaîne est rompue dans MySQL (fraude détectée dans le passé)
            JOptionPane.showMessageDialog(null, 
                "🚨 ALERTE SÉCURITÉ CRITIQUE 🚨\n\n" +
                "L'intégrité de la Blockchain de l'État Civil a été corrompue.\n" +
                "Une falsification de l'historique a été détectée dans les tables SQL.\n\n" +
                "L'application va se fermer immédiatement pour protéger le registre civil.", 
                "Rupture de Chaînage Détectée", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(0); // Arrêt immédiat et de force du programme
        }

        System.out.println("[SÉCURITÉ CUA] Registre intègre et validé à 100%. Lancement.");
        
        // Lancement de l'IHM standard si tout est en ordre
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}