package mg.controller;

import mg.DAO.DemandeDAO;
import mg.view.OfficierDashboard;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class OfficierController {
    
    private DemandeDAO model;
    private OfficierDashboard view;
    private JFrame parentFrame;

    public OfficierController(JFrame parent) {
        this.model = new DemandeDAO();
        this.parentFrame = parent;
    }

    public void setView(OfficierDashboard view) {
        this.view = view;
    }

    public void gererSignature() {
        int selectedRow = view.getTableAttente().getSelectedRow();
        if (selectedRow == -1)  {
            JOptionPane.showMessageDialog(parentFrame, "Veuillez selectionner une demande dans la liste");
            return;
        }

        int idDemande = (int) view.getTableAttente().getValueAt(selectedRow, 0);
        String nomCitoyen = (String) view.getTableAttente().getValueAt(selectedRow, 2);

        // Confirmation officielle
        int confirm = JOptionPane.showConfirmDialog(parentFrame, 
            "SIGNATURE NUMÉRIQUE CUA \n\n" +
            "Citoyen : " + nomCitoyen + "\n" +
            "ID Demande : " + idDemande + "\n\n" +
            "Confirmez-vous la signature et le scellement Blockchain de cet acte ?",
            "Validation de l'Officier", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = model.signerActe(idDemande);
            
            if (success) {
                JOptionPane.showMessageDialog(parentFrame, "Acte signé avec succès et scéllé dans la blockchain!");
                actualiserTableau();
            }
            else {
                JOptionPane.showMessageDialog(parentFrame, "Erreur lors de la signature.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actualiserTableau() {
        if (view == null) {
            return;
        }

        int index = view.getTabbedPane().getSelectedIndex();
        String typeActe = (index == 0) ? "Naissance" : (index == 1) ? "Mariage"  : "Décès";

        List<Object[]> demandes = model.obtenirDemandesParTypeEtStatut(typeActe, "Saisi");

        view.getTableModelAttente().setRowCount(0); // Vider le tableau avant de le remplir
        for (Object[] ligne : demandes) {
            view.getTableModelAttente().addRow(ligne);
        }
    }
}
