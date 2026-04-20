package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormulaireSaisieCopie extends JDialog {
    // Champs basés sur le document CUA
    private JTextField txtNumActe, txtBureau, txtNom, txtPrenom, txtLieu, txtPere, txtMere, txtLieuDelivrance;
    private JSpinner dateNais, heureNais, dateActe, dateDelivrance;
    private JButton btnEnvoyer, btnAnnuler;

    public FormulaireSaisieCopie(JFrame parent, int idDemande) {
        super(parent, "Saisie Officielle - Bulletin de Naissance #" + idDemande, true);
        
        setSize(600, 750); // Légèrement agrandi pour le confort
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header bleu CUA
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 204));
        JLabel title = new JLabel("RÉPUBLIQUE DE MADAGASCAR - COMMUNE URBAINE D'ANTANANARIVO");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 12));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // --- SECTION 1 : RÉFÉRENCES ---
        JPanel pRef = createSection("Références de l'Acte");
        txtBureau = addField(pRef, "Bureau (Ex: TANA I) :");
        txtNumActe = addField(pRef, "N° de l'acte :");
        dateActe = addDateSpinner(pRef, "Date de l'acte (DU) :");

        // --- SECTION 2 : L'ENFANT ---
        JPanel pEnfant = createSection("Identité du Nouveau-né");
        txtNom = addField(pEnfant, "Nom :");
        txtPrenom = addField(pEnfant, "Prénoms :");
        dateNais = addDateSpinner(pEnfant, "Né(e) le :");
        
        heureNais = new JSpinner(new SpinnerDateModel());
        heureNais.setEditor(new JSpinner.DateEditor(heureNais, "HH:mm"));
        pEnfant.add(new JLabel("Heure de naissance :"));
        pEnfant.add(heureNais);
        
        txtLieu = addField(pEnfant, "À (Lieu de naissance) :");

        // --- SECTION 3 : FILIATION ---
        JPanel pParents = createSection("Filiation");
        txtPere = addField(pParents, "Fils/Fille de (Père) :");
        txtMere = addField(pParents, "Et de (Mère) :");

        // --- SECTION 4 : DÉLIVRANCE ---
        JPanel pDelivrance = createSection("Délivrance de la copie");
        txtLieuDelivrance = addField(pDelivrance, "Fait à :");
        dateDelivrance = addDateSpinner(pDelivrance, "Le (Date du jour) :");

        mainPanel.add(pRef);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pEnfant);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pParents);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pDelivrance);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Footer avec boutons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        
        btnEnvoyer = new JButton("Enregistrer et Envoyer");
        btnEnvoyer.setBackground(new Color(46, 204, 113));
        btnEnvoyer.setForeground(Color.WHITE);
        btnEnvoyer.setFont(new Font("Arial", Font.BOLD, 12));

        footer.add(btnAnnuler);
        footer.add(btnEnvoyer);
        add(footer, BorderLayout.SOUTH);
    }

    // --- HELPERS ---
    private JSpinner addDateSpinner(JPanel p, String label) {
        p.add(new JLabel(label));
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        p.add(spinner);
        return spinner;
    }

    private JPanel createSection(String title) {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 11)
        ));
        return p;
    }

    private JTextField addField(JPanel p, String label) {
        p.add(new JLabel(label));
        JTextField tf = new JTextField();
        p.add(tf);
        return tf;
    }

    // --- GETTERS (Pour le Controller) ---
    public String getBureau() { return txtBureau.getText(); }
    public String getNumActe() { return txtNumActe.getText(); }
    public String getNom() { return txtNom.getText(); }
    public String getPrenom() { return txtPrenom.getText(); }
    public String getLieu() { return txtLieu.getText(); }
    public String getPere() { return txtPere.getText(); }
    public String getMere() { return txtMere.getText(); }
    public String getLieuDelivrance() { return txtLieuDelivrance.getText(); }
    public JButton getBtnEnvoyer() { return btnEnvoyer; }

    public String getDateNaissance() {
        return new SimpleDateFormat("yyyy-MM-dd").format((Date) dateNais.getValue());
    }
    
    public String getHeureNaissance() {
        return new SimpleDateFormat("HH:mm:ss").format((Date) heureNais.getValue());
    }

    // --- SETTERS (Pour le pré-remplissage du Controller) ---
    public void setNom(String nom) { this.txtNom.setText(nom); }
    public void setPrenom(String prenom) { this.txtPrenom.setText(prenom); }
    public void setLieuDelivrance(String lieu) { this.txtLieuDelivrance.setText(lieu); }
}