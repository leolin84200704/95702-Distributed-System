import com.google.gson.Gson;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

// Name: Leo Lin
// AndrewID: hungfanl

public class Block extends java.lang.Object {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    int index;
    Timestamp timestamp;
    String data;
    String previousHash;
    BigInteger nonce;
    int difficulty;
    Block (int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        nonce = new BigInteger("0");
    }
    // Calculate the hash for the block
    public String calculateHash() {
        String information = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        String hash_value = null;
        try {
            MessageDigest md;
            // Compute SHA-265 code for the input
            md = MessageDigest.getInstance("SHA-256");
            md.update(information.getBytes(StandardCharsets.UTF_8));
            hash_value = bytesToHex(md.digest());
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("No Hash available" + e);
        }
        return String.valueOf(hash_value);
    }

    public String getData() {
        return data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getIndex() {
        return index;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    // Make sure that the has for the block meets the requirement, otherwise add the nonce by one and recalculate.
    public String proofOfWork() {
        String hash_value = null;
        boolean leading_zero = false;
        while(!leading_zero) {
            leading_zero = true;
            hash_value = calculateHash();
            for(int i = 0; i < difficulty; i++){
                if(hash_value.charAt(i) != '0') {
                    leading_zero = false;
                    nonce = nonce.add(BigInteger.valueOf(1));
                    break;
                }
            }
        }
        return hash_value;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Transfer the block in the form of json.
    public String toString() {
        Block b = new Block (index, timestamp, data, difficulty);
        b.nonce = nonce;
        b.setPreviousHash(previousHash);
        Gson gson = new Gson();
        String messageToSend = gson.toJson(b);
        return messageToSend;
    }
    // Reference from Lab1 submission
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
