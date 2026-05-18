package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import mg.controller.OfficierController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OfficierDashboard extends JPanel{
    
    private static final Color COLOR_CUA_BLUE = new Color(0, 102, 204);
    private static final Color COLOR_CUA_BLUE_HOVER = new Color(0, 82, 164); 
    private static final Color COLOR_CUA_YELLOW = new Color(255, 204, 0);
    private static final Color COLOR_CUA_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_CUA_GREEN_HOVER = new Color(36, 164, 93);
    private static final Color COLOR_BG_PANEL = new Color(245, 246, 250); 
    private static final Color COLOR_TEXT_HEADER = Color.WHITE;

    private JTable tableAttente;
    private JTabbedPane tabbedPane;
    private DefaultTableModel tableModelAttente;
    private OfficierController controller;

    public OfficierDashboard(OfficierController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG_PANEL);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(COLOR_CUA_BLUE);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        ImageIcon logoIcon = null;

        try {
            ImageIcon tempIcon = new ImageIcon("src/mg/view/images.png");
            if (tempIcon.getIconWidth() > 0) {
                Image img = tempIcon.getImage();
                logoIcon = new ImageIcon(img.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.out.println("Erreur chargement logo Dashboard: " + e.getMessage());
        }

        JLabel logoLabel = new JLabel(logoIcon);
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        JLabel titleLabel = new JLabel("DIRECTION DE L'ÉTAT CIVIL - COMMUNE URBAINE D'ANTANANARIVO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_TEXT_HEADER);
        headerPanel.add(titleLabel);

        headerPanel.add(Box.createHorizontalGlue());
        JLabel roleLabel = new JLabel("ESPACE OFFICIER - SIGNATURE NUMÉRIQUE");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(COLOR_CUA_YELLOW);
        headerPanel.add(roleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // 2. LE PANNEAU CENTRAL AVEC ONGLETS
        JPanel centralPanel = new JPanel(new BorderLayout(5, 5));
        centralPanel.setBackground(COLOR_BG_PANEL);
        centralPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // On crée les onglets (pour l'instant, on se concentre sur Naissances)
        tabbedPane.add("Naissances", createCategoryTab("Naissance"));
        tabbedPane.add("Mariages", createCategoryTab("Mariage"));
        tabbedPane.add("Décès", createCategoryTab("Décès"));

        centralPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centralPanel, BorderLayout.CENTER);

        JPanel sideBarJPanel = new JPanel();
        sideBarJPanel.setLayout(new BoxLayout(sideBarJPanel, BoxLayout.Y_AXIS));
        sideBarJPanel.setBackground(COLOR_BG_PANEL);
        sideBarJPanel.setBorder(new EmptyBorder(25, 10, 10, 15));

        JButton btnActualiser = new JButton("Actualiser");
        styleAnimateButton(btnActualiser, COLOR_CUA_BLUE, COLOR_CUA_BLUE_HOVER);
        btnActualiser.addActionListener(e -> controller.actualiserTableau());
        sideBarJPanel.add(btnActualiser);

        sideBarJPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnSigner = new JButton("Signer l'acte");
        styleAnimateButton(btnSigner, COLOR_CUA_GREEN, COLOR_CUA_GREEN_HOVER);
        btnSigner.addActionListener(e -> controller.gererSignature());
        sideBarJPanel.add(btnSigner);

        sideBarJPanel.add(Box.createVerticalGlue());
        add(sideBarJPanel, BorderLayout.EAST);
    }

    private JPanel createCategoryTab(String category) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] columnNames = {"ID", "Citoyen", "Nom et Prénoms", "Date de la Demande", "Statut"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        if (category.equals("Naissance")) {
            this.tableAttente = table;
            this.tableModelAttente = model;
        }

        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(table), BorderLayout.CENTER);
        }};
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(30);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(new Color(60, 60, 60));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void styleAnimateButton(JButton button, Color normalColor, Color hoverColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setFont(new Font("Arial", Font.BOLD,  14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    // --- GETTERS POUR LE CONTROLLER ---
    public JTable getTableAttente() { return tableAttente; }
    public JTabbedPane getTabbedPane() {
        return tabbedPane; // Assure-toi que ta variable s'appelle bien tabbedPane
    }
    public DefaultTableModel getTableModelAttente() { return tableModelAttente; }
}
