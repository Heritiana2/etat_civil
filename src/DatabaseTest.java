import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/etat_civil_cua";
        String user = "root";
        String password = "root123";

        System.out.println("Tentative de connexion...");


        try (Connection connection = DriverManager.getConnection(url, user, password)){
            if (connection != null) {
                System.out.println("Succès : Le paquet Java est bien connécté a MySQL !");
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Erreur : Connexion échouée.");
            e.printStackTrace();
        }
    }
}
