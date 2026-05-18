package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import mg.controller.AgentController;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AgentDashboard extends JPanel {

    private static final Color COLOR_CUA_BLUE = new Color(0, 102, 204);
    private static final Color COLOR_CUA_BLUE_HOVER = new Color(0, 82, 164); 
    private static final Color COLOR_CUA_YELLOW = new Color(255, 204, 0);
    private static final Color COLOR_CUA_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_CUA_GREEN_HOVER = new Color(36, 164, 93);
    private static final Color COLOR_BG_PANEL = new Color(245, 246, 250); 
    private static final Color COLOR_TEXT_HEADER = Color.WHITE;

    private JTable tableNaissance;
    private JTable tableMariage; 
    private DefaultTableModel tableModelNaissance;
    private DefaultTableModel tableModelMariage;
    
    private JTabbedPane tabbedPane; 
    private AgentController controller;

    public AgentDashboard(AgentController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG_PANEL);

        // --- HEADER ---
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
        JLabel agentLabel = new JLabel("ESPACE AGENT - TABLEAU DE BORD");
        agentLabel.setFont(new Font("Arial", Font.ROMAN_BASELINE, 14));
        agentLabel.setForeground(COLOR_CUA_YELLOW);
        headerPanel.add(agentLabel);

        add(headerPanel, BorderLayout.NORTH);

        // --- ZONE CENTRALE (ONGLETS) ---
        JPanel centralPanel = new JPanel(new BorderLayout(5, 5));
        centralPanel.setBackground(COLOR_BG_PANEL);
        centralPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabbedPane.addTab("Naissances", createCategoryTab("Naissance"));
        tabbedPane.addTab("Mariages", createCategoryTab("Mariage"));
        tabbedPane.addTab("Décès", createCategoryTab("Décès"));

        centralPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centralPanel, BorderLayout.CENTER);

        // --- BARRE D'ACTIONS LATÉRALE (SIDEBAR) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_BG_PANEL);
        sidebarPanel.setBorder(new EmptyBorder(25, 10, 10, 15));

        JButton btnSaisir = new JButton("Saisir la copie");
        styleAnimatedButton(btnSaisir, COLOR_CUA_BLUE, COLOR_CUA_BLUE_HOVER);

        // Action dynamique selon l'onglet actif
        btnSaisir.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            
            if (selectedTab == 0) { // Onglet Naissance
                int row = tableNaissance.getSelectedRow();
                if (row != -1) {
                    int idDemande = (int) tableNaissance.getValueAt(row, 0);
                    String statut = tableNaissance.getValueAt(row, 4).toString();
                    controller.traiterAction(idDemande, statut);
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez une demande de naissance.");
                }
            } else if (selectedTab == 1) { // Onglet Mariage
                int row = tableMariage.getSelectedRow();
                if (row != -1) {
                    // 1. Récupération du numéro de l'acte sélectionné
                    int idMariage = (int) tableMariage.getValueAt(row, 0);
                    
                    // 2. Recherche du conteneur JFrame parent pour attacher la boîte JDialog
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    
                    // MODIFICATION ICI : On passe -1 pour ne pas déclencher le traitement automatique d'une demande
                    FormulaireMariage form = new FormulaireMariage(topFrame, -1, 0, 0);
                    
                    // Remplissage automatique du champ de recherche de l'acte
                    form.getTxtIdMariage().setText(String.valueOf(idMariage));
                    
                    form.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez un acte de mariage dans le tableau.");
                }
            }
        });

        sidebarPanel.add(btnSaisir);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnRemettre = new JButton("Remettre au Citoyen");
        styleAnimatedButton(btnRemettre, COLOR_CUA_GREEN, COLOR_CUA_GREEN_HOVER);
        sidebarPanel.add(btnRemettre);

        sidebarPanel.add(Box.createVerticalGlue());
        add(sidebarPanel, BorderLayout.EAST);
    }

    private JPanel createCategoryTab(String category) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columnNames;
        DefaultTableModel model;

        if (category.equals("Mariage")) {
            columnNames = new String[]{"N° Acte", "Nom Époux", "Nom Épouse", "Témoin 1", "Témoin 2", "Date Mariage", "Lieu", "Régime Matrimonial"};
            model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            this.tableModelMariage = model;
            this.tableMariage = new JTable(model);
            styleTable(tableMariage);
            panel.add(new JScrollPane(tableMariage), BorderLayout.CENTER);
        } else {
            columnNames = new String[]{"ID", "Citoyen", "Nom et Prénoms", "Date de la demande", "Statut"};
            model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            
            JTable table = new JTable(model);
            styleTable(table);

            if (category.equals("Naissance")) {
                this.tableModelNaissance = model;
                this.tableNaissance = table;
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
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    private void styleAnimatedButton(JButton button, Color baseColor, Color hoverColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 0),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(baseColor); }
        });
    }

    public void chargerDonnees(List<Object[]> lignes) {
        tableModelNaissance.setRowCount(0);
        for(Object[] ligne : lignes) {
            tableModelNaissance.addRow(ligne);
        }
    }

    public void chargerDonneesMariage(List<Object[]> lignes) {
        if (tableModelMariage != null) {
            tableModelMariage.setRowCount(0);
            for(Object[] ligne : lignes) {
                tableModelMariage.addRow(ligne);
            }
        }
    }

    public JTable getTableNaissance() { return tableNaissance; }
    public JTable getTableMariage() { return tableMariage; }
}