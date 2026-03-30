package mg.view;
import javax.swing.*;
import java.awt.*;
import mg.controller.*;

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
        setSize(400, 600);
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
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
      
        addLabel(panel, "Rôle : ");
        String[] roles = {"Agent", "Officier", "Administrateur"};
        roleCombo = new JComboBox<>(roles);
        styleComponent(roleCombo);
        panel.add(roleCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        addLabel(panel, "Mot de passe :");
        passwordField = new JPasswordField();
        styleComponent(passwordField);
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnLogin = new JButton("SE CONNECTER");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(COLOR_CUA_BLUE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnClose = new JButton("Quitter");
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setForeground(Color.GRAY);
        btnClose.addActionListener(e -> System.exit(0));

        btnLogin.addActionListener(e-> {
            LoginController controller = new LoginController(this);
            controller.validerConnexion();
        });

        panel.add(btnLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
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
        l.setForeground(COLOR_TEXT_LABEL);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void styleComponent(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.setBackground(Color.WHITE); // Plus moderne pour le thème CUA
        c.setForeground(Color.BLACK);
        c.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}