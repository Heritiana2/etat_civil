package mg.model;

public class Acte {
    private String numActe, bureau, nom, prenom, sexe, lieuNais, pere, mere, lieuDelivrance;
    private String dateNais, heureNais, dateDelivrance;
    private int idDemande, idCitoyen;

    public Acte() {
        // Constructeur par défaut
    }

    public Acte(int idDemande, int idCitoyen, String numActe, String bureau, String nom, String prenom, String sexe, String lieuNais,
                String dateNais, String heureNais, String pere, String mere, String dateDelivrance, String lieuDelivrance) {
        this.idDemande = idDemande;
        this.idCitoyen = idCitoyen;
        this.numActe = numActe;
        this.bureau = bureau;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.lieuNais = lieuNais;
        this.dateNais = dateNais;
        this.heureNais = heureNais;
        this.pere = pere;
        this.mere = mere;
        this.dateDelivrance = dateDelivrance;
        this.lieuDelivrance = lieuDelivrance;
    }

    public String getNumActe() {
        return numActe;
    }

    public void setNumActe(String numActe) {
        this.numActe = numActe;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getLieuNais() {
        return lieuNais;
    }

    public void setLieuNais(String lieuNais) {
        this.lieuNais = lieuNais;
    }

    public String getPere() {
        return pere;
    }

    public void setPere(String pere) {
        this.pere = pere;
    }

    public String getMere() {
        return mere;
    }

    public void setMere(String mere) {
        this.mere = mere;
    }

    public String getLieuDelivrance() {
        return lieuDelivrance;
    }

    public void setLieuDelivrance(String lieuDelivrance) {
        this.lieuDelivrance = lieuDelivrance;
    }

    public String getDateNais() {
        return dateNais;
    }

    public void setDateNais(String dateNais) {
        this.dateNais = dateNais;
    }

    public String getHeureNais() {
        return heureNais;
    }

    public void setHeureNais(String heureNais) {
        this.heureNais = heureNais;
    }

    public String getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(String dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
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
}
