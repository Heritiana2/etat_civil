package mg.model;

import java.sql.Date;

public class Citoyen {
    
    private int idCitoyen;
    private String nom;
    private String prenoms;
    private char sexe;
    private Date dateNaissance;
    private String lieuNaissance;
    private String adresse;
    private String statutMatrimonial;
    private String nomPere;
    private String nomMere;

    public Citoyen() {}

    public Citoyen(int idCitoyen, String nom, String prenoms, char sexe, Date dateNaissance, String lieuNaissance,
            String adresse, String statutMatrimonial, String nomPere, String nomMere) {
        this.idCitoyen = idCitoyen;
        this.nom = nom;
        this.prenoms = prenoms;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;
        this.adresse = adresse;
        this.statutMatrimonial = statutMatrimonial;
        this.nomPere = nomPere;
        this.nomMere = nomMere;
    }

    public int getIdCitoyen() {
        return idCitoyen;
    }

    public void setIdCitoyen(int idCitoyen) {
        this.idCitoyen = idCitoyen;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public char getSexe() {
        return sexe;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getStatutMatrimonial() {
        return statutMatrimonial;
    }

    public void setStatutMatrimonial(String statutMatrimonial) {
        this.statutMatrimonial = statutMatrimonial;
    }

    public String getNomPere() { return nomPere; }
    public void setNomPere(String nomPere) { this.nomPere = nomPere; }
    
    public String getNomMere() { return nomMere; }
    public void setNomMere(String nomMere) { this.nomMere = nomMere; }

    @Override
    public String toString() {
        return "Citoyen [ID=" + idCitoyen + ", Nom=" + " " + prenoms +
                ", Statut=" + statutMatrimonial + "]";
    }
}
