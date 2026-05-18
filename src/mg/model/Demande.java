package mg.model;

import java.time.LocalDateTime;

public abstract class Demande {
    protected int idDemande;
    protected int idCitoyen;
    protected LocalDateTime dateDemande;
    protected String statut;

    public Demande(int idDemande, int idCitoyen) {
        this.idDemande = idDemande;
        this.idCitoyen = idCitoyen;
        this.dateDemande = LocalDateTime.now();
        this.statut = "Nouveau";
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public int getIdCitoyen() {
        return idCitoyen;
    }

    public void setIdCitoyen(int idCitoyen) {
        this.idCitoyen = idCitoyen;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public abstract boolean verifierValidite();
}
