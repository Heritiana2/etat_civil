package mg.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class FormulaireSaisieCopie extends JDialog {
    // Champs de saisie (à récupérer dans le Controller)
    private JTextField txtNom, txtPrenom, txtLieu, txtPere, txtMere;
    private JSpinner dateNais;
    private JButton btnEnvoyer, btnAnnuler;

    public FormulaireSaisieCopie(JFrame parent, int idDemande) {
        // 'true' rend la fenêtre modale (bloque le parent)
        super(parent, "Saisie de l'Acte - Dossier #" + idDemande, true);
        
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(new Color(0, 102, 204));
        JLabel title = new JLabel("SAISIE DES INFORMATIONS DE L'ACTE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Section Enfant
        JPanel pEnfant = createSection("Identité du Nouveau-né");
        txtNom = addField(pEnfant, "Nom :");
        txtPrenom = addField(pEnfant, "Prénoms :");
        
        dateNais = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateNais, "dd/MM/yyyy");
        dateNais.setEditor(editor);
        pEnfant.add(new JLabel("Date de Naissance :"));
        pEnfant.add(dateNais);
        
        txtLieu = addField(pEnfant, "Lieu de Naissance :");

        JPanel pParents = createSection("Filiation");
        txtPere = addField(pParents, "Père (Nom complet) :");
        txtMere = addField(pParents, "Mère (Nom complet) :");

        mainPanel.add(pEnfant);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(pParents);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(new EmptyBorder(10, 0, 10, 10));

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        
        btnEnvoyer = new JButton("Envoyer à l'Officier");
        btnEnvoyer.setBackground(new Color(46, 204, 113)); // Vert
        btnEnvoyer.setForeground(Color.WHITE);
        btnEnvoyer.setFont(new Font("Arial", Font.BOLD, 12));

        footer.add(btnAnnuler);
        footer.add(btnEnvoyer);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title) {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 15));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12)
        ));
        return p;
    }

    private JTextField addField(JPanel p, String label) {
        p.add(new JLabel(label));
        JTextField tf = new JTextField();
        p.add(tf);
        return tf;
    }

    // --- GETTERS (Pour ton Controller) ---
    public String getNom() { return txtNom.getText(); }
    public String getPrenom() { return txtPrenom.getText(); }
    public String getLieu() { return txtLieu.getText(); }
    public String getPere() { return txtPere.getText(); }
    public String getMere() { return txtMere.getText(); }
    public JButton getBtnEnvoyer() { return btnEnvoyer; }
    
    // Pour la date, on la formate en String ISO (yyyy-MM-dd) pour MySQL
    public String getDateNaissance() {
        java.util.Date d = (java.util.Date) dateNais.getValue();
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
    }
}