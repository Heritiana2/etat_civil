public class Main {
    public static void main(String[] args) {
        String text = "heritiana CUA";
        String hash = StringUtil.applySha256(text);
        System.out.println("Texte : " + text);
        System.out.println("Hash SHA-256 : " + hash);
    }
}
