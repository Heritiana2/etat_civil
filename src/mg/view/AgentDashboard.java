package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import mg.controller.AgentController;
import mg.DAO.MariageDAO;
import mg.DAO.NaissanceDAO; 
import mg.DAO.DecesDAO; // NOUVEAU
import mg.util.PDFGenerationService;
import mg.DAO.DemandeDAO;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

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
    private JTable tableDeces; // NOUVEAU
    
    private DefaultTableModel tableModelNaissance;
    private DefaultTableModel tableModelMariage;
    private DefaultTableModel tableModelDeces; // NOUVEAU
    
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
        tabbedPane.addTab("Décès", createCategoryTab("Décès")); // Modifié via createCategoryTab

        centralPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centralPanel, BorderLayout.CENTER);

        // --- BARRE D'ACTIONS LATÉRALE (SIDEBAR) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_BG_PANEL);
        sidebarPanel.setBorder(new EmptyBorder(25, 10, 10, 15));

        // BOUTON 1 : Saisir l'acte
        JButton btnSaisir = new JButton("Saisir la copie");
        styleAnimatedButton(btnSaisir, COLOR_CUA_BLUE, COLOR_CUA_BLUE_HOVER);

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
                    int idMariage = (int) tableMariage.getValueAt(row, 0);
                    String statutActe = tableMariage.getValueAt(row, 8).toString().toUpperCase().trim();
                    
                    if (statutActe.equals("SAISI") || statutActe.equals("SIGNE") || statutActe.equals("LIVRE") || statutActe.equals("DELIVRE")) {
                        JOptionPane.showMessageDialog(this, 
                            "Action Impossible : Cet acte est au statut [" + statutActe + "].\n" +
                            "Il est verrouillé et ne peut plus être modifié par un agent de saisie.", 
                            "Workflow Sécurisé", JOptionPane.WARNING_MESSAGE);
                        return; 
                    }
                    
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    FormulaireMariage form = new FormulaireMariage(topFrame, -1, 0, 0);
                    form.getTxtIdMariage().setText(String.valueOf(idMariage));
                    form.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez un acte de mariage dans le tableau.");
                }
            } else if (selectedTab == 2) { // NOUVEAU : Onglet Décès
                int row = tableDeces.getSelectedRow();
                if (row != -1) {
                    int idDemande = (int) tableDeces.getValueAt(row, 0);
                    String statut = tableDeces.getValueAt(row, 4).toString();
                    
                    controller.traiterAction(idDemande, statut, "Décès");
                } else {
                    JOptionPane.showMessageDialog(this, "Sélectionnez une demande de décès.");
                }
            }
        });

        sidebarPanel.add(btnSaisir);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // BOUTON 2 : Générer PDF, QR Code et Délivrer l'acte au citoyen
        JButton btnRemettre = new JButton("Imprimer & Délivrer");
        styleAnimatedButton(btnRemettre, COLOR_CUA_GREEN, COLOR_CUA_GREEN_HOVER);
        
        btnRemettre.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            
            if (selectedTab == 0) { 
                // --- TRAITEMENT ONGLET NAISSANCE ---------
                int row = tableNaissance.getSelectedRow();
                if (row != -1) {
                    int idDemande = (int) tableNaissance.getValueAt(row, 0);
                    NaissanceDAO dao = new NaissanceDAO();
                    DemandeDAO deDAO = new DemandeDAO();
                    
                    Map<String, String> donneesActe = dao.getDonneesCompletesPourPDF(idDemande);
                    if (donneesActe != null) {
                        String statutReel = donneesActe.get("statut");
                        if (statutReel == null) statutReel = "NON_DEFINI";
                        statutReel = statutReel.toUpperCase().trim();
                        
                        if (!statutReel.equals("SIGNE") && !statutReel.equals("SIGNÉ") && !statutReel.equals("DELIVRE")) {
                            JOptionPane.showMessageDialog(this, 
                                "Impression impossible : Cet acte n'a pas encore été validé et signé par l'Officier.\n(Statut actuel en BDD : " + statutReel + ")", 
                                "Sécurité d'Impression", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Enregistrer l'Acte de Naissance Officiel");
                        fileChooser.setSelectedFile(new File("Acte_Naissance_No_" + idDemande + ".pdf"));
                        
                        int userSelection = fileChooser.showSaveDialog(this);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
                            PDFGenerationService.genererActeNaissancePDF(donneesActe, cheminFichier);
                            
                            try {
                                int idDemandeAssociee = Integer.parseInt(donneesActe.get("id_demande"));
                                deDAO.mettreAJourStatut(idDemandeAssociee, "Délivré");
                            } catch (Exception ex) {
                                deDAO.mettreAJourStatut(idDemande, "Délivré");
                            }
                            
                            JOptionPane.showMessageDialog(this, 
                                "📄 Copie d'acte de naissance générée au format PDF avec succès !\n" +
                                "🔒 Le document inclut le QR Code de scellement de la Blockchain.", 
                                "Délivrance Réussie", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de l'acte.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un acte de naissance dans le tableau.");
                }
                
            } else if (selectedTab == 1) { 
                // --- TRAITEMENT ONGLET MARIAGE -----------
                int row = tableMariage.getSelectedRow();
                if (row != -1) {
                    String statut = tableMariage.getValueAt(row, 8).toString().toUpperCase().trim();
                    
                    if (!statut.equals("SIGNE") && !statut.equals("LIVRE") && !statut.equals("DELIVRE") && !statut.equals("SIGNÉ") && !statut.equals("DÉLIVRÉ")) {
                        JOptionPane.showMessageDialog(this, 
                            "Impression impossible : Cet acte n'a pas encore été validé et signé par l'Officier.\n(Statut actuel : " + statut + ")", 
                            "Sécurité d'Impression", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    int idMariage = (int) tableMariage.getValueAt(row, 0);
                    MariageDAO dao = new MariageDAO();
                    
                    Map<String, String> donneesActe = dao.getDonneesCompletesPourPDF(idMariage);
                    if (donneesActe != null) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Enregistrer l'Acte de Mariage Officiel");
                        fileChooser.setSelectedFile(new File("Acte_Mariage_No_" + idMariage + ".pdf"));
                        
                        int userSelection = fileChooser.showSaveDialog(this);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
                            PDFGenerationService.genererActeMariagePDF(donneesActe, cheminFichier);
                            dao.updateStatut(idMariage, "LIVRE");
                            
                            JOptionPane.showMessageDialog(this, 
                                "📄 Copie d'acte de mariage générée au format PDF avec succès !\n" +
                                "🔒 Le document inclut le QR Code de scellement de la Blockchain.", 
                                "Délivrance Réussie", JOptionPane.INFORMATION_MESSAGE);    
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de l'acte.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un acte de mariage dans le tableau.");
                }
            } else if (selectedTab == 2) {
                // ==========================================
                // --- NOUVEAU : TRAITEMENT ONGLET DÉCÈS ----
                // ==========================================
                int row = tableDeces.getSelectedRow();
                if (row != -1) {
                    int idDemande = (int) tableDeces.getValueAt(row, 0);
                    String statut = tableDeces.getValueAt(row, 4).toString().toUpperCase().trim();
                    
                    if (!statut.equals("SIGNE") && !statut.equals("SIGNÉ") && !statut.equals("DÉLIVRÉ") && !statut.equals("DELIVRE")) {
                        JOptionPane.showMessageDialog(this, 
                            "Impression impossible : Cet acte n'a pas encore été validé et signé par l'Officier.\n(Statut actuel : " + statut + ")", 
                            "Sécurité d'Impression", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    DecesDAO dao = new DecesDAO();
                    DemandeDAO deDAO = new DemandeDAO();
                    Map<String, String> donneesActe = dao.getDonneesDefuntPourBlockchain(idDemande); // Méthode de récupération
                    
                    if (donneesActe != null) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Enregistrer l'Acte de Décès Officiel");
                        fileChooser.setSelectedFile(new File("Acte_Deces_No_" + idDemande + ".pdf"));
                        
                        int userSelection = fileChooser.showSaveDialog(this);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            String cheminFichier = fileChooser.getSelectedFile().getAbsolutePath();
                            
                            // Appel de ton service d'impression (il faudra ajouter cette méthode dans ton PDFGenerationService)
                            PDFGenerationService.genererActeDecesPDF(donneesActe, cheminFichier);
                            deDAO.mettreAJourStatut(idDemande, "Délivré");
                            
                            JOptionPane.showMessageDialog(this, 
                                "📄 Copie d'acte de décès générée au format PDF avec succès !\n" +
                                "🔒 Le document inclut le QR Code de scellement de la Blockchain.", 
                                "Délivrance Réussie", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données de l'acte.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un acte de décès dans le tableau.");
                }
            }
        });

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
            columnNames = new String[]{"N° Acte", "Nom Époux", "Nom Épouse", "Témoin 1", "Témoin 2", "Date Mariage", "Lieu", "Régime Matrimonial", "Statut"};
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
            } else if (category.equals("Décès")) { // NOUVEAU
                this.tableModelDeces = model;
                this.tableDeces = table;
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

    // NOUVEAU
    public void chargerDonneesDeces(List<Object[]> lignes) {
        if (tableModelDeces != null) {
            tableModelDeces.setRowCount(0);
            for(Object[] ligne : lignes) {
                tableModelDeces.addRow(ligne);
            }
        }
    }

    public JTable getTableNaissance() { return tableNaissance; }
    public JTable getTableMariage() { return tableMariage; }
    public JTable getTableDeces() { return tableDeces; } // NOUVEAU
}