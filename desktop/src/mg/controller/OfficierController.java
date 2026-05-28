package mg.controller;

import mg.DAO.DemandeDAO;
import mg.DAO.MariageDAO;
import mg.DAO.DecesDAO;
import mg.view.OfficierDashboard;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class OfficierController {
    
    private DemandeDAO model;
    private OfficierDashboard view;
    private JFrame parentFrame;
    private int officierId;

    public OfficierController(JFrame parent, int officierId) {
        this.model = new DemandeDAO();
        this.parentFrame = parent;
        this.officierId = officierId;
    }

    public int getOfficierId() {
        return officierId;
    }

    public void setView(OfficierDashboard view) {
        this.view = view;
    }

    public void gererSignature() {
        int indexOnglet = view.getTabbedPane().getSelectedIndex(); // 0 = Naissance, 1 = Mariage, 2 = Décès
        int selectedRow = -1;
        int idDemande = -1;
        String nomCitoyen = "";

        // --- CORRECTION : Récupération des données depuis le BON tableau selon l'onglet actif ---
        if (indexOnglet == 0) {
            selectedRow = view.getTableAttente().getSelectedRow();
            if (selectedRow != -1) {
                idDemande = (int) view.getTableAttente().getValueAt(selectedRow, 0);
                nomCitoyen = (String) view.getTableAttente().getValueAt(selectedRow, 2);
            }
        } else if (indexOnglet == 1) {
            selectedRow = view.getTableMariageAttente().getSelectedRow();
            if (selectedRow != -1) {
                idDemande = (int) view.getTableMariageAttente().getValueAt(selectedRow, 0);
                nomCitoyen = (String) view.getTableMariageAttente().getValueAt(selectedRow, 1); // Nom Époux
            }
        } else if (indexOnglet == 2) {
            selectedRow = view.getTableDecesAttente().getSelectedRow();
            if (selectedRow != -1) {
                idDemande = (int) view.getTableDecesAttente().getValueAt(selectedRow, 0);
                nomCitoyen = (String) view.getTableDecesAttente().getValueAt(selectedRow, 2);
            }
        }

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Veuillez sélectionner une demande dans la liste.");
            return;
        }

        // Confirmation officielle
        int confirm = JOptionPane.showConfirmDialog(parentFrame, 
            "SIGNATURE NUMÉRIQUE CUA \n\n" +
            "Citoyen : " + nomCitoyen + "\n" +
            "ID Demande : " + idDemande + "\n\n" +
            "Confirmez-vous la signature et le scellement Blockchain de cet acte ?",
            "Validation de l'Officier", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            
            if (indexOnglet == 0) {
                // ========================================================
                // 👶 LOGIQUE POUR L'ACTE DE NAISSANCE
                // ========================================================
                mg.DAO.NaissanceDAO naissanceDAO = new mg.DAO.NaissanceDAO();
                java.util.Map<String, String> donnees = naissanceDAO.getDonneesCompletesPourPDF(idDemande);
                
                if (donnees == null || donnees.get("nom_enfant") == null) {
                    JOptionPane.showMessageDialog(parentFrame, "Erreur : Les données de l'enfant n'ont pas encore été saisies par l'agent.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String hashNaissance = mg.util.SecurityUtils.calculerHashNaissance(
                    donnees.get("nom_enfant"),
                    donnees.get("prenoms_enfant"),
                    donnees.get("date_naissance"),
                    donnees.get("lieu_naissance"),
                    donnees.get("nom_pere"),
                    donnees.get("nom_mere")
                );

                boolean success = model.mettreAJourStatutEtHash(idDemande, "SIGNE", hashNaissance); 
                
                if (success) {
                    MariageDAO mariageDAO = new MariageDAO();
                    mariageDAO.ancrerNouveauBloc(hashNaissance, officierId);
                    
                    JOptionPane.showMessageDialog(parentFrame, "Acte de naissance signé avec succès et scellé dans la Blockchain !");
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Erreur lors de la signature de la naissance.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } else if (indexOnglet == 1) {
                // ========================================================
                // 💍 LOGIQUE POUR L'ACTE DE MARIAGE
                // ========================================================
                boolean success = model.signerActe(idDemande); 
                if (success) {
                    String hashMariage = mg.util.StringUtil.applySha256(nomCitoyen + "Mariage" + idDemande);
                    MariageDAO mariageDAO = new MariageDAO();
                    
                    boolean blockOk = mariageDAO.ancrerNouveauBloc(hashMariage, officierId);
                    if (blockOk) {
                        mariageDAO.updateStatutEtHash(idDemande, "SIGNE", hashMariage);
                        JOptionPane.showMessageDialog(parentFrame, "Acte de mariage signé avec succès et scellé dans la Blockchain !");
                    }
                }
            } else if (indexOnglet == 2) {
                // ========================================================
                // 🪦 LOGIQUE POUR L'ACTE DE DÉCÈS
                // ========================================================
                mg.DAO.DecesDAO decesDAO = new mg.DAO.DecesDAO();
                java.util.Map<String, String> donnees = decesDAO.getDonneesDefuntPourBlockchain(idDemande);
                
                if (donnees == null || donnees.get("nom_defunt") == null) {
                    JOptionPane.showMessageDialog(parentFrame, "Erreur : Les données du défunt sont introuvables.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String hashDeces = mg.util.SecurityUtils.calculerHashDeces(
                    donnees.get("nom_defunt"),
                    donnees.get("prenoms_defunt"),
                    donnees.get("date_deces"),
                    donnees.get("lieu_deces")
                );

                boolean success = model.mettreAJourStatutEtHash(idDemande, "SIGNE", hashDeces); 
                
                if (success) {
                    MariageDAO mariageDAO = new MariageDAO();
                    mariageDAO.ancrerNouveauBloc(hashDeces, officierId);
                    
                    // ========================================================
                    // 🌟 NOUVEAU : MARQUAGE DU CITOYEN COMME DÉCÉDÉ
                    // ========================================================
                    if (donnees.get("id_defunt") != null) {
                        int idDefunt = Integer.parseInt(donnees.get("id_defunt"));
                        decesDAO.marquerCitoyenCommeDecede(idDefunt);
                    }
                    
                    JOptionPane.showMessageDialog(parentFrame, "Acte de décès signé avec succès, scellé dans la Blockchain et citoyen marqué comme Décédé !");
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Erreur lors de la signature de l'acte de décès.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
}             
            // On rafraîchit la vue après une signature validée
            actualiserTableau();
        }

    public void actualiserTableau() {
        if (view == null) {
            return;
        }

        int index = view.getTabbedPane().getSelectedIndex();

        if (index == 0) {
            // Naissances assignées à l'officier connecté
            List<Object[]> demandes = model.obtenirDemandesParTypeEtStatutPourOfficier("Naissance", "Saisi", officierId);
            view.getTableModelAttente().setRowCount(0);
            for (Object[] ligne : demandes) {
                view.getTableModelAttente().addRow(ligne);
            }
        } else if (index == 1) {
            // Mariages
            MariageDAO mariageDao = new MariageDAO();
            List<Object[]> actes = mariageDao.findAllActesPourTableau();
            view.chargerDonneesMariage(actes);
            
        } else if (index == 2) {
            // Décès assignés à l'officier connecté
            List<Object[]> demandes = model.obtenirDemandesParTypeEtStatutPourOfficier("Décès", "Saisi", officierId);
            view.getTableModelDecesAttente().setRowCount(0);
            for (Object[] ligne : demandes) {
                view.getTableModelDecesAttente().addRow(ligne);
            }
        }
    }
}