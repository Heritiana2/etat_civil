package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import mg.controller.OfficierController;
import mg.model.Mariage;
import mg.DAO.MariageDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class OfficierDashboard extends JPanel {
    
    private static final Color COLOR_CUA_BLUE = new Color(0, 102, 204);
    private static final Color COLOR_CUA_BLUE_HOVER = new Color(0, 82, 164); 
    private static final Color COLOR_CUA_YELLOW = new Color(255, 204, 0);
    private static final Color COLOR_CUA_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_CUA_GREEN_HOVER = new Color(36, 164, 93);
    private static final Color COLOR_BG_PANEL = new Color(245, 246, 250); 
    private static final Color COLOR_TEXT_HEADER = Color.WHITE;

    // Tableaux et modèles distincts pour Naissances, Mariages et Décès
    private JTable tableAttente;
    private DefaultTableModel tableModelAttente;
    
    private JTable tableMariageAttente;
    private DefaultTableModel tableModelMariageAttente;

    private JTable tableDecesAttente; // NOUVEAU
    private DefaultTableModel tableModelDecesAttente; // NOUVEAU

    private JTabbedPane tabbedPane;
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

        tabbedPane.addTab("Naissances", createCategoryTab("Naissance"));
        tabbedPane.addTab("Mariages", createCategoryTab("Mariage"));
        tabbedPane.addTab("Décès", createCategoryTab("Décès")); // Ajouté dynamiquement

        // Écouteur pour actualiser automatiquement les données quand l'Officier change d'onglet
        tabbedPane.addChangeListener(e -> {
            if (controller != null) {
                controller.actualiserTableau();
            }
        });

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

        // --- TRAITEMENT DU BOUTON SIGNER SELON L'ONGLET ACTIF ---
        JButton btnSigner = new JButton("Signer l'acte");
        styleAnimateButton(btnSigner, COLOR_CUA_GREEN, COLOR_CUA_GREEN_HOVER);
        
        btnSigner.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            
            if (selectedTab == 0) { 
                // Onglet Naissance -> On délègue au contrôleur existant
                controller.gererSignature();
                
            } else if (selectedTab == 1) { 
                // Onglet Mariage -> Logique d'affichage du FormulaireValidationOfficier
                int row = tableMariageAttente.getSelectedRow();
                if (row != -1) {
                    int idMariage = (int) tableMariageAttente.getValueAt(row, 0);
                    
                    // Récupération de l'acte complet via le DAO
                    MariageDAO dao = new MariageDAO();
                    Mariage acteAValider = dao.findById(idMariage);
                    
                    if (acteAValider != null) {
                        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        FormulaireValidationOfficier form = new FormulaireValidationOfficier(topFrame, acteAValider);
                        form.setVisible(true);
                        
                        if (form.isActionEffectuee()) {
                            controller.actualiserTableau(); 
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez un acte de mariage à valider.");
                }
            } else if (selectedTab == 2) {
                // ========================================================
                // 🪦 NOUVEAU : Onglet Décès -> On délègue au contrôleur
                // ========================================================
                int row = tableDecesAttente.getSelectedRow();
                if (row != -1) {
                    controller.gererSignature(); // Appelle la logique mise à jour dans le contrôleur
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez un acte de décès à signer.");
                }
            }
        });
        
        sideBarJPanel.add(btnSigner);
        sideBarJPanel.add(Box.createVerticalGlue());
        add(sideBarJPanel, BorderLayout.EAST);
    }

    private JPanel createCategoryTab(String category) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] columnNames;
        DefaultTableModel model;

        if (category.equals("Mariage")) {
            columnNames = new String[]{"N° Acte", "Nom Époux", "Nom Épouse", "Témoin 1", "Témoin 2", "Date Mariage", "Lieu", "Régime Matrimonial", "Statut"};
            model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            this.tableMariageAttente = new JTable(model);
            styleTable(tableMariageAttente);
            this.tableModelMariageAttente = model;
            panel.add(new JScrollPane(tableMariageAttente), BorderLayout.CENTER);
            
        } else {
            columnNames = new String[]{"ID", "Citoyen", "Nom et Prénoms", "Date de la Demande", "Statut"};
            model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(model);
            styleTable(table);

            if (category.equals("Naissance")) {
                this.tableAttente = table;
                this.tableModelAttente = model;
            } else if (category.equals("Décès")) { // NOUVEAU
                this.tableDecesAttente = table;
                this.tableModelDecesAttente = model;
            }
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
        }

        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
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
        button.setBackground(normalColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(normalColor); }
        });
    }

    public void chargerDonneesMariage(List<Object[]> lignes) {
        if (tableModelMariageAttente != null) {
            tableModelMariageAttente.setRowCount(0);
            for(Object[] ligne : lignes) {
                if(ligne[8].toString().equalsIgnoreCase("SAISI")) {
                    tableModelMariageAttente.addRow(ligne);
                }
            }
        }
    }

    // NOUVEAU : Méthode de chargement dédiée pour l'onglet Décès
    public void chargerDonneesDeces(List<Object[]> lignes) {
        if (tableModelDecesAttente != null) {
            tableModelDecesAttente.setRowCount(0);
            for(Object[] ligne : lignes) {
                tableModelDecesAttente.addRow(ligne);
            }
        }
    }

    // --- GETTERS ---
    public JTable getTableAttente() { return tableAttente; }
    public JTable getTableMariageAttente() { return tableMariageAttente; }
    public JTable getTableDecesAttente() { return tableDecesAttente; } // NOUVEAU
    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public DefaultTableModel getTableModelAttente() { return tableModelAttente; }
    public DefaultTableModel getTableModelMariageAttente() { return tableModelMariageAttente; }
    public DefaultTableModel getTableModelDecesAttente() { return tableModelDecesAttente; } // NOUVEAU
}