package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.security.MessageDigest;
import mg.model.Mariage;
import mg.DAO.MariageDAO;

public class FormulaireValidationOfficier extends JDialog {

    // Réutilisation des champs (Bloqués en lecture seule)
    private JTextField txtIdMariage = new JTextField(12);
    private JTextField txtNomEpoux = new JTextField();
    private JTextField txtNomEpouse = new JTextField();
    private JTextField txtNomTemoin1 = new JTextField();
    private JTextField txtNomTemoin2 = new JTextField();
    private JTextField txtLieu = new JTextField();
    private JTextField txtRegime = new JTextField();
    private JTextField txtDate = new JTextField();

    private JLabel lblStatut = new JLabel(" Statut : SAISI ");
    
    // Boutons spécifiques à l'Officier
    private JButton btnSigner = new JButton("✍️ Approuver & Signer l'Acte");
    private JButton btnRejeter = new JButton("❌ Rejeter (Erreur de saisie)");
    private JButton btnAnnuler = new JButton("Fermer");

    private int idMariageCible;
    private boolean actionEffectuee = false;

    public FormulaireValidationOfficier(JFrame parent, Mariage m) {
        super(parent, "Espace Officier - Validation et Signature Numérique", true);
        this.idMariageCible = m.getIdMariage();

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        // --- EN-TÊTE STATUT ---
        JPanel pHeader = new JPanel(new BorderLayout());
        pHeader.setBackground(new Color(245, 245, 245));
        pHeader.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel lblTitre = new JLabel("Examen de l'Acte de Mariage N° : " + idMariageCible);
        lblTitre.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblStatut.setFont(new Font("Arial", Font.BOLD, 13));
        lblStatut.setOpaque(true);
        lblStatut.setBackground(new Color(255, 193, 7)); // Orange pour SAISI
        lblStatut.setForeground(Color.BLACK);
        lblStatut.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        pHeader.add(lblTitre, BorderLayout.WEST);
        pHeader.add(lblStatut, BorderLayout.EAST);
        add(pHeader, BorderLayout.NORTH);

        // --- CORPS DU DOSSIER (LECTURE SEULE) ---
        JPanel pCentral = new JPanel(new GridLayout(2, 1, 0, 15));
        pCentral.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Ligne Civils
        JPanel pCivils = new JPanel(new GridLayout(1, 2, 20, 0));
        
        JPanel pEpoux = createSection("L'ÉPOUX", new Color(245, 245, 245));
        addField(pEpoux, "Nom et Prénoms :", txtNomEpoux);
        txtNomEpoux.setText(m.getNomCompletEpoux());
        pCivils.add(pEpoux);

        JPanel pEpouse = createSection("L'ÉPOUSE", new Color(245, 245, 245));
        addField(pEpouse, "Nom et Prénoms :", txtNomEpouse);
        txtNomEpouse.setText(m.getNomCompletEpouse());
        pCivils.add(pEpouse);
        
        pCentral.add(pCivils);

        // Ligne Témoins & Détails
        JPanel pDetails = new JPanel(new GridLayout(1, 2, 20, 0));
        
        JPanel pTemoins = createSection("TÉMOINS", new Color(245, 245, 245));
        addField(pTemoins, "Témoin 1 :", txtNomTemoin1);
        addField(pTemoins, "Témoin 2 :", txtNomTemoin2);
        txtNomTemoin1.setText(m.getNomCompletTemoin1());
        txtNomTemoin2.setText(m.getNomCompletTemoin2());
        pDetails.add(pTemoins);

        JPanel pUnion = createSection("DÉTAILS DE L'UNION", new Color(245, 245, 245));
        addField(pUnion, "Date du Mariage :", txtDate);
        addField(pUnion, "Lieu de Célébration :", txtLieu);
        addField(pUnion, "Régime Matrimonial :", txtRegime);
        txtDate.setText(m.getDateMariage().toString());
        txtLieu.setText(m.getLieuMariage());
        txtRegime.setText(m.getRegimeMatrimonial());
        pDetails.add(pUnion);

        pCentral.add(pDetails);
        add(pCentral, BorderLayout.CENTER);

        // --- BARRE DE BOUTONS ---
        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        
        btnSigner.setBackground(new Color(33, 150, 243)); // Bleu Validation
        btnSigner.setForeground(Color.WHITE);
        btnSigner.setFont(new Font("Arial", Font.BOLD, 13));

        btnRejeter.setBackground(new Color(231, 76, 60)); // Rouge Rejet
        btnRejeter.setForeground(Color.WHITE);

        pButtons.add(btnAnnuler);
        pButtons.add(btnRejeter);
        pButtons.add(btnSigner);
        add(pButtons, BorderLayout.SOUTH);

        // Événements
        btnSigner.addActionListener(e -> executerSignatureBlockchain(m));
        btnRejeter.addActionListener(e -> executerRejet());
        btnAnnuler.addActionListener(e -> dispose());
    }


    private void executerSignatureBlockchain(Mariage m) {
        int choix = JOptionPane.showConfirmDialog(this, 
            "Confirmez-vous la validité de cette union pour l'ancrage Blockchain ?",
            "Signature de l'Officier", JOptionPane.YES_NO_OPTION);

        int idOfficierConnecte = 1; // À remplacer par l'ID réel de l'officier connecté (ex: session.getIdOfficier())
        if (choix == JOptionPane.YES_OPTION) {
            // 1. Calcul du Hash du bloc (Contenu de l'acte)
            String donneesActes = m.getIdMariage() + m.getNomCompletEpoux() + m.getNomCompletEpouse() + m.getDateMariage();
            String hashActuel = genererSHA256(donneesActes);
            
            MariageDAO dao = new MariageDAO();
            
            // ACTION 1 : Écriture dans la table 'bloc_blockchain'
            boolean blockchainOk = dao.ancrerNouveauBloc(hashActuel, idOfficierConnecte);
            boolean updateMariageOk = false;

            if (blockchainOk) {
                // ACTION 2 : Mise à jour de la table 'mariage' avec le hash Blockchain
                updateMariageOk = dao.updateStatutEtHash(m.getIdMariage(), "SIGNE", hashActuel);
            }

            if (blockchainOk && updateMariageOk) {
                JOptionPane.showMessageDialog(this, 
                    "✔️ Acte signé et ancré avec succès dans la Blockchain !\n\n" +
                    "⛓️ Bloc N° " + (m.getIdMariage() + 1) + " créé.\n" +
                    "Hash : " + hashActuel, 
                    "Ancrage Réussi", JOptionPane.INFORMATION_MESSAGE);
                
                actionEffectuee = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ancrage Blockchain ou de l'écriture du hash dans la table mariage.", "Erreur technique", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void executerRejet() {
        String motif = JOptionPane.showInputDialog(this, "Spécifiez le motif du rejet (renvoyé à l'agent) :");
        if (motif != null && !motif.trim().isEmpty()) {
            
            // Logique de renvoi : On repasse le statut à NOUVEAU pour que l'agent puisse corriger
            // dao.updateStatut(idMariageCible, "NOUVEAU");
            
            JOptionPane.showMessageDialog(this, "Dossier renvoyé à l'agent de saisie pour correction.");
            actionEffectuee = true;
            dispose();
        }
    }

    private String genererSHA256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception ex) {
            return "ERROR_HASH";
        }
    }

    private JPanel createSection(String title, Color bg) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 11)));
        return p;
    }

    private void addField(JPanel p, String label, JTextField tf) {
        tf.setEditable(false);
        tf.setBackground(Color.WHITE);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        p.add(new JLabel(label));
        p.add(tf);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public boolean isActionEffectuee() { return actionEffectuee; }
}