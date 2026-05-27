package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;
import mg.DAO.NaissanceDAO;
import mg.util.SecurityUtils;

public class VerificateurActeFrame extends JFrame {

    private JTextField txtIdDemande;
    private JTextArea txtVisualisationDonnees;
    private JLabel lblStatusResultat;
    private JLabel lblHashStocke;
    private JLabel lblHashRecalcule;
    private JPanel panelResultat;

    public VerificateurActeFrame() {
        setTitle("VÉRIFICATEUR D'ACTES CIVIL - ESPACE PUBLIC COMPTABLE");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(245, 246, 250));

        // --- EN-TÊTE ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80)); // Couleur sombre "Sécurité"
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("PORTAIL PUBLIC DE VÉRIFICATION BLOCKCHAIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- ZONE DE RECHERCHE ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        centerPanel.setBackground(new Color(245, 246, 250));

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBarPanel.setBackground(Color.WHITE);
        searchBarPanel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        searchBarPanel.add(new JLabel("Entrez l'identifiant de l'acte (N° Demande) : "));
        txtIdDemande = new JTextField(12);
        txtIdDemande.setFont(new Font("Arial", Font.BOLD, 14));
        searchBarPanel.add(txtIdDemande);

        JButton btnVerifier = new JButton("Vérifier l'authenticité");
        btnVerifier.setBackground(new Color(0, 102, 204));
        btnVerifier.setForeground(Color.WHITE);
        btnVerifier.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerifier.setFocusPainted(false);
        searchBarPanel.add(btnVerifier);
        
        centerPanel.add(searchBarPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- ZONE DE VISUALISATION DES DONNÉES EN DIRECT ---
        JLabel lblSectionDonnees = new JLabel("Données actuelles de la base de données (Table Naissance) :");
        lblSectionDonnees.setFont(new Font("Arial", Font.BOLD, 12));
        centerPanel.add(lblSectionDonnees);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtVisualisationDonnees = new JTextArea(6, 40);
        txtVisualisationDonnees.setEditable(false);
        txtVisualisationDonnees.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtVisualisationDonnees.setBackground(new Color(236, 240, 241));
        txtVisualisationDonnees.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        centerPanel.add(new JScrollPane(txtVisualisationDonnees));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- ZONE DE RÉSULTAT ET COMPARAISON DES HASH ---
        panelResultat = new JPanel();
        panelResultat.setLayout(new BoxLayout(panelResultat, BoxLayout.Y_AXIS));
        panelResultat.setBackground(Color.WHITE);
        panelResultat.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        lblStatusResultat = new JLabel("Veuillez lancer une vérification.");
        lblStatusResultat.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatusResultat.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelResultat.add(lblStatusResultat);
        panelResultat.add(Box.createRigidArea(new Dimension(0, 10)));

        lblHashStocke = new JLabel("Empreinte Blockchain d'origine : —");
        lblHashStocke.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblHashStocke.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelResultat.add(lblHashStocke);

        lblHashRecalcule = new JLabel("Empreinte recalculée en direct : —");
        lblHashRecalcule.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblHashRecalcule.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelResultat.add(lblHashRecalcule);

        centerPanel.add(panelResultat);
        add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // --- ACTION DE VÉRIFICATION EN DIRECT -----
        // ==========================================
        btnVerifier.addActionListener(e -> {
            String inputId = txtIdDemande.getText().trim();
            if (inputId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir un ID valide.");
                return;
            }

            try {
                int idDemande = Integer.parseInt(inputId);
                NaissanceDAO dao = new NaissanceDAO();
                
                // 1. Récupération des données textuelles actuelles
                Map<String, String> donnees = dao.getDonneesCompletesPourPDF(idDemande);
                
                if (donnees == null) {
                    txtVisualisationDonnees.setText("Aucun acte trouvé pour l'ID " + idDemande);
                    lblStatusResultat.setText("❌ INTROUVABLE : Cet acte n'existe pas dans le registre.");
                    lblStatusResultat.setForeground(Color.RED);
                    lblHashStocke.setText("");
                    lblHashRecalcule.setText("");
                    return;
                }

                // Affichage lisible des données récupérées pour l'utilisateur
                txtVisualisationDonnees.setText(
                        "--- DONNÉES DE L'ACTE N° " + idDemande + " ---\n" +
                        "Nom de l'enfant : " + donnees.get("nom_enfant") + "\n" +
                        "Prénoms          : " + donnees.get("prenoms_enfant") + "\n" +
                        "Né(e) le         : " + donnees.get("date_naissance") + "\n" +
                        "À                : " + donnees.get("lieu_naissance") + "\n" +
                        "Père             : " + donnees.get("nom_pere") + "\n" +
                        "Mère             : " + donnees.get("nom_mere") + "\n" +
                        "Statut de l'acte : " + donnees.get("statut")
                );

                // 2. Récupération du hash scellé à l'époque par l'officier (table demande)
                String hashStocke = donnees.get("hash_blockchain");
                
                // 3. Recalcul instantané du hash avec l'état ACTUEL des tables SQL
                String hashRecalcule = SecurityUtils.calculerHashNaissance(
                        donnees.get("nom_enfant"),
                        donnees.get("prenoms_enfant"),
                        donnees.get("date_naissance"),
                        donnees.get("lieu_naissance"),
                        donnees.get("nom_pere"),
                        donnees.get("nom_mere")
                );

                lblHashStocke.setText("      [Blockchain] : " + hashStocke);
                lblHashRecalcule.setText("      [En Direct]  : " + hashRecalcule);

                // 4. LE MOMENT DE VÉRITÉ : Comparaison
                if (hashRecalcule != null && hashRecalcule.equalsIgnoreCase(hashStocke)) {
                    lblStatusResultat.setText("✅ ACTE SECURISE & AUTHENTIQUE : L'empreinte concorde à 100%.");
                    lblStatusResultat.setForeground(new Color(46, 204, 113)); // Vert
                    panelResultat.setBorder(new LineBorder(new Color(46, 204, 113), 2, true));
                } else {
                    lblStatusResultat.setText("❌ ALERTE FALSIFICATION : Les données SQL ont été altérées ou modifiées !");
                    lblStatusResultat.setForeground(Color.RED); // Rouge alerte
                    panelResultat.setBorder(new LineBorder(Color.RED, 2, true));
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "L'identifiant doit être un nombre entier.");
            }
        });
    }

    // Un main rapide si tu veux exécuter cet onglet séparément pour faire une démonstration !
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VerificateurActeFrame().setVisible(true);
        });
    }
}