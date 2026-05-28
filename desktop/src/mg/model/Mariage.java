package mg.model;

import java.sql.Date;

public class Mariage {
    private int idMariage;
    private int idEpoux;
    private int idEpouse;
    private int idTemoin1; // Nouveau
    private int idTemoin2; // Nouveau
    private Date dateMariage;
    private String lieuMariage;
    private String regimeMatrimonial;

    // Propriétés calculées pour l'affichage dans l'interface
    private String nomCompletEpoux;
    private String nomCompletEpouse;
    private String nomCompletTemoin1; // Nouveau
    private String nomCompletTemoin2; // Nouveau

    // --- GETTERS ET SETTERS SÉLECTIONNÉS ---
    public int getIdMariage() { return idMariage; }
    public void setIdMariage(int idMariage) { this.idMariage = idMariage; }

    public int getIdEpoux() { return idEpoux; }
    public void setIdEpoux(int idEpoux) { this.idEpoux = idEpoux; }

    public int getIdEpouse() { return idEpouse; }
    public void setIdEpouse(int idEpouse) { this.idEpouse = idEpouse; }

    public int getIdTemoin1() { return idTemoin1; }
    public void setIdTemoin1(int idTemoin1) { this.idTemoin1 = idTemoin1; }

    public int getIdTemoin2() { return idTemoin2; }
    public void setIdTemoin2(int idTemoin2) { this.idTemoin2 = idTemoin2; }

    public Date getDateMariage() { return dateMariage; }
    public void setDateMariage(Date dateMariage) { this.dateMariage = dateMariage; }

    public String getLieuMariage() { return lieuMariage; }
    public void setLieuMariage(String lieuMariage) { this.lieuMariage = lieuMariage; }

    public String getRegimeMatrimonial() { return regimeMatrimonial; }
    public void setRegimeMatrimonial(String regimeMatrimonial) { this.regimeMatrimonial = regimeMatrimonial; }

    // Pour l'interface graphique
    public String getNomCompletEpoux() { return nomCompletEpoux; }
    public void setNomCompletEpoux(String nomCompletEpoux) { this.nomCompletEpoux = nomCompletEpoux; }

    public String getNomCompletEpouse() { return nomCompletEpouse; }
    public void setNomCompletEpouse(String nomCompletEpouse) { this.nomCompletEpouse = nomCompletEpouse; }

    public String getNomCompletTemoin1() { return nomCompletTemoin1; }
    public void setNomCompletTemoin1(String nomCompletTemoin1) { this.nomCompletTemoin1 = nomCompletTemoin1; }

    public String getNomCompletTemoin2() { return nomCompletTemoin2; }
    public void setNomCompletTemoin2(String nomCompletTemoin2) { this.nomCompletTemoin2 = nomCompletTemoin2; }
}