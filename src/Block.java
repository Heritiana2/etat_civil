import java.util.Date;

public class Block {
    public String Hash;
    public String previousHash;
    private String data;
    private long timeStamp;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.Hash = calculateHash();
    }

    public String calculateHash() {
        String input = previousHash + Long.toString(timeStamp) + data;
        return StringUtil.applySha256(input);
    }
}
