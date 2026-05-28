package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import mg.DAO.CitoyenDAO;
import mg.model.Citoyen;

public class FormulaireSaisieCopie extends JDialog {
    // Éléments de recherche
    private JTextField txtSearchId;
    private JButton btnRechercher;

    // Champs du document
    private JTextField txtNumActe, txtBureau, txtNom, txtPrenom, txtLieu, txtPere, txtMere, txtLieuDelivrance;
    private JSpinner dateNais; // Suppression de heureNais inutile
    private JButton btnEnvoyer, btnAnnuler;
    
    private int currentIdCitoyen = -1; 

    public FormulaireSaisieCopie(JFrame parent, int idDemande) {
        super(parent, "Saisie Officielle - Bulletin de Naissance #" + idDemande, true);
        
        setSize(600, 850); 
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- HEADER ---
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

        // --- SECTION 0 : RECHERCHE PAR ID ---
        JPanel pRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pRecherche.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2), "1. Rechercher le Citoyen",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)
        ));
        txtSearchId = new JTextField(10);
        btnRechercher = new JButton("Charger les données");
        pRecherche.add(new JLabel("ID Citoyen :"));
        pRecherche.add(txtSearchId);
        pRecherche.add(btnRechercher);
        mainPanel.add(pRecherche);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- SECTION 1 : RÉFÉRENCES ---
        JPanel pRef = createSection("2. Références de l'Acte");
        txtBureau = addField(pRef, "Bureau (Ex: TANA I) :");
        txtNumActe = addField(pRef, "N° de l'acte :");
        JSpinner dateActe = addDateSpinner(pRef, "Date de l'acte (DU) :");
        dateActe.setEnabled(true);

        // --- SECTION 2 : L'ENFANT ---
        JPanel pEnfant = createSection("3. Identité du Sujet");
        txtNom = addField(pEnfant, "Nom :");
        txtPrenom = addField(pEnfant, "Prénoms :");
        dateNais = addDateSpinner(pEnfant, "Né(e) le :");
                
        txtLieu = addField(pEnfant, "À (Lieu de naissance) :");

        txtNom.setEditable(false);
        txtPrenom.setEditable(false);
        txtLieu.setEditable(false);

        // --- SECTION 3 : FILIATION ---
        JPanel pParents = createSection("4. Filiation");
        txtPere = addField(pParents, "Fils/Fille de (Père) :");
        txtMere = addField(pParents, "Et de (Mère) :");
        
        // On verrouille aussi les noms des parents
        txtPere.setEditable(false);
        txtMere.setEditable(false);

        // --- SECTION 4 : DÉLIVRANCE ---
        JPanel pDelivrance = createSection("5. Délivrance de la copie");
        txtLieuDelivrance = addField(pDelivrance, "Fait à :");
        JSpinner dateDelivrance = addDateSpinner(pDelivrance, "Le (Date du jour) :");
        dateDelivrance.setEnabled(true);

        mainPanel.add(pRef);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pEnfant);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pParents);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pDelivrance);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        
        btnEnvoyer = new JButton("Enregistrer et Envoyer à l'Officier");
        btnEnvoyer.setBackground(new Color(46, 204, 113));
        btnEnvoyer.setForeground(Color.WHITE);
        btnEnvoyer.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnvoyer.setEnabled(false); 

        // Le contrôleur externe gère l'enregistrement et la validation pour ce formulaire.
        // btnEnvoyer ajoutera son ActionListener depuis AgentController.

        footer.add(btnAnnuler);
        footer.add(btnEnvoyer);
        add(footer, BorderLayout.SOUTH);

        btnRechercher.addActionListener(e -> actionRechercher());
    }

    private void actionRechercher() {
        try {
            int id = Integer.parseInt(txtSearchId.getText());
            CitoyenDAO dao = new CitoyenDAO();
            Citoyen c = dao.findById(id);
            
            if (c != null) {
                setNom(c.getNom());
                setPrenom(c.getPrenoms());
                txtLieu.setText(c.getLieuNaissance());
                
                txtPere.setText(c.getNomPere() != null ? c.getNomPere() : "Inconnu");
                txtMere.setText(c.getNomMere() != null ? c.getNomMere() : "Inconnue");
                
                if (c.getDateNaissance() != null) {
                    dateNais.setValue(new java.util.Date(c.getDateNaissance().getTime()));
                }
                
                currentIdCitoyen = c.getIdCitoyen();
                btnEnvoyer.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Citoyen chargé avec succès !");
            } else {
                viderChamps();
                JOptionPane.showMessageDialog(this, "Aucun citoyen trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un ID numérique valide.");
        }
    }

    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtLieu.setText("");
        txtPere.setText("");
        txtMere.setText("");
        btnEnvoyer.setEnabled(false);
        currentIdCitoyen = -1;
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

    // --- GETTERS & SETTERS ---
    public String getBureau() { return txtBureau.getText().trim(); }
    public String getNumActe() { return txtNumActe.getText().trim(); }
    public String getNom() { return txtNom.getText().trim(); }
    public String getPrenom() { return txtPrenom.getText().trim(); }
    public String getLieu() { return txtLieu.getText().trim(); }
    public String getPere() { return txtPere.getText().trim(); }
    public String getMere() { return txtMere.getText().trim(); }
    public String getLieuDelivrance() { return txtLieuDelivrance.getText().trim(); }
    public JButton getBtnEnvoyer() { return btnEnvoyer; }
    public int getCurrentIdCitoyen() { return currentIdCitoyen; }

    public String getDateNaissance() {
        return new SimpleDateFormat("yyyy-MM-dd").format((Date) dateNais.getValue());
    }
    
    public String getHeureNaissance() {
        return new SimpleDateFormat("HH:mm:ss").format((Date) dateNais.getValue());
    }

    public void setNom(String nom) { this.txtNom.setText(nom); }
    public void setPrenom(String prenom) { this.txtPrenom.setText(prenom); }
}