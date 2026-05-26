package mg.util; // À modifier en mg.service si ton dossier est 'service'

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class PDFGenerationService {

    // Utilisation du chemin complet pour exclure le conflit avec java.awt.Image
    private static com.itextpdf.text.Image genererImageQRCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        
        return com.itextpdf.text.Image.getInstance(pngData);
    }

    public static void genererActeMariagePDF(Map<String, String> donnees, String cheminSortie) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(cheminSortie));
            document.open();

            // --- REPUBLIQUE DE MADAGASCAR ---
            Font fontHeader1 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
            Font fontHeader2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
            Font fontTitre = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Font fontCorps = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Font fontBlock = new Font(Font.FontFamily.COURIER, 9, Font.NORMAL, BaseColor.DARK_GRAY);

            Paragraph rep = new Paragraph("REPOBLIKAN'I MADAGASCAR\n", fontHeader1);
            rep.setAlignment(Element.ALIGN_RIGHT);
            Paragraph dev = new Paragraph("Fitiavana - Tanindrazana - Fandrosoana\n---------\n", fontHeader2);
            dev.setAlignment(Element.ALIGN_RIGHT);
            document.add(rep);
            document.add(dev);

            // --- COMMUNE URBAINE D'ANTANANARIVO ---
            Paragraph cua = new Paragraph("PROVINCE D'ANTANANARIVO\nCOMMUNE URBAINE D'ANTANANARIVO\nARRONDISSEMENT CENTRAL\n", fontHeader1);
            cua.setAlignment(Element.ALIGN_LEFT);
            document.add(cua);

            document.add(new Paragraph("\n\n"));

            // --- TITRE DU DOCUMENT ---
            Paragraph titre = new Paragraph("COPIE D'ACTE DE MARIAGE", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Paragraph("N° Acte : " + donnees.get("id_mariage") + "\n\n", fontHeader1));

            // --- CORPS DE L'ACTE ---
            StringBuilder corps = new StringBuilder();
            corps.append("Le ").append(donnees.get("date_mariage"))
                 .append(", a été célébré publiquement devant l'Officier de l'État Civil de la Commune Urbaine d'Antananarivo, au ").append(donnees.get("lieu_mariage"))
                 .append(", le mariage entre :\n\n")
                 .append("ÉPOUX : ").append(donnees.get("nom_epoux")).append("\n")
                 .append("ÉPOUSE : ").append(donnees.get("nom_epouse")).append("\n\n")
                 .append("Le régime matrimonial choisi par les époux est : ").append(donnees.get("regime_matrimonial")).append(".\n\n")
                 .append("En présence des témoins majeurs requis :\n")
                 .append("1° Témoin : ").append(donnees.get("temoin1")).append("\n")
                 .append("2° Témoin : ").append(donnees.get("temoin2")).append("\n\n")
                 .append("L'acte a été validé, certifié conforme et signé électroniquement par l'Officier de l'État Civil.");
            
            Paragraph pCorps = new Paragraph(corps.toString(), fontCorps);
            pCorps.setLeading(18f); 
            document.add(pCorps);

            document.add(new Paragraph("\n\n\n"));

            // --- ZONE SÉCURITÉ BLOCKCHAIN & QR CODE ---
            Paragraph ligne = new Paragraph("________________________________________________________________________\n\n", fontHeader2);
            ligne.setAlignment(Element.ALIGN_CENTER);
            document.add(ligne);

            Paragraph securite = new Paragraph("SÉCURITÉ ET SCELLLEMENT NUMÉRIQUE\n", fontHeader1);
            securite.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.RED));
            document.add(securite);

            Paragraph hashInfo = new Paragraph("Empreinte Blockchain (SHA-256) :\n" + donnees.get("hash_blockchain"), fontBlock);
            document.add(hashInfo);

            // Utilisation explicite du type iText ici aussi
            com.itextpdf.text.Image qrCodeImg = genererImageQRCode(donnees.get("hash_blockchain"), 120, 120);
            qrCodeImg.setAlignment(Element.ALIGN_RIGHT);
            qrCodeImg.setSpacingBefore(-40); 
            document.add(qrCodeImg);

            document.close();
            System.out.println("PDF créé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void genererActeNaissancePDF(java.util.Map<String, String> donnees, String cheminSortie) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 50, 50, 50, 50);
        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(cheminSortie));
            document.open();

            // --- REPUBLIQUE DE MADAGASCAR ---
            com.itextpdf.text.Font fontHeader1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontHeader2 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, com.itextpdf.text.Font.ITALIC);
            com.itextpdf.text.Font fontTitre = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontCorps = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontBlock = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 9, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.DARK_GRAY);

            com.itextpdf.text.Paragraph rep = new com.itextpdf.text.Paragraph("REPOBLIKAN'I MADAGASCAR\n", fontHeader1);
            rep.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            com.itextpdf.text.Paragraph dev = new com.itextpdf.text.Paragraph("Fitiavana - Tanindrazana - Fandrosoana\n---------\n", fontHeader2);
            dev.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(rep);
            document.add(dev);

            // --- CUA ARRONDISSEMENT CENTRAL ---
            com.itextpdf.text.Paragraph cua = new com.itextpdf.text.Paragraph("PROVINCE D'ANTANANARIVO\nCOMMUNE URBAINE D'ANTANANARIVO\nARRONDISSEMENT CENTRAL\n", fontHeader1);
            cua.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            document.add(cua);

            document.add(new com.itextpdf.text.Paragraph("\n\n"));

            // --- TITRE DE L'ACTE ---
            com.itextpdf.text.Paragraph titre = new com.itextpdf.text.Paragraph("COPIE D'ACTE DE NAISSANCE", fontTitre);
            titre.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new com.itextpdf.text.Paragraph("N° Acte : " + donnees.get("id_naissance") + "\n\n", fontHeader1));

            // --- CORPS DE L'ACTE ---
            StringBuilder corps = new StringBuilder();
            corps.append("Le ").append(donnees.get("date_naissance"))
                .append(", est né à ").append(donnees.get("lieu_naissance"))
                .append(" un enfant du sexe mentionné au registre, nommé :\n\n")
                .append("NOM : ").append(donnees.get("nom_enfant")).append("\n")
                .append("PRÉNOMS : ").append(donnees.get("prenoms_enfant")).append("\n\n")
                .append("Fils/Fille de :\n")
                .append("PÈRE : ").append(donnees.get("nom_pere")).append("\n")
                .append("MÈRE : ").append(donnees.get("nom_mere")).append("\n\n")
                .append("Certifié conforme aux registres de l'État Civil de la Commune Urbaine d'Antananarivo.");
            
            com.itextpdf.text.Paragraph pCorps = new com.itextpdf.text.Paragraph(corps.toString(), fontCorps);
            pCorps.setLeading(18f);
            document.add(pCorps);

            document.add(new com.itextpdf.text.Paragraph("\n\n\n"));

            // --- COUCHE BLOCKCHAIN & SÉCURITÉ ---
            com.itextpdf.text.Paragraph ligne = new com.itextpdf.text.Paragraph("________________________________________________________________________\n\n", fontHeader2);
            ligne.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(ligne);

            com.itextpdf.text.Paragraph securite = new com.itextpdf.text.Paragraph("SÉCURITÉ ET SCELLLEMENT NUMÉRIQUE\n", fontHeader1);
            securite.setFont(new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.RED));
            document.add(securite);

            com.itextpdf.text.Paragraph hashInfo = new com.itextpdf.text.Paragraph("Empreinte Blockchain (SHA-256) :\n" + donnees.get("hash_blockchain"), fontBlock);
            document.add(hashInfo);

            // Intégration du QR code contenant le Hash de Naissance
            com.itextpdf.text.Image qrCodeImg = genererImageQRCode(donnees.get("hash_blockchain"), 120, 120);
            qrCodeImg.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            qrCodeImg.setSpacingBefore(-40); 
            document.add(qrCodeImg);

            document.close();
            System.out.println("PDF Naissance créé avec succès.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}