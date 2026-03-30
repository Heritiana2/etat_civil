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
    private AgentController controller;

    public AgentDashboard(AgentController controller) {

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
        JLabel agentLabel = new JLabel("ESPACE AGENT - TABLEAU DE BORD");
        agentLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        agentLabel.setForeground(COLOR_CUA_YELLOW);
        headerPanel.add(agentLabel);

        add(headerPanel, BorderLayout.NORTH);


        JPanel centralPanel = new JPanel(new BorderLayout(5, 5));
        centralPanel.setBackground(COLOR_BG_PANEL);
        centralPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabbedPane.addTab("Naissances", createCategoryTab("Naissance"));
        tabbedPane.addTab("Mariages", createCategoryTab("Mariage"));
        tabbedPane.addTab("Décès", createCategoryTab("Décès"));

        centralPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centralPanel, BorderLayout.CENTER);

        // 3. LA BARRE D'ACTIONS LATÉRALE (SIDEBAR)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_BG_PANEL);
        sidebarPanel.setBorder(new EmptyBorder(25, 10, 10, 15)); // Alignement avec le tableau

        JButton btnSaisir = new JButton("Saisir la copie");
        styleAnimatedButton(btnSaisir, COLOR_CUA_BLUE, COLOR_CUA_BLUE_HOVER);

        // --- ACTION DU BOUTON SAISIR ---

        btnSaisir.addActionListener(e -> {
            int row = tableNaissance.getSelectedRow();
            if (row != -1) {
                // On récupère l'ID (colonne 0) et le Statut (colonne 3)
                int idDemande = (int) tableNaissance.getValueAt(row, 0);
                String statut = (String) tableNaissance.getValueAt(row, 3);
                
                // On envoie l'ordre au controller
                controller.traiterAction(idDemande, statut);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une demande dans le tableau.");
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

    /**
     * Crée un panneau avec un JTable stylisé pour un onglet spécifique.
     * @param category La catégorie (Naissance, Mariage, Décès)
     * @return Le JPanel contenant le tableau.
     */
    private DefaultTableModel tableModelNaissance;

    private JPanel createCategoryTab(String category) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel catLabel = new JLabel("Liste des demandes : " + category);
        catLabel.setFont(new Font("Arial", Font.BOLD, 16));
        catLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(catLabel, BorderLayout.NORTH);

        // Titre de la catégorie (Optionnel, tes onglets suffisent déjà)
        String[] columnNames = {"ID", "Citoyen", "Date de Demande", "Statut"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        if (category.equals("Naissance")) {
            this.tableModelNaissance = model;
            this.tableNaissance = table;
        } 
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230))); // Bordure très claire

        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(table), BorderLayout.CENTER);
        }};
    }

    /**
     * Applique un style moderne Flat Design à un JTable.
     * @param table Le JTable à styliser.
     */
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

    /**
     * Applique un style Flat Design et des animations de survol (hover) à un bouton.
     * @param button Le JButton à styliser.
     * @param baseColor La couleur de fond par défaut.
     * @param hoverColor La couleur de fond lors du survol.
     */
    private void styleAnimatedButton(JButton button, Color baseColor, Color hoverColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer dans la Sidebar BoxLayout
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Largeur max de la sidebar
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false); // Enlever le rectangle de focus
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Curseur main
        button.setOpaque(true); // Nécessaire pour les boutons Swing
        
        // Bordure invisible pour le style "Flat"
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 0), // Pas de bordure externe
            BorderFactory.createEmptyBorder(5, 15, 5, 15) // Padding interne
        ));

        // Animation de survol (Hover)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor); // Changement de couleur au survol
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor); // Retour à la couleur de base
            }
        });
    }

    public void chargerDonnees(List<Object[]> lignes) {
        tableModelNaissance.setRowCount(0);
        for(Object[] ligne : lignes) {
            tableModelNaissance.addRow(ligne);
        }
    }

}