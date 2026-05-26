package mg.controller;

import mg.DAO.DemandeDAO;
import mg.DAO.MariageDAO;
import mg.view.OfficierDashboard;
import mg.DAO.NaissanceDAO;
import mg.util.StringUtil;
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
                String hashNaissance = StringUtil.applySha256(nomCitoyen + "Naissance" + idDemande);
                NaissanceDAO naisDAO = new NaissanceDAO();
                boolean blockOk = naisDAO.ancrerNouveauBloc(hashNaissance, 1);
                
                if (blockOk) {
                    JOptionPane.showMessageDialog(parentFrame, "Acte signé avec succès et scéllé dans la blockchain!");
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Acte signé mais erreur lors de l'ancrage blockchain.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                }
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

        if (index == 0) {
            // Naissances : on charge la table des demandes saisies
            List<Object[]> demandes = model.obtenirDemandesParTypeEtStatut("Naissance", "Saisi");
            view.getTableModelAttente().setRowCount(0);
            for (Object[] ligne : demandes) {
                view.getTableModelAttente().addRow(ligne);
            }
        } else if (index == 1) {
            // Mariages : on charge les actes mariage existants et on filtre ceux en statut SAISI
            MariageDAO mariageDao = new MariageDAO();
            List<Object[]> actes = mariageDao.findAllActesPourTableau();
            view.chargerDonneesMariage(actes);
        } else {
            // Décès (non implémenté ici) : on vide juste la table actuellement affichée
            view.getTableModelAttente().setRowCount(0);
        }
    }
}
