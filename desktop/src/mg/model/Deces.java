package mg.model;

import java.util.Date;

public class Deces {
    private int idDeces;
    private int idDemande;
    private int idDefunt;
    private Date dateDeces;
    private String lieuDeces;
    private String causeDeces;

    // Constructeur vide
    public Deces() {}

    // Constructeur complet
    public Deces(int idDeces, int idDemande, int idDefunt, Date dateDeces, String lieuDeces, String causeDeces) {
        this.idDeces = idDeces;
        this.idDemande = idDemande;
        this.idDefunt = idDefunt;
        this.dateDeces = dateDeces;
        this.lieuDeces = lieuDeces;
        this.causeDeces = causeDeces;
    }

    // --- GETTERS ET SETTERS ---
    public int getIdDeces() { return idDeces; }
    public void setIdDeces(int idDeces) { this.idDeces = idDeces; }

    public int getIdDemande() { return idDemande; }
    public void setIdDemande(int idDemande) { this.idDemande = idDemande; }

    public int getIdDefunt() { return idDefunt; }
    public void setIdDefunt(int idDefunt) { this.idDefunt = idDefunt; }

    public Date getDateDeces() { return dateDeces; }
    public void setDateDeces(Date dateDeces) { this.dateDeces = dateDeces; }

    public String getLieuDeces() { return lieuDeces; }
    public void setLieuDeces(String lieuDeces) { this.lieuDeces = lieuDeces; }

    public String getCauseDeces() { return causeDeces; }
    public void setCauseDeces(String causeDeces) { this.causeDeces = causeDeces; }
}