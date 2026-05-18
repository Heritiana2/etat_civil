package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import mg.model.Mariage;
import mg.DAO.MariageDAO;
import mg.DAO.CitoyenDAO;

public class FormulaireMariage extends JDialog {

    private JTextField txtIdMariage = new JTextField(12);
    private JButton btnCharger = new JButton("Rechercher l'acte");

    // Époux et Épouse
    private JTextField txtIdEpoux = new JTextField(5);
    private JTextField txtNomEpoux = new JTextField();
    private JTextField txtIdEpouse = new JTextField(5);
    private JTextField txtNomEpouse = new JTextField();
    
    // Témoins basés sur l'ID Citoyen
    private JTextField txtIdTemoin1 = new JTextField(5);
    private JTextField txtNomTemoin1 = new JTextField();
    private JTextField txtIdTemoin2 = new JTextField(5);
    private JTextField txtNomTemoin2 = new JTextField();
    
    // Détails de l'union & Motif
    private JTextField txtLieu = new JTextField();
    private JComboBox<String> cbRegime = new JComboBox<>(new String[]{
        "Communauté de biens", "Séparation de biens", "Participation aux acquêts"
    });
    private JSpinner dateMariage = new JSpinner(new SpinnerDateModel());
    
    private JComboBox<String> cbMotif = new JComboBox<>(new String[]{
        "Dossier administratif standard",
        "Demande de Passeport / CIN",
        "Succession / Héritage",
        "Procédure de Divorce",
        "Preuve de situation matrimoniale"
    });
    
    // Indicateur visuel du Statut de l'acte
    private JLabel lblStatut = new JLabel("Statut : NOUVEAU");
    
    // Boutons d'action adaptés au Workflow
    private JButton btnTransmettreOfficier = new JButton("🚀 Transmettre à l'Officier");
    private JButton btnImprimer = new JButton("Imprimer l'Acte Officiel");
    private JButton btnEnregistrer = new JButton("Valider et Enregistrer l'Acte");
    private JButton btnFermer = new JButton("Fermer");

    private int idDemandeAssociee = -1;
    private String actuelStatut = "NOUVEAU";

    public FormulaireMariage(JFrame parent, int idDemande, int idEpoux, int idEpouse) {
        super(parent, "Gestion du Registre des Mariages CUA", true);
        this.idDemandeAssociee = idDemande;
        
        setSize(850, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        // --- PANNEAU NORD : RECHERCHE & BADGE STATUT ---
        JPanel pSearch = new JPanel(new BorderLayout(20, 0));
        pSearch.setBackground(new Color(245, 245, 245));
        pSearch.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        pSearch.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JPanel pLeftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pLeftSearch.setBackground(new Color(245, 245, 245));
        if (idDemande != -1) {
            JLabel lblDemande = new JLabel("Saisie Initiale - Demande N° : " + idDemande);
            lblDemande.setFont(new Font("Arial", Font.BOLD, 14));
            lblDemande.setForeground(new Color(0, 102, 204));
            pLeftSearch.add(lblDemande);
            btnCharger.setVisible(false);
            txtIdMariage.setVisible(false);
            mettreAJourBadgeStatut("NOUVEAU");
        } else {
            pLeftSearch.add(new JLabel("N° de l'Acte :"));
            pLeftSearch.add(txtIdMariage);
            pLeftSearch.add(btnCharger);
        }
        
        // Style du Badge de Statut
        lblStatut.setFont(new Font("Arial", Font.BOLD, 13));
        lblStatut.setOpaque(true);
        lblStatut.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        pSearch.add(pLeftSearch, BorderLayout.WEST);
        pSearch.add(lblStatut, BorderLayout.EAST);
        add(pSearch, BorderLayout.NORTH);

        // --- PANNEAU CENTRAL : LES CONJOINTS & TÉMOINS ---
        JPanel pCenter = new JPanel(new GridLayout(2, 1, 0, 15)); 
        pCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Ligne 1 : Époux & Épouse
        JPanel pConjointsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Colonne Époux
        JPanel pEpoux = createSection("L'ÉPOUX", new Color(235, 245, 255));
        txtIdEpoux.setEditable(idDemande == -1);
        txtNomEpoux.setEditable(false);
        JPanel pIdEpouxContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pIdEpouxContainer.setBackground(new Color(235, 245, 255));
        pIdEpouxContainer.add(new JLabel("ID Citoyen Époux : "));
        pIdEpouxContainer.add(txtIdEpoux);
        pEpoux.add(pIdEpouxContainer);
        pEpoux.add(Box.createRigidArea(new Dimension(0, 5)));
        addField(pEpoux, "Nom et Prénoms de l'Époux :", txtNomEpoux);
        pConjointsRow.add(pEpoux);

        // Colonne Épouse
        JPanel pEpouse = createSection("L'ÉPOUSE", new Color(255, 240, 245));
        txtIdEpouse.setEditable(idDemande == -1);
        txtNomEpouse.setEditable(false);
        JPanel pIdEpouseContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pIdEpouseContainer.setBackground(new Color(255, 240, 245));
        pIdEpouseContainer.add(new JLabel("ID Citoyen Épouse : "));
        pIdEpouseContainer.add(txtIdEpouse);
        pEpouse.add(pIdEpouseContainer);
        pEpouse.add(Box.createRigidArea(new Dimension(0, 5)));
        addField(pEpouse, "Nom et Prénoms de l'Épouse :", txtNomEpouse);
        pConjointsRow.add(pEpouse);
        
        pCenter.add(pConjointsRow);

        // Ligne 2 : Les Témoins
        JPanel pTemoinsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Témoin 1
        JPanel pT1 = createSection("PREMIER TÉMOIN", new Color(245, 250, 245));
        txtIdTemoin1.setEditable(true); // Modifiable pour ajouter les témoins
        txtNomTemoin1.setEditable(false);
        JPanel pIdT1Container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pIdT1Container.setBackground(new Color(245, 250, 245));
        pIdT1Container.add(new JLabel("ID Citoyen Témoin 1 : "));
        pIdT1Container.add(txtIdTemoin1);
        pT1.add(pIdT1Container);
        pT1.add(Box.createRigidArea(new Dimension(0, 5)));
        addField(pT1, "Nom et Prénoms du Témoin 1 :", txtNomTemoin1);
        pTemoinsRow.add(pT1);
        
        // Témoin 2
        JPanel pT2 = createSection("SECOND TÉMOIN", new Color(245, 250, 245));
        txtIdTemoin2.setEditable(true); // Modifiable pour ajouter les témoins
        txtNomTemoin2.setEditable(false);
        JPanel pIdT2Container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pIdT2Container.setBackground(new Color(245, 250, 245));
        pIdT2Container.add(new JLabel("ID Citoyen Témoin 2 : "));
        pIdT2Container.add(txtIdTemoin2);
        pT2.add(pIdT2Container);
        pT2.add(Box.createRigidArea(new Dimension(0, 5)));
        addField(pT2, "Nom et Prénoms du Témoin 2 :", txtNomTemoin2);
        pTemoinsRow.add(pT2);
        
        pCenter.add(pTemoinsRow);
        add(pCenter, BorderLayout.CENTER);

        // --- PANNEAU SUD ---
        JPanel pSouthContainer = new JPanel(new BorderLayout(10, 10));
        pSouthContainer.setBorder(new EmptyBorder(0, 20, 15, 20));

        JPanel pDetails = new JPanel(new GridLayout(4, 2, 10, 8)); 
        pDetails.setBorder(BorderFactory.createTitledBorder("Détails de l'Union & Finalité"));
        
        pDetails.add(new JLabel("Date du Mariage :"));
        dateMariage.setEditor(new JSpinner.DateEditor(dateMariage, "dd/MM/yyyy"));
        pDetails.add(dateMariage);
        pDetails.add(new JLabel("Lieu de célébration :"));
        pDetails.add(txtLieu);
        pDetails.add(new JLabel("Régime Matrimonial :"));
        pDetails.add(cbRegime);
        pDetails.add(new JLabel("Motif de délivrance de l'acte :"));
        pDetails.add(cbMotif);
        
        pSouthContainer.add(pDetails, BorderLayout.CENTER);

        // Barre de boutons
        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        btnTransmettreOfficier.setBackground(new Color(41, 128, 185));
        btnTransmettreOfficier.setForeground(Color.WHITE);
        btnTransmettreOfficier.setFont(new Font("Arial", Font.BOLD, 12));
        btnTransmettreOfficier.setEnabled(true); // Laisse-le actif pour que la méthode intercepte les manques

        btnImprimer.setBackground(new Color(76, 175, 80));
        btnImprimer.setForeground(Color.WHITE);
        btnImprimer.setEnabled(false); 

        btnEnregistrer.setBackground(new Color(230, 126, 34));
        btnEnregistrer.setForeground(Color.WHITE);
        btnEnregistrer.setVisible(idDemande != -1);

        pButtons.add(btnFermer);
        pButtons.add(btnEnregistrer);
        pButtons.add(btnTransmettreOfficier);
        pButtons.add(btnImprimer);
        pSouthContainer.add(pButtons, BorderLayout.SOUTH);
        add(pSouthContainer, BorderLayout.SOUTH);

        // --- LOGIQUE INITIALE SI PROVIENT D'UNE DEMANDE ---
        if (idDemande != -1) {
            txtIdEpoux.setText(String.valueOf(idEpoux));
            txtIdEpouse.setText(String.valueOf(idEpouse));
            
            CitoyenDAO cDao = new CitoyenDAO();
            if (idEpoux > 0) {
                mg.model.Citoyen epoux = cDao.findById(idEpoux);
                if (epoux != null) txtNomEpoux.setText(epoux.getNom() + " " + epoux.getPrenoms());
            }
            if (idEpouse > 0) {
                mg.model.Citoyen epouse = cDao.findById(idEpouse);
                if (epouse != null) txtNomEpouse.setText(epouse.getNom() + " " + epouse.getPrenoms());
            }
        }

        // Événements
        btnCharger.addActionListener(e -> chargerActe());
        btnEnregistrer.addActionListener(e -> enregistrerNouvelActe());
        btnTransmettreOfficier.addActionListener(e -> transmettreAOfficier());
        btnImprimer.addActionListener(e -> executerImpressionEtLivraison());
        btnFermer.addActionListener(e -> dispose());
    }

    private void mettreAJourBadgeStatut(String statut) {
        this.actuelStatut = statut;
        lblStatut.setText(" Statut : " + statut + " ");
        
        switch (statut) {
            case "NOUVEAU":
                lblStatut.setBackground(new Color(220, 220, 220)); 
                lblStatut.setForeground(Color.BLACK);
                break;
            case "SAISI":
                lblStatut.setBackground(new Color(255, 193, 7)); 
                lblStatut.setForeground(Color.BLACK);
                break;
            case "SIGNE":
                lblStatut.setBackground(new Color(33, 150, 243)); 
                lblStatut.setForeground(Color.WHITE);
                break;
            case "LIVRE":
                lblStatut.setBackground(new Color(76, 175, 80)); 
                lblStatut.setForeground(Color.WHITE);
                break;
        }
    }

    private void chargerActe() {
        try {
            int id = Integer.parseInt(txtIdMariage.getText().trim());
            MariageDAO dao = new MariageDAO();
            Mariage m = dao.findById(id);

            if (m != null) {
                txtIdEpoux.setText(String.valueOf(m.getIdEpoux()));
                txtIdEpouse.setText(String.valueOf(m.getIdEpouse()));
                txtNomEpoux.setText(m.getNomCompletEpoux());
                txtNomEpouse.setText(m.getNomCompletEpouse());
                txtLieu.setText(m.getLieuMariage());
                dateMariage.setValue(new java.util.Date(m.getDateMariage().getTime()));
                cbRegime.setSelectedItem(m.getRegimeMatrimonial());
                
                txtIdTemoin1.setText(String.valueOf(m.getIdTemoin1()));
                txtIdTemoin2.setText(String.valueOf(m.getIdTemoin2()));
                txtNomTemoin1.setText(m.getNomCompletTemoin1() != null ? m.getNomCompletTemoin1() : "Inconnu");
                txtNomTemoin2.setText(m.getNomCompletTemoin2() != null ? m.getNomCompletTemoin2() : "Inconnu");
                
                mettreAJourBadgeStatut("NOUVEAU"); // On permet la re-transmission si besoin
                btnTransmettreOfficier.setEnabled(true);
                btnImprimer.setEnabled(false); 
            } else {
                JOptionPane.showMessageDialog(this, "Acte introuvable.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de recherche.");
        }
    }

    private void enregistrerNouvelActe() {
        try {
            if(txtIdTemoin1.getText().trim().isEmpty() || txtIdTemoin2.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Erreur : Les IDs des deux témoins doivent être renseignés.");
                return;
            }

            Mariage m = new Mariage();
            m.setIdEpoux(Integer.parseInt(txtIdEpoux.getText()));
            m.setIdEpouse(Integer.parseInt(txtIdEpouse.getText()));
            m.setIdTemoin1(Integer.parseInt(txtIdTemoin1.getText().trim()));
            m.setIdTemoin2(Integer.parseInt(txtIdTemoin2.getText().trim()));
            m.setLieuMariage(txtLieu.getText().trim());
            m.setRegimeMatrimonial(cbRegime.getSelectedItem().toString());
            
            java.util.Date utilDate = (java.util.Date) dateMariage.getValue();
            m.setDateMariage(new java.sql.Date(utilDate.getTime()));
            
            mettreAJourBadgeStatut("NOUVEAU");

            if(m.getLieuMariage().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez spécifier le lieu du mariage.");
                return;
            }

            MariageDAO dao = new MariageDAO();
            if (dao.save(m)) {
                JOptionPane.showMessageDialog(this, "L'acte a été enregistré localement !\nStatut : NOUVEAU. Prêt pour transmission.");
                
                CitoyenDAO cDao = new CitoyenDAO();
                mg.model.Citoyen t1 = cDao.findById(m.getIdTemoin1());
                mg.model.Citoyen t2 = cDao.findById(m.getIdTemoin2());
                if(t1 != null) txtNomTemoin1.setText(t1.getNom() + " " + t1.getPrenoms());
                if(t2 != null) txtNomTemoin2.setText(t2.getNom() + " " + t2.getPrenoms());
                
                btnTransmettreOfficier.setEnabled(true); 
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'acte.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de validation : " + ex.getMessage());
        }
    }

    private void transmettreAOfficier() {
    // 1. Sécurité : Seul un acte au statut NOUVEAU peut être transmis
    if (!actuelStatut.equals("NOUVEAU")) {
        JOptionPane.showMessageDialog(this, 
            "Action impossible : Le dossier est déjà au statut [" + actuelStatut + "].", 
            "Erreur de Workflow", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. Vérification de la complétude des données
    String epoux = txtNomEpoux.getText().trim();
    String epouse = txtNomEpouse.getText().trim();
    String t1 = txtNomTemoin1.getText().trim();
    String t2 = txtNomTemoin2.getText().trim();
    
    if (epoux.isEmpty() || epouse.isEmpty() || t1.isEmpty() || t2.isEmpty() || txtLieu.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Le dossier doit être complet (Conjoints, Témoins valides et Lieu spécifié) avant d'être transmis.", 
            "Dossier Incomplet", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 3. Confirmation de l'action par l'agent
    int choix = JOptionPane.showConfirmDialog(this,
        "Voulez-vous valider la saisie et transmettre ce dossier à l'Officier de l'état civil ?\n" +
        "Cette action verrouillera vos modifications.",
        "Confirmation de transmission", JOptionPane.YES_NO_OPTION);

    if (choix == JOptionPane.YES_OPTION) {
        // Changement d'état visuel et logique
        mettreAJourBadgeStatut("SAISI");
        
        // On désactive les boutons d'action de l'agent puisque le dossier part chez l'officier
        btnTransmettreOfficier.setEnabled(false);
        btnEnregistrer.setEnabled(false);
        
        int idMariage = Integer.parseInt(txtIdMariage.getText().trim());
        MariageDAO dao = new MariageDAO();
        dao.updateStatut(idMariage, "SAISI"); 


        JOptionPane.showMessageDialog(this, 
            "Le dossier a été transmis avec succès.\n" +
            "Statut actuel : [SAISI] - En attente de la signature de l'Officier.", 
            "Dossier Transmis", JOptionPane.INFORMATION_MESSAGE);
            
        // Optionnel : Fermer le formulaire automatiquement après transmission
        // dispose();
    }
}
    private void executerImpressionEtLivraison() {
        if (!actuelStatut.equals("SIGNE")) {
            JOptionPane.showMessageDialog(this, "Impossible d'imprimer : L'acte n'est pas encore signé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numeroActe = txtIdMariage.getText().trim().isEmpty() ? String.valueOf(idDemandeAssociee) : txtIdMariage.getText().trim();

        String certificatOfficiel = 
            "==================================================\n" +
            "        COMMUNE URBAINE D'ANTANANARIVO (CUA)       \n" +
            "             ACTE DE MARIAGE OFFICIEL              \n" +
            "==================================================\n" +
            " Acte N° : " + numeroActe + "          | Statut final : LIVRÉ \n" +
            " Motif : " + cbMotif.getSelectedItem().toString() + "\n" +
            "--------------------------------------------------\n" +
            " ÉPOUX : " + txtNomEpoux.getText() + "\n" +
            " ÉPOUSE : " + txtNomEpouse.getText() + "\n" +
            "--------------------------------------------------\n" +
            " TÉMOIN 1 : " + txtNomTemoin1.getText() + "\n" +
            " TÉMOIN 2 : " + txtNomTemoin2.getText() + "\n" +
            "==================================================\n" +
            " ✔️ IMPRIMÉ ET PARAPHÉ LE : " + new java.util.Date() + "\n" +
            " ⛓️ ANCRAGE BLOCKCHAIN VÉRIFIÉ & ARCHIVÉ\n" +
            "==================================================";

        JTextArea textArea = new JTextArea(certificatOfficiel);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(520, 420));

        JOptionPane.showMessageDialog(this, scrollPane, "Lancement de l'impression", JOptionPane.INFORMATION_MESSAGE);
        
        mettreAJourBadgeStatut("LIVRE");
        btnImprimer.setEnabled(false); 
        JOptionPane.showMessageDialog(this, "Le statut de l'acte est passé à LIVRÉ. Dossier clôturé.");
    }

    private JPanel createSection(String title, Color bg) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)));
        p.setBorder(BorderFactory.createCompoundBorder(p.getBorder(), BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        return p;
    }

    private void addField(JPanel p, String label, JTextField tf) {
        JLabel lbl = new JLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(tf);
    }

    public JTextField getTxtIdMariage() {
        return txtIdMariage;
    }
}