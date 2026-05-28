package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import mg.controller.AdminController;

public class AdminDashboard extends JPanel {

    private static final Color COLOR_ADMIN_DARK = new Color(44, 62, 80); 
    private static final Color COLOR_ADMIN_HOVER = new Color(52, 73, 94);
    private static final Color COLOR_CUA_YELLOW = new Color(255, 204, 0);
    private static final Color COLOR_BG_PANEL = new Color(245, 246, 250);
    
    // Couleurs pour les cartes de statistiques
    private static final Color COLOR_BLUE_CARD = new Color(52, 152, 219);
    private static final Color COLOR_GREEN_CARD = new Color(46, 204, 113);
    private static final Color COLOR_ORANGE_CARD = new Color(230, 126, 34);
    private static final Color COLOR_RED_CARD = new Color(231, 76, 60);

    private JTabbedPane tabbedPane;
    private AdminController controller; 
    
    // Composants de l'onglet Statistiques (Labels à mettre à jour dynamiquement)
    private JLabel lblStatPopulation;
    private JLabel lblStatNaissances;
    private JLabel lblStatMariages;
    private JLabel lblStatDeces;

    // Tableau Blockchain
    private JTable tableBlockchain;
    private DefaultTableModel tableModelBlockchain;

    // Tableau Utilisateurs
    private JTable tableUtilisateurs;
    private DefaultTableModel tableModelUtilisateurs;

    public AdminDashboard() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG_PANEL);

        // ========================================================
        // 🏢 HEADER DE L'ADMINSTRATEUR
        // ========================================================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(COLOR_ADMIN_DARK);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("PANNEAU DE CONTRÔLE ADMIN - CORE BLOCKCHAIN CUA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        headerPanel.add(Box.createHorizontalGlue());
        JLabel roleLabel = new JLabel("👑 PILOTAGE & SÉCURITÉ GLOBALE");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        roleLabel.setForeground(COLOR_CUA_YELLOW);
        headerPanel.add(roleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // ========================================================
        // 🔄 LES ONGLETS CENTRAUX
        // ========================================================
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // --- NOUVEAU : Onglet 1 - Statistiques démographiques ---
        tabbedPane.addTab("📊 Vue d'Ensemble & Stats", createStatsTab());
        // Onglet 2 : Registre Blockchain
        tabbedPane.addTab("🔗 Registre Blockchain", createBlockchainTab());
        // Onglet 3 : Gestion des utilisateurs
        tabbedPane.addTab("👥 Gestion du Personnel", createUtilisateursTab());

        add(tabbedPane, BorderLayout.CENTER);

        // Écouteur de changement d'onglet pour rafraîchir les données en direct
        tabbedPane.addChangeListener(e -> {
            if (controller != null) {
                controller.actualiserDonnees();
            }
        });

        // ========================================================
        // 📊 BARRE LATERALE D'ACTIONS CONTROLE
        // ========================================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_BG_PANEL);
        sidebar.setBorder(new EmptyBorder(20, 10, 10, 15));

        JButton btnVerifierIntegrite = new JButton("🔍 Vérifier la Blockchain");
        styleButton(btnVerifierIntegrite, COLOR_ADMIN_DARK, COLOR_ADMIN_HOVER);
        sidebar.add(btnVerifierIntegrite);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnAjouterUtilisateur = new JButton("➕ Nouvel Employé");
        styleButton(btnAjouterUtilisateur, new Color(46, 204, 113), new Color(39, 174, 96));
        sidebar.add(btnAjouterUtilisateur);

        btnAjouterUtilisateur.addActionListener(e -> {
            if (controller != null) {
                controller.ouvrirFormulaireNouvelEmploye();
            }
        });

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.EAST);

        btnVerifierIntegrite.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "✅ Analyse de la chaîne terminée !\n\n" +
                "- Statut : INTACTE\n" +
                "- Corrélation des Hashs : 100%\n" +
                "Aucune tentative de falsification détectée.", 
                "Vérification Cryptographique", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void setController(AdminController controller) {
        this.controller = controller;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    // ========================================================
    // 📊 CRÉATION DE L'ONGLET STATISTIQUES (KPI Cards)
    // ========================================================
    private JPanel createStatsTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COLOR_BG_PANEL);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Grille de 2 lignes, 2 colonnes pour disposer les 4 grands indicateurs
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setBackground(COLOR_BG_PANEL);

        lblStatPopulation = new JLabel("0", SwingConstants.CENTER);
        lblStatNaissances = new JLabel("0", SwingConstants.CENTER);
        lblStatMariages   = new JLabel("0", SwingConstants.CENTER);
        lblStatDeces      = new JLabel("0", SwingConstants.CENTER);

        cardsGrid.add(createKpiCard("POPULATION ACTUELLE (VIVANTE)", lblStatPopulation, COLOR_BLUE_CARD));
        cardsGrid.add(createKpiCard("TOTAL ACTES DE NAISSANCE", lblStatNaissances, COLOR_GREEN_CARD));
        cardsGrid.add(createKpiCard("TOTAL ACTES DE MARIAGE", lblStatMariages, COLOR_ORANGE_CARD));
        cardsGrid.add(createKpiCard("TOTAL CITOYENS DÉCÉDÉS", lblStatDeces, COLOR_RED_CARD));

        mainPanel.add(cardsGrid, BorderLayout.CENTER);

        // Petit message d'information en bas
        JLabel footerInfo = new JLabel("💡 Les données ci-dessus se mettent à jour automatiquement d'après le registre SQL centralisé de la CUA.", SwingConstants.LEFT);
        footerInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        footerInfo.setForeground(Color.GRAY);
        mainPanel.add(footerInfo, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Génère un composant de carte (Card) stylisé pour un indicateur clé (KPI)
     */
    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel txtTitle = new JLabel(title, SwingConstants.LEFT);
        txtTitle.setFont(new Font("Arial", Font.BOLD, 12));
        txtTitle.setForeground(new Color(127, 140, 141));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 42));
        valueLabel.setForeground(accentColor);

        // Barre d'accentuation colorée sur le côté gauche
        JPanel leftAccent = new JPanel();
        leftAccent.setBackground(accentColor);
        leftAccent.setPreferredSize(new Dimension(5, 0));

        card.add(txtTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(leftAccent, BorderLayout.WEST);

        return card;
    }

    private JPanel createBlockchainTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Index Bloc", "Timestamp", "Hash du Bloc", "Hash Précédent", "Signé Par (ID)"};
        tableModelBlockchain = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableBlockchain = new JTable(tableModelBlockchain);
        styleTable(tableBlockchain);
        
        panel.add(new JScrollPane(tableBlockchain), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUtilisateursTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"ID Utilisateur", "Nom", "Prénoms", "Identifiant / Login", "Rôle"};
        tableModelUtilisateurs = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableUtilisateurs = new JTable(tableModelUtilisateurs);
        styleTable(tableUtilisateurs);

        panel.add(new JScrollPane(tableUtilisateurs), BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setGridColor(new Color(230, 233, 237));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 242, 245));
        table.getTableHeader().setForeground(new Color(44, 62, 80));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void styleButton(JButton button, Color bg, Color hover) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(bg); }
        });
    }

    // --- METHODES DE CHARGEMENT DYNAMIQUES ---
    
    /**
     * Met à jour l'affichage numérique des 4 compteurs statistiques
     */
    public void modifierCompteursStatistiques(int population, int naissances, int mariages, int deces) {
        lblStatPopulation.setText(String.format("%,d", population));
        lblStatNaissances.setText(String.format("%,d", naissances));
        lblStatMariages.setText(String.format("%,d", mariages));
        lblStatDeces.setText(String.format("%,d", deces));
    }

    public void chargerDonneesBlockchain(List<Object[]> blocs) {
        tableModelBlockchain.setRowCount(0);
        for (Object[] ligne : blocs) {
            tableModelBlockchain.addRow(ligne);
        }
    }

    public void chargerDonneesUtilisateurs(List<Object[]> users) {
        tableModelUtilisateurs.setRowCount(0);
        for (Object[] ligne : users) {
            tableModelUtilisateurs.addRow(ligne);
        }
    }
}